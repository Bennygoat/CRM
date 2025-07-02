package com.example.demo.controller;

import com.example.demo.dto.request.CustomerTagRequest;
import com.example.demo.dto.response.CustomerTagDto;
import com.example.demo.service.CustomerTagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-tags")
public class CustomerTagController {

    private final CustomerTagService customerTagService;

    public CustomerTagController(CustomerTagService customerTagService) {
        this.customerTagService = customerTagService;
    }


    /**
     * 獲取所有客戶標籤列表。
     * HTTP GET 請求到 /api/customer-tags
     * @return 包含所有客戶標籤 DTO 的列表。
     */
    @GetMapping
    public ResponseEntity<List<CustomerTagDto>> getAllCustomerTags() {
        List<CustomerTagDto> tags = customerTagService.findAll();
        return ResponseEntity.ok(tags);
    }

    /**
     * 根據ID獲取單個客戶標籤。
     * HTTP GET 請求到 /api/customer-tags/{id}
     * @param id 標籤的唯一識別碼。
     * @return 對應的標籤 DTO (HTTP 200 OK) 或 404 Not Found 響應。
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerTagDto> getCustomerTagById(@PathVariable Long id) { // *** 修正：回傳類型為 CustomerTagDto ***
        return customerTagService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 創建一個新的客戶標籤。
     * HTTP POST 請求到 /api/customer-tags
     * @param request 包含標籤名稱和顏色的請求 DTO。
     * @return 創建成功的標籤 DTO (HTTP 201 Created)。
     */
    @PostMapping
    public ResponseEntity<CustomerTagDto> createCustomerTag(@Valid @RequestBody CustomerTagRequest request) { // *** 修正：請求參數為 CustomerTagRequest ***
        CustomerTagDto createdTag = customerTagService.create(request); // *** 修正：回傳類型為 CustomerTagDto ***
        return new ResponseEntity<>(createdTag, HttpStatus.CREATED);
    }

    /**
     * 更新現有的客戶標籤。
     * HTTP PUT 請求到 /api/customer-tags/{id}
     * @param id 要更新的標籤 ID。
     * @param request 包含更新資訊的請求 DTO。
     * @return 更新後的標籤 DTO (HTTP 200 OK)。
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerTagDto> updateCustomerTag(@PathVariable Long id, @Valid @RequestBody CustomerTagRequest request) {
        CustomerTagDto updatedTag = customerTagService.update(id, request);
        return ResponseEntity.ok(updatedTag);
    }

    /**
     * 刪除一個客戶標籤。
     * HTTP DELETE 請求到 /api/customer-tags/{id}
     * @param id 要刪除的標籤 ID。
     * @return 204 No Content 響應。
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomerTag(@PathVariable Long id) {
        customerTagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
