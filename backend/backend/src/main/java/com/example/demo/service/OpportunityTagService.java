package com.example.demo.service;

import com.example.demo.dto.request.OpportunityTagRequest;
import com.example.demo.dto.response.OpportunityTagDto;
import com.example.demo.entity.OpportunityTag;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public interface OpportunityTagService {

    /**
     * 獲取所有標籤。
     * @return 標籤 DTO 列表。
     */
    List<OpportunityTagDto> findAll();

    /**
     * 根據 ID 查找標籤。
     * @param id 標籤 ID。
     * @return 標籤 DTO 的 Optional。
     */
    Optional<OpportunityTagDto> findById(Long id);

    /**
     * 根據名稱查找標籤。
     * @param tagName 標籤名稱。
     * @return 標籤 DTO 的 Optional。
     */
    Optional<OpportunityTagDto> findByTagName(String tagName);

    /**
     * 創建新標籤。
     * @param request 標籤請求 DTO。
     * @return 創建後的標籤 DTO。
     */
    OpportunityTagDto create(OpportunityTagRequest request);

    /**
     * 更新現有標籤。
     * @param id 標籤 ID。
     * @param request 標籤請求 DTO。
     * @return 更新後的標籤 DTO。
     * @throws EntityNotFoundException 如果標籤不存在。
     */
    OpportunityTagDto update(Long id, OpportunityTagRequest request);

    /**
     * 根據 ID 刪除標籤。
     * @param id 標籤 ID。
     * @throws EntityNotFoundException 如果標籤不存在。
     */
    void delete(Long id);

    /**
     * 根據 ID 列表獲取所有標籤實體。
     * @param tagIds 標籤 ID 列表。
     * @return 標籤實體列表。
     * @throws EntityNotFoundException 如果列表中有任何 ID 對應的標籤不存在。
     */
    List<OpportunityTag> getTagsByIds(List<Long> tagIds);
}
