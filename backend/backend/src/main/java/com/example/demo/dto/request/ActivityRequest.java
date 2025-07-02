package com.example.demo.dto.request;

import com.example.demo.enums.ActivityType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ActivityRequest {

    @NotBlank(message = "活動標題不可為空")
    private String title;

    @NotNull(message = "活動類型不可為空")
    private ActivityType activityType;

    @NotNull(message = "開始時間不可為空")
    private LocalDateTime startTime;

    private LocalDateTime endTime;
    private String notes;
    private Long opportunityId;
    private Long contactId;

}
