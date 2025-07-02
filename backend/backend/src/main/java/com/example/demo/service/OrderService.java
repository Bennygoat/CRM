package com.example.demo.service;

import com.example.demo.config.EcpayProperties;
import com.example.demo.dto.request.CreateOrderRequestDto;
import com.example.demo.dto.response.OrderDetailDto;
import com.example.demo.dto.response.OrderDto;
import com.example.demo.entity.*;
import com.example.demo.enums.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import com.example.demo.service.erp.InventoryService;
import com.example.demo.util.EcpayCheckMacValueUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.enums.PaymentMethod.CASH_ON_DELIVERY;
import static com.example.demo.enums.PaymentMethod.ONLINE_PAYMENT;

@Service
@RequiredArgsConstructor // 使用 Lombok 的 @RequiredArgsConstructor 簡化依賴注入
public class OrderService {

    // 使用 final 關鍵字，Lombok 會自動生成包含這些欄位的建構子
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CCustomerRepo cCustomerRepo;
    private final CCustomerAddressRepository cCustomerAddressRepository;
    private final InventoryService inventoryService;
    private final PlatformRepository platformRepository;
    private final EntityManager entityManager;
    private final CCustomerService cCustomerService;
    private final UserRepository userRepository; // ✨ 1. 確保 UserRepository 已注入
    private final EcpayProperties ecpayProperties;
    private final EcpayService ecpayService;
    private final CustomerCouponRepository customerCouponRepository;

    // 定義一個常數來代表「系統使用者」的ID
    private static final Long SYSTEM_USER_ID = 1L;

