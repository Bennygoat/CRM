package com.example.demo.initializer;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.BCustomerDto;
import com.example.demo.dto.response.ContactDto;
import com.example.demo.dto.response.OpportunityDto;
import com.example.demo.dto.response.OpportunityTagDto;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.service.*;
import com.example.demo.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
@Profile("dev")
public class DataInitializer_BCustomer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer_BCustomer.class);
    private final Random random = new Random();

    @Autowired
    private BCustomerService bCustomerService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private OpportunityService opportunityService;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private OpportunityTagService opportunityTagService;

    @Autowired
    private ActivityService activityService;

    // ----- Fakers -----

    @Autowired
    private BCustomerFaker bCustomerFaker;

    @Autowired
    private ContactFaker contactFaker;

    @Autowired
    private OpportunityFaker opportunityFaker;

    @Autowired
    private OpportunityTagFaker opportunityTagFaker;

    @Autowired
    private ActivityFaker activityFaker;


    @Override
    public void run(String... args) throws Exception {
        if (opportunityRepository.count() > 0) {
            logger.info("資料庫已有商機資料，跳過初始資料植入程序。");
            return;
        }
        logger.info("--- 應用程式啟動，開始生成一年份的假資料 (僅限 dev profile) ---");

        // 步驟 1: 建立「商機」專用的標籤，並取得它們的 ID
        List<Long> opportunityTagIds = createOpportunityTags(5);
        if (opportunityTagIds.isEmpty()) {
            logger.warn("未能建立任何商機標籤，商機將不會有關聯標籤。");
        }

        // 步驟 2: 建立客戶和聯絡人 (此處不關聯任何標籤)
        List<Long> customerIds = createCustomers(20);
        if (customerIds.isEmpty()) {
            logger.error("未能創建任何客戶。終止資料生成。");
            return;
        }
        List<Long> contactIds = createContacts(30, customerIds);

        // 步驟 3: 遍歷過去 12 個月，生成帶有關聯「商機標籤」的商機
        LocalDate today = LocalDate.now();
        for (int i = 11; i >= 0; i--) {
            LocalDate currentMonth = today.minusMonths(i).withDayOfMonth(1);
            logger.info("--- 開始為 {} 月份生成資料 ---", currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")));

            int opportunitiesToCreate = random.nextInt(3) + 2;
            List<Long> monthlyOpportunityIds = createMonthlyOpportunities(
                    opportunitiesToCreate, customerIds, contactIds, opportunityTagIds, currentMonth
            );

            if (monthlyOpportunityIds.isEmpty()) {
                logger.warn("月份 {} 未能成功創建任何商機，跳過此月份的後續步驟。", currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")));
                continue;
            }

            rateOpportunities(monthlyOpportunityIds);
            createMonthlyActivities(monthlyOpportunityIds.size() * (random.nextInt(3) + 1), monthlyOpportunityIds, contactIds, currentMonth);
        }

        logger.info("--- 所有年度假資料生成結束 ---");
    }

    /**
     * 建立「商機」標籤
     */
    private List<Long> createOpportunityTags(int count) {
        logger.info("--- 開始生成商機標籤 ({} 筆)...", count);
        List<OpportunityTagRequest> fakeRequests = opportunityTagFaker.generateFakeTagRequests(count);
        List<Long> generatedIds = new ArrayList<>();
        for (OpportunityTagRequest request : fakeRequests) {
            try {
                OpportunityTagDto created = opportunityTagService.create(request);
                generatedIds.add(created.getTagId());
            } catch (Exception e) {
                logger.error("創建商機標籤失敗: {}", e.getMessage());
            }
        }
        logger.info("商機標籤生成完畢。");
        return generatedIds;
    }

    /**
     * 商機隨機分配標籤
     */
    private List<Long> createMonthlyOpportunities(int count, List<Long> customerIds, List<Long> contactIds, List<Long> allTagIds, LocalDate month) {
        logger.info("生成 {} 筆商機...", count);
        List<OpportunityRequest> fakeRequests = opportunityFaker.generateFakeOpportunityRequests(count, customerIds, contactIds);
        List<Long> generatedIds = new ArrayList<>();

        for (OpportunityRequest request : fakeRequests) {
            // 設定歷史日期
            int dayOfMonth = random.nextInt(month.lengthOfMonth()) + 1;
            LocalDateTime creationDateTime = month.withDayOfMonth(dayOfMonth).atTime(random.nextInt(24), random.nextInt(60));
            request.setCreateDate(creationDateTime);
            request.setCloseDate(creationDateTime.toLocalDate().plusDays(random.nextInt(90) + 30));

            // 為商機隨機分配 1 到 2 個標籤
            if (allTagIds != null && !allTagIds.isEmpty()) {
                Collections.shuffle(allTagIds);
                int numberOfTagsToAssign = random.nextInt(2) + 1;
                List<Long> assignedTagIds = new ArrayList<>(allTagIds.subList(0, Math.min(numberOfTagsToAssign, allTagIds.size())));
                request.setTagIds(assignedTagIds);
            }

            try {
                OpportunityDto created = opportunityService.create(request);
                generatedIds.add(created.getOpportunityId());
            } catch (Exception e) {
                logger.error("創建商機失敗: {}", e.getMessage(), e);
            }
        }
        logger.info("本月商機生成完畢。");
        return generatedIds;
    }

    // ----- 輔助方法 -----

    private List<Long> createCustomers(int count) {
        logger.info("--- 開始生成客戶 ({} 筆)...", count);
        List<BCustomerRequest> fakeRequests = bCustomerFaker.generateFakeBCustomerRequests(count);
        List<Long> generatedIds = new ArrayList<>();
        for (BCustomerRequest request : fakeRequests) {
            try {
                BCustomerDto created = bCustomerService.create(request);
                generatedIds.add(created.getCustomerId());
            } catch (Exception e) {
                logger.error("創建客戶失敗: {}", e.getMessage());
            }
        }
        logger.info("客戶生成完畢。");
        return generatedIds;
    }

    private List<Long> createContacts(int count, List<Long> customerIds) {
        logger.info("--- 開始生成聯絡人 ({} 筆)...", count);
        List<ContactRequest> fakeRequests = contactFaker.generateFakeContactRequests(count, customerIds);
        List<Long> generatedIds = new ArrayList<>();
        for (ContactRequest request : fakeRequests) {
            try {
                ContactDto created = contactService.create(request);
                generatedIds.add(created.getContactId());
            } catch (Exception e) {
                logger.error("創建聯絡人失敗: {}", e.getMessage());
            }
        }
        logger.info("聯絡人生成完畢。");
        return generatedIds;
    }

    private void rateOpportunities(List<Long> opportunityIds) {
        logger.info("--- 開始為商機評分 ---");
        if (opportunityIds == null || opportunityIds.isEmpty()) {
            logger.warn("沒有商機可供評分，跳過此步驟。");
            return;
        }
        Long testUserId = 1L;
        for (Long oppId : opportunityIds) {
            int numRatings = random.nextInt(3) + 1;
            for (int i = 0; i < numRatings; i++) {
                int ratingScore = random.nextInt(3) + 1;
                try {
                    opportunityService.rateOpportunity(oppId, testUserId, ratingScore);
                } catch (Exception e) {
                    logger.error("評分商機 ID {} 失敗: {}", oppId, e.getMessage());
                }
            }
        }
        logger.info("商機評分結束。");
    }

    private void createMonthlyActivities(int count, List<Long> opportunityIds, List<Long> contactIds, LocalDate month) {
        logger.info("生成 {} 筆活動...", count);
        if (opportunityIds == null || opportunityIds.isEmpty()) {
            logger.warn("沒有商機可供創建活動，跳過。");
            return;
        }
        for (int i = 0; i < count; i++) {
            try {
                ActivityRequest request = activityFaker.generateFakeActivityRequest(opportunityIds, contactIds);
                int dayOfMonth = random.nextInt(month.lengthOfMonth()) + 1;
                LocalDateTime activityStartTime = month.withDayOfMonth(dayOfMonth).atTime(random.nextInt(8) + 9, random.nextInt(60));
                request.setStartTime(activityStartTime);
                request.setEndTime(activityStartTime.plusHours(1));
                activityService.create(request);
            } catch (Exception e) {
                logger.error("創建活動失敗: {}", e.getMessage());
            }
        }
        logger.info("本月活動生成完畢。");
    }
}
