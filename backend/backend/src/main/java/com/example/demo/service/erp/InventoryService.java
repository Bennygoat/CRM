package com.example.demo.service.erp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.example.demo.dto.erp.InventoryAdjustmentCreateDTO;
import com.example.demo.dto.erp.InventoryAdjustmentDetailCreateDTO;
import com.example.demo.dto.erp.InventoryViewDTO;
import com.example.demo.entity.*;
import com.example.demo.entity.Inventory;
import com.example.demo.entity.InventoryAdjustment;
import com.example.demo.entity.InventoryAdjustmentDetail;
import com.example.demo.entity.InventoryMovement;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.SalesOrderStatus;
import com.example.demo.event.SalesOrderShippedEvent;
import com.example.demo.exception.DataConflictException;
import com.example.demo.repository.*;
import com.example.demo.specification.InventorySpecification;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.MovementType;
import com.example.demo.enums.PurchaseOrderStatus;
import com.example.demo.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class InventoryService {


    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final InventoryAdjustmentRepository adjustmentRepository;

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final UserRepository userRepository;

    private final SalesOrderRepository salesOrderRepository;
    private final SalesShipmentRepository salesShipmentRepository;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public void receivePurchaseOrder(Long orderId, Long receivingUserId) {

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到ID為 " + orderId + " 的採購單"));

//        User receivingUser = userRepository.findById(receivingUserId)
//                .orElseThrow(() -> new ResourceNotFoundException("找不到ID為 " + receivingUserId + " 的使用者"));


        if (purchaseOrder.getStatus() == PurchaseOrderStatus.RECEIVED
                || purchaseOrder.getStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new IllegalStateException("採購單 " + purchaseOrder.getOrderNumber() + " 已完成或已取消，無法執行入庫。");
        }


        for (PurchaseOrderDetail detail : purchaseOrder.getDetails()) {
//            this.adjustInventory(
//                    detail.getProduct().getProductId(),
//                    detail.getWarehouse().getWarehouseId(),
//                    detail.getQuantity(),
//                    detail.getUnitPrice(),
//                    MovementType.PURCHASE_IN,
//                    "PURCHASE_ORDER",
//                    purchaseOrder.getPurchaseOrderId(),
//                    detail.getItemId(),
//                    receivingUserId);
//
//        }
            //TODO(joshkuei): Call the more specific method for handling PO line receipts
            this.receivePurchaseOrderLine(detail, receivingUserId);
        }


        purchaseOrder.setStatus(PurchaseOrderStatus.RECEIVED);
        purchaseOrder.setUpdatedAt(LocalDateTime.now());
        purchaseOrder.setUpdatedBy(receivingUserId);
        purchaseOrderRepository.save(purchaseOrder);
    }

    //TODO(joshkuei): Helper to get or create inventory record
    private Inventory getOrCreateInventory(Product product, Warehouse warehouse, Long userId) {
        return inventoryRepository.findByProductAndWarehouse(product, warehouse)
                .orElseGet(() -> {
                    Inventory newInventory = new Inventory();
                    newInventory.setProduct(product);
                    newInventory.setWarehouse(warehouse);
                    newInventory.setCurrentStock(BigDecimal.ZERO);
                    newInventory.setUnitsOnOrder(BigDecimal.ZERO);
                    newInventory.setUnitsAllocated(BigDecimal.ZERO);
                    newInventory.setAverageCost(BigDecimal.ZERO);
                    newInventory.setCreatedAt(LocalDateTime.now());
                    newInventory.setCreatedBy(userId);
                    newInventory.setUpdatedAt(LocalDateTime.now());
                    newInventory.setUpdatedBy(userId);
                    return inventoryRepository.save(newInventory); //TODO(joshkuei): Save immediately to ensure it has an ID if needed later in same transaction
                });
    }

    @Transactional
    public void receivePurchaseOrderLine(PurchaseOrderDetail detail, Long receivingUserId) {
        Product product = detail.getProduct();
        Warehouse warehouse = warehouseRepository.findById(detail.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found for ID: " + detail.getWarehouseId()));

        Inventory inventory = getOrCreateInventory(product, warehouse, receivingUserId);

        // Increase current stock
        inventory.setCurrentStock(inventory.getCurrentStock().add(detail.getQuantity()));
        // Decrease units on order
        inventory.setUnitsOnOrder(inventory.getUnitsOnOrder().subtract(detail.getQuantity()));
        if (inventory.getUnitsOnOrder().compareTo(BigDecimal.ZERO) < 0) {
            // This case should ideally not happen if unitsOnOrder was managed correctly when PO was created/confirmed
            inventory.setUnitsOnOrder(BigDecimal.ZERO);
        }

        // Recalculate average cost (simplified from adjustInventory)
        BigDecimal oldStock = inventory.getCurrentStock().subtract(detail.getQuantity()); // Stock before this receipt line
        BigDecimal oldTotalValue = oldStock.multiply(inventory.getAverageCost());
        BigDecimal incomingValue = detail.getQuantity().multiply(detail.getUnitPrice());
        if (inventory.getCurrentStock().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal newAverageCost = (oldTotalValue.add(incomingValue)).divide(inventory.getCurrentStock(), 4, RoundingMode.HALF_UP);
            inventory.setAverageCost(newAverageCost);
        } else if (detail.getQuantity().compareTo(BigDecimal.ZERO) > 0) { // If current stock becomes non-zero only due to this receipt
            inventory.setAverageCost(detail.getUnitPrice());
        } else {
            inventory.setAverageCost(BigDecimal.ZERO);
        }
//TODO(josh): Move to AuditingEntityListener
//        inventory.setUpdatedAt(LocalDateTime.now());
//        inventory.setUpdatedBy(receivingUserId);
        inventoryRepository.save(inventory);

        createInventoryMovement(
                product, warehouse, MovementType.PURCHASE_IN.name(), detail.getQuantity(), detail.getUnitPrice(),
                inventory.getCurrentStock(), "PURCHASE_ORDER", detail.getPurchaseOrder().getPurchaseOrderId(), detail.getItemId(),
                userRepository.getReferenceById(receivingUserId)
        );
    }


    public List<Inventory> getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProduct_ProductId(productId);
    }


    public BigDecimal getProductStock(Long productId) {

        // This method sums current stock across all warehouses for a given product.
        List<Inventory> inventories = inventoryRepository.findByProduct_ProductId(productId);
        return inventories.stream()
                .map(Inventory::getCurrentStock)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // New method as per plan
    public BigDecimal getAvailableStock(Long productId, Long warehouseId) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProduct_ProductIdAndWarehouse_WarehouseId(productId, warehouseId);
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            return inventory.getCurrentStock().subtract(inventory.getUnitsAllocated());
        }
        return BigDecimal.ZERO;
    }

    // Overloaded for product total available stock across all warehouses
    public BigDecimal getAvailableStock(Long productId) {
        List<Inventory> inventories = inventoryRepository.findByProduct_ProductId(productId);
        BigDecimal totalCurrentStock = inventories.stream()
                .map(Inventory::getCurrentStock)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalUnitsAllocated = inventories.stream()
                .map(Inventory::getUnitsAllocated)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalCurrentStock.subtract(totalUnitsAllocated);
    }



    @Transactional
    public void reserveStock(Long productId, Long warehouseId, BigDecimal quantityToReserve,
                             String documentType, Long documentId, Long documentDetailId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到商品ID: " + productId+"的商品"));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到倉庫ID為: " + warehouseId+"的倉庫"));
        User user = userRepository.getReferenceById(userId);

        Inventory inventory = getOrCreateInventory(product, warehouse, userId);

        BigDecimal currentStock = inventory.getCurrentStock();
        BigDecimal currentAllocated = inventory.getUnitsAllocated();
        BigDecimal availableToReserve = currentStock.subtract(currentAllocated);

        if (availableToReserve.compareTo(quantityToReserve) < 0) {
            throw new DataConflictException("Insufficient available stock for product " + product.getName() +
                    " in warehouse " + warehouse.getName() +
                    ". Available: " + availableToReserve + ", Requested: " + quantityToReserve);
        }

        inventory.setUnitsAllocated(currentAllocated.add(quantityToReserve));
