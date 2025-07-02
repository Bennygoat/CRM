package com.example.demo.controller;

import com.example.demo.dto.request.AddItemRequestDto;
import com.example.demo.dto.response.CartViewDto;
import com.example.demo.dto.request.UpdateQuantityRequestDto;
import com.example.demo.security.CheckJwt;
import com.example.demo.security.JwtUserPayload;
import com.example.demo.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    /**
     * 從 HttpServletRequest 中取得 customerId 的輔助方法。
     * 這可以讓所有 API 端點共用相同的取值邏輯，減少重複程式碼。
     * @param request HTTP請求物件
     * @return 使用者ID (customerId)
     */
    private Long getCustomerIdFromRequest(HttpServletRequest request) {
        JwtUserPayload userPayload = (JwtUserPayload) request.getAttribute("userPayload");
        if (userPayload == null) {
            // 在實際應用中，如果 @CheckJwt 有正確執行，這裡應該不會是 null
            // 但加上檢查可以讓程式更健壯
            throw new IllegalStateException("無法從 JWT payload 中取得使用者資訊");
        }
        return userPayload.getId();
    }

    /**
     * 檢查並取得當前使用者的購物車內容
     */
    @GetMapping("/check")
    @CheckJwt // ★★★【修改重點 1】★★★：加上這個註解來啟用 JWT 驗證
    public ResponseEntity<CartViewDto> checkCart(HttpServletRequest request) {
        Long customerId = getCustomerIdFromRequest(request);
        CartViewDto cart = cartService.getCartByCCustomerId(customerId);
        return ResponseEntity.ok(cart);
    }

    /**
     * 新增商品到購物車
     */
    @PostMapping("/items")
    @CheckJwt
    public ResponseEntity<CartViewDto> addItemToCart(HttpServletRequest request,
                                                     @RequestBody AddItemRequestDto requestDto) {
        Long customerId = getCustomerIdFromRequest(request);
        CartViewDto updatedCart = cartService.addItemToCart(customerId, requestDto);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * ✨✨✨【全新的】批次新增多個商品到購物車 ✨✨✨
     */
    @PostMapping("/items/more") // 維持您原本的 /items 路徑用於批次新增
    @CheckJwt
    public ResponseEntity<CartViewDto> addMultipleItemsToCart(HttpServletRequest request,
                                                              @RequestBody List<AddItemRequestDto> requestDtos) { // ✨ 接收一個 List
        Long customerId = getCustomerIdFromRequest(request);
        // 呼叫我們即將建立的新 Service 方法
        CartViewDto updatedCart = cartService.addMultipleItemsToCart(customerId, requestDtos);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * 更新購物車項目數量
     */
    // 修改前：@PutMapping("/items/{cartDetailId}/{userid}")
    // 修改後：
    @PutMapping("/items/{cartDetailId}")
    @CheckJwt
    public ResponseEntity<CartViewDto> updateItemQuantity(HttpServletRequest request,
                                                          @PathVariable Long cartDetailId,
                                                          @RequestBody UpdateQuantityRequestDto requestDto) {
        Long customerId = getCustomerIdFromRequest(request);
        CartViewDto updatedCart = cartService.updateItemQuantity(customerId, cartDetailId, requestDto.getQuantity());
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * 移除購物車中的單一項目
     */
    // 修改前：@DeleteMapping("/items/{cartDetailId}/{userid}")
    // 修改後：
    @DeleteMapping("/items/{cartDetailId}")
    @CheckJwt
    public ResponseEntity<CartViewDto> removeItemFromCart(HttpServletRequest request,
                                                          @PathVariable Long cartDetailId) {
        Long customerId = getCustomerIdFromRequest(request);
        CartViewDto updatedCart = cartService.removeItemFromCart(customerId, cartDetailId);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * 清空購物車
     */
    // 修改前：@DeleteMapping("delete/{userid}")
    // 修改後：
    @DeleteMapping("/delete")
    @CheckJwt
    public ResponseEntity<Void> clearCart(HttpServletRequest request) {
        Long customerId = getCustomerIdFromRequest(request);
        cartService.clearCart(customerId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
