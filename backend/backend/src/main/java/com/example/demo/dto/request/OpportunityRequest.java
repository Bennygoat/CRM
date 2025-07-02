package com.example.demo.dto.request;

import com.example.demo.enums.OpportunityStage;
import com.example.demo.enums.OpportunityStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用於接收客戶端創建或更新商機的請求資料。
 * 包含所有可由客戶端提交的商機相關資訊。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpportunityRequest {

    @NotBlank(message = "商機名稱不能為空")
    private String opportunityName;

    private String description;

    @PositiveOrZero(message = "預期價值必須大於等於零")
    private BigDecimal expectedValue;

    private LocalDateTime createDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate closeDate;

    @NotNull(message = "商機階段不能為空")
    private OpportunityStage stage;

    @NotNull(message = "商機狀態不能為空")
    private OpportunityStatus status;

    @NotNull(message = "必須指定關聯客戶ID")
    private Long customerId;

    private Long contactId;

    private List<Long> tagIds;

    @Min(value = 0, message = "優先級不能小於 0")
    @Max(value = 3, message = "優先級不能大於 3")
    private int priority;

}
