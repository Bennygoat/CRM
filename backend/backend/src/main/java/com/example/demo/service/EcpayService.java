package com.example.demo.service;

import com.example.demo.config.EcpayProperties;
import com.example.demo.dto.ecpay.AioCheckoutDto;
import com.example.demo.dto.ecpay.EcpayRequestDto;
import com.example.demo.dto.ecpay.LogisticsAllInOneDto;
import com.example.demo.entity.Order;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.repository.OrderRepository;
import com.example.demo.util.EcpayAesUtil;
import com.example.demo.util.EcpayCheckMacValueUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

@Service
public class EcpayService {

    @Autowired
    private EcpayProperties ecpayProperties;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private OrderRepository orderRepository;

    /**
     * 金流 AIO 結帳
     */
    public AioCheckoutDto createAioOrder(Integer totalAmount, String itemName, String merchantTradeNo) {
        AioCheckoutDto dto = new AioCheckoutDto();
        EcpayProperties.Aio aioConfig = ecpayProperties.getAio();

        dto.setMerchantID(aioConfig.getMerchantId());
        dto.setMerchantTradeNo(merchantTradeNo);
        dto.setMerchantTradeDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        dto.setTotalAmount(totalAmount);
        dto.setTradeDesc("交易描述範例"); // 可自行修改
        dto.setItemName(itemName);
        dto.setReturnURL(aioConfig.getReturnUrl());

        // 將 DTO 轉換為 Map 並產生 CheckMacValue
        Map<String, String> params = dto.toMap();
        String checkMacValue = EcpayCheckMacValueUtil.generate(
                params,
                aioConfig.getHashKey(),
                aioConfig.getHashIv()
        );
        dto.setCheckMacValue(checkMacValue);

        return dto;
    }

