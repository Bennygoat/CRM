package com.example.demo.controller;

import com.example.demo.dto.ecpay.AioCheckoutDto;
import com.example.demo.dto.ecpay.EcpayPaymentData;
import com.example.demo.entity.Order;
import com.example.demo.enums.OrderStatus;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.EcpayService;
import com.example.demo.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

//@Controller
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private EcpayService ecpayService;
    @Autowired
    private com.example.demo.config.EcpayProperties ecpayProperties;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/pay") // test用
    public String pay(Model model) {
        String merchantTradeNo = "test" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 14);
        AioCheckoutDto dto = ecpayService.createAioOrder(100, "範例商品一批", merchantTradeNo);

        model.addAttribute("ecpayUrl", ecpayProperties.getAio().getUrl());
        model.addAttribute("aioCheckoutDto", dto);

        return "ecpay-checkout";
    }

//    /**
//     * 【建議新增】根據真實訂單 ID 發起金流支付
//     * @param orderId 訂單 ID
//     * @param model Spring Model
//     * @return 導向到 ecpay-checkout 模板
//     */
//    @GetMapping("/order/{orderId}")
//    @Transactional // 確保能正確 Lazy-load 訂單明細
//    public String getOrderPaymentPage(@PathVariable("orderId") Integer orderId, Model model) {
//        // 1. 查找訂單
//        Order order = orderRepository.findById((long)orderId)
//                .orElseThrow(() -> new EntityNotFoundException("找不到訂單 ID: " + orderId));
//
//        // 2. 檢查訂單狀態 (這部分的邏輯我們暫不處理，但保留原樣)
//        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
//            throw new IllegalStateException("此訂單狀態不是待付款，無法進行支付");
//        }
//
//        // 3. 【核心修改】從訂單明細動態組合商品名稱字串
//        String itemName = order.getOrderDetails().stream()
//                .map(detail -> detail.getProduct().getName() + " x " + detail.getQuantity())
//                .collect(Collectors.joining("#"));
//
//        // 4. 使用組合好的商品名稱字串來建立 AIO 物件
//        AioCheckoutDto dto = ecpayService.createAioOrder(
//                order.getTotalAmount().intValue(),
//                itemName, // 使用上面組合好的真實商品名稱
//                order.getMerchantTradeNo()
//        );
//
//        model.addAttribute("ecpayUrl", ecpayProperties.getAio().getUrl());
//        model.addAttribute("aioCheckoutDto", dto);
//
//        return "ecpay-checkout";
//    }

    /**
     * 【全新修改】根據訂單 ID 獲取發起支付所需的參數
     * @param orderId 訂單 ID
     * @return 包含綠界 URL 和表單參數的 JSON 物件
     */
    @GetMapping("/order/{orderId}") // 您也可以改成 @PostMapping，語意上更貼切
    @Transactional
    // ✨ 2. 移除 Model，回傳型別改為 ResponseEntity<EcpayPaymentData>
    public ResponseEntity<EcpayPaymentData> getPaymentParameters(@PathVariable("orderId") Integer orderId) {

        // --- 中間的邏輯幾乎不變 ---
        Order order = orderRepository.findById((long)orderId)
                .orElseThrow(() -> new EntityNotFoundException("找不到訂單 ID: " + orderId));

        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("此訂單狀態不是待付款，無法進行支付");
        }

        String itemName = order.getOrderDetails().stream()
                .map(detail -> detail.getProduct().getName() + " x " + detail.getQuantity())
                .collect(Collectors.joining("#"));

        AioCheckoutDto dto = ecpayService.createAioOrder(
                order.getTotalAmount().intValue(),
                itemName,
                order.getMerchantTradeNo()
        );

        // ✨ 3. 將需要的資料包裝起來
        EcpayPaymentData paymentData = new EcpayPaymentData(
                ecpayProperties.getAio().getUrl(), // 綠界表單要提交的 action URL
                dto // 包含所有表單參數的 DTO
        );

        // ✨ 4. 直接回傳 JSON 資料和 HTTP 200 OK 狀態
        return ResponseEntity.ok(paymentData);
    }

    /**
     * 【修改】接收綠界金流 AIO 付款結果通知
     * @param callbackData 綠界發送的 POST 表單資料
     * @return 字串 "1|OK" 或 "0|ErrorMessage"
     */
    @PostMapping("/ecpay/callback")
    @ResponseBody
    public String handleCallback(@RequestParam Map<String, String> callbackData) {
        System.out.println("ECPay 金流回調收到: " + callbackData);

        try {
            // 將處理邏輯交給 Service 層
            orderService.processPaymentCallback(callbackData);
            // 成功處理後，必須回傳 "1|OK"
            return "1|OK";
        } catch (Exception e) {
            System.err.println("處理金流回調時發生錯誤: " + e.getMessage());
            // 雖然發生錯誤，但為避免綠界不斷重試，通常仍建議回傳 "1|OK"
            // 並在系統內部做好錯誤日誌記錄，以便追查問題
            return "1|OK";
        }
    }
}