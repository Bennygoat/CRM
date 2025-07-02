package com.example.demo.service.impl;

import com.example.demo.dto.request.OpportunityTagRequest;
import com.example.demo.dto.response.OpportunityTagDto;
import com.example.demo.entity.OpportunityTag;
import com.example.demo.mapper.OpportunityTagMapper;
import com.example.demo.repository.OpportunityTagRepository;
import com.example.demo.service.OpportunityTagService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OpportunityTagServiceImpl implements OpportunityTagService {

    private final OpportunityTagRepository opportunityTagRepository;
    private final OpportunityTagMapper opportunityTagMapper;

    public OpportunityTagServiceImpl(OpportunityTagRepository opportunityTagRepository, OpportunityTagMapper opportunityTagMapper) {
        this.opportunityTagRepository = opportunityTagRepository;
        this.opportunityTagMapper = opportunityTagMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpportunityTagDto> findAll() {
        return opportunityTagRepository.findAll().stream()
                .map(opportunityTagMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OpportunityTagDto> findById(Long id) {
        return opportunityTagRepository.findById(id)
                .map(opportunityTagMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OpportunityTagDto> findByTagName(String tagName) {
        return opportunityTagRepository.findByTagName(tagName)
                .map(opportunityTagMapper::toResponse);
    }

    @Override
    @Transactional
    public OpportunityTagDto create(OpportunityTagRequest request) {
        if (opportunityTagRepository.findByTagName(request.getTagName()).isPresent()) {
            throw new IllegalArgumentException("商機標籤名稱 '" + request.getTagName() + "' 已存在。");
        }
        OpportunityTag tag = opportunityTagMapper.toEntity(request);
        OpportunityTag savedTag = opportunityTagRepository.save(tag);
        return opportunityTagMapper.toResponse(savedTag);
    }

    @Override
    @Transactional
    public OpportunityTagDto update(Long id, OpportunityTagRequest request) {
        OpportunityTag existingTag = opportunityTagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的商機標籤"));

        Optional<OpportunityTag> tagWithSameName = opportunityTagRepository.findByTagName(request.getTagName());
        if (tagWithSameName.isPresent() && !tagWithSameName.get().getTagId().equals(id)) {
            throw new IllegalArgumentException("商機標籤名稱 '" + request.getTagName() + "' 已被其他標籤使用。");
        }

        opportunityTagMapper.updateEntityFromRequest(existingTag, request);
        OpportunityTag updatedTag = opportunityTagRepository.save(existingTag);
        return opportunityTagMapper.toResponse(updatedTag);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!opportunityTagRepository.existsById(id)) {
            throw new EntityNotFoundException("找不到 ID 為 " + id + " 的商機標籤進行刪除");
        }
        opportunityTagRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpportunityTag> getTagsByIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return List.of();
        }

        List<OpportunityTag> tags = opportunityTagRepository.findAllById(tagIds);
        if (tags.size() != tagIds.size()) {
            Set<Long> foundIds = tags.stream().map(OpportunityTag::getTagId).collect(Collectors.toSet());
            String missingIds = tagIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw new EntityNotFoundException("找不到部分商機標籤 ID: " + missingIds);
        }
        return tags;
    }
}

