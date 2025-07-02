package com.example.demo.mapper;

import com.example.demo.dto.request.CustomerTagRequest;
import com.example.demo.dto.response.CustomerTagDto;
import com.example.demo.entity.CustomerTag;
import org.springframework.stereotype.Component;

@Component
public class CustomerTagMapper {
    public CustomerTag toEntity(CustomerTagRequest request) {
        if (request == null) {
            return null;
        }
        return CustomerTag.builder()
                .tagName(request.getTagName())
                .color(request.getColor())
                .build();
    }

    public CustomerTagDto toResponse(CustomerTag tag) {
        if (tag == null) {
            return null;
        }
        return CustomerTagDto.builder()
                .tagId(tag.getTagId())
                .tagName(tag.getTagName())
                .color(tag.getColor())
                .build();
    }

    public void updateEntityFromRequest(CustomerTag tag, CustomerTagRequest request) {
        if (tag == null || request == null) {
            return;
        }
        tag.setTagName(request.getTagName());
        tag.setColor(request.getColor());
    }
}
