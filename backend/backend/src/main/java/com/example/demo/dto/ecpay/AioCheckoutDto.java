package com.example.demo.dto.ecpay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.TreeMap;

// 使用 @JsonProperty 來確保序列化為 ECPay 需要的欄位名稱
@Getter
@Setter
public class AioCheckoutDto {

    @JsonProperty("MerchantID")
    private String merchantID;

    @JsonProperty("MerchantTradeNo")
    private String merchantTradeNo;

    @JsonProperty("MerchantTradeDate")
    private String merchantTradeDate;

    @JsonProperty("PaymentType")
    private String paymentType = "aio";

    @JsonProperty("TotalAmount")
    private Integer totalAmount;

    @JsonProperty("TradeDesc")
    private String tradeDesc;

    @JsonProperty("ItemName")
    private String itemName;

    @JsonProperty("ReturnURL")
    private String returnURL;

    @JsonProperty("ChoosePayment")
    private String choosePayment = "ALL";

    @JsonProperty("EncryptType")
    private Integer encryptType = 1;

    @JsonProperty("CheckMacValue")
    private String checkMacValue;

//    @JsonProperty("OrderResultURL")
//    private String orderResultURL;


    /**
     * 將 DTO 轉換為 Map 以便產生 CheckMacValue
     * @return Map<String, String>
     */
    public Map<String, String> toMap() {
        Map<String, String> map = new TreeMap<>();
        map.put("MerchantID", this.merchantID);
        map.put("MerchantTradeNo", this.merchantTradeNo);
        map.put("MerchantTradeDate", this.merchantTradeDate);
        map.put("PaymentType", this.paymentType);
        map.put("TotalAmount", String.valueOf(this.totalAmount));
        map.put("TradeDesc", this.tradeDesc);
        map.put("ItemName", this.itemName);
        map.put("ReturnURL", this.returnURL);
        map.put("ChoosePayment", this.choosePayment);
        map.put("EncryptType", String.valueOf(this.encryptType));
//        map.put("OrderResultURL", this.orderResultURL);

        return map;
    }
}