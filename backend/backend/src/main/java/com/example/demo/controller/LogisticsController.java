package com.example.demo.controller;

import com.example.demo.config.EcpayProperties;
import com.example.demo.entity.Order;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.EcpayService;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/logistics")
public class LogisticsController {

    @Autowired
    private EcpayService ecpayService;
    @Autowired
    private OrderService orderService;
    // 移除 OrderRepository 的注入，這個職責應該在 Service 層

    @GetMapping("/select/{orderId}")
    public String selectLogistics(@PathVariable Long orderId,
                                  @RequestParam("type") String type,
                                  @RequestParam("subType") String subType, // 注意：宅配也需要 subType, e.g., TCAT, ECAN
                                  Model model) {
        ecpayService.prepareLogisticsRedirect(orderId, type, subType, model);
        return "ecpay-logistics-redirect";
    }

    @PostMapping("/store-reply")
    @ResponseBody
    public String handleStoreSelectionReply(@RequestParam Map<String, String> replyData) {
        System.out.println("收到物流選擇結果: " + replyData);
        try {
            orderService.processLogisticsSelection(replyData);
            return "1|OK";
        } catch (Exception e) {
            System.err.println("處理物流選擇結果失敗: " + e.getMessage());
            e.printStackTrace();
            return "0|Error: " + e.getMessage();
        }
    }

    /**
     * 【新增】接收綠界物流狀態更新的回調
     * @param callbackData 綠界以 POST form-data 形式發送的資料
     * @return 回應給綠界的字串 "1|OK"
     */
    @PostMapping("/callback")
    @ResponseBody
    public String handleLogisticsCallback(@RequestParam Map<String, String> callbackData) {
        System.out.println("收到綠界物流回調: " + callbackData);

        try {
            orderService.processLogisticsCallback(callbackData);
            return "1|OK";
        } catch (Exception e) {
            System.err.println("處理綠界物流回調失敗: " + e.getMessage());
            return "1|OK";
        }
    }


    // 在 LogisticsController.java 的 class 內部新增此方法
    @GetMapping("/test-page")
    public String showTestPage() {
        return "test-logistics.html";
    }
}