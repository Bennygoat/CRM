# ADR 0001：資料模型重整

| 項目 | 內容 |
|---|---|
| 狀態 | 已接受；第 8 節的問題已於 2026-11-08 全部定案 |
| 日期 | 2026-11-07 |
| 工單 | CRM-35、CRM-41 |
| 新資料表與關聯圖 | [資料模型](../data-model.md) |
| 相關 ADR | [ADR 0002：模組化單體](0002-modular-monolith.md) |

ADR（Architecture Decision Record，架構決策紀錄）用來保存「當時為什麼這樣決定」。程式碼看得出結果，看不出原因和被放棄的選項。

本文件內容分成兩種，閱讀時請分開看：

- **已決定**：2026-11-07 使用者確認，記錄在 CRM-35 留言「資料模型決定」、「產品範圍決定」，以及 CRM-41 底下工單的驗收條件。
- **建議**：寫 ADR 時對照程式發現的缺口，還沒定案，都列在第 8 節，以 Q 編號標示。

## 1. 背景

專案由多人分模組開發，資料模型留下這些問題：

- 有兩套訂單：B2C 用 `Order`，ERP 用 `SalesOrder`。B2C 訂單只做庫存預留，沒有接上釋放與出貨流程。
- CRM 和 ERP 沒有關聯：合約和銷貨單之間沒有欄位相連。
- 命名不一致：`orderid`、`createat` 和 `created_at` 並存，資料表有單數也有複數。
- 金額型別不一致：有 `Double`、`Long`，也有 `BigDecimal`。
- 部分關聯只存 ID，沒有外鍵（Foreign Key，資料庫層級保證參照的資料存在）。

資料庫會用 Flyway（資料庫版本管理工具，用有編號的 SQL 檔依序建立結構）從 V1 重建，不保留 `database/sample.sql` 的結構與資料。沒有舊資料要搬移，所以這是修正資料模型成本最低的時機。

## 2. 決定一：訂單合併成一套，保留 SalesOrder

**決定**

- 刪除 `Order`、`OrderDetail`，B2C 改走 `SalesOrder` 的預留與出貨流程。
- 新增 `channel` 欄位區分 B2B、B2C。
- `merchantTradeNo`、優惠券等 B2C 欄位搬到 `SalesOrder`。

產品範圍定案後，這個決定補上三項修正：付款與出貨狀態分開、重新設計訂單狀態、新增運費，見第 5 節。

**原因**

`SalesOrder` 已經有完整的預留、釋放、出貨和出貨單，`Order` 沒有。把 B2C 接上去，比替 `Order` 再寫一份庫存流程省事。`SalesOrder.customer` 指向 `CustomerBase`，本來就能接 C 端客戶。

**考慮過的選項**

| 選項 | 好處 | 壞處 | 結果 |
|---|---|---|---|
| 合併到 `SalesOrder` | 只有一份訂單與庫存流程 | 綠界和購物車的程式要改接 | 採用 |
| 保留兩套，各自接庫存 | 不動綠界 | 同一套邏輯要維護兩份 | 放棄 |
| 保留兩套，`Order` 成立時同步產生 `SalesOrder` | 兩邊程式都少改 | 要處理兩份訂單的同步與不一致 | 放棄 |

**欄位對應**

| `orders` | `sales_orders` | 說明 |
|---|---|---|
| `orderid` | `sales_order_id` | |
| `platformid` | `channel` | 固定為 `B2C`，`platforms` 刪除 |
| `customer_id` | `customer_id` | 參照對象從 C 端表改成父表 `customers` |
| `orderdate` | `order_date` | |
| `orderstatus` | `order_status`、`shipping_status` | 重新設計，見 Q1 |
| `paymentstatus` | `payment_status` | 兩邊都用 `PaymentStatus` |
| `payment_method` | `payment_method` | 兩套值不同，見 Q2 |
| `addressid` | `shipping_address` 等 | 改成下單時複製，見第 6 節與 Q5 |
| `total_amount` | `total_amount` | `Double` 改 DECIMAL，語意見 Q3 |
| `merchant_trade_no` | `merchant_trade_no` | 唯一、可為空，B2B 訂單沒有 |
| `createat`、`updateat` | `created_at`、`updated_at` | |
| 反向關聯 `usedCoupon` | `customer_coupons.sales_order_id` | 外鍵留在優惠券這一側，見第 6 節 |

| `orderdetails` | `sales_order_details` | 說明 |
|---|---|---|
| 複合主鍵（訂單＋商品） | `item_id` | 明細有自己的 ID，庫存異動才能指到明細 |
| `quantity`（整數） | `quantity`（DECIMAL） | 購物車數量要轉型 |
| `unitprice`（Double） | `unit_price`（DECIMAL） | |

`sales_orders` 和 `sales_order_details` 有一些必填欄位是 B2C 下單時目前不會提供的，`OrderService` 改接時要補上：

| 欄位 | B2C 要怎麼提供 |
|---|---|
| `order_number` | 比照 ERP 產生 |
| `warehouse_id` | 預設倉庫，見第 7 節 CRM-32 |
| `total_tax_amount`、`total_net_amount`、明細的金額欄位 | 計算方式見 Q3 |
| `shipping_method` | 目前下單後才選，見 Q4 |
| `created_by`、`updated_by` | 見 Q8 |
| 明細的 `item_sequence`、`unit_id` | 依購物車順序編號；從商品帶入單位 |

