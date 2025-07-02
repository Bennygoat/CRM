package com.example.demo.util;

import com.example.demo.dto.request.OpportunityRequest;
import com.example.demo.enums.OpportunityStage;
import com.example.demo.enums.OpportunityStatus;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class OpportunityFaker {

    private final Faker faker = new Faker();
    private final Random random = new Random();

    /**
     * 產生一個隨機的 OpportunityRequest 物件。
     *
     * @param existingCustomerIds 實際存在的客戶ID列表
     * @param existingContactIds  實際存在的聯絡人ID列表
     * @return 包含假商機資料的 OpportunityRequest
     * @throws IllegalArgumentException 如果提供的客戶ID列表為空
     */
    public OpportunityRequest generateFakeOpportunityRequest(List<Long> existingCustomerIds, List<Long> existingContactIds) {
        if (existingCustomerIds == null || existingCustomerIds.isEmpty()) {
            throw new IllegalArgumentException("生成商機假資料需要至少一個客戶ID。");
        }

        // 隨機選擇商機階段和狀態
        List<OpportunityStage> stages = Arrays.asList(OpportunityStage.values());
        List<OpportunityStatus> statuses = Arrays.asList(OpportunityStatus.values());

        // 產生隨機預期結束日期 (未來一年內)
        LocalDate closeDate = faker.date().future(365, TimeUnit.DAYS).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // 產生隨機客戶ID
        Long customerId = existingCustomerIds.get(random.nextInt(existingCustomerIds.size()));

        // 隨機選擇聯絡人ID (可以是 null)，50% 機率關聯聯絡人
        Long contactId = null;
        if (existingContactIds != null && !existingContactIds.isEmpty() && faker.bool().bool()) {
            contactId = existingContactIds.get(random.nextInt(existingContactIds.size()));
        }

        return OpportunityRequest.builder()
                .opportunityName(faker.company().industry() + " - " + faker.commerce().productName() + " 商機")
                .description(faker.lorem().paragraph(3))
                .expectedValue(BigDecimal.valueOf(faker.number().randomDouble(2, 1000, 50000)))
                .closeDate(closeDate)
                .stage(stages.get(random.nextInt(stages.size())))
                .status(statuses.get(random.nextInt(statuses.size())))
                .customerId(customerId)
                .contactId(contactId)
                .build();
    }

    /**
     * 產生指定數量的假商機請求。
     * @param count 要產生的請求數量
     * @param existingCustomerIds 實際存在的客戶ID列表
     * @param existingContactIds  實際存在的聯絡人ID列表
     * @return 包含假商機請求的列表
     */
    public List<OpportunityRequest> generateFakeOpportunityRequests(int count, List<Long> existingCustomerIds, List<Long> existingContactIds) {
        return IntStream.range(0, count)
                .mapToObj(i -> generateFakeOpportunityRequest(existingCustomerIds, existingContactIds))
                .collect(Collectors.toList());
    }
}
