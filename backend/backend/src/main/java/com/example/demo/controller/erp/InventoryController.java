package com.example.demo.controller.erp;


import com.example.demo.dto.erp.*;
import com.example.demo.entity.Inventory;
import com.example.demo.entity.InventoryAdjustment;
import com.example.demo.entity.SalesShipment;
import com.example.demo.service.erp.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name="庫存管理API(Inventory Management)",
        description = "包含採購商品收取、銷貨單出貨、庫存調整 ")
public class InventoryController {

    private final InventoryService inventoryService;



    @PostMapping("/purchase-orders/{orderId}/receive")
    @Operation(summary = "收取商品", description = "使用採購單收取採購的商品，並更新庫存")
    @ApiResponse(responseCode = "200", description = "採購商品收取成功！")
    @ApiResponse(responseCode = "404", description = "找不到採購單")
    public ResponseEntity<Void> receivePurchaseOrder(
            @Parameter(description = "欲收取的採購單") @PathVariable Long orderId
            // @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails // Example for real auth
    ) {
// TODO(joshkuei): Replace hardcoded user ID with the authenticated user from the security context.
        // Example using Spring Security:
        // Long currentUserId = userDetails.getId();
        Long currentUserId = 1L;

        inventoryService.receivePurchaseOrder(orderId, currentUserId);


        return ResponseEntity.ok().build();
    }

//    @GetMapping("/product/{productId}") //TODO(joshkuei): replaced by the method below.
//    @Operation(summary = "Get Inventory by Product", description = "Fetches inventory records for a given product across all warehouses.")
//    public ResponseEntity<List<Inventory>> getInventoryByProductId(
//            @Parameter(description = "ID of the product") @PathVariable Long productId) {
//        List<Inventory> inventoryList = inventoryService.getInventoryByProductId(productId);
//        return ResponseEntity.ok(inventoryList);
//    }

    @GetMapping
    @Operation(summary = "查詢庫存", description = "以商品ID、倉庫ID，以及分頁查詢庫存")
    @ApiResponse(responseCode = "200", description = "查詢庫存成功！")
    @ApiResponse(responseCode = "404", description = "找不到")
    public ResponseEntity<Page<InventoryViewDTO>> searchInventories(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long warehouseId,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<InventoryViewDTO> inventoryPage = inventoryService.searchInventories(productId, warehouseId, pageable);

        return ResponseEntity.ok(inventoryPage);
    }

    @PostMapping("/adjust")
    @Operation(summary = "手動調整庫存",
            description = "執行庫存的手動調整，例如盤點或報損用途。")
    public ResponseEntity<InventoryViewDTO> adjustInventory(
            @Valid @RequestBody InventoryAdjustmentDTO adjustmentDTO) {
        // TODO(joshkuei): The user ID for the adjustment should ideally come from the security context,
        Long operatorId = adjustmentDTO.getUserId() != null ? adjustmentDTO.getUserId() : 1L;
        Inventory updatedInventoryEntity = inventoryService.adjustInventory(
                adjustmentDTO.getProductId(),
                adjustmentDTO.getWarehouseId(),
                adjustmentDTO.getQuantity(),
                adjustmentDTO.getUnitCost(),
                adjustmentDTO.getMovementType(),
                adjustmentDTO.getDocumentType(),
                adjustmentDTO.getDocumentId(),
                adjustmentDTO.getDocumentItemId(),
                operatorId
        );
        InventoryViewDTO responseDTO = InventoryViewDTO.fromEntity(updatedInventoryEntity);

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/adjustments")
    @Operation(summary = "建立庫存調整單",
            description = "建立一張包含多筆調整明細的正式庫存調整單")
    public ResponseEntity<InventoryAdjustmentResponseDTO> createInventoryAdjustment(@Valid @RequestBody InventoryAdjustmentCreateDTO dto) {
        Long currentUserId = 1L; // TODO(joshkuei): MUST From Security Context
//        InventoryAdjustment adjustment = inventoryService.createInventoryAdjustment(dto, currentUserId);
//        return ResponseEntity.status(HttpStatus.CREATED).body(adjustment);
        // The service still returns the entity
        InventoryAdjustment adjustmentEntity = inventoryService.createInventoryAdjustment(dto, currentUserId);
        // Map the entity to our new DTO before returning
        InventoryAdjustmentResponseDTO responseDTO = InventoryAdjustmentResponseDTO.fromEntity(adjustmentEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }


    @PostMapping("/sales-orders/{orderId}/ship")
    @Operation(summary = "銷貨單出貨", description = "針對已確認的銷售訂單建立出貨記錄，並自動扣除相應庫存數量")
    @ApiResponse(responseCode = "201", description = "銷貨單出貨成功，已建立出貨單")
    @ApiResponse(responseCode = "404", description = "找不到銷貨單或倉庫")
    @ApiResponse(responseCode = "409", description = "無法出貨（例如：訂單狀態錯誤或庫存不足)")
    public ResponseEntity<SalesShipmentDTO> shipSalesOrder(
            @Parameter(description = "欲出貨的銷貨單ID") @PathVariable Long orderId,
            @Valid @RequestBody ShipmentRequestDTO requestDTO
            // @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails // Example
    ) {
        // TODO: Replace hardcoded user ID with the authenticated user from the security context.
        // Example using Spring Security:
        // Long currentUserId = userDetails.getId();
        Long currentUserId = 1L;

        SalesShipment shipmentEntity = inventoryService.shipSalesOrder(orderId, requestDTO.getWarehouseId(), currentUserId);

        SalesShipmentDTO shipmentDTO = SalesShipmentDTO.fromEntity(shipmentEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(shipmentDTO); //TODO(josh): Debug
    }
}
