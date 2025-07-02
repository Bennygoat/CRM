package com.example.demo.repository;

import com.example.demo.dto.response.dashboard.NameValueDto;
import com.example.demo.entity.Opportunity;
import com.example.demo.enums.OpportunityStage;
import com.example.demo.enums.OpportunityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OpportunityRepository extends JpaRepository<Opportunity, Long>,
        JpaSpecificationExecutor<Opportunity> {

    /**
     * 根據商機名稱進行模糊查詢（不區分大小寫），並支持分頁。
     * Spring Data JPA 會根據方法名自動構建查詢。
     * `Containing` 表示模糊查詢 (SQL 的 LIKE %value%)。
     * `IgnoreCase` 表示不區分大小寫。
     * @param opportunityName 商機名稱的部分字串 (例如輸入 "新產品" 可以找到 "新產品推廣")。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的商機分頁列表。
     */
    Page<Opportunity> findByOpportunityNameContainingIgnoreCase(String opportunityName, Pageable pageable);

    /**
     * 根據商機狀態查詢商機列表，並支持分頁。
     * @param status 商機狀態枚舉 (例如 OpportunityStatus.WON)。
     * @param pageable 分頁和排序資訊。
     * @return 符合指定狀態的商機分頁列表。
     */
    Page<Opportunity> findByStatus(OpportunityStatus status, Pageable pageable);

    /**
     * 根據商機階段查詢商機列表，並支持分頁。
     * @param stage 商機階段枚舉 (例如 OpportunityStage.PROPOSAL)。
     * @param pageable 分頁和排序資訊。
     * @return 符合指定階段的商機分頁列表。
     */
    Page<Opportunity> findByStage(OpportunityStage stage, Pageable pageable);

    /**
     * 根據關聯客戶的 ID 查詢商機列表，並支持分頁。
     * `findByBCustomer_CustomerId` 語法表示遍歷 `Opportunity` 的 `bCustomer` 屬性，
     * 再查詢 `bCustomer` 實體中的 `customerId` 屬性。
     * @param customerId 客戶的唯一識別碼。
     * @param pageable 分頁和排序資訊。
     * @return 屬於指定客戶的商機分頁列表。
     */
    Page<Opportunity> findByBCustomer_CustomerId(Long customerId, Pageable pageable);

    /**
     * 根據關聯聯絡人的 ID 查詢商機列表，並支持分頁。
     * `findByContact_ContactId` 語法表示遍歷 `Opportunity` 的 `contact` 屬性，
     * 再查詢 `contact` 實體中的 `contactId` 屬性。
     * @param contactId 聯絡人的唯一識別碼。
     * @param pageable 分頁和排序資訊。
     * @return 屬於指定聯絡人的商機分頁列表。
     */
    Page<Opportunity> findByContact_ContactId(Long contactId, Pageable pageable);

    /**
     * 查詢預計結束日期在指定日期之前的商機列表，並支持分頁。
     * `Before` 表示查詢日期小於或等於指定日期。
     * @param closeDate 指定的結束日期。
     * @param pageable 分頁和排序資訊。
     * @return 預計結束日期在指定日期之前的商機分頁列表。
     */
    Page<Opportunity> findByCloseDateBefore(LocalDate closeDate, Pageable pageable);

    /**
     * 查詢所有狀態為 OPEN 的商機。
     * 用於在服務層中一次性獲取所有需要處理的商機，避免 N+1 查詢問題。
     * @param status 要查詢的狀態，固定為 OPEN
     * @return 狀態為 OPEN 的商機列表
     */
    List<Opportunity> findByStatus(OpportunityStatus status);

    /**
     * 根據指定的狀態，查詢所有「不等於」該狀態的商機。
     * 我們將用它來獲取所有未結案(失敗)的商機 (OPEN 或 WON)。
     * @param status 要排除的狀態 (例如 OpportunityStatus.CLOSED_LOST)。
     * @return 符合條件的商機列表。
     */
    List<Opportunity> findByStatusNot(OpportunityStatus status);

    /**
     * 匯總查詢：按階段分組，計算每個階段的商機數量和預期總金額。
     * - 只計算狀態為 OPEN 的商機，因為已結束的商機不屬於銷售漏斗。
     * - 使用我們上面定義的 FunnelStageSummary 介面來接收結果。
     * @return 每個階段的匯總數據列表
     */
    @Query("SELECT o.stage as stage, COUNT(o.opportunityId) as totalCount, SUM(o.expectedValue) as totalValue " +
            "FROM Opportunity o " +
            "WHERE o.status = 'OPEN' " +
            "GROUP BY o.stage " +
            "ORDER BY o.stage")
    List<SalesFunnelRepository> getFunnelStageSummaries();

    /**
     * [匯總查詢] 按階段統計活躍商機的數量。
     * @return 一個列表，每個元素包含一個階段名稱(name)和對應的商機數量(value)。
     */
    @Query("SELECT o.stage, COUNT(o) FROM Opportunity o WHERE o.status = 'OPEN' GROUP BY o.stage")
    List<Object[]> countOpenOpportunitiesByStage();

    /**
     * [匯總查詢] 按月份統計指定日期之後的新增商機數量。
     * @param startDate 開始統計的日期時間。
     * @return 一個列表，每個元素包含一個年月字串(name)和對應的新增商機數量(value)。
     */
    @Query("SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m'), COUNT(o) FROM Opportunity o WHERE o.createdAt >= :startDate GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m') ORDER BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m') ASC")
    List<Object[]> countNewOpportunitiesByMonth(@Param("startDate") LocalDateTime startDate);

    /**
     * 根據指定的商機狀態，計算符合條件的商機總數。
     * @param status 要計算的商機狀態。
     * @return 符合條件的商機數量。
     */
    long countByStatus(OpportunityStatus status);

    /**
     * 根據指定的商機狀態，計算符合條件的商機預期價值總和 (expectedValue)。
     * @param status 要計算的商機狀態 (例如 OpportunityStatus.WON)。
     * @return 符合條件的商機金額總和。如果沒有符合條件的商機，則返回 null。
     */
    @Query("SELECT SUM(o.expectedValue) FROM Opportunity o WHERE o.status = :status")
    BigDecimal sumExpectedValueByStatus(@Param("status") OpportunityStatus status);
}