## 3. 決定二：客戶保留 CustomerBase 加 B、C 兩張子表

**決定**

- 保留 `CustomerBase`、`BCustomer`、`CCustomer`，使用 JOINED 繼承。
- `spending` 移到 `CCustomer`。
- 調整容易混淆的命名（具體名稱見 Q7）。
- 登入身分區分由 CRM-26 處理，不在資料模型解決。

JOINED 繼承是 JPA 把繼承關係存進資料庫的一種方式：父類別一張表放共用欄位，每個子類別一張表放專屬欄位，子表主鍵同時是指向父表的外鍵。父表的 `customer_type` 是鑑別欄位（Discriminator，標示這筆資料屬於哪個子類別）。

**原因**

JOINED 同時滿足兩種參照需求，而且都能用外鍵保證：

- 銷貨單、出貨單要能指向任一種客戶，所以指向父表。
- 聯絡人、商機只屬於 B 端，購物車、優惠券只屬於 C 端，所以指向子表。

**考慮過的選項**

| 選項 | 好處 | 壞處 | 結果 |
|---|---|---|---|
| JOINED（現況） | 外鍵能精準區分 B、C；不用改繼承結構 | 查客戶完整資料要 JOIN | 採用 |
| 單表（SINGLE_TABLE） | 查詢不用 JOIN | B、C 專屬欄位都要可為空，外鍵分不出 B、C | 放棄 |
| B、C 完全分開 | 兩邊最獨立 | 銷貨單要有兩個客戶外鍵，訂單合併的好處消失 | 放棄 |

**`spending` 移動的原因**：消費總額和 VIP 等級只跟 C 端有關，放在父表會讓 B 端多一個永遠用不到的欄位。型別同時從 `Long` 改成 DECIMAL。

## 4. 決定三：用 contract_id 串起 CRM 和 ERP

**決定**

- `SalesOrder` 加 `contract_id`，可為空，因為 B2C 訂單沒有合約。
- 流程：商機 → 合約 → 銷貨單 → 出貨。

**原因**

商機到合約已經有關聯（`contracts.opportunity_id`），銷貨單到出貨也有（`sales_shipments.sales_order_id`），只缺合約到銷貨單這一段。加一個外鍵就能從商機一路查到出貨。

**取捨**

- 外鍵放在銷貨單，代表一份合約可以有多張銷貨單，符合分批出貨的長期合約；一張銷貨單最多對應一份合約。
- 銷貨單的 `customer_id` 和合約所屬客戶可能不一致，資料庫難以直接檢查，要由 Service 驗證，見 Q10。
- CRM-35 只規定 B2C 沒有合約。B2B 訂單是否一定要有合約，見 Q10。

## 5. 決定四：產品範圍

**定位**：中小型品牌商的 CRM + 進銷存，不做完整 ERP。

進銷存指採購、銷售和庫存管理。這個定位決定資料模型的邊界：資料表要涵蓋從商機到收款、從採購到出貨退貨的主線，但不延伸到會計和生產。

**不做的項目**

| 項目 | 對資料模型的意義 |
|---|---|
| 總帳 | 不建會計科目、傳票資料表 |
| 製造 | 不建物料清單、工單資料表 |
| 薪資 | 不建人事薪資資料表 |
| 多幣別 | 不建匯率資料表，金額一律視為台幣；既有 `currency` 欄位的去留見 Q12 |
| 批號序號 | 庫存維持以「商品＋倉庫」為單位，不追蹤到批號 |

**第一批：影響資料表，V1 前定案**

| 項目 | 工單 | 對 V1 的影響 |
|---|---|---|
| 付款與出貨狀態分開 | CRM-44、CRM-43 | `sales_orders` 新增 `shipping_status`；`order_status` 不再表達出貨進度，出貨也不再改動付款狀態（Q1、Q20） |
| 運費 | CRM-45 | `sales_orders` 新增 `shipping_fee`，訂單總額包含運費；運費規則放 `system_params`（Q3、Q15） |
| 退貨退款 | CRM-46 | 新增 `sales_returns`、`sales_return_details`，可退部分商品；供應商退貨不在範圍（Q17） |
| 分批收貨與出貨 | CRM-47 | `purchase_order_details` 新增 `received_quantity`，`sales_order_details` 新增 `shipped_quantity`（Q18） |
| 商機 status 改由 stage 推算 | CRM-50 | 刪除 `opportunities.status`，不另外存；`CLOSED_WON` 是 `WON`、`CLOSED_LOST` 是 `LOST`，其他階段是 `OPEN` |
| 移除商機評分 | CRM-50 | 刪除 `total_rating_sum`、`number_of_ratings`，保留 `priority` |
| 移除 `BCustomerType.VENDOR` | CRM-50 | 資料表不變，供應商一律用 `suppliers` |
| 稅率讀商品設定 | CRM-51 | 不再寫死 5%，要支援免稅；稅率數值放 `system_params`（Q3、Q16） |
| VIP 改看近 12 個月消費 | CRM-48 | `spending` 的意義改變，福利要能在結帳時計算（Q19） |
| 可調參數 | CRM-57 | 新增 `system_params`，存放稅率、運費等可調參數，第一批的運費和稅率會用到（[ADR 0002](0002-modular-monolith.md)） |

