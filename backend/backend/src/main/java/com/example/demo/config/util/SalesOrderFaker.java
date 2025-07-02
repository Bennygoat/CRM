package com.example.demo.config.util;


import com.example.demo.entity.*;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.SalesOrderStatus;
import com.example.demo.repository.CustomerBaseRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SalesOrderRepository;
import com.example.demo.repository.WarehouseRepository;
import com.example.demo.service.erp.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
@Profile("dev-seeder") // 使用特定 Profile，避免與 'dev' 衝突
@Slf4j
@RequiredArgsConstructor
public class SalesOrderFaker {

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerBaseRepository customerBaseRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryService inventoryService;

    // 定義假資料的常數
    private static final Long SYSTEM_USER_ID = 1L;
    private static final String[] PAYMENT_METHODS = {"CASH", "CREDIT_CARD", "LINE_PAY", "CASH_ON_DELIVERY"};
    private static final PaymentStatus[] PAYMENT_STATUSES = {PaymentStatus.PAID, PaymentStatus.UNPAID};
    private static final String[] SHIPPING_METHODS = {"DHL", "HCT", "t-cat"};
    private static final SalesOrderStatus[] ORDER_STATUSES = {SalesOrderStatus.CONFIRMED, SalesOrderStatus.SHIPPED, SalesOrderStatus.COMPLETED};

    // 地址相關的隨機選項
    private static final String[] CITIES = {"台北市", "台中市", "台南市", "高雄市", "桃園市"};
    private static final String[] DISTRICTS = {"南屯區", "西區", "南區", "大雅區", "前鎮區", "平鎮區", "楊梅區", "永和區"};
    private static final String[] STREETS = {"忠孝路", "中正路", "大港路", "資展路", "大連路", "大進路", "公益路"};
    private static final String[] SECTIONS = {"一段", "二段", "三段"};

