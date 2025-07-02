package com.example.demo.service.impl;

import com.example.demo.dto.request.OpportunityPriorityRequest;
import com.example.demo.dto.request.OpportunityRequest;
import com.example.demo.dto.response.OpportunityDto;
import com.example.demo.dto.response.SalesFunnelDto;
import com.example.demo.entity.BCustomer;
import com.example.demo.entity.Contact;
import com.example.demo.entity.Opportunity;
import com.example.demo.entity.OpportunityTag;
import com.example.demo.enums.OpportunityStage;
import com.example.demo.enums.OpportunityStatus;
import com.example.demo.mapper.OpportunityMapper;
import com.example.demo.repository.*;
import com.example.demo.service.OpportunityService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class OpportunityServiceImpl implements OpportunityService {

    private static final Logger logger = LoggerFactory.getLogger(OpportunityServiceImpl.class);

    private final OpportunityRepository opportunityRepository;
    private final BCustomerRepository bCustomerRepository;
    private final ContactRepository contactRepository;
    private final OpportunityMapper opportunityMapper;
    private final OpportunityTagRepository opportunityTagRepository;

    public OpportunityServiceImpl(OpportunityRepository opportunityRepository,
                                  BCustomerRepository bCustomerRepository,
                                  ContactRepository contactRepository,
                                  OpportunityMapper opportunityMapper,
                                  OpportunityTagRepository opportunityTagRepository) {
        this.opportunityRepository = opportunityRepository;
        this.bCustomerRepository = bCustomerRepository;
        this.contactRepository = contactRepository;
        this.opportunityMapper = opportunityMapper;
        this.opportunityTagRepository = opportunityTagRepository;
    }

    /**
     * 獲取所有商機的分頁列表。
     * @param pageable 分頁和排序資訊。
     * @return 包含商機的回應 DTO 分頁對象。
     */

    @Override
    @Transactional(readOnly = true)
    public Page<OpportunityDto> findAll(Pageable pageable) {
        return opportunityRepository.findAll(pageable)
                .map(opportunity -> opportunityMapper.toResponse(opportunity, null));
    }

    /**
     * 根據商機ID查詢單個商機。
     * @param id 商機的唯一識別碼。
     * @return 包含商機回應 DTO 的 Optional，如果找不到則為空。
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<OpportunityDto> findById(Long id) {
        Optional<Opportunity> opportunityOptional = opportunityRepository.findById(id);
        return opportunityOptional.map(
                opportunity -> opportunityMapper.toResponse(opportunity, null));
    }

    /**
     * 創建一個新的商機。
     * 接收 DTO 進行創建，返回創建後的回應 DTO。
     * 驗證關聯的客戶和聯絡人是否存在。
     * @param request 包含新商機資料的請求 DTO。
     * @return 創建成功的商機回應 DTO。
     * @throws EntityNotFoundException 如果關聯的客戶或聯絡人不存在。
     */
    @Override
    @Transactional
    public OpportunityDto create(OpportunityRequest request) {
        Opportunity opportunity = opportunityMapper.toEntity(request);

        BCustomer customer = bCustomerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("找不到客戶 ID: " + request.getCustomerId()));
        opportunity.setBCustomer(customer);

        if (request.getContactId() != null) {
            Contact contact = contactRepository.findById(request.getContactId())
                    .orElseThrow(() -> new EntityNotFoundException("找不到聯絡人 ID: " + request.getContactId()));
            opportunity.setContact(contact);
        }
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<OpportunityTag> tags = opportunityTagRepository.findAllById(request.getTagIds());
            if (tags.size() != request.getTagIds().size()) {
                throw new EntityNotFoundException("一個或多個用於創建的商機標籤ID不存在。");
            }
            opportunity.setTags(new HashSet<>(tags));
        }

        LocalDateTime creationTime;
        if (request.getCreateDate() != null) {
            creationTime = request.getCreateDate();
        } else {
            creationTime = LocalDateTime.now();
        }

        opportunity.setCreatedAt(creationTime);
        opportunity.setUpdatedAt(creationTime);

        Opportunity savedOpportunity = opportunityRepository.save(opportunity);

        return opportunityMapper.toResponse(savedOpportunity, null);
    }

    /**
     * 更新一個現有的商機。
     * 接收 ID 和 DTO 進行更新，返回更新後的回應 DTO。
     * 驗證商機本身及關聯的客戶和聯絡人是否存在。
     * @param id 商機的唯一識別碼。
     * @param request 包含更新資料的請求 DTO。
     * @return 更新成功的商機回應 DTO。
     * @throws EntityNotFoundException 如果商機、關聯客戶或聯絡人不存在。
     */
    @Override
    @Transactional
    public OpportunityDto update(Long id, OpportunityRequest request) {
        Opportunity existingOpportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的商機"));

        opportunityMapper.updateEntityFromRequest(existingOpportunity, request);

        if (!existingOpportunity.getBCustomer().getCustomerId().equals(request.getCustomerId())) {
            BCustomer newBCustomer = bCustomerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + request.getCustomerId() + " 的客戶"));
            existingOpportunity.setBCustomer(newBCustomer);
        }

        if (request.getContactId() != null) {
            if (existingOpportunity.getContact() == null || !existingOpportunity.getContact().getContactId().equals(request.getContactId())) {
                Contact newContact = contactRepository.findById(request.getContactId())
                        .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + request.getContactId() + " 的聯絡人"));
                existingOpportunity.setContact(newContact);
            }
        } else {
            existingOpportunity.setContact(null);
        }

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<OpportunityTag> tags = opportunityTagRepository.findAllById(request.getTagIds());
            if (tags.size() != request.getTagIds().size()) {
                throw new EntityNotFoundException("一個或多個用於更新的商機標籤ID不存在。");
            }
            existingOpportunity.setTags(new HashSet<>(tags));
        } else {
            existingOpportunity.getTags().clear();
        }

        Opportunity updatedOpportunity = opportunityRepository.save(existingOpportunity);
        return opportunityMapper.toResponse(updatedOpportunity, null);
    }

    /**
     * 根據商機ID刪除一個商機。
     * @param id 要刪除的商機的唯一識別碼。
     * @throws EntityNotFoundException 如果找不到對應的商機。
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (!opportunityRepository.existsById(id)) {
            throw new EntityNotFoundException("找不到 ID 為 " + id + " 的商機進行刪除");
        }
        opportunityRepository.deleteById(id);
    }

    /**
     * 為指定的商機添加或更新評分。
     * 這將更新商機的總評分和評分次數，並重新計算平均評分。
     * @param opportunityId 要評分的商機的唯一識別碼。
     * @param userId 進行評分的用戶的 ID。(在實際應用中，此ID應從安全上下文獲取)
     * @param ratingScore 評分分數 (1-3)。
     * @return 更新評分後的商機回應 DTO。
     * @throws EntityNotFoundException 如果找不到對應的商機。
     * @throws IllegalArgumentException 如果評分分數無效 (例如不在 1-3 範圍內)。
     */
    @Override
    @Transactional
    public OpportunityDto rateOpportunity(Long opportunityId, Long userId, int ratingScore) {
        // 1. 驗證評分分數是否在有效範圍 (1 - 3)
        if (ratingScore < 1 || ratingScore > 3) {
            logger.warn("Received invalid rating score: {}. Rating must be between 1 and 3.", ratingScore);
            throw new IllegalArgumentException("評分分數必須在 1 到 3 之間");
        }

        // 2. 查找商機，如果不存在則拋出異常。
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + opportunityId + " 的商機進行評分"));

        // 3. 更新評分相關字段
        // 一個用戶可以重複評分，每次評分都會計入總和。
        opportunity.setTotalRatingSum(opportunity.getTotalRatingSum() + ratingScore);
        opportunity.setNumberOfRatings(opportunity.getNumberOfRatings() + 1);

        logger.info("Opportunity ID {} rated by User ID {}. New total sum: {}, new count: {}",
                opportunityId, userId, opportunity.getTotalRatingSum(), opportunity.getNumberOfRatings());

        // 4. 保存更新後的商機。
        Opportunity updatedOpportunity = opportunityRepository.save(opportunity);

        // 5. 將更新後的實體轉換為 DTO。
        return opportunityMapper.toResponse(updatedOpportunity, null);
    }


    @Override
    @Transactional
    public OpportunityDto setPriority(Long opportunityId, OpportunityPriorityRequest request) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + opportunityId + " 的商機"));

        opportunity.setPriority(request.getPriority());

        Opportunity updatedOpportunity = opportunityRepository.save(opportunity);

        return opportunityMapper.toResponse(updatedOpportunity, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesFunnelDto> getSalesFunnelData() {
        // 查詢所有「未結案(失敗)」的商機
        // 這會包含所有 OPEN 和 WON 的商機
        List<Opportunity> activeOpportunities = opportunityRepository.findByStatusNot(OpportunityStatus.LOST);

        // 按階段分組
        Map<OpportunityStage, List<Opportunity>> opportunitiesByStage = activeOpportunities.stream()
                .collect(Collectors.groupingBy(Opportunity::getStage));

        List<SalesFunnelDto> funnelData = new ArrayList<>();

        // 遍歷所有可能的階段
        for (OpportunityStage stage : OpportunityStage.values()) {
            if (stage == OpportunityStage.CLOSED_LOST) {
                continue;
            }

            SalesFunnelDto stageDto = new SalesFunnelDto();
            stageDto.setStage(stage);
            stageDto.setStageDisplayName(stage.name());

            List<Opportunity> opportunitiesInStage = opportunitiesByStage.getOrDefault(stage, Collections.emptyList());
            List<OpportunityDto> opportunityDtos = opportunitiesInStage.stream()
                    .map(opportunity -> opportunityMapper.toResponse(opportunity, null))
                    .collect(Collectors.toList());

            stageDto.setOpportunities(opportunityDtos);
            stageDto.setTotalCount((long) opportunitiesInStage.size());
            stageDto.setTotalExpectedValue(
                    opportunitiesInStage.stream()
                            .map(Opportunity::getExpectedValue)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
            );

            funnelData.add(stageDto);
        }
        return funnelData;
    }

    /**
     * 實作多條件動態搜尋商機的方法。
     * 利用 Spring Data JPA 的 Specification 來動態構建查詢條件。
     * @param name 商機名稱模糊搜尋關鍵字
     * @param status 商機狀態
     * @param stage 商機階段
     * @param customerId 關聯客戶ID
     * @param contactId 關聯聯絡人ID
     * @param closeDateBefore 預計結束日期在指定日期之前
     * @param pageable 分頁資訊
     * @return 符合所有條件的商機回應 DTO 分頁列表
     */
    @Override
    @Transactional(readOnly = true) // 標註為只讀事務
    public Page<OpportunityDto> searchOpportunities(String name, OpportunityStatus status, OpportunityStage stage, Long customerId, Long contactId, LocalDate closeDateBefore, Pageable pageable) {
        Specification<Opportunity> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 條件：商機名稱模糊搜尋 (不區分大小寫)
            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("opportunityName")), "%" + name.toLowerCase() + "%"));
            }
            // 條件：商機狀態
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            // 條件：商機階段
            if (stage != null) {
                predicates.add(criteriaBuilder.equal(root.get("stage"), stage));
            }
            // 條件：關聯客戶ID
            if (customerId != null) {
                // 通過關聯物件 bCustomer 訪問其 customerId 屬性
                predicates.add(criteriaBuilder.equal(root.get("bCustomer").get("customerId"), customerId));
            }
            // 條件：關聯聯絡人ID
            if (contactId != null) {
                predicates.add(criteriaBuilder.equal(root.get("contact").get("contactId"), contactId));
            }
            // 條件：預計結束日期在指定日期之前 (包含指定日期)
            if (closeDateBefore != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("closeDate"), closeDateBefore));
            }

            // 將所有條件組合成一個 AND 邏輯的 Predicate
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return opportunityRepository.findAll(spec, pageable)
                .map(opportunity -> opportunityMapper.toResponse(opportunity, null));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OpportunityDto> findByBCustomerId(Long customerId, Pageable pageable) {
        return opportunityRepository.findByBCustomer_CustomerId(customerId, pageable)
                .map(opportunity -> opportunityMapper.toResponse(opportunity, null));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OpportunityDto> findByStatus(OpportunityStatus status, Pageable pageable) {
        return opportunityRepository.findByStatus(status, pageable)
                .map(opportunity -> opportunityMapper.toResponse(opportunity, null));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OpportunityDto> findByStage(OpportunityStage stage, Pageable pageable) {
        return opportunityRepository.findByStage(stage, pageable)
                .map(opportunity -> opportunityMapper.toResponse(opportunity, null));
    }

}