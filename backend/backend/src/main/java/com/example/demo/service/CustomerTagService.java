package com.example.demo.service;

import com.example.demo.dto.request.CustomerTagRequest;
import com.example.demo.dto.response.CustomerTagDto;

import java.util.List;
import java.util.Optional;

public interface CustomerTagService {
    /**
     * 查找所有客戶標籤。
     * @return 包含所有客戶標籤 DTO 的列表。
     */
    List<CustomerTagDto> findAll();

    /**
     * 根據 ID 查找客戶標籤。
     * @param id 標籤的唯一識別碼。
     * @return 包含客戶標籤 DTO 的 Optional。
     */
    Optional<CustomerTagDto> findById(Long id);

    /**
     * 創建一個新的客戶標籤。
     * @param request 包含標籤名稱和顏色的請求 DTO。
     * @return 創建成功的客戶標籤 DTO。
     */
    CustomerTagDto create(CustomerTagRequest request);

    /**
     * 更新一個現有的客戶標籤。
     * @param id 要更新的標籤 ID。
     * @param request 包含更新資訊的請求 DTO。
     * @return 更新後的客戶標籤 DTO。
     */
    CustomerTagDto update(Long id, CustomerTagRequest request);

    /**
     * 根據 ID 刪除一個客戶標籤。
     * @param id 要刪除的標籤 ID。
     */
    void delete(Long id);
}
