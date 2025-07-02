package com.example.demo.mapper;

import com.example.demo.dto.request.OpportunityTagRequest;
import com.example.demo.dto.response.OpportunityTagDto;
import com.example.demo.entity.OpportunityTag;
import org.springframework.stereotype.Component;


@Component
public class OpportunityTagMapper {

    /**
     * 將 OpportunityTag 實體轉換為 TagDto。
     * @param tag 要轉換的 OpportunityTag 實體。
     * @return 轉換後的 TagDto。
     */
    public OpportunityTagDto toResponse(OpportunityTag tag) {
        if (tag == null) {
            return null;
        }
        return OpportunityTagDto.builder()
                .tagId(tag.getTagId())
                .tagName(tag.getTagName())
                .color(tag.getColor())
                .build();
    }

    /**
     * 將 TagRequest DTO 轉換為 OpportunityTag 實體 (用於創建)。
     * @param request 標籤請求 DTO。
     * @return 轉換後的 OpportunityTag 實體。
     */
    public OpportunityTag toEntity(OpportunityTagRequest request) {
        if (request == null) {
            return null;
        }
        return OpportunityTag.builder()
                .tagName(request.getTagName())
                .color(request.getColor())
                .build();
    }

    /**
     * 更新現有 OpportunityTag 實體的屬性。
     * @param tag 要更新的現有 OpportunityTag 實體。
     * @param request 包含更新數據的 TagRequest DTO。
     */
    public void updateEntityFromRequest(OpportunityTag tag, OpportunityTagRequest request) {
        if (tag == null || request == null) {
            return;
        }
        tag.setTagName(request.getTagName());
        tag.setColor(request.getColor());
    }
}
