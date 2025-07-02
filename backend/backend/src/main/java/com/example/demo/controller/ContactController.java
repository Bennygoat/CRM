package com.example.demo.controller;

import com.example.demo.dto.request.ContactRequest;
import com.example.demo.dto.response.ContactDto;
import com.example.demo.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * 獲取所有聯絡人（支援分頁和排序）。
     * GET /api/contacts
     * 例如：/api/contacts?page=0&size=10&sort=contactName,asc
     * @param pageable Spring Data JPA 的 Pageable 物件，自動從請求參數解析分頁和排序資訊。
     * @return 包含聯絡人 DTO 列表和分頁資訊的 ResponseEntity。
     */
    @Operation(summary = "獲取所有聯絡人 (分頁)")
    @GetMapping
    public ResponseEntity<Page<ContactDto>> getAllContacts(Pageable pageable) {
        Page<ContactDto> contacts = contactService.findAll(pageable);
        return ResponseEntity.ok(contacts); // 返回 200 OK 狀態碼
    }

    /**
     * 根據聯絡人 ID 獲取單個聯絡人。
     * GET /api/contacts/{id}
     * @param id 聯絡人的唯一 ID。
     * @return 包含聯絡人 DTO 的 ResponseEntity。
     */
    @Operation(summary = "根據 ID 獲取單一聯絡人")
    @GetMapping("/{id}")
    public ResponseEntity<ContactDto> getContactById(@PathVariable Long id) {
        ContactDto contact = contactService.findById(id);
        return ResponseEntity.ok(contact); // 返回 200 OK 狀態碼
    }

    /**
     * 創建一個新的聯絡人。
     * POST /api/contacts
     * @param request 包含新聯絡人資訊的請求 DTO。
     * @return 包含新創建聯絡人 DTO 的 ResponseEntity，狀態碼為 201 Created。
     */
    @Operation(summary = "建立一個新聯絡人")
    @PostMapping
    public ResponseEntity<ContactDto> createContact(@Valid @RequestBody ContactRequest request) {
        ContactDto createdContact = contactService.create(request);
        return new ResponseEntity<>(createdContact, HttpStatus.CREATED); // 返回 201 Created 狀態碼
    }

    /**
     * 根據聯絡人 ID 更新一個已存在的聯絡人。
     * PUT /api/contacts/{id}
     * @param id 要更新的聯絡人 ID。
     * @param request 包含更新資訊的請求 DTO。
     * @return 包含更新後聯絡人 DTO 的 ResponseEntity。
     */
    @Operation(summary = "根據 ID 更新一個聯絡人")
    @PutMapping("/{id}")
    public ResponseEntity<ContactDto> updateContact(@PathVariable Long id, @Valid @RequestBody ContactRequest request) {
        ContactDto updatedContact = contactService.update(id, request);
        return ResponseEntity.ok(updatedContact); // 返回 200 OK 狀態碼
    }

    /**
     * 根據聯絡人 ID 刪除一個聯絡人。
     * DELETE /api/contacts/{id}
     * @param id 要刪除的聯絡人 ID。
     * @return 不包含內容的 ResponseEntity，狀態碼為 204 No Content。
     */
    @Operation(summary = "根據 ID 刪除一個聯絡人")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.delete(id);
        return ResponseEntity.noContent().build(); // 返回 204 No Content 狀態碼
    }

    /**
     * 根據聯絡人姓名進行模糊查詢（不區分大小寫）。
     * GET /api/contacts/search/by-name?name={name}&page=0&size=10
     * @param name 聯絡人名稱的部分字串。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的聯絡人 DTO 分頁列表。
     */
    @Operation(summary = "根據姓名進行模糊查詢")
    @GetMapping("/search/by-name")
    public ResponseEntity<Page<ContactDto>> searchContactsByName(
            @RequestParam String name, Pageable pageable) {
        Page<ContactDto> contacts = contactService.findContactsByNameContaining(name, pageable);
        return ResponseEntity.ok(contacts);
    }

    /**
     * 根據聯絡人的電子郵件查詢聯絡人（支援分頁）。
     * GET /api/contacts/search/by-email?email={email}&page=0&size=10
     * @param email 聯絡人的電子郵件。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的聯絡人 DTO 分頁列表。
     */
    @Operation(summary = "根據電子郵件進行查詢")
    @GetMapping("/search/by-email")
    public ResponseEntity<Page<ContactDto>> searchContactsByEmail(
            @RequestParam String email, Pageable pageable) {
        Page<ContactDto> contacts = contactService.findContactsByEmail(email, pageable);
        return ResponseEntity.ok(contacts);
    }

    /**
     * 根據聯絡人的電話號碼查詢聯絡人（支援分頁）。
     * GET /api/contacts/search/by-phone?phone={phone}&page=0&size=10
     * @param phone 聯絡人的電話號碼。
     * @param pageable 分頁和排序資訊。
     * @return 符合條件的聯絡人 DTO 分頁列表。
     */
    @Operation(summary = "根據電話號碼進行查詢")
    @GetMapping("/search/by-phone")
    public ResponseEntity<Page<ContactDto>> searchContactsByPhone(
            @RequestParam String phone, Pageable pageable) {
        Page<ContactDto> contacts = contactService.findContactsByPhone(phone, pageable);
        return ResponseEntity.ok(contacts);
    }

    /**
     * 根據聯絡人所屬的客戶 ID 查詢聯絡人列表（支援分頁）。
     * GET /api/contacts/search/by-customer/{customerId}?page=0&size=10
     * @param customerId 客戶的 ID。
     * @param pageable 分頁和排序資訊。
     * @return 屬於指定客戶的聯絡人 DTO 分頁列表。
     */
    @Operation(summary = "根據客戶 ID 進行查詢")
    @GetMapping("/search/by-customer/{customerId}")
    public ResponseEntity<Page<ContactDto>> getContactsByCustomerId(
            @PathVariable Long customerId, Pageable pageable) {
        Page<ContactDto> contacts = contactService.findContactsByCustomerId(customerId, pageable);
        return ResponseEntity.ok(contacts);
    }
}
