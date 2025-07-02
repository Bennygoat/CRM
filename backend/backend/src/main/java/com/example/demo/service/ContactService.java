package com.example.demo.service;

import com.example.demo.dto.request.ContactRequest;
import com.example.demo.dto.response.ContactDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.EntityNotFoundException;

public interface ContactService {

    /**
     * 以分頁的方式獲取所有聯絡人。
     * @param pageable 包含分頁和排序資訊的請求物件。
     * @return 包含聯絡人 DTO 列表和分頁資訊的 Page 物件。
     */
    Page<ContactDto> findAll(Pageable pageable);

    /**
     * 根據聯絡人的唯一 ID 尋找聯絡人。
     * @param id 要尋找的聯絡人 ID。
     * @return 包含聯絡人詳細資訊的 DTO。
     * @throws EntityNotFoundException 如果找不到對應 ID 的聯絡人。
     */
    ContactDto findById(Long id);

    /**
     * 根據請求資料建立一個新的聯絡人。
     * @param request 包含新聯絡人所有必要資訊的請求 DTO。
     * @return 建立成功後，包含新聯絡人完整資訊（含 ID）的 DTO。
     * @throws EntityNotFoundException 如果關聯的客戶不存在。
     */
    ContactDto create(ContactRequest request);

    /**
     * 根據 ID 更新一個已存在的聯絡人資訊。
     * @param id 要更新的聯絡人 ID。
     * @param request 包含要更新欄位的請求 DTO。
     * @return 更新成功後，包含最新聯絡人資訊的 DTO。
     * @throws EntityNotFoundException 如果找不到對應 ID 的聯絡人或關聯的客戶。
     */
    ContactDto update(Long id, ContactRequest request);

    /**
     * 根據 ID 刪除一個聯絡人。
     * @param id 要刪除的聯絡人 ID。
     * @throws EntityNotFoundException 如果找不到對應 ID 的聯絡人。
     */
    void delete(Long id);

    /**
     * 根據聯絡人姓名進行模糊查詢（不區分大小寫）。
     * @param name 聯絡人名稱的部分字串。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的聯絡人 DTO 分頁列表。
     */
    Page<ContactDto> findContactsByNameContaining(String name, Pageable pageable);

    /**
     * 根據聯絡人的電子郵件查詢聯絡人。
     * @param email 聯絡人的電子郵件。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的聯絡人 DTO 分頁列表。
     */
    Page<ContactDto> findContactsByEmail(String email, Pageable pageable);

    /**
     * 根據聯絡人的電話號碼查詢聯絡人。
     * @param phone 聯絡人的電話號碼。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的聯絡人 DTO 分頁列表。
     */
    Page<ContactDto> findContactsByPhone(String phone, Pageable pageable);

    /**
     * 根據聯絡人所屬的客戶 ID 查詢聯絡人列表。
     * @param customerId 客戶的 ID。
     * @param pageable 分頁和排序資訊。
     * @return 屬於指定客戶的聯絡人 DTO 分頁列表。
     * @throws EntityNotFoundException 如果客戶不存在。
     */
    Page<ContactDto> findContactsByCustomerId(Long customerId, Pageable pageable);
}
