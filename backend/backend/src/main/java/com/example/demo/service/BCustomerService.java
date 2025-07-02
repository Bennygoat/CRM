package com.example.demo.service;

import com.example.demo.dto.request.BCustomerRequest;
import com.example.demo.dto.response.BCustomerDto;
import com.example.demo.enums.BCustomerIndustry;
import com.example.demo.enums.BCustomerLevel;
import com.example.demo.enums.BCustomerType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface BCustomerService {

    /**
     * 以分頁的方式獲取所有客戶。
     * @param pageable 包含分頁和排序資訊的請求物件。
     * @return 包含客戶 DTO 列表和分頁資訊的 Page 物件。
     */
    Page<BCustomerDto> findAll(Pageable pageable);

    /**
     * 根據客戶的唯一 ID 尋找客戶。
     * @param id 要尋找的客戶 ID。
     * @return 包含客戶詳細資訊的 DTO。
     * @throws EntityNotFoundException 如果找不到對應 ID 的客戶。
     */
    BCustomerDto findById(Long id);

    /**
     * 根據請求資料建立一個新的客戶。
     * @param request 包含新客戶所有必要資訊的請求 DTO。
     * @return 建立成功後，包含新客戶完整資訊（含 ID）的 DTO。
     */
    BCustomerDto create(BCustomerRequest request);

    /**
     * 根據 ID 更新一個已存在的客戶資訊。
     * @param id 要更新的客戶 ID。
     * @param request 包含要更新欄位的請求 DTO。
     * @return 更新成功後，包含最新客戶資訊的 DTO。
     * @throws EntityNotFoundException 如果找不到對應 ID 的客戶。
     */
    BCustomerDto update(Long id, BCustomerRequest request);

    /**
     * 根據 ID 刪除一個客戶。
     * @param id 要刪除的客戶 ID。
     * @throws EntityNotFoundException 如果找不到對應 ID 的客戶。
     */
    void delete(Long id);

    /**
     * 根據客戶名稱進行模糊查詢（不區分大小寫）。
     * @param name 客戶名稱的部分字串。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的客戶 DTO 分頁列表。
     */
    Page<BCustomerDto> findCustomersByNameContaining(String name, Pageable pageable);

    /**
     * 根據行業查詢客戶。
     * @param industry 行業名稱 Enum。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的客戶 DTO 分頁列表。
     */
    Page<BCustomerDto> findCustomersByIndustry(BCustomerIndustry industry, Pageable pageable);

    /**
     * 根據客戶類型查詢客戶。
     * @param BCustomerType 客戶類型 Enum。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的客戶 DTO 分頁列表。
     */
    Page<BCustomerDto> findCustomersByCustomerType(BCustomerType BCustomerType, Pageable pageable);

    /**
     * 根據客戶等級查詢客戶。
     * @param BCustomerLevel 客戶等級 Enum。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的客戶 DTO 分頁列表。
     */
    Page<BCustomerDto> findCustomersByCustomerLevel(BCustomerLevel BCustomerLevel, Pageable pageable);
}
