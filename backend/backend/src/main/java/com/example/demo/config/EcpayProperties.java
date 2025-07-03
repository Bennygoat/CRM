package com.example.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ecpay")
@Getter
@Setter
public class EcpayProperties {

    private Aio aio = new Aio();
    private Logistics logistics = new Logistics();
    private Invoice invoice = new Invoice();

    // Getters and Setters...
    public Aio getAio() { return aio; }
    public void setAio(Aio aio) { this.aio = aio; }
    public Logistics getLogistics() { return logistics; }
    public void setLogistics(Logistics logistics) { this.logistics = logistics; }
    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }

    // --- AIO 金流設定 ---
    @Getter
    @Setter
    public static class Aio {
        private String url;
        private String merchantId;
        private String hashKey;
        private String hashIv;
        private String returnUrl;
//        private String orderResultUrl;
        // Getters and Setters for Aio...
    }

    // --- 全方位物流設定 ---
    @Getter
    @Setter
    public static class Logistics {
        private String url;
        private String merchantId;
        private String hashKey;
        private String hashIv;
        private String senderName;
        private String senderPhone;
        private String senderCellphone;
        private String senderZipCode;
        private String senderAddress;
        private String serverReplyUrl; // <<-- 【新增】
        private String clientReplyUrl; // <<-- 【新增】
        // Getters and Setters for Logistics...
    }

    // --- 電子發票設定 ---
    @Getter
    @Setter
    public static class Invoice {
        private String url;
        private String merchantId;
        private String hashKey;
        private String hashIv;
        // Getters and Setters for Invoice...
    }
}