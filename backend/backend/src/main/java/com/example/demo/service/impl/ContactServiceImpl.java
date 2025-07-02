package com.example.demo.service.impl;

import com.example.demo.dto.request.ContactRequest;
import com.example.demo.dto.response.ContactDto;
import com.example.demo.entity.Contact;
import com.example.demo.entity.BCustomer;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.BCustomerRepository;
import com.example.demo.service.ContactService;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final BCustomerRepository BCustomerRepository;

    public ContactServiceImpl(ContactRepository contactRepository, BCustomerRepository BCustomerRepository) {
        this.contactRepository = contactRepository;
        this.BCustomerRepository = BCustomerRepository;
    }

    /**
     * 以分頁的方式獲取所有聯絡人。
     * @param pageable 包含分頁和排序資訊的請求物件。
     * @return 包含聯絡人 DTO 列表和分頁資訊的 Page 物件。
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ContactDto> findAll(Pageable pageable) {
        return contactRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    /**
     * 根據聯絡人的唯一 ID 尋找聯絡人。
     * @param id 要尋找的聯絡人 ID。
     * @return 包含聯絡人詳細資訊的 DTO。
     * @throws EntityNotFoundException 如果找不到對應 ID 的聯絡人。
     */
    @Override
    @Transactional(readOnly = true)
    public ContactDto findById(Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到聯絡人，ID: " + id));
        return convertToDto(contact);
    }

    /**
     * 根據請求資料建立一個新的聯絡人。
     * @param request 包含新聯絡人所有必要資訊的請求 DTO。
     * @return 建立成功後，包含新聯絡人完整資訊（含 ID）的 DTO。
     * @throws EntityNotFoundException 如果關聯的客戶不存在。
     */
    @Override
    @Transactional
    public ContactDto create(ContactRequest request) {
        BCustomer BCustomer = BCustomerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("找不到客戶，ID: " + request.getCustomerId()));

        Contact contact = new Contact();
        mapRequestToEntity(request, contact);
        contact.setBCustomer(BCustomer);

        Contact savedContact = contactRepository.save(contact);
        return convertToDto(savedContact);
    }

    /**
     * 根據 ID 更新一個已存在的聯絡人資訊。
     * @param id 要更新的聯絡人 ID。
     * @param request 包含要更新欄位的請求 DTO。
     * @return 更新成功後，包含最新聯絡人資訊的 DTO。
     * @throws EntityNotFoundException 如果找不到對應 ID 的聯絡人或關聯的客戶。
     */
    @Override
    @Transactional
    public ContactDto update(Long id, ContactRequest request) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到聯絡人，ID: " + id));

        BCustomer BCustomer = BCustomerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("找不到客戶，ID: " + request.getCustomerId()));
        contact.setBCustomer(BCustomer);

        mapRequestToEntity(request, contact);

        Contact updatedContact = contactRepository.save(contact);
        return convertToDto(updatedContact);
    }

    /**
     * 根據 ID 刪除一個聯絡人。
     * @param id 要刪除的聯絡人 ID。
     * @throws EntityNotFoundException 如果找不到對應 ID 的聯絡人。
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (!contactRepository.existsById(id)) {
            throw new EntityNotFoundException("找不到聯絡人，ID: " + id);
        }
        contactRepository.deleteById(id);
    }

    /**
     * 根據聯絡人姓名進行模糊查詢（不區分大小寫）。
     * @param name 聯絡人名稱的部分字串。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的聯絡人 DTO 分頁列表。
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ContactDto> findContactsByNameContaining(String name, Pageable pageable) {
        return contactRepository.findByContactNameContainingIgnoreCase(name, pageable)
                .map(this::convertToDto);
    }

    /**
     * 根據聯絡人的電子郵件查詢聯絡人。
     * @param email 聯絡人的電子郵件。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的聯絡人 DTO 分頁列表。
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ContactDto> findContactsByEmail(String email, Pageable pageable) {
        return contactRepository.findByEmail(email, pageable)
                .map(this::convertToDto);
    }

    /**
     * 根據聯絡人的電話號碼查詢聯絡人。
     * @param contactPhone 聯絡人的電話號碼。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的聯絡人 DTO 分頁列表。
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ContactDto> findContactsByPhone(String contactPhone, Pageable pageable) {
        return contactRepository.findByContactPhone(contactPhone, pageable)
                .map(this::convertToDto);
    }

    /**
     * 根據聯絡人所屬的客戶 ID 查詢聯絡人列表。
     * @param customerId 客戶的 ID。
     * @param pageable 分頁和排序資訊。
     * @return 屬於指定客戶的聯絡人 DTO 分頁列表。
     * @throws EntityNotFoundException 如果客戶不存在。
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ContactDto> findContactsByCustomerId(Long customerId, Pageable pageable) {
        if (!BCustomerRepository.existsById(customerId)) {
            throw new EntityNotFoundException("找不到客戶，ID: " + customerId);
        }
        return contactRepository.findBybCustomer_CustomerId(customerId, pageable)
                .map(this::convertToDto);
    }

    // ----- 輔助方法 -----
    private ContactDto convertToDto(Contact contact) {
        ContactDto dto = new ContactDto();
        dto.setContactId(contact.getContactId());
        dto.setContactName(contact.getContactName());
        dto.setTitle(contact.getContactTitle());
        dto.setEmail(contact.getEmail());
        dto.setPhone(contact.getContactPhone());
        dto.setNotes(contact.getContactNotes());
        dto.setCreatedAt(contact.getCreatedAt());
        dto.setUpdatedAt(contact.getUpdatedAt());

        if (contact.getBCustomer() != null) {
            dto.setCustomerId(contact.getBCustomer().getCustomerId());
            dto.setCustomerName(contact.getBCustomer().getCustomerName());
        }

        return dto;
    }

    /**
     * 將 ContactRequest DTO 中的資料映射到 Contact 實體。
     * 此方法主要處理簡單的屬性複製。
     * 關聯實體（如 Customer）的設定是在 Service 方法中單獨處理的，因為這涉及資料庫查詢和存在性驗證。
     * @param request 包含請求資料的 DTO。
     * @param contact 要映射的目標 Contact 實體。
     */
    private void mapRequestToEntity(ContactRequest request, Contact contact) {
        contact.setContactName(request.getContactName());
        contact.setContactTitle(request.getTitle());
        contact.setEmail(request.getEmail());
        contact.setContactPhone(request.getPhone());
        contact.setContactNotes(request.getNotes());
    }
}