//        inventory.setUpdatedAt(LocalDateTime.now()); //TODO(josh): move to AuditingEntityListener
//        inventory.setUpdatedBy(userId);
        inventoryRepository.save(inventory);

        createInventoryMovement(
                product, warehouse, MovementType.STOCK_RESERVE.name(), quantityToReserve,
                inventory.getAverageCost(), // Use current average cost for informational value of reservation
                inventory.getCurrentStock(), // Current physical stock doesn't change on reservation
                documentType, documentId, documentDetailId, user
        );
    }

    @Transactional
    public void releaseStock(Long productId, Long warehouseId, BigDecimal quantityToRelease,
                             String documentType, Long documentId, Long documentDetailId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + productId));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found for ID: " + warehouseId));
        User user = userRepository.getReferenceById(userId);

        Inventory inventory = getOrCreateInventory(product, warehouse, userId);

        BigDecimal currentAllocated = inventory.getUnitsAllocated();

        if (currentAllocated.compareTo(quantityToRelease) < 0) {
            // This indicates an inconsistency: trying to release more than allocated.
            // Log a warning or throw a more specific exception if this should never happen.
            // For now, just release up to what's allocated to prevent negative allocation.
            // quantityToRelease = currentAllocated; // Option to cap release
            throw new DataConflictException("嘗試釋放的預留庫存量 (" + quantityToRelease + ") 大於實際預留量 (" + currentAllocated + ")。產品 '" + product.getName() + "' 在倉庫 '" + warehouse.getName() + "'。");
        }

        inventory.setUnitsAllocated(currentAllocated.subtract(quantityToRelease));
        // Auditing fields - recommend moving to AuditingEntityListener
        // These should be set by AuditingEntityListener if configured
        // inventory.setUpdatedAt(LocalDateTime.now());
        // inventory.setUpdatedBy(userId);
        inventoryRepository.save(inventory);

        // Create a movement record for releasing stock
        createInventoryMovement(
                product, warehouse, MovementType.STOCK_RELEASE.name(), quantityToRelease.negate(), // Negative quantity for release
                inventory.getAverageCost(), // Use current average cost
                inventory.getCurrentStock(), // Physical stock doesn't change
                documentType, documentId, documentDetailId, user
        );
    }

    public Page<InventoryViewDTO> searchInventories(Long productId, Long warehouseId, Pageable pageable) {

        Specification<Inventory> spec = InventorySpecification.findByCriteria(productId, warehouseId);


        Page<Inventory> inventoryPage = inventoryRepository.findAll(spec, pageable);


        return inventoryPage.map(InventoryViewDTO::fromEntity);
    }


    @Transactional
    public Inventory adjustInventory(Long productId, Long warehouseId,
                                     BigDecimal quantityChange, BigDecimal unitCost,
                                     MovementType movementType, String documentType,
                                     Long documentId, Long documentItemId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到ID為 " + productId + " 的產品"));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到ID為 " + warehouseId + " 的倉庫"));
        User user = userRepository.getReferenceById(userId);

        Inventory inventory = getOrCreateInventory(product, warehouse, userId); //TODO(josh): Use helper

        BigDecimal oldStock = inventory.getCurrentStock();
        BigDecimal newStock = oldStock.add(quantityChange);

        if (newStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("庫存調整後數量不可為負。產品 '" + product.getName() + "' 在倉庫 '" + warehouse.getName()
                    + "' 的目前庫存為 " + oldStock + ", 調整量 " + quantityChange);
        }

        //TODO(josh): Recalculate average cost for incoming positive adjustments with cost
        if (movementType == MovementType.ADJUSTMENT_IN && quantityChange.compareTo(BigDecimal.ZERO) > 0 && unitCost != null) {
            BigDecimal oldTotalValue = oldStock.multiply(inventory.getAverageCost());
            BigDecimal incomingValue = quantityChange.multiply(unitCost);
            if (newStock.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal newAverageCost = (oldTotalValue.add(incomingValue)).divide(newStock, 4, RoundingMode.HALF_UP);
                inventory.setAverageCost(newAverageCost);
            } else {
                inventory.setAverageCost(BigDecimal.ZERO);
            }
        } else if (movementType == MovementType.PURCHASE_IN) {
            // Average cost for PURCHASE_IN is now handled by receivePurchaseOrderLine or a more specific method
            // This generic adjustInventory might not change average cost for POs unless explicitly told to.
            // For now, let's assume unitCost for PURCHASE_IN here is for movement record only, avg cost is handled elsewhere for PO.
        }


        inventory.setCurrentStock(newStock);
        // This generic adjustInventory primarily affects currentStock.
        // unitsOnOrder and unitsAllocated are typically managed by more specific business process methods.
        // For example, a positive ADJUSTMENT_IN might also need to affect unitsOnOrder if it's correcting a PO receipt.
        // A negative ADJUSTMENT_OUT might need to affect unitsAllocated if it's for stock found damaged that was reserved.
        // These would require more parameters or more specific service methods.

