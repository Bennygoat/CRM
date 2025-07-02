package com.example.demo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OpportunityPriorityRequest {

    @Min(value = 0, message = "優先級不能小於 0")
    @Max(value = 3, message = "優先級不能大於 3")
    private int priority;
}
