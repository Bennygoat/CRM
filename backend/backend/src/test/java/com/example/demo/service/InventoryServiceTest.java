package com.example.demo.service;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.demo.entity.*;
import com.example.demo.entity.Inventory;
import com.example.demo.entity.InventoryMovement;
import com.example.demo.enums.SalesOrderStatus;
import com.example.demo.repository.*;
import com.example.demo.service.erp.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.enums.PurchaseOrderStatus;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private SalesShipmentRepository salesShipmentRepository;


    private Product sampleProduct;
    private Warehouse sampleWarehouse;
    private Inventory sampleInventory;
    private PurchaseOrder samplePurchaseOrder;
    private User receivingUser;

    @BeforeEach
    void setUp() {

        receivingUser = new User();
        receivingUser.setUserId(1L);
        receivingUser.setUserName("TestUser");

        sampleProduct = new Product();
        sampleProduct.setProductId(1L);
        sampleProduct.setName("TestProduct");

        sampleWarehouse = new Warehouse();
        sampleWarehouse.setWarehouseId(1L);
        sampleWarehouse.setName("TestWarehouse");

        samplePurchaseOrder = new PurchaseOrder();
        samplePurchaseOrder.setPurchaseOrderId(10L);
        samplePurchaseOrder.setOrderNumber("PO-001");
        samplePurchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);
        samplePurchaseOrder.setWarehouseId(1L);
//        samplePurchaseOrder.addDetail(new PurchaseOrderDetail());

        PurchaseOrderDetail detail1 = new PurchaseOrderDetail();
        detail1.setItemId(10L);
        detail1.setProduct(sampleProduct);
        detail1.setWarehouse(sampleWarehouse);
        detail1.setQuantity(new BigDecimal("20"));
        samplePurchaseOrder.addDetail(detail1);

    }

    @Test
    void testReceivePurchaseOrder() {
        Long orderId = 1L;
        Long userId = 1L;

        // --- 關鍵修正 3: 設定新 Mock 物件的行為 ---
        // 當 purchaseOrderRepository.findById 被呼叫時，回傳我們準備好的 purchaseOrder 物件
        when(purchaseOrderRepository.findById(orderId)).thenReturn(Optional.of(samplePurchaseOrder));
        // 當 userRepository.findById 被呼叫時，回傳我們準備好的 receivingUser 物件
        when(userRepository.findById(userId)).thenReturn(Optional.of(receivingUser));

        // 為了讓 adjustInventory 方法能夠順利執行，我們也需要設定它的依賴
        // 這裡我們假設庫存是第一次建立
        when(inventoryRepository.findByProductAndWarehouse(any(Product.class), any(Warehouse.class)))
                .thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(sampleProduct));
        when(warehouseRepository.findById(anyLong())).thenReturn(Optional.of(sampleWarehouse));

        // 執行被測試的方法
        inventoryService.receivePurchaseOrder(orderId, userId);

        // --- 關鍵修正 4: 驗證新的互動 ---
        // 驗證 purchaseOrderRepository 的 findById 方法是否被呼叫了一次
        verify(purchaseOrderRepository, times(1)).findById(orderId);
        // 驗證 userRepository 的 findById 方法是否被呼叫了一次
        verify(userRepository, times(1)).findById(userId);

        // 驗證核心的 adjustInventory 邏輯是否有被呼叫 (這裡我們假設採購單只有一個品項)
        verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));

        // 驗證採購單的狀態是否被更新為 COMPLETED
        assertEquals(PurchaseOrderStatus.RECEIVED, samplePurchaseOrder.getStatus());
        // 驗證採購單的 save 方法是否被呼叫了一次來儲存更新後的狀態
        verify(purchaseOrderRepository, times(1)).save(samplePurchaseOrder);
    }

    // 你原有的其他測試案例 (testGetInventoryByProductId, testAdjustInventory)
    // 為了讓它們能繼續運作，需要確保它們的 setup 沒有被破壞。
    // 使用 @InjectMocks 後，它們應該能正常工作。

    @Test
    void testShipSalesOrder_Success_shouldDeductInventory(){
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setSalesOrderId(301L);
        salesOrder.setOrderStatus(SalesOrderStatus.CONFIRMED);

        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setName("TestProduct1");

        SalesOrderDetail detail1 = new SalesOrderDetail();
        detail1.setProduct(product1);
        detail1.setQuantity(new BigDecimal("10"));
        salesOrder.addDetail(detail1);

        User operator = new User();
        operator.setUserId(1L);
        operator.setUserName("TestUser");

        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseId(1L);
        warehouse.setName("TestWarehouse");

        Inventory inventory = new Inventory();
        inventory.setCurrentStock(new BigDecimal("90"));
        inventory.setProduct(product1);
        inventory.setWarehouse(warehouse);


        when(salesOrderRepository.findById(301L)).thenReturn(Optional.of(salesOrder));
        when(userRepository.findById(1L)).thenReturn(Optional.of(operator)); // This was missing!
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse)); // This was missing!
//        when(inventoryRepository.deductStock(1L, 1L, new BigDecimal("10"))).thenReturn(1);
        when(inventoryRepository.findByProductAndWarehouse(product1, warehouse)).thenReturn(Optional.of(inventory));
        when(salesShipmentRepository.save(any(SalesShipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        
        inventoryService.shipSalesOrder(301L,1L,1L);

        assertEquals(SalesOrderStatus.SHIPPED, salesOrder.getOrderStatus());
        verify(salesOrderRepository, times(1)).save(salesOrder);
        verify(salesShipmentRepository, times(1)).save(any(SalesShipment.class));
        verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));

    }
}
