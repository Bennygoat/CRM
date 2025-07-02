package com.example.demo.controller;

import com.example.demo.dto.request.OpportunityPriorityRequest;
import com.example.demo.dto.request.OpportunityRequest;
import com.example.demo.dto.response.OpportunityDto;
import com.example.demo.dto.response.SalesFunnelDto;
import com.example.demo.enums.OpportunityStage;
import com.example.demo.enums.OpportunityStatus;
import com.example.demo.service.OpportunityService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    /**
     * 獲取所有商機列表，支持分頁。
     * HTTP GET 請求到 /api/opportunities
     * @param pageable 分頁資訊 (page, size, sort 等參數會自動由 Spring 解析)
     * @return 包含商機回應 DTO 分頁列表的 HTTP 200 OK 響應
     */
    @GetMapping
    public ResponseEntity<Page<OpportunityDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(opportunityService.findAll(pageable));
    }

    /**
     * 根據ID獲取單個商機。
     * HTTP GET 請求到 /api/opportunities/{id}
     * @param id 路徑變數，商機的唯一識別碼
     * @return 對應的商機回應 DTO (HTTP 200 OK) 或 404 Not Found 響應
     */
    @GetMapping("/{id}")
    public ResponseEntity<OpportunityDto> getById(@PathVariable Long id) {
        return opportunityService.findById(id)
                .map(ResponseEntity::ok) // 如果找到商機，返回 HTTP 200 OK
                .orElse(ResponseEntity.notFound().build()); // 如果找不到，返回 HTTP 404 Not Found
    }

    /**
     * 創建一個新的商機。
     * HTTP POST 請求到 /api/opportunities
     * @param request 從請求體中獲取的商機請求 DTO (已啟用驗證)
     * @return 創建成功的商機回應 DTO (HTTP 201 Created)
     */
    @PostMapping
    public ResponseEntity<OpportunityDto> create(@Valid @RequestBody OpportunityRequest request) {
        OpportunityDto createdOpportunity = opportunityService.create(request);
        return new ResponseEntity<>(createdOpportunity, HttpStatus.CREATED); // 返回 HTTP 201 Created
    }

    /**
     * 更新現有的商機。
     * HTTP PUT 請求到 /api/opportunities/{id}
     * @param id 路徑變數，要更新的商機ID
     * @param request 從請求體中獲取的包含更新資訊的商機請求 DTO (已啟用驗證)
     * @return 更新後的商機回應 DTO (HTTP 200 OK) 或 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<OpportunityDto> update(@PathVariable Long id, @Valid @RequestBody OpportunityRequest request) {
        OpportunityDto updatedOpportunity = opportunityService.update(id, request);
        return ResponseEntity.ok(updatedOpportunity); // 返回 HTTP 200 OK
    }

    /**
     * 刪除一個商機。
     * HTTP DELETE 請求到 /api/opportunities/{id}
     * @param id 路徑變數，要刪除的商機ID
     * @return 204 No Content 響應 (表示成功刪除但沒有內容返回)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        opportunityService.delete(id);
        return ResponseEntity.noContent().build(); // 返回 HTTP 204 No Content
    }

    /**
     * 根據多個條件搜索商機，支持分頁。
     * HTTP GET 請求到 /api/opportunities/search
     * 請求參數範例：/api/opportunities/search?name=新&status=WON&stage=PROPOSAL&customerId=1&contactId=2&closeDateBefore=2025-12-31&page=0&size=10
     * @param name 商機名稱模糊搜尋關鍵字 (可選)
     * @param status 商機狀態枚舉 (可選)
     * @param stage 商機階段枚舉 (可選)
     * @param customerId 關聯客戶ID (可選)
     * @param contactId 關聯聯絡人ID (可選)
     * @param closeDateBefore 預計結束日期在指定日期之前 (可選，日期格式為YYYY-MM-DD，例如 2025-12-31)
     * @param pageable 分頁資訊
     * @return 符合所有條件的商機回應 DTO 分頁列表。
     */
    @GetMapping("/search")
    public ResponseEntity<Page<OpportunityDto>> searchOpportunities(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) OpportunityStatus status,
            @RequestParam(required = false) OpportunityStage stage,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long contactId,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(
                    iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate closeDateBefore,
            Pageable pageable) {

        Page<OpportunityDto> opportunities = opportunityService.searchOpportunities(name, status, stage, customerId, contactId, closeDateBefore, pageable);
        return ResponseEntity.ok(opportunities);
    }

    /**
     * 為指定商機提交評分。
     * HTTP POST 請求到 /api/opportunities/{id}/rate
     * @param id 路徑變數，要評分的商機ID。
     * @param ratingScore 請求參數，評分分數 (1-5)。
     * @return 更新評分後的商機回應 DTO (HTTP 200 OK)。
     */
    @PostMapping("/{id}/rate")
    public ResponseEntity<OpportunityDto> rateOpportunity(
            @PathVariable Long id,
            @RequestParam int ratingScore) {

        Long currentUserId = 1L;

        OpportunityDto updatedOpportunity = opportunityService.rateOpportunity(id, currentUserId, ratingScore);
        return ResponseEntity.ok(updatedOpportunity);
    }

    /**
     * PUT /api/opportunities/{id}/priority : 設定商機的優先級 (星級)
     * @param id 商機 ID
     * @param request 包含優先級的請求 Body
     * @return 更新後的商機 DTO
     */
    @PutMapping("/{id}/priority")
    public ResponseEntity<OpportunityDto> setOpportunityPriority(
            @PathVariable Long id,
            @Valid @RequestBody OpportunityPriorityRequest request) {

        OpportunityDto updatedOpportunity = opportunityService.setPriority(id, request);
        return ResponseEntity.ok(updatedOpportunity);
    }

    /**
     * GET /api/opportunities/funnel : 獲取銷售漏斗資料
     * @return 包含所有活躍銷售階段及其商機的列表
     */
    @GetMapping("/funnel")
    public ResponseEntity<List<SalesFunnelDto>> getSalesFunnel() {
        List<SalesFunnelDto> funnelData = opportunityService.getSalesFunnelData();
        return ResponseEntity.ok(funnelData);
    }
}