CRM-42（B2C 改接 `SalesOrder`）是決定一的實作。CRM-49（優惠券檢查）只補 Service 的驗證，`coupon_templates` 已有需要的欄位，不影響資料表。

**第二批：V1 之後加入**

這些功能不放進 V1 的資料表，上線時用新的 migration 加入。

| 項目 | 工單 | 之後的改動 |
|---|---|---|
| 業務負責人 | CRM-52 | 客戶和商機加負責人欄位 |
| 報價單 | CRM-53 | 新增報價單與明細，取代已刪除的 `quotationId` |
| B2B 付款條件與信用額度 | CRM-54 | 客戶加付款條件，訂單算出到期日 |
| 應收應付與沖帳 | CRM-55 | 新增應收應付資料表，支援部分沖帳 |
| 電子發票 | CRM-56 | 新增發票資料，和訂單關聯 |

**原因與取捨**

第一批都會改到訂單、庫存的核心欄位，趁 V1 一起建，不必再搬資料。第二批大多是新增資料表或可為空的欄位，之後用新的 migration 加入，不影響現有資料，所以不用為了它們延後 V1。代價是 V1 之後到 CRM-55 完成之前，B2B 月結只有訂單層級的收款紀錄，看不到帳齡和部分沖帳（Q20）。

## 6. 直接套用的規則

這些規則不需要比較選項，CRM-35 已決定。「落實方式」是對照程式後的具體做法。

| 規則 | 落實方式 |
|---|---|
| 刪除 `Employee`、`Platform`、`quotationId` | `employees`、`platforms` 不建表。`quotation_id` 不建欄位，目前也沒有程式使用 |
| 資料表一律小寫複數 | 完整對照見[資料模型](../data-model.md)第 3 節；`customer_base` 等命名見 Q7 |
| 欄位統一 | 全部用 snake_case，例如 `cartid` 改 `cart_id`、`createat` 改 `created_at` |
| 金額全部用 DECIMAL，對應 BigDecimal | 精度建議統一，見 Q12 |
| `contactPersonId` 改外鍵 | 欄位改名 `contact_id`，指向 `contacts`，和其他資料表一致；目前也沒有程式讀寫這個欄位 |
| `createdBy` 加外鍵 | 所有 `created_by`、`updated_by` 指向 `users`；客戶自己操作時填什麼見 Q8 |
| Product 的 `createdBy` 和 `createdByUser` 只留一組 | 留下 `Long createdBy`、`Long updatedBy`，刪除 `createdByUser`、`updatedByUser`：其他 Entity 都用 `Long`，稽核機制也是填 `Long`，而物件關聯只有註解掉的程式在用。外鍵在資料庫層建立，不受影響 |
| 優惠券的多對多改成一對多 | `CCustomer` 和 `CustomerCoupon` 的 Entity 與 `sample.sql` 都已經是一對多，舊的多對多只剩註解。V1 不建中介表即可，順手刪掉註解 |
| 下單時把地址複製進訂單 | 訂單不再關聯地址表。這是快照（Snapshot，把當下的值存一份，之後來源改變也不受影響）：客戶改地址或刪地址，舊訂單的收件資料不變。`OrderService` 不必再為每筆訂單新增一筆地址 |
| Inventory 加 `version` | `inventories.version`，`BIGINT NOT NULL DEFAULT 0` |
| `V1_1__Add_FK_Constraints.sql` 併進 V1 | 外鍵全部寫進 V1，舊檔刪除。這支檔案對 `suppliers.updated_by` 建外鍵，但這個欄位不存在，見 Q9 |

優惠券和訂單的外鍵放在 `customer_coupons.sales_order_id`（唯一、可為空），不放在 `sales_orders`。這樣維持現有結構，唯一限制保證一張券只用在一張訂單；CRM-35 說的「優惠券欄位搬到 SalesOrder」，落實為把 `Order.usedCoupon` 這個反向關聯搬到 `SalesOrder`。取捨是取消訂單退回優惠券時，要決定是否保留使用紀錄，見第 7 節 CRM-43。

## 7. 對其他工單的影響

### CRM-17 導入 Flyway

- V1 依[資料模型](../data-model.md)建立 47 張資料表，`V1_1` 的外鍵併入後刪除舊檔。
- 種子資料（Seed Data，全新環境一定要有的基本資料）要調整：CRM-17 留言提到的「平台」不再需要，因為 `platforms` 已刪除。仍需要系統使用者（見 Q8）、預設倉庫、VIP 等級、`system_params` 的預設值、預設角色與權限，以及代碼表的內容。
- `ddl-auto=validate` 會在啟動時比對 Entity 和資料表，不符就啟動失敗。所以 V1 不能單獨合併：改表名、改欄位、刪 `Order` 都要和 Entity 同時改，見 Q14。
- `validate` 不檢查 Entity 沒有對應的欄位和資料表。V1 新增的欄位只要有預設值或可為空，功能工單就能晚於 V1 完成。
- `sample.sql` 顯示 `@Enumerated` 欄位被 Hibernate 建成 MySQL 的 `enum` 型別。V1 如果用 VARCHAR，要實測 `validate` 會不會判定型別不符。
- 專案的測試資料庫是 H2，正式環境是 MySQL，V1 的語法兩邊都要能跑，見 Q13。
- 動手前要先完成 CRM-4，確認線上資料庫狀態。

