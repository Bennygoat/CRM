package com.example.demo.dto.ecpay;

import lombok.AllArgsConstructor;
import lombok.Data;

// 一個簡單的 DTO，用來同時存放綠界的 URL 和表單參數
@Data
@AllArgsConstructor
public class EcpayPaymentData {
    private String ecpayUrl;
    private AioCheckoutDto aioCheckoutDto;
}