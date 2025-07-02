package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpportunityTagRequest {

    @NotBlank(message = "標籤名稱不能為空")
    @Size(max = 50, message = "標籤名稱長度不能超過50個字元")
    private String tagName;

    @Size(max = 10, message = "顏色代碼長度不能超過7個字元 (例如 #RRGGBB)")
    private String color;
}