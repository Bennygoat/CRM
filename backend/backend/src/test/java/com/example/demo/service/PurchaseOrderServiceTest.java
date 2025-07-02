package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.demo.service.erp.PurchaseOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dto.erp.PurchaseOrderCreateDTO;
import com.example.demo.dto.erp.PurchaseOrderDetailCreateDTO;
import com.example.demo.entity.Product;
import com.example.demo.entity.PurchaseOrder;
import com.example.demo.entity.Supplier;
import com.example.demo.entity.Warehouse;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.PurchaseOrderRepository;
import com.example.demo.repository.SupplierRepository;
import com.example.demo.repository.WarehouseRepository;

@ExtendWith(MockitoExtension.class)
public class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    @Test
    void testCreatePurchaseOrder_Success() {
  
        PurchaseOrderDetailCreateDTO detailCreateDTO = new PurchaseOrderDetailCreateDTO();
        detailCreateDTO.setProductId(1L);
        detailCreateDTO.setQuantity(new BigDecimal(10));
        detailCreateDTO.setUnitPrice(new BigDecimal(100));
        detailCreateDTO.setWarehouseId(1L);

        PurchaseOrderCreateDTO orderCreateDTO = new PurchaseOrderCreateDTO();
        orderCreateDTO.setSupplierId(1L);
        orderCreateDTO.setOrderDate(LocalDate.now());
        orderCreateDTO.setCurrency("TWD");
        orderCreateDTO.setRemarks("Test");
        orderCreateDTO.setDetails(Collections.singletonList(detailCreateDTO));
        when(supplierRepository.findById(1L))
                .thenReturn(Optional.of(new Supplier())); 

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(new Product())); 

       
        when(warehouseRepository.findById(1L))
                .thenReturn(Optional.of(new Warehouse())); 

        
        PurchaseOrder savedOrder = new PurchaseOrder();
        savedOrder.setPurchaseOrderId(1L);
        when(purchaseOrderRepository.save(any(PurchaseOrder.class)))
                .thenReturn(savedOrder);

        
        PurchaseOrder result = purchaseOrderService.createPurchaseOrder(orderCreateDTO, 1L);

        
        assertNotNull(result);
        assertEquals(1L, result.getPurchaseOrderId());

        
        verify(supplierRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(warehouseRepository).findById(1L);
        verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
    }
}