    @Bean
    @Transactional
    public CommandLineRunner seedSalesOrders() {
        return args -> {
            if (salesOrderRepository.count() > 0) {
                log.warn("資料庫中已有銷售訂單，跳過 Faker 產生程序。");
                return;
            }
            log.info("偵測到無銷售訂單資料，開始執行 SalesOrder Faker...");

            // 1. 預先載入所需資料，避免在迴圈中重複查詢
            List<CustomerBase> customers = customerBaseRepository.findAll();
            List<Product> products = productRepository.findAll();
            List<Warehouse> warehouses = warehouseRepository.findAll();

            if (customers.isEmpty() || products.isEmpty() || warehouses.isEmpty()) {
                log.error("無法產生訂單假資料，因為客戶、商品或倉庫資料不存在！");
                return;
            }

            LocalDate startDate = LocalDate.of(2023, 1, 1);
            LocalDate endDate = LocalDate.now();
            log.info("將產生從 {} 到 {} 的訂單資料。", startDate, endDate);

            // 使用 AtomicLong 確保在多執行緒環境下也能安全地產生唯一訂單號後綴
            AtomicLong orderCounter = new AtomicLong(salesOrderRepository.count());
            List<SalesOrder> allGeneratedOrders = new ArrayList<>();

            // 2. 遍歷每一天
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                int ordersPerDay = ThreadLocalRandom.current().nextInt(15, 21); // 每天 15~20 筆

                for (int i = 0; i < ordersPerDay; i++) {
                    SalesOrder order = createSingleFakeOrder(date, customers, products, warehouses, orderCounter);
                    allGeneratedOrders.add(order);
                }
            }

            log.info("共產生 {} 筆銷售訂單，正在存入資料庫...", allGeneratedOrders.size());
            salesOrderRepository.saveAll(allGeneratedOrders);
            log.info("訂單資料儲存完畢。開始為已確認的訂單預留庫存...");

            // 3. 為已確認(CONFIRMED)的訂單預留庫存
            for (SalesOrder savedOrder : allGeneratedOrders) {
                if (savedOrder.getOrderStatus() == SalesOrderStatus.CONFIRMED) {
                    for (SalesOrderDetail detail : savedOrder.getDetails()) {
                        try {
                            inventoryService.reserveStock(
                                    detail.getProduct().getProductId(),
                                    savedOrder.getWarehouse().getWarehouseId(),
                                    detail.getQuantity(),
                                    "SALES_ORDER",
                                    savedOrder.getSalesOrderId(),
                                    detail.getItemId(),
                                    SYSTEM_USER_ID
                            );
                        } catch (Exception e) {
                            log.error("為訂單 {} 預留庫存失敗: {}", savedOrder.getOrderNumber(), e.getMessage());
                        }
                    }
                }
            }
            log.info("銷售訂單假資料產生程序全部完成！");
        };
    }

    private SalesOrder createSingleFakeOrder(LocalDate date, List<CustomerBase> customers, List<Product> products, List<Warehouse> warehouses, AtomicLong orderCounter) {
        SalesOrder order = new SalesOrder();
        LocalDateTime now = LocalDateTime.now();

        // --- 設定訂單主檔 ---
        order.setCustomer(getRandomElement(customers));
        order.setWarehouse(getRandomElement(warehouses));
        order.setOrderDate(date);
        order.setShippingAddress(generateRandomAddress());
        order.setPaymentMethod(getRandomElement(PAYMENT_METHODS));
        order.setShippingMethod(getRandomElement(SHIPPING_METHODS));
        order.setOrderStatus(getRandomElement(ORDER_STATUSES));
        order.setPaymentStatus(getRandomElement(PAYMENT_STATUSES));
        order.setRemarks("由 Faker 自動產生的訂單");
        order.setCreatedBy(SYSTEM_USER_ID);
        order.setUpdatedBy(SYSTEM_USER_ID);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        // --- 產生訂單明細 (1~5筆) ---
        BigDecimal totalNetAmount = BigDecimal.ZERO;
        int detailCount = ThreadLocalRandom.current().nextInt(1, 6);
        for (int i = 0; i < detailCount; i++) {
            SalesOrderDetail detail = new SalesOrderDetail();
            Product product = getRandomElement(products);

            detail.setProduct(product);
            detail.setQuantity(BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(1, 4))); // 數量 1~3
            detail.setUnitPrice(product.getBasePrice()); // 直接使用產品的基準價格
            detail.setItemSequence(i + 1);
            detail.setDiscountRate(BigDecimal.ZERO);

            if (product.getUnit() != null) {
                detail.setUnitId(product.getUnit().getUnitId());
            }

            // 計算金額
            BigDecimal itemAmount = detail.getUnitPrice().multiply(detail.getQuantity());
            detail.setItemAmount(itemAmount);

            BigDecimal taxRateDivisor = new BigDecimal("1.05");
            BigDecimal itemNetAmount = itemAmount.divide(taxRateDivisor, 2, RoundingMode.HALF_UP);
            detail.setItemNetAmount(itemNetAmount);
            detail.setItemTaxAmount(itemAmount.subtract(itemNetAmount));

            // 設定稽核欄位
            detail.setCreatedBy(SYSTEM_USER_ID);
            detail.setUpdatedBy(SYSTEM_USER_ID);
            detail.setCreatedAt(now);
            detail.setUpdatedAt(now);

            order.addDetail(detail);
            totalNetAmount = totalNetAmount.add(itemNetAmount);
        }

        // --- 回填訂單主檔的總金額 ---
        order.setTotalNetAmount(totalNetAmount);
        BigDecimal totalTaxAmount = totalNetAmount.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        order.setTotalTaxAmount(totalTaxAmount);
        order.setTotalAmount(order.getTotalNetAmount().add(order.getTotalTaxAmount()));

        // --- 產生唯一的訂單編號 ---
        String orderNumber = "SO-" + date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + String.format("%05d", orderCounter.incrementAndGet());
        order.setOrderNumber(orderNumber);

        return order;
    }

    // 輔助方法：從列表中隨機取得一個元素
    private <T> T getRandomElement(T[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }

    private <T> T getRandomElement(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    // 輔助方法：產生隨機地址
    private String generateRandomAddress() {
        return getRandomElement(CITIES) +
                getRandomElement(DISTRICTS) +
                getRandomElement(STREETS) +
                getRandomElement(SECTIONS) +
                ThreadLocalRandom.current().nextInt(1, 446) + "號";
    }
}