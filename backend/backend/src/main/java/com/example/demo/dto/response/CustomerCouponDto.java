package com.example.demo.dto.response;

import com.example.demo.enums.CouponType;
import com.example.demo.enums.CustomerCouponStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用於向前台展示顧客所持有的單張優惠券資訊的資料傳輸物件 (DTO)。
 * 這個物件組合了 CustomerCoupon 和 CouponTemplate 的資訊，只提供前端需要的欄位。
 */
@Data
@Builder
@Schema(description = "顧客持有的優惠券資訊")
public class CustomerCouponDto {

    @Schema(description = "顧客優惠券的唯一ID (這張券自己的ID)", example = "101")
    private Long customerCouponId; // 欄位名也一併調整以符合類別名稱

    // --- 以下資訊來自關聯的 CouponTemplate (優惠券的規則) ---

    @Schema(description = "優惠券名稱", example = "新會員註冊禮-全館9折券")
    private String name;

    @Schema(description = "優惠券詳細描述", example = "全館商品消費滿 1 元即可享9折優惠。")
    private String description;

    @Schema(description = "優惠券類型 (PERCENTAGE: 折扣, FIXED_AMOUNT: 現金折抵)", example = "PERCENTAGE")
    private CouponType couponType;

    @Schema(description = "折扣數值 (若是9折則為 0.90，若是折100元則為 100.00)", example = "0.90")
    private BigDecimal discountValue;

    @Schema(description = "最低消費金額門檻", example = "1.00")
    private BigDecimal minPurchaseAmount;

    @Schema(description = "有效期限至", example = "2025-12-31T23:59:59")
    private LocalDateTime validTo;

    // --- 以下資訊來自 CustomerCoupon 本身 (這張券的狀態) ---

    @Schema(description = "這張優惠券的狀態 (UNUSED: 未使用, USED: 已使用, EXPIRED: 已過期)", example = "UNUSED")
    private CustomerCouponStatus status;
}
