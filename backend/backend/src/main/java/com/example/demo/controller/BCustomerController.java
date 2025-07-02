package com.example.demo.controller;

import com.example.demo.dto.request.BCustomerRequest;
import com.example.demo.dto.response.BCustomerDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.impl.BCustomerServiceImpl;


@RestController
@RequestMapping("/api/customers")
public class BCustomerController {

    private final BCustomerServiceImpl customerService;

    public BCustomerController(BCustomerServiceImpl customerService) {
        this.customerService = customerService;
    }

    /**
     * 獲取所有客戶 (分頁)
     * GET /api/customers?page=0&size=10&sort=customerName,asc
     * @param pageable Spring 自動傳入的分頁與排序參數
     * @return 客戶分頁資料
     */
    @Operation(summary = "獲取所有客戶 (分頁)")
    @GetMapping
    public ResponseEntity<Page<BCustomerDto>> getAll(Pageable pageable) {
        Page<BCustomerDto> customers = customerService.findAll(pageable);
        return ResponseEntity.ok(customers);
    }

    /**
     * 根據 ID 獲取單一客戶
     * GET /api/customers/{id}
     * @param id 客戶 ID
     * @return 找到的客戶 DTO
     */
    @Operation(summary = "根據 ID 獲取單一客戶")
    @GetMapping("/{id}")
    public ResponseEntity<BCustomerDto> getById(@PathVariable Long id) {
        BCustomerDto BCustomerDto = customerService.findById(id);
        return ResponseEntity.ok(BCustomerDto);
    }

    /**
     * 建立一個新客戶
     * POST /api/customers
     * @param request 包含客戶資料的請求 DTO
     * @return 建立成功後的新客戶 DTO，HTTP 狀態為 201 Created
     */
    @Operation(summary = "建立一個新客戶")
    @PostMapping
    public ResponseEntity<BCustomerDto> create(@Valid @RequestBody BCustomerRequest request) {
        BCustomerDto createdCustomer = customerService.create(request);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    /**
     * 根據 ID 更新一個客戶
     * PUT /api/customers/{id}
     * @param id 要更新的客戶 ID
     * @param request 包含更新資料的請求 DTO
     * @return 更新成功後的客戶 DTO
     */
    @Operation(summary = "根據 ID 更新一個客戶")
    @PutMapping("/{id}")
    public ResponseEntity<BCustomerDto> update(@PathVariable Long id, @Valid @RequestBody BCustomerRequest request) {
        BCustomerDto updatedCustomer = customerService.update(id, request);
        return ResponseEntity.ok(updatedCustomer);
    }

    /**
     * 根據 ID 刪除一個客戶
     * DELETE /api/customers/{id}
     * @param id 要刪除的客戶 ID
     * @return HTTP 狀態 204 No Content
     */
    @Operation(summary = "根據 ID 刪除一個客戶")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
