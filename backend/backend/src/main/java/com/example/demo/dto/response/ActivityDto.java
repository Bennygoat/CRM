package com.example.demo.dto.response;

import com.example.demo.enums.ActivityStatus;
import com.example.demo.enums.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDto {

    private Long id;
    private String title;
    private ActivityType activityType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ActivityStatus status;
    private String notes;
    private Long opportunityId;
    private String opportunityName;
    private Long contactId;
    private String contactName;
}