### CRM-13 金額改 BigDecimal

- 工單背景列的 `Order.totalAmount`、`OrderDetail.unitprice` 會隨 `Order` 刪除消失，剩下要改的是 `VIPLevel` 門檻和 `spending`。
- 驗收條件的誤差測試要改以 `spending` 和 VIP 門檻為對象，因為被測的舊欄位不存在了。
- V1 已經把這些欄位建成 DECIMAL，Java 型別不改的話，`validate` 預期會因型別不符而失敗（CRM-17 要實測），所以這部分要和 V1 一起完成，不能排在後面。
- 綠界金額是整數，程式用 `intValue()`、`longValue()` 轉換，小數會被直接捨去。訂單金額改 DECIMAL 後要明確規定 B2C 金額的進位方式，見 Q3。

### CRM-14 庫存併發

樂觀鎖（Optimistic Lock，更新時比對版本號，版本不符就代表別人先改過，這次更新失敗）需要 `version` 欄位。

- `inventories.version` 在 V1 就會建好。Entity 加 `@Version` 可以晚於 V1。
- B2B 和 B2C 合併後走同一支 `InventoryService`，一套併發測試就能涵蓋兩邊。
- `InventoryRepository.deductStockAndAllocation` 是直接下 UPDATE 的批次更新，不會經過 Entity，所以 `version` 不會自動增加。CRM-14 要在這條 UPDATE 手動加版本號，或改走 Entity，否則其他交易偵測不到這次修改。
- `inventories` 目前沒有「商品＋倉庫」的唯一限制，併發時可能建出兩筆庫存，版本號也救不了，見 Q11。

### CRM-26 員工身分驗證

- 資料模型不新增欄位。員工在 `users`，客戶在 `c_customers`，兩邊的 ID 各自編號，同一個數字可能同時存在，所以 CRM-26 要以「身分類型＋使用者 ID」判斷身分。
- CRM-26 是 P0，可能早於資料庫重建完成。它不依賴新結構，改表名時只需要動 Entity 對應，不必等待。

### CRM-32 B2C 訂單與庫存

訂單合併後，這張工單的範圍大幅縮小：

- 取消釋放預留、出貨扣庫存改用 `SalesOrder` 現有流程，不必另外寫。
- `documentDetailId` 傳商品 ID 的問題會消失，因為明細有自己的 `item_id`。
- 「倉庫固定用 ID 1」改成讀設定值，寫入 `sales_orders.warehouse_id`。
- 逾期自動取消：建議用 `created_at` 加設定的期限判斷，不新增欄位。

CRM-32 和 CRM-43 都寫到狀態規則與取消退券，開工前要先劃清範圍。CRM-43 的背景寫明釋放庫存由 CRM-32 處理，本文件依此把退券放在 CRM-43。

### CRM-43、CRM-44 訂單狀態與付款

- 狀態機要等 Q1 定案。`shipSalesOrder` 只接受 `CONFIRMED`，Q1 的建議維持這個條件：貨到付款下單即 `CONFIRMED`、線上付款在付款成功後轉 `CONFIRMED`。分批出貨期間 `order_status` 也停在 `CONFIRMED`，可以繼續出貨。
- `SalesOrderService.handleOrderShippedEvent` 同時改訂單狀態和付款狀態，要改成只更新 `shipping_status`。`updateSalesOrder` 用 `order_status` 判斷是否已出貨，也要改看 `shipping_status`。
- 取消時退回優惠券：建議狀態改回 `UNUSED`、清空 `sales_order_id` 和 `used_at`。代價是已取消訂單查不到用過哪張券；要保留的話，需要在訂單上另存一份券號，見 Q3。

### CRM-48 VIP

- `updateCustomerSpending` 改成讀 `sales_orders`，不再讀 `orders`；統計範圍和 `spending` 的意義見 Q19。
- `evaluateAndUpdateVipLevel` 的 90 天降級判斷由近 12 個月的規則取代。

### CRM-50 商機清理

`opportunities` 少了三個欄位，Entity 對應的欄位要和 V1 一起移除（Q14）。其他要配合的地方：

- `OpportunityRepository` 的 `findByStatus`、`countByStatus`、`sumExpectedValueByStatus` 等查詢改用 `stage`。
- `OpportunityService` 的評分方法、`POST /api/opportunities/{id}/rate`、`OpportunityDto.averageRating` 移除。
- 前端 `SalesFunnel`、`SalesFunnelBoard` 的評分，以及 `CRMCompany`、`ERPCustomers` 的「供應商」選項移除。
- status 由 stage 推算後，`stage` 不能是空值，見 Q11。

### CRM-51 稅額

CRM-51 的背景只提到 `SalesOrderService`，但 `PurchaseOrderService` 和 `SalesOrderFaker` 同樣寫死 5%，也要一起改。

## 8. 問題與決定

這些問題尚未定案。建議只是起點，定案後請更新本節和資料模型文件。

