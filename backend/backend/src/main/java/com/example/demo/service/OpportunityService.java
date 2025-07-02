package com.example.demo.service;

import com.example.demo.dto.request.OpportunityPriorityRequest;
import com.example.demo.dto.request.OpportunityRequest;
import com.example.demo.dto.response.OpportunityDto;
import com.example.demo.dto.response.SalesFunnelDto;
import com.example.demo.enums.OpportunityStage;
import com.example.demo.enums.OpportunityStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OpportunityService {

    /**
     * 獲取所有商機的分頁列表。
     * @param pageable 分頁和排序資訊。
     * @return 包含商機的回應 DTO 分頁對象。
     */
    Page<OpportunityDto> findAll(Pageable pageable);

    /**
     * 根據商機ID查詢單個商機。
     * @param id 商機的唯一識別碼。
     * @return 包含商機回應 DTO 的 Optional，如果找不到則為空。
     */
    Optional<OpportunityDto> findById(Long id);

    /**
     * 創建一個新的商機。
     * 接收 DTO 進行創建，返回創建後的回應 DTO。
     * @param request 包含新商機資料的請求 DTO。
     * @return 創建成功的商機回應 DTO。
     */
    OpportunityDto create(OpportunityRequest request);

    /**
     * 更新一個現有的商機。
     * 接收 ID 和 DTO 進行更新，返回更新後的回應 DTO。
     * @param id 商機的唯一識別碼。
     * @param request 包含更新資料的請求 DTO。
     * @return 更新成功的商機回應 DTO。
     * @throws EntityNotFoundException 如果找不到對應的商機。
     */
    OpportunityDto update(Long id, OpportunityRequest request);

    /**
     * 根據商機ID刪除一個商機。
     * @param id 要刪除的商機的唯一識別碼。
     */
    void delete(Long id);

    /**
     * 根據多個條件搜尋商機，支持分頁。
     * @param name 商機名稱的部分字串 (模糊查詢)。
     * @param status 商機狀態。
     * @param stage 商機階段。
     * @param customerId 關聯客戶ID。
     * @param contactId 關聯聯絡人ID。
     * @param closeDateBefore 預計結束日期在指定日期之前。
     * @param pageable 分頁和排序資訊。
     * @return 符合所有條件的商機回應 DTO 分頁列表。
     */
    Page<OpportunityDto> searchOpportunities(String name,
                                                  OpportunityStatus status,
                                                  OpportunityStage stage,
                                                  Long customerId, Long contactId,
                                                  LocalDate closeDateBefore,
                                                  Pageable pageable);

    // ----- searchOpportunities 方法整合或用於特定查詢 -----

    /**
     * 根據客戶ID查詢其所有商機的分頁列表。
     * @param customerId 客戶的唯一識別碼。
     * @param pageable 分頁和排序資訊。
     * @return 屬於指定客戶的商機回應 DTO 分頁列表。
     */
    Page<OpportunityDto> findByBCustomerId(Long customerId, Pageable pageable);

    /**
     * 根據商機狀態查詢商機的分頁列表。
     * @param status 商機狀態枚舉。
     * @param pageable 分頁和排序資訊。
     * @return 符合指定狀態的商機回應 DTO 分頁列表。
     */
    Page<OpportunityDto> findByStatus(OpportunityStatus status, Pageable pageable);

    /**
     * 根據商機階段查詢商機的分頁列表。
     * @param stage 商機階段枚舉。
     * @param pageable 分頁和排序資訊。
     * @return 符合指定階段的商機回應 DTO 分頁列表。
     */
    Page<OpportunityDto> findByStage(OpportunityStage stage, Pageable pageable);

    // ----- 以下為星級評分 -----

    /**
     * 為指定的商機添加或更新評分。
     * @param opportunityId 要評分的商機的唯一識別碼。
     * @param userId 進行評分的用戶的 ID。(在實際應用中，此ID應從安全上下文獲取)
     * @param ratingScore 評分分數 (1-3)
     * @return 更新評分後的商機回應 DTO。
     * @throws EntityNotFoundException 如果找不到對應的商機。
     * @throws IllegalArgumentException 如果評分分數無效 (例如不在 1-3 範圍內)。
     */
    OpportunityDto rateOpportunity(Long opportunityId, Long userId, int ratingScore);

    OpportunityDto setPriority(Long opportunityId, OpportunityPriorityRequest request);

    // ----- 銷售漏斗 -----

    /**
     * 獲取用於銷售漏斗的匯總資料。
     * @return 一個列表，其中每個元素代表一個銷售階段及其相關數據。
     */
    List<SalesFunnelDto> getSalesFunnelData();
}