//        inventory.setUpdatedAt(LocalDateTime.now());
//        inventory.setUpdatedBy(userId);

        Inventory updatedInventory = inventoryRepository.save(inventory);


//        InventoryMovement movement = new InventoryMovement();
//        movement.setProduct(product);
//        movement.setWarehouse(warehouse);
//        movement.setMovementType(movementType.name());
//        movement.setQuantityChange(quantityChange);
//        movement.setCurrentStockAfterMovement(newQuantity);
//        movement.setUnitCostAtMovement(unitCost);
//        movement.setMovementDate(LocalDateTime.now());
//        movement.setDocumentType(documentType);
//        movement.setDocumentId(documentId);
//        movement.setDocumentItemId(documentItemId);
//        movement.setRecordedBy(userRepository.getReferenceById(1L)
//                );
        createInventoryMovement(
                product,
                warehouse,
                movementType.name(),
                quantityChange,
                unitCost,
                newStock,
                documentType,
                documentId,
                documentItemId,
                user
        );

//        inventoryMovementRepository.save(movement);

        return updatedInventory;
    }

    @Transactional
    public InventoryAdjustment createInventoryAdjustment(InventoryAdjustmentCreateDTO dto, Long operatorId) {

        if (dto.getDetails() == null || dto.getDetails().isEmpty()) {
            throw new DataConflictException("庫存調整單至少需要一筆明細。");
        }

        User operator = userRepository.findById(operatorId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到 ID 為 " + operatorId + " 的操作員"));


        Long warehouseId = dto.getDetails().get(0).getWarehouseId();
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到 ID 為 " + warehouseId + " 的倉庫"));


        InventoryAdjustment adjustment = new InventoryAdjustment();
        adjustment.setAdjustmentNumber("ADJ-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        adjustment.setAdjustmentDate(dto.getAdjustmentDate());
        adjustment.setAdjustmentType(dto.getAdjustmentType()); // This should be an Enum in InventoryAdjustment entity ideally
        adjustment.setRemarks(dto.getRemarks());
        adjustment.setStatus("EXECUTED"); // Or "PENDING" then "EXECUTED" if there's an approval step
        adjustment.setWarehouse(warehouse);

        //TODO(josh): Move to AuditingEntityListener
//        LocalDateTime now = LocalDateTime.now();
//        adjustment.setCreatedBy(operatorId);
//        adjustment.setUpdatedBy(operatorId);
//        adjustment.setCreatedAt(now);
//        adjustment.setUpdatedAt(now);


        for (InventoryAdjustmentDetailCreateDTO detailDTO : dto.getDetails()) {

            if (!detailDTO.getWarehouseId().equals(warehouseId)) {
                throw new DataConflictException("單張調整單的所有明細必須屬於同一個倉庫。");
            }

            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("找不到 ID 為 " + detailDTO.getProductId() + " 的產品"));


            Inventory inventory = getOrCreateInventory(product, warehouse, operatorId);
            BigDecimal adjustedQuantity = detailDTO.getAdjustedQuantity();
            BigDecimal oldStock = inventory.getCurrentStock();
            BigDecimal newStock = oldStock.add(adjustedQuantity);

            if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                throw new DataConflictException("庫存調整後數量不可為負。產品 '" + product.getName() + "' 目前庫存 " + oldStock + ", 調整量 " + adjustedQuantity);
            }
            inventory.setCurrentStock(newStock);
//            inventory.setUpdatedBy(operatorId);
//            inventory.setUpdatedAt(now);  //TODO(josh): Move to AuditingEntityListener
            inventoryRepository.save(inventory);


            InventoryAdjustmentDetail detail = new InventoryAdjustmentDetail();
            detail.setProduct(product);
            detail.setAdjustedQuantity(detailDTO.getAdjustedQuantity());
            detail.setRemarks(detailDTO.getRemarks());
//            detail.setCreatedBy(operatorId);
//            detail.setUpdatedBy(operatorId);//TODO(josh): Move to AuditingEntityListener
//            detail.setCreatedAt(now);
//            detail.setUpdatedAt(now);
            adjustment.addDetail(detail);
        }


        InventoryAdjustment savedAdjustment = adjustmentRepository.saveAndFlush(adjustment); // Save parent and cascade save details

        // Now create movements as details have IDs
        for (InventoryAdjustmentDetail savedDetail : savedAdjustment.getDetails()) {
            Product product = savedDetail.getProduct();
            Inventory currentInventoryState = inventoryRepository.findByProductAndWarehouse(product, warehouse)
                    .orElseThrow(() -> new IllegalStateException("Inventory missing for product " + product.getProductId() + " wh: " + warehouse.getWarehouseId()));

            MovementType movementType;
            if (savedDetail.getAdjustedQuantity().compareTo(BigDecimal.ZERO) > 0) {
                movementType = MovementType.ADJUSTMENT_IN;
            } else {
                movementType = MovementType.ADJUSTMENT_OUT;
            }

            createInventoryMovement(
                    product,
                    warehouse,
                    movementType.name(),
                    savedDetail.getAdjustedQuantity(),
                    currentInventoryState.getAverageCost(), // Value of adjustment is at current average cost
                    currentInventoryState.getCurrentStock(), // Stock after this specific adjustment
                    "InventoryAdjustment",
                    savedAdjustment.getAdjustmentId(),
                    savedDetail.getItemId(),
                    operator
            );
        }
        return savedAdjustment;
    }

    @Transactional
    public SalesShipment shipSalesOrder(Long salesOrderId, Long warehouseId, Long operatorId) {


        SalesOrder order = salesOrderRepository.findById(salesOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到 ID 為 " + salesOrderId + " 的銷售訂單"));

        if (order.getOrderStatus() != SalesOrderStatus.CONFIRMED) {
            throw new DataConflictException("訂單狀態不為「已確認」，無法出貨。");
        }

        // Validate that the order's warehouse matches the shipment warehouse
        if (!order.getWarehouse().getWarehouseId().equals(warehouseId)) {
            throw new DataConflictException("銷售訂單 (ID: " + salesOrderId + ") 預計從倉庫 ID " + order.getWarehouse().getWarehouseId() + " 出貨，但請求的出貨倉庫為 ID " + warehouseId + "。請確認倉庫一致性。");
        }

//        if (order.getOrderStatus() != SalesOrderStatus.CONFIRMED) {
//            throw new DataConflictException("此銷售訂單狀態為 " + order.getOrderStatus() + "，不可執行出貨。");
//        }

        User operator = userRepository.findById(operatorId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到 ID 為 " + operatorId + " 的操作員"));
        Warehouse shipmentWarehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到 ID 為 " + warehouseId + " 的出貨倉庫"));


        SalesShipment shipment = new SalesShipment();
        shipment.setSalesOrder(order);
        shipment.setCustomer(order.getCustomer());
        shipment.setWarehouse(shipmentWarehouse);
        shipment.setShipmentDate(LocalDate.now());
        shipment.setShippingMethod(order.getShippingMethod());
        shipment.setShippingStatus("SHIPPED");

        String shipmentNumber = "SHIP-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        shipment.setShipmentNumber(shipmentNumber);


        for (SalesOrderDetail detail : order.getDetails()) {


            int updatedRows = inventoryRepository.deductStockAndAllocation(
                    detail.getProduct().getProductId(),
                    warehouseId,
                    detail.getQuantity()
            );


            if (updatedRows == 0) {
                throw new DataConflictException("庫存不足：產品 '" + detail.getProduct().getName() + "' 在倉庫 ID " + warehouseId + " 中數量不足，無法出貨。");
            }


            SalesShipmentDetail shipmentDetail = new SalesShipmentDetail();
            shipmentDetail.setProduct(detail.getProduct());
            shipmentDetail.setShippedQuantity(detail.getQuantity());
            shipmentDetail.setSalesOrderItemId(detail.getItemId());
            shipment.addDetail(shipmentDetail);

        }

        SalesShipment savedShipment = salesShipmentRepository.save(shipment);

        for (SalesShipmentDetail savedShipmentDetail : savedShipment.getDetails()) {
            Product product = savedShipmentDetail.getProduct();
            Warehouse warehouse = savedShipment.getWarehouse();
            Inventory inventory = inventoryRepository.findByProductAndWarehouse(product, warehouse)
                    .orElseThrow(() -> new IllegalStateException("庫存紀錄消失，資料異常！產品ID: " + product.getProductId()));
//            Product product = detail.getProduct();
//            Warehouse warehouse = shipment.getWarehouse();
//            Inventory inventory = inventoryRepository.findByProductAndWarehouse(product, warehouse)
//                    .orElseThrow(() -> new IllegalStateException("庫存紀錄消失，資料異常！"));

//            for(SalesShipmentDetail salesShipmentDetail : savedSh)

// After successfully deducting stock via repository (which only touches currentStock)
            // We also need to reduce unitsAllocated for the shipped quantity.
//            inventory.setUnitsAllocated(inventory.getUnitsAllocated().subtract(savedShipmentDetail.getShippedQuantity()));
//            inventoryRepository.save(inventory); // Save the change to unitsAllocated

            createInventoryMovement(
                    product,
                    shipmentWarehouse,
                    MovementType.SALE_SHIPMENT_OUT.name(),
                    savedShipmentDetail.getShippedQuantity().negate(),
                    inventory.getAverageCost(),
                    inventory.getCurrentStock(),
                    "SalesShipment",
                    savedShipment.getShipmentId(),
                    savedShipmentDetail.getItemId(),
                    operator
            );
//            InventoryMovement movement = new InventoryMovement();
//            movement.setProduct(product);
//            movement.setWarehouse(shipment.getWarehouse());
//            movement.setMovementType("OUT_SALE");
//            movement.setQuantityChange(savedShipmentDetail.getShippedQuantity().negate());
//            movement.setCurrentStockAfterMovement(inventory.getCurrentStock());
//            movement.setUnitCostAtMovement(inventory.getAverageCost());
//            movement.setDocumentType("SalesShipment");
//            movement.setDocumentId(savedShipment.getShipmentId());
//            movement.setDocumentItemId(savedShipmentDetail.getItemId());
//            movement.setRecordedBy(operator);
//            movement.setMovementDate(LocalDateTime.now());
//            inventoryMovementRepository.save(movement);
        }

        // ✨ 4. 發布事件，而不是直接更新訂單
        // 建立一個事件物件，裝載必要的資訊
        SalesOrderShippedEvent event = new SalesOrderShippedEvent(
                savedShipment.getSalesOrder().getSalesOrderId(),
                savedShipment.getShipmentId(),
                LocalDateTime.now()
        );
        // 使用發布器將事件廣播出去
        eventPublisher.publishEvent(event);

        // ✨ 5. 【重要】刪除舊的、直接修改訂單的程式碼
        // order.setOrderStatus(SalesOrderStatus.SHIPPED);
        // order.setPaymentStatus(PaymentStatus.PAID);
        // salesOrderRepository.save(order);





        return savedShipment;
    }

    private void createInventoryMovement(Product product, Warehouse warehouse, String movementType,
                                         BigDecimal quantityChange, BigDecimal unitCost, BigDecimal stockAfterMovement,
                                         String documentType, Long documentId, Long documentItemId, User recordedBy) {
        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(product);
        movement.setWarehouse(warehouse);
        movement.setMovementType(movementType);
        movement.setQuantityChange(quantityChange);
        movement.setUnitCostAtMovement(unitCost);
        movement.setTotalCostChange(unitCost.multiply(quantityChange));
        movement.setCurrentStockAfterMovement(stockAfterMovement);
        movement.setDocumentType(documentType);
        movement.setDocumentId(documentId);
        movement.setDocumentItemId(documentItemId);
        movement.setRecordedBy(recordedBy);
        movement.setMovementDate(LocalDateTime.now());

        inventoryMovementRepository.save(movement);
    }

    @Transactional
    public Inventory receiveMiscellaneousGoods(Long productId, Long warehouseId, BigDecimal quantity, BigDecimal unitCost, Long userId) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity for miscellaneous receipt must be positive.");
        }
        if (unitCost != null && unitCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit cost cannot be negative.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + productId));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found for ID: " + warehouseId));
        User user = userRepository.getReferenceById(userId);

        Inventory inventory = getOrCreateInventory(product, warehouse, userId);

        BigDecimal oldStock = inventory.getCurrentStock();
        BigDecimal newStock = oldStock.add(quantity);

        // Recalculate average cost
        if (unitCost != null) { // Only update average cost if a cost is provided
            BigDecimal oldTotalValue = oldStock.multiply(inventory.getAverageCost());
            BigDecimal incomingValue = quantity.multiply(unitCost);
            if (newStock.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal newAverageCost = (oldTotalValue.add(incomingValue)).divide(newStock, 4, RoundingMode.HALF_UP);
                inventory.setAverageCost(newAverageCost);
            } else { // Should not happen if quantity is positive
                inventory.setAverageCost(unitCost); // Or BigDecimal.ZERO if preferred when stock becomes zero then positive
            }
        }

        inventory.setCurrentStock(newStock);
//        inventory.setUpdatedAt(LocalDateTime.now()); //TODO(josh): move to AuditingEntityListener
//        inventory.setUpdatedBy(userId);
        Inventory savedInventory = inventoryRepository.save(inventory);

        createInventoryMovement(
                product, warehouse, MovementType.MISCELLANEOUS_RECEIPT.name(), quantity,
                unitCost != null ? unitCost : inventory.getAverageCost(), // Use provided cost, or new avg cost if null
                newStock, "MISC_RECEIPT", null, null, // No specific document/item ID for misc receipt unless further defined
                user
        );
        return savedInventory;
    }
}

