package com.example.demo.service.impl;

import com.example.demo.dto.request.CustomerTagRequest;
import com.example.demo.dto.response.CustomerTagDto;
import com.example.demo.entity.CustomerTag;
import com.example.demo.mapper.CustomerTagMapper;
import com.example.demo.repository.CustomerTagRepository;
import com.example.demo.service.CustomerTagService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerTagServiceImpl implements CustomerTagService {

    private final CustomerTagRepository customerTagRepository;
    private final CustomerTagMapper customerTagMapper;

    public CustomerTagServiceImpl(CustomerTagRepository customerTagRepository, CustomerTagMapper customerTagMapper) {
        this.customerTagRepository = customerTagRepository;
        this.customerTagMapper = customerTagMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerTagDto> findAll() {
        return customerTagRepository.findAll().stream()
                .map(customerTagMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerTagDto> findById(Long id) {
        return customerTagRepository.findById(id)
                .map(customerTagMapper::toResponse);
    }

    @Override
    @Transactional
    public CustomerTagDto create(CustomerTagRequest request) {
        CustomerTag tag = customerTagMapper.toEntity(request);
        CustomerTag savedTag = customerTagRepository.save(tag);
        return customerTagMapper.toResponse(savedTag);
    }

    @Override
    @Transactional
    public CustomerTagDto update(Long id, CustomerTagRequest request) {
        CustomerTag existingTag = customerTagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的客戶標籤"));

        customerTagMapper.updateEntityFromRequest(existingTag, request);
        CustomerTag updatedTag = customerTagRepository.save(existingTag);
        return customerTagMapper.toResponse(updatedTag);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!customerTagRepository.existsById(id)) {
            throw new EntityNotFoundException("找不到 ID 為 " + id + " 的客戶標籤進行刪除");
        }
        customerTagRepository.deleteById(id);
    }
}