| 問題 | 狀態 | 這次範圍決定後的變化 |
|---|---|---|
| Q1 訂單狀態設計 | 已決定 | 2026-11-08 採用三個狀態欄位 |
| Q2 付款方式 | 已決定 | 2026-11-08 擴充 `PaymentMethod` enum |
| Q3 金額欄位語意 | 已決定 | 2026-11-08 B2C 含稅、B2B 未稅，總額公式採用建議 |
| Q4 物流方式與物流資訊 | 已決定 | 2026-11-08 下單時選配送方式 |
| Q5 收件資料 | 已決定 | 2026-11-08 新增收件人姓名與電話 |
| Q6 `channel` | 已決定 | 2026-11-08 後台不替 C 端開單 |
| Q7 客戶命名 | 已決定 | 2026-11-08 採用建議命名 |
| Q8 `created_by` | 已決定 | 2026-11-08 採用系統使用者 |
| Q9 `suppliers.updated_by` | 已決定 | 2026-11-08 `suppliers`、`warehouses` 都補上 |
| Q10 合約規則 | 已決定 | 2026-11-08 採用建議 |
| Q11 其他外鍵與限制 | 已決定 | 2026-11-08 採用建議 |
| Q12 精度與 `currency` | 已決定 | 2026-11-08 統一精度，`currency` 保留 |
| Q13 主鍵命名與測試資料庫 | 已決定 | 2026-11-08 採用建議 |
| Q14 合併順序 | 已決定 | 2026-11-08 刪除和改名同批，新增先建表 |
| Q15 運費規則 | 已決定 | 2026-11-08 單一運費加免運門檻，宅配和超商相同 |
| Q16 稅率存放 | 已決定 | 2026-11-08 採用建議 |
| Q17 退貨退款細節 | 已決定 | 2026-11-08 採用建議，訂單加 `PARTIALLY_REFUNDED` |
| Q18 分批收貨紀錄 | 已決定 | 2026-11-08 不新增收貨單 |
| Q19 VIP 消費與福利 | 已決定 | 2026-11-08 近 12 個月消費，SILVER、GOLD 都免運 |
| Q20 收款登記 | 已決定 | 2026-11-08 後台登記收款，新增 `paid_at` |
| Q21 共用欄位 | 已決定 | 2026-11-08 採用分級 |
| Q22 畫面權限與狀態機資料表 | 已決定 | 2026-11-08 代碼表並用 enum，新增角色資料表 |

**2026-11-08 起全部定案。之後若有變更，請更新本節和資料模型文件。**

### 訂單

**Q1：訂單狀態怎麼設計？**
已決定（2026-11-08 使用者確認）：分成 `order_status`、`payment_status`、`shipping_status` 三個欄位，值如下表。

舊設計的問題是一個欄位混了三件事：`PENDING_PAYMENT` 是付款進度，`SHIPPED` 是出貨進度，`CANCELLED` 才是訂單本身。分批出貨和月結都需要這三件事各自前進。

| 欄位 | 值 |
|---|---|
| `order_status` | `DRAFT`、`PENDING`、`CONFIRMED`、`COMPLETED`、`CANCELLED` |
| `payment_status` | `UNPAID`、`PAID`、`PARTIALLY_REFUNDED`、`REFUNDING`、`REFUNDED`（`PARTIALLY_REFUNDED` 是新增，見 Q17） |
| `shipping_status` | `NOT_SHIPPED`、`PARTIALLY_SHIPPED`、`SHIPPED`、`PENDING_PICKUP`、`DELIVERED` |

舊狀態的對應：

| 舊值 | `order_status` | `shipping_status` | 備註 |
|---|---|---|---|
| `PENDING_PAYMENT` | `PENDING` | `NOT_SHIPPED` | 線上付款，等待付款 |
| `PENDING_SHIPMENT` | `CONFIRMED` | `NOT_SHIPPED` | |
| `PARTIAL_SHIPPED` | `CONFIRMED` | `PARTIALLY_SHIPPED` | |
| `SHIPPED` | `CONFIRMED` | `SHIPPED` | |
| `PENDING_PICKUP` | `CONFIRMED` | `PENDING_PICKUP` | |
| `COMPLETE`、`COMPLETED` | `COMPLETED` | `DELIVERED` | 條件建議是已送達且已付款 |
| `CANCELLED` | `CANCELLED` | `NOT_SHIPPED` | |
| `RETURN_REQUESTED`、`RETURNED` | 不變 | 不變 | 改由 `sales_returns.status` 表達 |

前台 `frontend/src/components/tabs/Orders.jsx` 和後台 OM 頁用到舊狀態值，要一起改。

**Q2：付款方式的值怎麼統一？**
`sales_orders.payment_method` 是自由字串，ERP 前端送 `CASH`、`CREDIT_CARD`、`TRANSFER`，Faker 另有 `LINE_PAY`、`CASH_ON_DELIVERY`；B2C 用 enum，值是 `ONLINE_PAYMENT`、`CASH_ON_DELIVERY`。

已決定（2026-11-08 使用者確認）：擴充 `PaymentMethod` enum，涵蓋上述全部的值，資料庫存字串。

**Q3：金額欄位各代表什麼？折扣、運費和稅怎麼算？**
已決定（CRM-45、CRM-51）：稅率依商品設定、支援免稅、總額要能對上明細、訂單總額包含運費、傳給綠界的金額等於訂單總額。

CRM-51 的驗收條件寫「訂單總額等於明細加總」，加上運費和折扣後這句話要調整。

已決定（2026-11-08 使用者確認）：