    /**
     * 【新增】準備物流選擇頁面所需的參數
     */
    @Transactional(readOnly = true)
    public void prepareLogisticsRedirect(Long orderId, String logisticsType, String logisticsSubType, Model model) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("找不到訂單 ID: " + orderId));

        Map<String, String> params = this.createLogisticsRedirectParams(order, logisticsType, logisticsSubType);

        // 【優化】動態組合目標 URL
        String baseUrl = ecpayProperties.getLogistics().getUrl();
        String logisticsUrl;
        if ("CVS".equalsIgnoreCase(logisticsType)) {
            logisticsUrl = baseUrl + "/Express/map"; // 超商導向電子地圖
        } else {
            logisticsUrl = baseUrl + "/Express/Create"; // 宅配導向地址填寫頁
        }

        model.addAttribute("logisticsUrl", logisticsUrl);
        model.addAttribute("params", params);
    }

    public Map<String, String> createLogisticsRedirectParams(Order order, String logisticsType, String logisticsSubType) {
        EcpayProperties.Logistics config = ecpayProperties.getLogistics();
        Map<String, String> params = new TreeMap<>();

        params.put("MerchantID", config.getMerchantId());
        params.put("MerchantTradeNo", order.getMerchantTradeNo());
        params.put("MerchantTradeDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        params.put("LogisticsType", logisticsType);
        params.put("LogisticsSubType", logisticsSubType);
        params.put("GoodsAmount", String.valueOf(order.getTotalAmount().longValue()));
        params.put("GoodsName", "網站商品一批");

        if (order.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
            params.put("IsCollection", "Y");
        } else {
            params.put("IsCollection", "N");
        }

        params.put("SenderName", config.getSenderName());
        params.put("SenderCellPhone", config.getSenderCellphone());

        params.put("ServerReplyURL", config.getServerReplyUrl());
        params.put("ClientReplyURL", config.getClientReplyUrl());

        if ("HOME".equalsIgnoreCase(logisticsType)) {
            params.put("SenderZipCode", config.getSenderZipCode());
            params.put("SenderAddress", config.getSenderAddress());
            // 宅配模式，不需在此階段提供收件人資訊，由綠界頁面讓使用者填寫
        }

        String checkMacValue = EcpayCheckMacValueUtil.generate(params, config.getHashKey(), config.getHashIv());
        params.put("CheckMacValue", checkMacValue);

        return params;
    }

    /**
     * 【修改】呼叫 API 建立物流訂單 (整合超商與宅配)
     * @param order 訂單實體
     * @param replyData 從綠界回調的資料 Map
     * @return 綠界 API 的回應字串
     */
    public String createLogisticsOrder(Order order, Map<String, String> replyData) {
        String createApiUrl = ecpayProperties.getLogistics().getUrl() + "/Express/Create";
        EcpayProperties.Logistics config = ecpayProperties.getLogistics();
        Map<String, String> params = new TreeMap<>();

        // --- 基本資料 ---
        params.put("MerchantID", config.getMerchantId());
        params.put("MerchantTradeNo", replyData.get("MerchantTradeNo"));
        params.put("MerchantTradeDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        params.put("GoodsName", "網站商品一批");
        params.put("GoodsAmount", String.valueOf(order.getTotalAmount().longValue()));

        // ▼▼▼▼▼ 【請在此處補上這段 IsCollection 的判斷邏輯】 ▼▼▼▼▼
        // 根據付款方式決定是否代收貨款
        if (order.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
            params.put("IsCollection", "Y");
        } else {
            params.put("IsCollection", "N");
        }
        // ▲▲▲▲▲ 【請在此處補上這段 IsCollection 的判斷邏輯】 ▲▲▲▲▲

        // --- 寄件人資料 ---
        params.put("SenderName", config.getSenderName());
        params.put("SenderCellPhone", config.getSenderCellphone());

        // 【修正 2】改用更可靠的方式判斷物流類型，並填入對應參數
        // 如果 replyData 裡面有 CVSStoreID，就代表是超商物流
        if (replyData.containsKey("CVSStoreID")) {
            params.put("LogisticsType", "CVS"); // 手動設定物流類型為 CVS
            params.put("LogisticsSubType", replyData.get("LogisticsSubType"));

            // 從我們自己的 Order 中獲取收件人資料
            params.put("ReceiverName", order.getCCustomerAddress().getName());
            params.put("ReceiverCellPhone", order.getCCustomerAddress().getPhone());

            // 從綠界回傳的資料中獲取門市資訊
            params.put("ReceiverStoreID", replyData.get("CVSStoreID"));

        } else if (replyData.containsKey("ReceiverName")) { // 如果有 ReceiverName，代表是宅配
            params.put("LogisticsType", "HOME"); // 手動設定物流類型為 HOME
            params.put("LogisticsSubType", replyData.get("LogisticsSubType"));

            // 宅配的收件資訊是從綠界頁面回傳的
            params.put("ReceiverName", replyData.get("ReceiverName"));
            params.put("ReceiverCellPhone", replyData.get("ReceiverCellPhone"));
            params.put("ReceiverZipCode", replyData.get("ReceiverZipCode"));
            params.put("ReceiverAddress", replyData.get("ReceiverAddress"));
        } else {
            throw new IllegalArgumentException("無法判斷物流類型，回傳資料缺少 CVSStoreID 或 ReceiverName");
        }

        // ServerReplyURL 要用接收「狀態」的 callback URL
        String serverCallbackUrl = config.getServerReplyUrl().replace("/store-reply", "/callback");
        params.put("ServerReplyURL", serverCallbackUrl);

        // ▼▼▼▼▼ 【最終修正】在這裡加入 PlatformID 參數 ▼▼▼▼▼
        params.put("PlatformID", "");
        // ▲▲▲▲▲ 【最終修正】在這裡加入 PlatformID 參數 ▲▲▲▲▲

        // (除錯用的 System.out.println 您可以保留或刪除)
        System.out.println("=============================================");
        System.out.println("準備發送到綠界的物流訂單參數 (最終版)：");
        params.forEach((key, value) -> System.out.println(key + " = " + value));
        System.out.println("=============================================");

        // --- 計算 CheckMacValue ---
        String checkMacValue = EcpayCheckMacValueUtil.generate(params, config.getHashKey(), config.getHashIv());
        params.put("CheckMacValue", checkMacValue);

        // --- 發送 Server-to-Server 請求 ---
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.setAll(params);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(createApiUrl, request, String.class);
        return response.getBody();
    }
}