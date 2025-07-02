package com.example.demo.util;

import com.example.demo.dto.request.ActivityRequest;
import com.example.demo.enums.ActivityType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class ActivityFaker {

    private final Random random = new Random();

    /**
     * 生成一個包含隨機資料的 ActivityRequest DTO。
     * @param opportunityIds 可供關聯的商機ID列表。
     * @param contactIds 可供關聯的聯絡人ID列表。
     * @return 一個已填入假資料的 ActivityRequest 物件。
     */
    public ActivityRequest generateFakeActivityRequest(List<Long> opportunityIds, List<Long> contactIds) {
        ActivityType randomType = ActivityType.values()[random.nextInt(ActivityType.values().length)];
        LocalDateTime startTime = LocalDateTime.now().plusDays(random.nextInt(45) - 15).withHour(9 + random.nextInt(8));
        LocalDateTime endTime = startTime.plusHours(random.nextInt(3) + 1);

        ActivityRequest request = new ActivityRequest();
        request.setTitle(generateTitle(randomType));
        request.setActivityType(randomType);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setNotes("這是自動生成的活動備註。");

        if (opportunityIds != null && !opportunityIds.isEmpty() && random.nextBoolean()) {
            request.setOpportunityId(opportunityIds.get(random.nextInt(opportunityIds.size())));
        }
        if (contactIds != null && !contactIds.isEmpty() && random.nextBoolean()) {
            request.setContactId(contactIds.get(random.nextInt(contactIds.size())));
        }

        return request;
    }

    /**
     * 根據活動類型，產生一個更具體的標題。
     * @param type 活動類型
     * @return 一個隨機的活動標題字串
     */
    private String generateTitle(ActivityType type) {
        return switch (type) {
            case MEETING -> "產品展示會議";
            case CALL -> "客戶追蹤電話";
            case TASK -> "準備報價單";
            case EMAIL -> "寄送季末問候郵件";
            case DEADLINE -> "合約簽署截止日";
        };
    }
}