- 總額公式。`total_amount` 是客戶實付金額，等於明細金額加總，減 `discount_amount`，加 `shipping_fee`。
- 折扣欄位。`Order` 只有折扣後總額，`SalesOrder` 沒有折扣欄位，所以新增 `discount_amount`（DECIMAL，預設 0），優惠券和 VIP 折扣都算在內；如果要保留已取消訂單的用券紀錄，再加券號快照。
- 單價是否含稅。B2C 含稅，由總額反算未稅額；B2B 未稅，符合報價和發票分列稅額的習慣，`SalesOrderService` 目前的含稅反算要改。
- 折扣怎麼分攤到明細。部分退貨要知道每項商品實際付多少，所以依金額比例分攤，尾差放最後一筆。
- 運費是否課稅。視為應稅，併入表頭的稅額，不做成明細。
- 進位。綠界只收整數，B2C 的 `total_amount` 四捨五入到整數。

**Q4：B2C 什麼時候決定物流方式？物流資訊存哪裡？**
B2C 目前是下單後才選物流。有了運費之後，如果運費依物流方式不同，下單時就要知道方式，否則金額無法在付款前確定。綠界回傳的物流編號現在沒有保存，超商門市代號也沒有欄位。

已決定（2026-11-08 使用者確認）：配送方式（宅配或超商）移到下單時選，`shipping_method` 維持必填，只有門市在之後選；物流編號存 `sales_shipments.tracking_number`。門市代號的欄位，CRM-42 實作時再依綠界回傳內容定。

**Q5：訂單要複製哪些收件資料？**
地址快照已確定。`shipping_address` 已有，但沒有收件人姓名和電話。綠界超商物流要帶這兩項，而 `CreateOrderRequestDto` 只傳地址。

已決定（2026-11-08 使用者確認）：新增 `recipient_name`、`recipient_phone`，可為空（B2B 可能不需要）；B2C 下單 API 改成必填。

**Q6：後台能不能替 C 端客戶開單？**
已決定（CRM-42）：`channel` 由 Service 依客戶類型帶入，API 不開放傳入。

`channel` 和客戶的 `customer_type` 目前一對一，等於是重複資料，好處是查詢不用 JOIN，之後也能增加渠道。ERP 開單 API 目前接受任何客戶，`SalesOrderFaker` 也隨機選客戶，都要配合調整。

已決定（2026-11-08 使用者確認）：後台不替 C 端客戶開單，只有前台產生 `B2C` 訂單。

### 客戶

**Q7：客戶相關的具體命名？**
已決定（2026-11-08 使用者確認）：採用下表命名，父表用 `customers`。

| 現況 | 問題 | 新名稱 |
|---|---|---|
| `customer_base` | 不是複數 | `customers`（Entity 類別名不變） |
| `customer` | 名字像父表，其實只放 C 端 | `c_customers`，和 `b_customers` 對稱 |
| `customer_address` | 看不出只屬於 C 端 | `c_customer_addresses` |
| `customertel` | 沒有分隔 | `phone` |
| `bcustomer_type` | 和鑑別欄位 `customer_type` 容易混淆，內容其實是合作關係 | `relationship_type` |
| `bcustomer_level` | 同上 | `customer_level` |

注意：舊的 `customer` 是 C 端，新的 `customers` 是父表，名字很像但意思不同。

### 關聯與稽核

**Q8：客戶自己操作時，`created_by` 填什麼？**
`created_by`、`updated_by` 必填，加外鍵後只能填 `users` 的 ID，而 `inventory_movements.recorded_by` 也一樣。客戶註冊、B2C 下單和申請退貨時沒有員工。目前的稽核設定固定回傳 1，CRM-16 改成讀目前登入者後，客戶 ID 會寫進這些欄位，造成外鍵錯誤或記到錯的員工。

已決定（2026-11-08 使用者確認）：V1 的種子資料建立一筆不可登入的系統使用者，客戶操作一律記到這筆，欄位維持必填。`OrderService` 的 `SYSTEM_USER_ID` 和目前固定回傳 1 的稽核設定已經是這個做法，這裡只是把它正式化。

放棄的做法：

- 改成 VARCHAR 存 `'SYS'`：直覺，但要放棄 `createdBy` 加外鍵的決定，打錯字資料庫也不會擋。
- 改成可為空，空值代表客戶本人：不用種子資料，但分不出是客戶操作還是程式漏填。

**Q9：`suppliers` 要不要加 `updated_by`？**
`V1_1` 對 `suppliers.updated_by` 建外鍵，但 Entity 和 `sample.sql` 都沒有這個欄位，所以這段 SQL 不能照搬。

已決定（2026-11-08 使用者確認）：補上 `updated_by`、`updated_at`，和其他主檔一致；`warehouses` 同樣缺這兩個欄位，一起補上。

**Q10：合約和銷貨單的規則？**
`contracts` 沒有客戶欄位，只能經由 `opportunity_id` 找到客戶，而這個欄位可為空。`ContractController` 卻假設它一定有值。

已決定（2026-11-08 使用者確認）：

- `contracts.opportunity_id` 改成必填，合約一定從商機產生，這也是目前唯一的建立方式。
- B2B 訂單可以不綁合約，保留 ERP 現有的直接開單流程，也讓 CRM-53 的報價單接受後能直接轉銷貨單。
- 銷貨單有綁合約時由 Service 驗證：`channel` 是 `B2B`、合約的客戶和銷貨單客戶相同、合約狀態是 `SIGNED` 或 `ACTIVE`。
- 一份合約可有多張銷貨單，銷貨單總額是否不得超過合約金額，先不限制。

