package com.example.demo.service.impl;

import com.example.demo.dto.request.BCustomerRequest;
import com.example.demo.dto.response.BCustomerDto;
import com.example.demo.entity.BCustomer;


import com.example.demo.entity.CustomerTag;
import com.example.demo.enums.BCustomerIndustry;
import com.example.demo.enums.BCustomerLevel;
import com.example.demo.enums.BCustomerType;
import com.example.demo.repository.BCustomerRepository;
import com.example.demo.repository.CustomerTagRepository;
import com.example.demo.service.BCustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class BCustomerServiceImpl implements BCustomerService {

    private final BCustomerRepository BCustomerRepository;
    private final CustomerTagRepository customerTagRepository;

    public BCustomerServiceImpl(BCustomerRepository BCustomerRepository,
                                CustomerTagRepository customerTagRepository) {
        this.BCustomerRepository = BCustomerRepository;
        this.customerTagRepository = customerTagRepository;
    }

    /**
     * 以分頁的方式獲取所有客戶。
     * @param pageable 包含分頁和排序資訊的請求物件。
     * @return 包含客戶 DTO 列表和分頁資訊的 Page 物件。
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BCustomerDto> findAll(Pageable pageable) {
        return BCustomerRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    /**
     * 根據客戶的唯一 ID 尋找客戶。
     * @param id 要尋找的客戶 ID。
     * @return 包含客戶詳細資訊的 DTO。
     * @throws EntityNotFoundException 如果找不到對應 ID 的客戶。
     */
    @Override
    @Transactional(readOnly = true)
    public BCustomerDto findById(Long id) {
        BCustomer BCustomer = BCustomerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到客戶，ID: " + id));
        return convertToDto(BCustomer);
    }

    /**
     * 根據請求資料建立一個新的客戶。
     * @param request 包含新客戶所有必要資訊的請求 DTO。
     * @return 建立成功後，包含新客戶完整資訊（含 ID）的 DTO。
     */
    @Override
    @Transactional
    public BCustomerDto create(BCustomerRequest request) {
        BCustomer BCustomer = new BCustomer();
        mapRequestToEntity(request, BCustomer);

        if (BCustomer.getBCustomerType() == null) {
            BCustomer.setBCustomerType(BCustomerType.REGULAR); // 預設客戶類型
        }
        if (BCustomer.getBCustomerLevel() == null) {
            BCustomer.setBCustomerLevel(BCustomerLevel.BRONZE); // 預設客戶等級
        }
        if (BCustomer.getIndustry() == null) {
            BCustomer.setIndustry(BCustomerIndustry.OTHER); // 預設客戶行業
        }

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<CustomerTag> tags = customerTagRepository.findAllById(request.getTagIds());
            if (tags.size() != request.getTagIds().size()) {
                throw new EntityNotFoundException("一個或多個客戶標籤ID不存在。");
            }
            BCustomer.setTags(new HashSet<>(tags));
        }

        BCustomer savedBCustomer = BCustomerRepository.save(BCustomer);
        return convertToDto(savedBCustomer);
    }

    /**
     * 根據 ID 更新一個已存在的客戶資訊。
     * @param id 要更新的客戶 ID。
     * @param request 包含要更新欄位的請求 DTO。
     * @return 更新成功後，包含最新客戶資訊的 DTO。
     * @throws EntityNotFoundException 如果找不到對應 ID 的客戶。
     */
    @Override
    @Transactional
    public BCustomerDto update(Long id, BCustomerRequest request) {
        BCustomer BCustomer = BCustomerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到客戶，ID: " + id));

        mapRequestToEntity(request, BCustomer);

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<CustomerTag> tags = customerTagRepository.findAllById(request.getTagIds());
            if (tags.size() != request.getTagIds().size()) {
                throw new EntityNotFoundException("一個或多個客戶標籤ID不存在。");
            }
            BCustomer.setTags(new HashSet<>(tags));
        } else {
            BCustomer.getTags().clear();
        }

        BCustomer updatedBCustomer = BCustomerRepository.save(BCustomer);
        return convertToDto(updatedBCustomer);
    }

    /**
     * 根據 ID 刪除一個客戶。
     * @param id 要刪除的客戶 ID。
     * @throws EntityNotFoundException 如果找不到對應 ID 的客戶。
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (!BCustomerRepository.existsById(id)) {
            throw new EntityNotFoundException("找不到客戶，ID: " + id);
        }
        BCustomerRepository.deleteById(id);
    }

    /**
     * 根據客戶名稱進行模糊查詢（不區分大小寫）。
     * @param name 客戶名稱的部分字串。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的客戶 DTO 分頁列表。
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BCustomerDto> findCustomersByNameContaining(String name, Pageable pageable) {
        return BCustomerRepository.findByCustomerNameContainingIgnoreCase(name, pageable)
                .map(this::convertToDto);
    }

    /**
     * 根據行業查詢客戶。
     * @param industry 行業名稱 Enum。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的客戶 DTO 分頁列表。
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BCustomerDto> findCustomersByIndustry(BCustomerIndustry industry, Pageable pageable) {
        return BCustomerRepository.findByIndustry(industry, pageable)
                .map(this::convertToDto);
    }

    /**
     * 根據客戶類型查詢客戶。
     * @param BCustomerType 客戶類型 Enum。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的客戶 DTO 分頁列表。
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BCustomerDto> findCustomersByCustomerType(BCustomerType BCustomerType, Pageable pageable) {
        return BCustomerRepository.findByBCustomerType(BCustomerType, pageable)
                .map(this::convertToDto);
    }

    /**
     * 根據客戶等級查詢客戶。
     * @param BCustomerLevel 客戶等級 Enum。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的客戶 DTO 分頁列表。
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BCustomerDto> findCustomersByCustomerLevel(BCustomerLevel BCustomerLevel, Pageable pageable) {
        return BCustomerRepository.findByBCustomerLevel(BCustomerLevel, pageable)
                .map(this::convertToDto);
    }

    // ----- 輔助方法 -----
    /**
     * 將 Customer 實體轉換為 CustomerDto。
     * 此方法用於向前端返回資料，只包含前端所需的資訊。
     * @param BCustomer 要轉換的 Customer 實體。
     * @return 轉換後的 CustomerDto。
     */
    private BCustomerDto convertToDto(BCustomer BCustomer) {
        BCustomerDto dto = new BCustomerDto();
        dto.setCustomerId(BCustomer.getCustomerId());
        dto.setCustomerName(BCustomer.getCustomerName());
        dto.setIndustry(BCustomer.getIndustry());
        dto.setBCustomerType(BCustomer.getBCustomerType());
        dto.setBCustomerLevel(BCustomer.getBCustomerLevel());
        dto.setCustomerAddress(BCustomer.getAddress());
        dto.setCustomerTel(BCustomer.getTel());
        dto.setCustomerEmail(BCustomer.getEmail());
        dto.setCreatedAt(BCustomer.getCreatedAt());
        dto.setUpdatedAt(BCustomer.getUpdatedAt());

        List<Long> tagIds = (BCustomer.getTags() != null && !BCustomer.getTags().isEmpty())
                ? BCustomer.getTags().stream()
                .map(CustomerTag::getTagId)
                .collect(Collectors.toList())
                : List.of();
        dto.setTagIds(tagIds);
        return dto;
    }

    /**
     * 將 CustomerRequest DTO 中的資料映射到 Customer 實體。
     * 此方法主要處理簡單的屬性複製。
     * @param request 包含請求資料的 DTO。
     * @param BCustomer 要映射的目標 Customer 實體。
     */
    private void mapRequestToEntity(BCustomerRequest request, BCustomer BCustomer) {
        BCustomer.setCustomerName(request.getCustomerName());
        BCustomer.setIndustry(request.getIndustry());
        BCustomer.setBCustomerType(request.getBCustomerType());
        BCustomer.setBCustomerLevel(request.getBCustomerLevel());
        BCustomer.setAddress(request.getCustomerAddress());
        BCustomer.setTel(request.getCustomerTel());
        BCustomer.setEmail(request.getCustomerEmail());
    }
}
