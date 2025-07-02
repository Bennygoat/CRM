package com.example.demo.controller;

import com.example.demo.dto.request.CreateOrderRequestDto;
import com.example.demo.dto.request.UpdateStatusRequestDto;
import com.example.demo.dto.response.OrderDto;
import com.example.demo.enums.OrderStatus;
import com.example.demo.security.CheckJwt;
import com.example.demo.security.JwtUserPayload;
import com.example.demo.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 從 HttpServletRequest 中取得 customerId 的輔助方法。
     */
    private Long getCustomerIdFromRequest(HttpServletRequest request) {
        JwtUserPayload userPayload = (JwtUserPayload) request.getAttribute("userPayload");
        if (userPayload == null) {
            throw new IllegalStateException("無法從 JWT payload 中取得使用者資訊");
        }
        return userPayload.getId();
    }

    /**
     * 結帳 - 從購物車建立新訂單
     */
    @PostMapping("/create")
    @CheckJwt
    public ResponseEntity<OrderDto> createOrder(HttpServletRequest request,
                                                @RequestBody CreateOrderRequestDto requestDto) {
        Long customerId = getCustomerIdFromRequest(request);
        OrderDto newOrder = orderService.createOrderFromCart(customerId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
    }

    /**
     * 查詢自己的歷史訂單
     */
    @GetMapping("/my-orders")
    @CheckJwt
    public ResponseEntity<List<OrderDto>> getMyOrders(HttpServletRequest request) {
        Long customerId = getCustomerIdFromRequest(request);
        List<OrderDto> orders = orderService.getOrdersBycustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    // --- 以下為後台管理用 API (暫時不加 @CheckJwt 保護) ---

    @GetMapping("/all")
    public ResponseEntity<Page<OrderDto>> getAllOrders(
            @ParameterObject @PageableDefault(size = 10, sort = "orderid", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<OrderDto> orderPage = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orderPage);
    }

    @GetMapping("/status")
    public ResponseEntity<Page<OrderDto>> getOrdersByStatus(
            @RequestParam OrderStatus status,
            @ParameterObject @PageableDefault(size = 10, sort = "orderdate") Pageable pageable) {
        Page<OrderDto> orderPage = orderService.getOrdersByStatus(status, pageable);
        return ResponseEntity.ok(orderPage);
    }

    @GetMapping("/search")
    public ResponseEntity<List<OrderDto>> searchOrders(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endTime,
            @RequestParam(required = false) String productName
    ) {
        List<OrderDto> result = orderService.searchOrders(startTime, endTime, productName);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateStatusRequestDto requestDto) {
        OrderDto updatedOrder = orderService.updateOrderStatus(orderId, requestDto.getStatus());
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long orderId) {
        OrderDto orderDto = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderDto);
    }
}