**Q11：其他只存 ID 的欄位和缺少的限制要不要一起補？**
已決定（2026-11-08 使用者確認）：CRM-35 只點名 `contactPersonId` 和 `createdBy`，對照 Entity 後，下列項目一併處理。

| 位置 | 問題 | 處理方式 |
|---|---|---|
| `sales_order_details.unit_id` | 只存 ID | 加外鍵到 `units` |
| `purchase_orders.warehouse_id` | 只存 ID | 加外鍵到 `warehouses` |
| `sales_shipment_details.sales_order_item_id` | 只存 ID | 加外鍵到 `sales_order_details` |
| `inventories` | 沒有商品＋倉庫的唯一限制 | 加 `UNIQUE (product_id, warehouse_id)`，不做批號後這也是庫存的最小單位 |
| `sales_orders.customer_id`、`sales_shipments.sales_order_id` | Entity 可為空，但程式都一定有值 | 改成必填 |
| `opportunities.stage` | Entity 可為空，但 status 要由它推算 | 改成必填 |
| `sales_shipments.customer_id` | 可從銷貨單查到，是重複資料 | 保留，改掉要動 DTO，不值得 |
| `sales_order_details.transaction_number` | 沒有程式使用 | 刪除 |
| `inventory_movements.document_id` | 依 `document_type` 指向不同資料表 | 維持不加外鍵，資料庫無法表達這種關聯 |

### 其他

**Q12：金額和數量的精度？`currency` 要留嗎？**
現況有 `DECIMAL(10,2)`（合約、優惠券）、`DECIMAL(14,2)`（商機）和 `DECIMAL(18,2)`（訂單），`Inventory` 的數量與成本沒有宣告精度。

已決定（2026-11-08 使用者確認）：金額和數量全部用 `DECIMAL(18,2)`，稅率例外（Q16）。平均成本是否要更多小數位，等 ERP 模組整理時再評估。

不做多幣別之後，`sales_orders.currency`、`purchase_orders.currency` 沒有實際作用，但採購單的 DTO 和前端還在讀寫，所以先保留並預設 `TWD`。

**Q13：主鍵命名和測試資料庫？**

已決定（2026-11-08 使用者確認）：

- 主鍵有 `id`（合約、活動、優惠券）和 `xxx_id` 兩種。維持現狀：CRM-35 沒有要求，改名要動 Java 欄位，收益不大。
- V1 要能同時在 MySQL 和測試用的 H2 執行，或測試改用 Testcontainers（測試時用 Docker 啟動真的 MySQL）。V1 只用兩邊都支援的語法，Testcontainers 留給 CRM-19 評估。

**Q14：Entity 改動和 V1 怎麼排順序？**
「後端逐模組改」和「`validate` 要求 Entity 一次對齊」互相衝突：V1 合併當下，所有 Entity 都要對應新結構，`Order` 也要已經移除，否則程式無法啟動。

已決定（CRM-42）：B2C 改接 `SalesOrder` 開成獨立工單，和 CRM-13、CRM-17 的 V1 同批合併。

已決定（2026-11-08 使用者確認）同一批的範圍：

1. 全部 Entity 的表名與欄位對應、CRM-50 的三個欄位移除，一起放進這批，因為 Entity 對應到不存在的欄位時程式無法啟動。
2. CRM-44 到 CRM-48、CRM-51 的新增欄位和資料表，V1 先建好，並給預設值或允許空值，功能工單之後逐張完成。
3. 其他模組的 Service 和 DTO 整理，維持之後逐模組進行。

### 第一批新增

**Q15：運費規則有哪些內容？**
已決定（CRM-57）：運費等可調參數放 `system_params`，不寫死在程式，也符合 CRM-45 的要求。

已決定（2026-11-08 使用者確認）：只有基本運費和免運門檻，宅配和超商運費相同。`system_params` 用參數鍵、值、說明和稽核欄位的鍵值結構，見[資料模型](../data-model.md) 2.8。

**Q16：稅率怎麼對應到商品？**
已決定（CRM-57）：稅率數值放 `system_params`。

已決定（2026-11-08 使用者確認）：採用下列做法。

`products.tax_type` 是自由字串，目前有 `TAXABLE` 和 Faker 的 `VAT_EXCLUSIVE`，沒有對應的稅率。

- `tax_type` 改成 enum，值為 `TAXABLE`、`TAX_FREE`。`TAXABLE` 的稅率讀 `system_params`，`TAX_FREE` 固定為 0。
- `sales_order_details`、`purchase_order_details` 新增 `tax_rate`（`DECIMAL(5,4)`），下單時複製，日後稅率調整不影響舊單。

**Q17：退貨退款的細節？**
已決定（CRM-46）：有退貨單與明細、能退部分商品、入庫寫庫存異動、退款狀態可追蹤、消費同步扣回。

已決定（2026-11-08 使用者確認）：採用下列建議，狀態的顯示名稱由代碼表管理（Q22）。訂單的 `payment_status` 新增 `PARTIALLY_REFUNDED`，部分退貨退款後使用。

