package com.example.demo.controller;

import com.example.demo.dto.request.OpportunityTagRequest;
import com.example.demo.dto.response.OpportunityTagDto;
import com.example.demo.service.OpportunityTagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api//api/opportunity-tags")
public class OpportunityTagController {

    private final OpportunityTagService opportunityTagService;

    public OpportunityTagController(OpportunityTagService opportunityTagService) {
        this.opportunityTagService = opportunityTagService;
    }

    /**
     * 獲取所有商機標籤列表。
     * HTTP GET 請求到 /api/opportunity-tags
     * @return 包含所有商機標籤 DTO 的列表。
     */
    @GetMapping
    public ResponseEntity<List<OpportunityTagDto>> getAllOpportunityTags() {
        List<OpportunityTagDto> tags = opportunityTagService.findAll();
        return ResponseEntity.ok(tags);
    }

    /**
     * 根據ID獲取單個商機標籤。
     * HTTP GET 請求到 /api/opportunity-tags/{id}
     * @param id 標籤的唯一識別碼。
     * @return 對應的標籤 DTO (HTTP 200 OK) 或 404 Not Found 響應。
     */
    @GetMapping("/{id}")
    public ResponseEntity<OpportunityTagDto> getOpportunityTagById(@PathVariable Long id) {
        return opportunityTagService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 創建一個新的商機標籤。
     * HTTP POST 請求到 /api/opportunity-tags
     * @param request 包含標籤名稱和顏色的請求 DTO。
     * @return 創建成功的標籤 DTO (HTTP 201 Created)。
     */
    @PostMapping
    public ResponseEntity<OpportunityTagDto> createOpportunityTag(@Valid @RequestBody OpportunityTagRequest request) {
        OpportunityTagDto createdTag = opportunityTagService.create(request);
        return new ResponseEntity<>(createdTag, HttpStatus.CREATED);
    }

    /**
     * 更新現有的商機標籤。
     * HTTP PUT 請求到 /api/opportunity-tags/{id}
     * @param id 要更新的標籤 ID。
     * @param request 包含更新資訊的請求 DTO。
     * @return 更新後的標籤 DTO (HTTP 200 OK)。
     */
    @PutMapping("/{id}")
    public ResponseEntity<OpportunityTagDto> updateOpportunityTag(@PathVariable Long id, @Valid @RequestBody OpportunityTagRequest request) {
        OpportunityTagDto updatedTag = opportunityTagService.update(id, request);
        return ResponseEntity.ok(updatedTag);
    }

    /**
     * 刪除一個商機標籤。
     * HTTP DELETE 請求到 /api/opportunity-tags/{id}
     * @param id 要刪除的標籤 ID。
     * @return 204 No Content 響應。
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOpportunityTag(@PathVariable Long id) {
        opportunityTagService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
