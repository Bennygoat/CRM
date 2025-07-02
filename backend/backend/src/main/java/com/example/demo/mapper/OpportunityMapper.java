package com.example.demo.mapper;

import com.example.demo.dto.request.OpportunityRequest;
import com.example.demo.dto.response.OpportunityDto;
import com.example.demo.dto.response.OpportunityTagDto;
import com.example.demo.entity.BCustomer;
import com.example.demo.entity.Contact;
import com.example.demo.entity.Opportunity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用於 Opportunity 實體與其對應的 DTOs 之間進行資料轉換的映射器。
 * 負責將請求 DTO 轉換為實體，以及將實體轉換為回應 DTO。
 */
@Component
public class OpportunityMapper {

    /**
     * 將 OpportunityRequest DTO 轉換為 Opportunity 實體。
     * 在此過程中，需要將客戶ID和聯絡人ID設置到關聯的實體對象中。
     * @param request 從客戶端收到的 OpportunityDto DTO
     * @return 轉換後的 Opportunity 實體
     */
    public Opportunity toEntity(OpportunityRequest request) {
        if (request == null) {
            return null;
        }
        return Opportunity.builder()
                .opportunityName(request.getOpportunityName())
                .description(request.getDescription())
                .expectedValue(request.getExpectedValue())
                .closeDate(request.getCloseDate())
                .stage(request.getStage())
                .status(request.getStatus())
                .priority(request.getPriority())
                .bCustomer(BCustomer.builder().customerId(request.getCustomerId()).build())
                .contact(request.getContactId() != null ? Contact.builder().contactId(request.getContactId()).build() : null)
                .totalRatingSum(0L) // 初始化總評分
                .numberOfRatings(0) // 初始化評分次數
                .build();
    }

    /**
     * 將 Opportunity 實體轉換為 OpportunityDto DTO。
     * 將關聯實體 (BCustomer, Contact) 的資訊扁平化到回應 DTO 中。
     * @param opportunity 要轉換的 Opportunity 實體
     * @return 轉換後的 OpportunityResponse DTO
     */
    public OpportunityDto toResponse(Opportunity opportunity, Long currentUserId) {
        if (opportunity == null) {
            return null;
        }

        // --- 處理標籤的轉換 ---
        List<OpportunityTagDto> opportunityTagDtos = (opportunity.getTags() != null)
                ? opportunity.getTags().stream()
                .map(tag -> OpportunityTagDto.builder()
                        .tagId(tag.getTagId())
                        .tagName(tag.getTagName())
                        .color(tag.getColor())
                        .build())
                .collect(Collectors.toList())
                : Collections.emptyList();


        Long customerId = (opportunity.getBCustomer() != null) ? opportunity.getBCustomer().getCustomerId() : null;
        String customerName = (opportunity.getBCustomer() != null) ? opportunity.getBCustomer().getCustomerName() : null;

        Long contactId = (opportunity.getContact() != null) ? opportunity.getContact().getContactId() : null;
        String contactName = (opportunity.getContact() != null) ? opportunity.getContact().getContactName() : null;

        //計算平均評分
        Double averageRating = 0.0;
        if (opportunity.getNumberOfRatings() != null && opportunity.getNumberOfRatings() > 0) {
            averageRating = (double) opportunity.getTotalRatingSum() / opportunity.getNumberOfRatings();
        }

        return OpportunityDto.builder()
                .opportunityId(opportunity.getOpportunityId())
                .opportunityName(opportunity.getOpportunityName())
                .description(opportunity.getDescription())
                .expectedValue(opportunity.getExpectedValue())
                .closeDate(opportunity.getCloseDate())
                .stage(opportunity.getStage())
                .status(opportunity.getStatus())
                .customerId(customerId)
                .customerName(customerName)
                .contactId(contactId)
                .contactName(contactName)
                .createdAt(opportunity.getCreatedAt())
                .updatedAt(opportunity.getUpdatedAt())
                .averageRating(averageRating) // 平均評分
                .tags(opportunityTagDtos)
                .build();
    }

    /**
     * 將現有的 Opportunity 實體根據 OpportunityRequest DTO 進行更新。
     * 不會創建新的實體，僅更新現有實體的屬性。
     * @param existingOpportunity 已存在需要更新的 Opportunity 實體
     * @param request 包含更新資訊的 OpportunityRequest DTO
     * @return 更新後的 Opportunity 實體
     */
    public Opportunity updateEntityFromRequest(Opportunity existingOpportunity, OpportunityRequest request) {
        if (existingOpportunity == null || request == null) {
            return existingOpportunity;
        }

        existingOpportunity.setOpportunityName(request.getOpportunityName());
        existingOpportunity.setDescription(request.getDescription());
        existingOpportunity.setExpectedValue(request.getExpectedValue());
        existingOpportunity.setCloseDate(request.getCloseDate());
        existingOpportunity.setStage(request.getStage());
        existingOpportunity.setStatus(request.getStatus());
        existingOpportunity.setPriority(request.getPriority());

        return existingOpportunity;
        }
    }