    /**
     * 從購物車建立訂單
     */
    @Transactional
    public OrderDto createOrderFromCart(Long customerId, CreateOrderRequestDto requestDto) {
        System.out.println("====== 開始建立訂單流程 ======");
        System.out.println("顧客 ID: " + customerId);
        System.out.println("收到的請求 DTO: " + requestDto);

        CCustomer customer = cCustomerRepo.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到顧客，ID: " + customerId));

        Cart cart = cartRepository.findByCCustomer(customer)
                .orElseThrow(() -> new ResourceNotFoundException("找不到顧客 " + customer.getCustomerName() + " 的購物車"));

        if (cart.getCartdetails() == null || cart.getCartdetails().isEmpty()) {
            throw new IllegalStateException("購物車是空的，無法建立訂單。");
        }

        BigDecimal originalTotal = cart.getCartdetails().stream()
                .map(cd -> cd.getProduct().getBasePrice()
                        .multiply(new BigDecimal(cd.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("計算出的訂單原始總金額: " + originalTotal);

        BigDecimal finalTotal = originalTotal;
        CustomerCoupon appliedCoupon = null;

        // --- 關鍵的優惠券處理區塊 ---
        System.out.println("準備檢查是否使用優惠券...");
        if (requestDto.getCustomerCouponId() != null) {
            System.out.println("請求中包含 Coupon ID: " + requestDto.getCustomerCouponId() + "，進入折扣處理程序。");

            Long couponId = requestDto.getCustomerCouponId();
            appliedCoupon = customerCouponRepository.findById(couponId)
                    .orElseThrow(() -> new ResourceNotFoundException("找不到優惠券 ID: " + couponId));
            System.out.println("成功從資料庫找到優惠券!");

            // 驗證 1: 優惠券所有權
            System.out.println("驗證所有權 -> 優惠券持有者 ID: " + appliedCoupon.getCustomer().getCustomerId() + " | 目前顧客 ID: " + customerId);
            if (!appliedCoupon.getCustomer().getCustomerId().equals(customerId)) {
                System.out.println("!!! 驗證失敗: 優惠券不屬於此使用者。");
                throw new IllegalStateException("該優惠券不屬於這位使用者。");
            }

            // 驗證 2: 優惠券狀態
            System.out.println("驗證狀態 -> 優惠券狀態為: " + appliedCoupon.getStatus());
            if (appliedCoupon.getStatus() != CustomerCouponStatus.UNUSED) {
                System.out.println("!!! 驗證失敗: 優惠券已被使用或失效。");
                throw new IllegalStateException("該優惠券已被使用或已失效。");
            }

            CouponTemplate template = appliedCoupon.getCouponTemplate();
            System.out.println("取得優惠券模板: " + template.getName());

            // 驗證 3: 訂單金額是否滿足低消
            System.out.println("驗證低消 -> 訂單金額: " + originalTotal + " | 優惠券低消: " + template.getMinPurchaseAmount());
            if (originalTotal.compareTo(template.getMinPurchaseAmount()) < 0) {
                System.out.println("!!! 驗證失敗: 未達低消門檻。");
                throw new IllegalStateException("訂單金額 " + originalTotal + " 未達優惠券低消 $" + template.getMinPurchaseAmount());
            }

            System.out.println("所有驗證通過，準備計算折扣後金額...");
            finalTotal = calculateDiscountedPrice(originalTotal, template);
            System.out.println("計算完成，折扣後金額為: " + finalTotal);

        } else {
            System.out.println("請求中未包含 Coupon ID，跳過折扣處理。");
        }

        // ✨ --- 新的地址處理邏輯 --- ✨
// 1. 從 DTO 取得使用者輸入的地址字串
        String addressString = requestDto.getAddress();
        if (addressString == null || addressString.trim().isEmpty()) {
            throw new IllegalArgumentException("收貨地址不可為空。");
        }

// 2. 建立一個新的 CCustomerAddress 物件
        CCustomerAddress newAddress = new CCustomerAddress();
        newAddress.setCCustomer(customer); // 將這個新地址與當前顧客關聯
        newAddress.setAddress(addressString);
        newAddress.setIsdefault(false); // 新建立的地址預設為非預設地址
        newAddress.setCreateat(LocalDateTime.now());
        newAddress.setUpdateat(LocalDateTime.now());

// 3. 將這個新地址儲存到資料庫
        CCustomerAddress savedAddress = cCustomerAddressRepository.save(newAddress);

// --- ✨ 新邏輯結束，舊邏輯已被取代 --- ✨

        User systemUser = userRepository.findById(SYSTEM_USER_ID)
                .orElseThrow(() -> new IllegalStateException("資料庫中找不到 ID 為 " + SYSTEM_USER_ID + " 的系統使用者帳號，請先建立"));

        Platform defaultPlatform = platformRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("找不到預設的平台 (ID: 1)"));

        Order newOrder = new Order();
        newOrder.setPlatform(defaultPlatform);
        newOrder.setCCustomer(customer);
// ✨ 將訂單的地址設定為剛剛儲存的新地址 ✨
        newOrder.setCCustomerAddress(savedAddress);
        newOrder.setOrderdate(LocalDate.now());

        PaymentMethod paymentMethod = PaymentMethod.valueOf(requestDto.getPaymentMethod());
        newOrder.setPaymentMethod(paymentMethod);
        newOrder.setCreateat(LocalDateTime.now());
        newOrder.setUpdateat(LocalDateTime.now());

        switch (paymentMethod) {
            case ONLINE_PAYMENT:
                newOrder.setOrderStatus(OrderStatus.PENDING_PAYMENT);
                newOrder.setPaymentStatus(PaymentStatus.UNPAID);
                break;
            case CASH_ON_DELIVERY:
                newOrder.setOrderStatus(OrderStatus.PENDING_SHIPMENT);
                newOrder.setPaymentStatus(PaymentStatus.UNPAID);
                break;
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomPart = UUID.randomUUID().toString().substring(0, 6);
        newOrder.setMerchantTradeNo("T" + timestamp + randomPart);

        // --- ✨ 4. 這邊不再需要計算 totalAmount，因為前面已經算好了 ---
        // double totalAmount = 0; // 這行可以移除
        // ✨【關鍵修正】✨: 不要建立新的 List，而是直接操作 newOrder 內部的 orderDetails
        for (CartDetail cartDetail : cart.getCartdetails()) {
            OrderDetail orderDetail = new OrderDetail();

            orderDetail.setOrder(newOrder); // 設定關聯
            orderDetail.setProduct(cartDetail.getProduct()); // 設定關聯

            orderDetail.setQuantity(cartDetail.getQuantity());
            orderDetail.setUnitprice(cartDetail.getProduct().getBasePrice().doubleValue());
            orderDetail.setCreateat(LocalDateTime.now());
            orderDetail.setUpdateat(LocalDateTime.now());

            // 直接將新的 detail 加入到 newOrder 持有的列表中
            newOrder.getOrderDetails().add(orderDetail);
//            totalAmount += orderDetail.getQuantity() * orderDetail.getUnitprice();
        }

        // ✨ 5. 設定訂單的總金額為折扣後的 finalTotal
        newOrder.setTotalAmount(finalTotal.doubleValue());
        System.out.println("最終寫入訂單的金額為: " + newOrder.getTotalAmount());

        // ✨ 6. 先儲存 Order，取得 orderId
        Order savedOrder = orderRepository.save(newOrder);


        // ✨ 7. 如果成功使用了優惠券，回頭更新 CustomerCoupon 的狀態與關聯
        if (appliedCoupon != null) {
            appliedCoupon.setStatus(CustomerCouponStatus.USED);
            appliedCoupon.setOrder(savedOrder); // 將儲存後的訂單關聯回優惠券
            customerCouponRepository.save(appliedCoupon); // 儲存優惠券的變更
            System.out.println("優惠券 " + appliedCoupon.getId() + " 狀態已更新為 USED 並與訂單 " + savedOrder.getOrderid() + " 關聯。");
        }

        // 預留庫存
        for (OrderDetail detail : savedOrder.getOrderDetails()) {
            inventoryService.reserveStock(
                    detail.getProduct().getProductId(),
                    1L,
                    BigDecimal.valueOf(detail.getQuantity()),
                    "SALES_ORDER",
                    savedOrder.getOrderid(),
                    detail.getProduct().getProductId(),
                    systemUser.getUserId()
            );
        }

        // 清空購物車
        cartRepository.delete(cart);
        System.out.println("====== 訂單建立流程結束 ======");

        return mapToOrderDto(savedOrder);
    }

    public List<OrderDto> getOrdersBycustomerId(Long customerId) {
        return orderRepository.findByCCustomer_CustomerIdOrderByOrderdateDesc(customerId).stream()
                .map(this::mapToOrderDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return orderPage.map(this::mapToOrderDto);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("找不到訂單 ID: " + orderId));

        OrderStatus oldStatus = order.getOrderStatus();
        order.setOrderStatus(newStatus);

        Order updatedOrder = orderRepository.save(order);

        if (newStatus == OrderStatus.COMPLETE && oldStatus != OrderStatus.COMPLETE) {
            if (order.getCCustomer() != null && order.getCCustomer().getCustomerId() != null) {
                Long customerId = order.getCCustomer().getCustomerId();
                cCustomerService.updateCustomerSpending(customerId);
            }
        }
        return mapToOrderDto(updatedOrder);
    }

    public Page<OrderDto> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByOrderStatus(status, pageable)
                .map(this::mapToOrderDto);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> searchOrders(LocalDate startTime, LocalDate endTime, String productName) {
        List<Order> orders = orderRepository.searchOrders(startTime, endTime, productName);
        return orders.stream()
                .map(this::mapToOrderDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::mapToOrderDto)
                .orElseThrow(() -> new EntityNotFoundException("找不到訂單 ID: " + orderId));
    }

    private OrderDto mapToOrderDto(Order order) {
        List<OrderDetailDto> detailDtos = new ArrayList<>();
        if (order.getOrderDetails() != null) {
            for (OrderDetail detail : order.getOrderDetails()) {
                if(detail != null && detail.getProduct() != null) {
                    detailDtos.add(new OrderDetailDto(
                            detail.getProduct().getProductId(),
                            detail.getProduct().getName(),
                            detail.getQuantity(),
                            detail.getUnitprice()));
                }
            }
        }

        return new OrderDto(
                order.getOrderid(),
                order.getCCustomer().getCustomerId(),
                order.getCCustomer().getCustomerName(),
                order.getOrderdate(),
                order.getOrderStatus(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getTotalAmount(),
                detailDtos);
    }

    @Transactional
    public void processStoreSelection(Map<String, String> replyData) {
        String merchantTradeNo = replyData.get("MerchantTradeNo");
        Order order = orderRepository.findByMerchantTradeNo(merchantTradeNo)
                .orElseThrow(() -> new EntityNotFoundException("找不到訂單: " + merchantTradeNo));

        String result = ecpayService.createLogisticsOrder(order, replyData);
        System.out.println("建立物流訂單結果: " + result);

        if (result != null && result.startsWith("1|")) {
            order.setOrderStatus(OrderStatus.PENDING_SHIPMENT);
            orderRepository.save(order);
        } else {
            throw new RuntimeException("建立物流訂單失敗: " + result);
        }
    }

    @Transactional
    public void processLogisticsCallback(Map<String, String> callbackData) {
        String receivedMacValue = callbackData.get("CheckMacValue");
        Map<String, String> dataToVerify = new java.util.TreeMap<>(callbackData);
        dataToVerify.remove("CheckMacValue");

        String expectedMacValue = EcpayCheckMacValueUtil.generate(
                dataToVerify,
                ecpayProperties.getLogistics().getHashKey(),
                ecpayProperties.getLogistics().getHashIv()
        );

        if (receivedMacValue == null || !receivedMacValue.equals(expectedMacValue)) {
            throw new SecurityException("物流回調 CheckMacValue 驗證失敗！");
        }

        String merchantTradeNo = callbackData.get("MerchantTradeNo");
        String rtnCode = callbackData.get("RtnCode");

        Order order = orderRepository.findByMerchantTradeNo(merchantTradeNo)
                .orElseThrow(() -> new EntityNotFoundException("找不到對應的訂單編號: " + merchantTradeNo));

        updateOrderStatusFromLogistics(order, rtnCode);
    }

    private void updateOrderStatusFromLogistics(Order order, String logisticsStatusCode) {
        switch (logisticsStatusCode) {
            case "2063":
                order.setOrderStatus(OrderStatus.PENDING_PICKUP);
                break;
            case "2067":
                if (order.getPaymentMethod() == ONLINE_PAYMENT) {
                    order.setOrderStatus(OrderStatus.COMPLETE);
                    triggerUpdateSpending(order);
                }
                break;
            case "2073":
                if (order.getPaymentMethod() == CASH_ON_DELIVERY) {
                    order.setOrderStatus(OrderStatus.COMPLETE);
                    order.setPaymentStatus(PaymentStatus.PAID);
                    triggerUpdateSpending(order);
                }
                break;
        }
        orderRepository.save(order);
    }

    private void triggerUpdateSpending(Order order) {
        if (order.getCCustomer() != null && order.getCCustomer().getCustomerId() != null) {
            cCustomerService.updateCustomerSpending(order.getCCustomer().getCustomerId());
        }
    }

    @Transactional
    public void processPaymentCallback(Map<String, String> callbackData) {
        String receivedMacValue = callbackData.get("CheckMacValue");
        if (receivedMacValue == null) {
            throw new IllegalArgumentException("缺少 CheckMacValue，請求無效");
        }
        Map<String, String> dataToVerify = new java.util.TreeMap<>(callbackData);
        dataToVerify.remove("CheckMacValue");

        String expectedMacValue = EcpayCheckMacValueUtil.generate(
                dataToVerify,
                ecpayProperties.getAio().getHashKey(),
                ecpayProperties.getAio().getHashIv()
        );

        if (!receivedMacValue.equals(expectedMacValue)) {
            throw new SecurityException("金流回調 CheckMacValue 驗證失敗！");
        }

        String merchantTradeNo = callbackData.get("MerchantTradeNo");
        String rtnCode = callbackData.get("RtnCode");

        if ("1".equals(rtnCode)) {
            Order order = orderRepository.findByMerchantTradeNo(merchantTradeNo)
                    .orElseThrow(() -> new EntityNotFoundException("找不到對應的訂單編號: " + merchantTradeNo));

            if (order.getOrderStatus() == OrderStatus.PENDING_PAYMENT) {
                order.setPaymentStatus(PaymentStatus.PAID);
                order.setOrderStatus(OrderStatus.PENDING_SHIPMENT);
                order.setUpdateat(LocalDateTime.now());
                orderRepository.save(order);
                System.out.println("訂單 " + merchantTradeNo + " 付款成功，狀態已更新為待出貨。");
            } else {
                System.out.println("訂單 " + merchantTradeNo + " 的狀態不是 PENDING_PAYMENT，可能已被處理。");
            }
        } else {
            System.out.println("訂單 " + merchantTradeNo + " 交易失敗，RtnCode: " + rtnCode);
        }
    }


    // ▼▼▼▼▼ 請將以下三個方法完整複製到您的 OrderService class 中 ▼▼▼▼▼

    /**
     * 【全新增加】處理從綠界物流選擇頁面回來後的回調 (Server-to-Server)
     * 這個方法會在 LogisticsController 被呼叫。
     * @param replyData 綠界回傳的 Map 資料
     */
    @Transactional
    public void processLogisticsSelection(Map<String, String> replyData) {
        String merchantTradeNo = replyData.get("MerchantTradeNo");
        if (merchantTradeNo == null || merchantTradeNo.isEmpty()) {
            throw new IllegalArgumentException("從綠界回傳的資料中缺少 MerchantTradeNo");
        }

        Order order = orderRepository.findByMerchantTradeNo(merchantTradeNo)
                .orElseThrow(() -> new EntityNotFoundException("找不到對應的訂單編號: " + merchantTradeNo));

        // 呼叫 EcpayService 的方法，去跟綠界建立一筆真正的物流訂單
        String ecpayResponse = ecpayService.createLogisticsOrder(order, replyData);
        System.out.println("建立綠界物流訂單API的回應: " + ecpayResponse);

        // 解析綠界的回應，並儲存物流單號
        // 範例: 1|OK...
        // 範例: 1|OK\nMerchantID=2000132&...&AllPayLogisticsID=123456&...
        String[] responseParts = ecpayResponse.split("\\R", 2);
        if (responseParts.length > 0 && responseParts[0].startsWith("1|")) {
            // 成功
            System.out.println("物流訂單建立成功。");
            // 如果您需要在 Order Entity 中儲存物流單號，可以在這裡處理
            // Map<String, String> responseMap = parseEcpayResponse(responseParts[1]);
            // String logisticsId = responseMap.get("AllPayLogisticsID");
            // order.setLogisticsId(logisticsId);
            // order.setLogisticsType(replyData.get("LogisticsType"));
            // order.setOrderStatus(OrderStatus.PENDING_SHIPMENT); // 更新訂單狀態為待出貨
            // orderRepository.save(order);
        } else {
            // 失敗
            System.err.println("建立綠界物流訂單失敗，綠界回應: " + ecpayResponse);
            throw new RuntimeException("建立綠界物流訂單失敗。");
        }
    }

    /**
     * 【全新增加】處理綠界發送的物流狀態更新回調
     * 當貨物狀態改變時 (如: 已寄出、已到店、已取貨)，綠界會呼叫這個。
     * @param callbackData
     */
    @Transactional
    public void processLogisticsStatusCallback(Map<String, String> callbackData) {
        System.out.println("收到物流狀態更新: " + callbackData);
        String merchantTradeNo = callbackData.get("MerchantTradeNo");
        Order order = orderRepository.findByMerchantTradeNo(merchantTradeNo)
                .orElseThrow(() -> new EntityNotFoundException("找不到對應的訂單編號: " + merchantTradeNo));

        // String status = callbackData.get("RtnCode"); // 物流狀態碼
        // 您可以在這裡根據不同的狀態碼更新您的訂單物流狀態
        // order.setLogisticsStatus(status);
        // orderRepository.save(order);
    }

    /**
     * 【全新增加】輔助方法，用於解析綠界回傳的 Key-Value 字串
     */
    private Map<String, String> parseEcpayResponse(String responseBody) {
        Map<String, String> map = new TreeMap<>();
        if (responseBody == null || responseBody.isEmpty()) return map;
        String[] pairs = responseBody.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                map.put(pair.substring(0, idx), pair.substring(idx + 1));
            }
        }
        return map;
    }

    // ▲▲▲▲▲ 以上是您需要加入的三個方法 ▲▲▲▲▲

    /**
     * 【全新增加】根據優惠券模板計算折扣後的價格
     * @param originalPrice 原始價格
     * @param template 優惠券模板
     * @return 折扣後的價格
     */
    private BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, CouponTemplate template) {
        if (template.getCouponType() == CouponType.PERCENTAGE) {
            // 百分比折扣，例如 0.9 代表九折。使用 setScale確保結果為整數。
            return originalPrice.multiply(template.getDiscountValue()).setScale(0, RoundingMode.HALF_UP);
        } else if (template.getCouponType() == CouponType.FIXED_AMOUNT) {
            // 固定金額折抵
            BigDecimal discountedPrice = originalPrice.subtract(template.getDiscountValue());
            // 確保價格不會低於 0
            return discountedPrice.compareTo(BigDecimal.ZERO) > 0 ? discountedPrice : BigDecimal.ZERO;
        }
        // 如果有其他未知的優惠券類型，則不打折
        return originalPrice;
    }
}