- 退貨單狀態。建議 `REQUESTED`、`APPROVED`、`RECEIVED`、`COMPLETED`、`REJECTED`。
- 退款狀態放哪裡。建議放在 `sales_returns.refund_status`，一張訂單可能有多次部分退貨；訂單的 `payment_status` 是否要加 `PARTIALLY_REFUNDED` 也要定。
- 退款金額怎麼算。建議依明細分攤後的實付金額（Q3），運費預設不退。
- 整單退貨時優惠券是否退回。建議不退，因為券是在成立的訂單上用掉的。
- 防止超退。建議加總 `sales_return_details` 檢查，不在訂單明細另存已退數量。
- 不良品是否入庫。建議先全部退回原倉庫，需要時再評估。

**Q18：分批收貨要不要有收貨單？**
銷貨端有 `sales_shipments` 記錄每次出貨，採購端沒有對應的資料表。

已決定（2026-11-08 使用者確認）：不新增。每次收貨都會寫 `inventory_movements`，已有時間、數量和操作者，`document_item_id` 可指到採購明細，對中小型品牌商足夠。代價是沒有獨立的收貨單號，需要驗收流程或對帳時（可能是 CRM-55）再補。

**Q19：`spending` 和 VIP 福利怎麼存？**
已決定（CRM-48）：改看近 12 個月消費，每天排程檢查升降級，福利在結帳時生效。

已決定（2026-11-08 使用者確認）：

- `spending` 改存近 12 個月消費，由每日排程重算，是查詢用的快取。欄位名稱維持 `spending`，前台顯示的「累積消費」要改用詞。
- 依 `order_date` 計算，只算 `COMPLETED` 的 B2C 訂單，扣除完成的退款，不新增 `completed_at`。
- `vip_levels` 新增 `free_shipping`（布林）和 `discount_rate`（可為空）。
- SILVER 免運；GOLD 免運加 9 折。
- VIP 折扣可以和優惠券併用。

**Q20：貨到付款和 B2B 怎麼登記收款？**
CRM-44 要求有明確的收款登記方式，但應收應付（CRM-55）在 V1 之後。

已決定（2026-11-08 使用者確認）：V1 只做訂單層級。後台提供「登記收款」，把 `payment_status` 改成 `PAID`，並新增 `paid_at` 記錄時間。部分收款留給 CRM-55。

### 使用者提出

**Q21：哪些欄位是每張資料表都要有的？**
使用者提議每張資料表都有流水號、版本和建立、修改的人與時間。

已決定（2026-11-08 使用者確認）：分級，用共用的 `@MappedSuperclass` 父類別帶入。

| 欄位 | 適用範圍 | 原因 |
|---|---|---|
| 流水號 | 全部，中介表除外 | 就是現有的自動遞增主鍵 |
| `created_by`、`created_at`、`updated_by`、`updated_at` | 會被修改的業務資料表 | 追查誰改過資料 |
| 只有 `created_by`、`created_at` | 只新增不修改的紀錄，例如 `inventory_movements`、`user_log_actions` | 不會被修改，修改欄位沒有意義 |
| `version` | 會被同時修改的資料表，例如 `inventories`、`sales_orders`、`purchase_orders` | 樂觀鎖衝突時要處理重試或錯誤，不是加了就好 |

**Q22：畫面權限和狀態機要不要用資料表管理？**
使用者提議兩件事。這題影響 Q17 的退貨狀態。

**代碼表**：用母子兩層的資料表管理代碼，母代碼是類別（例如交易狀態），子代碼是類別下的值和顯示名稱。

- 好處：下拉選單和顯示文字不用改程式，新增值不必重新部署。
- 限制：代碼表只定義「有哪些值」，不定義「能從哪個狀態轉到哪個狀態」。程式仍要依狀態走不同流程，新增狀態值通常還是要改程式。
- 已決定（2026-11-08 使用者確認）：狀態值維持 Java enum，轉換規則寫在 Service；代碼表 `code_groups`、`code_items` 存顯示名稱和排序，以及退貨原因這類由營運維護的選項。Flyway 種子資料和 enum 要一致，用測試檢查。

**畫面權限**：用資料表控制按鈕和畫面能否顯示。

- 現況：`authorities`、`user_authorities` 已是權限代碼表，權限直接指派給使用者，`users.role_name` 只是文字，沒有角色資料表。
- 已決定（2026-11-08 使用者確認）：沿用 `authorities`，新增畫面和按鈕的權限代碼；新增角色資料表，改成「使用者 → 角色 → 權限」。前端依代碼顯示，後端同樣要檢查，和 ADR 0002 規則三一致。
- 落實方式：新增 `roles`、`role_authorities`、`user_roles`；`user_authorities` 和 `users.role_name` 不再需要，V1 不建。

## 9. 結果

**好處**

- 只有一份訂單與庫存流程，B2C 取消、出貨的問題用同一套程式解決。
- 從商機可以一路查到出貨與退貨。
- 付款、出貨各自前進，能表達月結、貨到付款和分批出貨。
- 資料一致性由外鍵和 DECIMAL 保證，不靠程式慣例。

**代價**

- 綠界、購物車、VIP 消費統計都要改接，V1 那一批改動很大（Q14）。
- 第一批的欄位、角色和代碼表都進 V1，V1 那一批的範圍比原本大。
- 前後台用到舊訂單 API、舊狀態值和商機評分的頁面要調整。
- `channel` 是重複資料，要靠 Service 維持一致（Q6）。
- Faker 要配合新結構調整，尤其 `SalesOrderFaker` 要依客戶類型帶入 `channel`。
