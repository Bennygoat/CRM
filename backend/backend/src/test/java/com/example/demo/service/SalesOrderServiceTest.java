package com.example.demo.service;


import com.example.demo.dto.erp.SalesOrderCreateDTO;
import com.example.demo.dto.erp.SalesOrderDetailCreateDTO;
import com.example.demo.dto.erp.SalesOrderSummaryDTO;
import com.example.demo.entity.BCustomer;
import com.example.demo.entity.Product;
import com.example.demo.entity.SalesOrder;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.SalesOrderStatus;
import com.example.demo.exception.DataConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CustomerBaseRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SalesOrderRepository;
import com.example.demo.service.erp.SalesOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalesOrderServiceTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private CustomerBaseRepository customerBaseRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SalesOrderService salesOrderService;
    private SalesOrderCreateDTO createDTO;
    private BCustomer mockCustomer;
    private Product mockProduct;
    private Long operatorId=1L;

    @BeforeEach
    void setUp() {

        mockCustomer = new BCustomer();
        mockCustomer.setCustomerId(1L);
        mockCustomer.setActive(true);

        mockProduct = new Product();
        mockProduct.setProductId(101L);
        mockProduct.setIsActive(true);
        mockProduct.setIsSalable(true);

        SalesOrderDetailCreateDTO detailDTO = new SalesOrderDetailCreateDTO();
        detailDTO.setProductId(101L);
        detailDTO.setQuantity(new BigDecimal("2"));
        detailDTO.setUnitPrice(new BigDecimal("500"));

        createDTO = new SalesOrderCreateDTO();
        createDTO.setCustomerId(1L);
        createDTO.setOrderDate(LocalDate.now());
        createDTO.setShippingAddress("測試用地址");
        createDTO.setPaymentMethod("CREDIT_CARD");
        createDTO.setDetails(Collections.singletonList(detailDTO));
    }

    @Test
    @DisplayName("成功建立銷售訂單")
    void testCreateSalesOrder_Success() {

        BCustomer mockCustomer = new BCustomer();
        mockCustomer.setCustomerId(1L);
        mockCustomer.setActive(true);
        when(customerBaseRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));


        Product mockProduct = new Product();
        mockProduct.setProductId(101L);
        mockProduct.setIsActive(true);
        mockProduct.setIsSalable(true);
        when(productRepository.findById(101L)).thenReturn(Optional.of(mockProduct));


        when(salesOrderRepository.save(any(SalesOrder.class))).thenAnswer(invocation -> {
            SalesOrder orderToSave = invocation.getArgument(0);
            orderToSave.setSalesOrderId(99L);
            return orderToSave;
        });


        SalesOrder result = salesOrderService.createSalesOrder(createDTO, operatorId);


        assertNotNull(result);
        assertEquals(99L, result.getSalesOrderId());
        assertEquals(SalesOrderStatus.CONFIRMED, result.getOrderStatus());
        assertEquals(PaymentStatus.UNPAID, result.getPaymentStatus());
        assertEquals(1, result.getDetails().size());
        assertEquals(0, new BigDecimal("1000").compareTo(result.getTotalAmount()));
    }

    @Test
    @DisplayName("建立訂單時，若客戶不存在，應拋出 ResourceNotFoundException")
    void testCreateSalesOrder_whenCustomerNotFound_shouldThrowException() {

        when(customerBaseRepository.findById(1L)).thenReturn(Optional.empty());


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            salesOrderService.createSalesOrder(createDTO, operatorId);
        });

        assertTrue(exception.getMessage().contains("找不到 ID 為 1 的客戶"));
    }

    @Test
    @DisplayName("建立訂單時，若產品不存在，應拋出 ResourceNotFoundException")
    void testCreateSalesOrder_whenProductNotFound_shouldThrowException() {

        when(customerBaseRepository.findById(1L)).thenReturn(Optional.of(new BCustomer()));
        when(productRepository.findById(101L)).thenReturn(Optional.empty());


        assertThrows(ResourceNotFoundException.class, () -> {
            salesOrderService.createSalesOrder(createDTO, operatorId);
        });
    }

    @Test
    @DisplayName("建立訂單時，若產品為非啟用狀態，應拋出 DataConflictException")
    void testCreateSalesOrder_whenProductIsInactive_shouldThrowException() {
        BCustomer activeCustomer = new BCustomer();
        activeCustomer.setCustomerId(1L);
        activeCustomer.setActive(true);


        Product inactiveProduct = new Product();
        inactiveProduct.setProductId(101L);
        inactiveProduct.setName("測試產品");
        inactiveProduct.setIsActive(false);
        inactiveProduct.setIsSalable(true);

        when(customerBaseRepository.findById(1L)).thenReturn(Optional.of(activeCustomer));
        when(productRepository.findById(101L)).thenReturn(Optional.of(inactiveProduct));


        DataConflictException exception = assertThrows(DataConflictException.class, () -> {
            salesOrderService.createSalesOrder(createDTO, operatorId);
        });

        System.out.println("Actual exception message: " + exception.getMessage());


        assertTrue(exception.getMessage().contains("非啟用或不可銷售狀態"));
    }

    @Test
    @DisplayName("查詢訂單: 成功情境，應回傳分頁結果")
    void testSearchSalesOrders_Success() {

        Pageable pageable = PageRequest.of(0, 10);

        SalesOrder order = new SalesOrder();
        order.setSalesOrderId(1L);
        order.setOrderNumber("SO-TEST-001");
        order.setCustomer(mockCustomer);

        Page<SalesOrder> orderPage = new PageImpl<>(Collections.singletonList(order), pageable, 1);

        when(salesOrderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(orderPage);


        Page<SalesOrderSummaryDTO> resultPage = salesOrderService.searchSalesOrders(1L, null, null, null, null, pageable);


        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals("SO-TEST-001", resultPage.getContent().get(0).getOrderNumber());
        assertEquals(mockCustomer.getCustomerId(), resultPage.getContent().get(0).getCustomerId());
    }
}