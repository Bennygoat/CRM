package com.example.demo.service;

import com.example.demo.dto.request.AddItemRequestDto;
import com.example.demo.dto.response.CartDetailDto;
import com.example.demo.dto.response.CartViewDto;
import com.example.demo.entity.*;
import com.example.demo.repository.CCustomerRepo;
import com.example.demo.repository.CartDetailRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;

import com.example.demo.service.erp.InventoryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ProductRepository productRepository;
    private final CCustomerRepo cCustomerRepo;
    private final InventoryService inventoryService;

    //    private EntityManager entityManager;
//
//
//    public CartService(CartRepository cartRepository, CartDetailRepository cartDetailRepository,
//                       ProductRepository productRepository, CCustomerRepo cCustomerRepo,
//                       InventoryService inventoryService) {
//        this.cartRepository = cartRepository;
//        this.cartDetailRepository = cartDetailRepository;
//        this.productRepository = productRepository;
//        this.cCustomerRepo = cCustomerRepo;
//        this.inventoryService = inventoryService;
//    }
    private final EntityManager entityManager; //TODO(joshkuei): Made final

    //TODO(joshkuei): Manual constructor removed, @AllArgsConstructor will handle all final fields.


    /**
     * 檢查此 customerId 有無購物車，若有則呈現出對應的 CartViewDto；若無則呈現空的 CartViewDto
     */
    @Transactional
    public CartViewDto getCartByCCustomerId(Long CCustomerId) {
        return cartRepository.findByCCustomer_CustomerId(CCustomerId)
                .map(this::mapToCartViewDto) // 如果找到購物車，則轉換成 DTO
                .orElse(getEmptyCart());   // 如果找不到，則回傳一個空購物車 DTO
    }


    /**
     * 新增商品到購物車
     */
    @Transactional
    public CartViewDto addItemToCart(Long customerId, AddItemRequestDto requestDto) {
        Cart cart = findOrCreateCartByUser(customerId);

        Product product = productRepository.findById(requestDto.getProductid())
                .orElseThrow(() -> new EntityNotFoundException("找不到商品 ID: " + requestDto.getProductid()));

//        // === 以下是庫存檢查邏輯 ===
//        // 1. 從 Product 物件中取得所有相關的 Inventory 紀錄列表
////        List<Inventory> inventories = product.getInventory;
//
//        BigDecimal currentStock = inventoryService.getProductStock(product.getProductId());
//        if (currentStock.intValue() < requestDto.getQuantity()) {
//            throw new RuntimeException("Insufficient stock for product: " + product.getName());
//        }
//        // 2. 使用 Stream API 將所有庫存地點的 unitsinstock 加總
////        int totalStock = inventories.stream()
////                .mapToInt(Inventory::getUnitsinstock) // 將每個 Inventory 物件轉換成它的庫存數量(int)
////                .sum(); // 將所有的 int 數字加總

////        int reservedStock = inventories.stream()
////                .mapToInt(Inventory::getUnitsinreserved)
////                .sum();
////
////        int usableStock = totalStock - reservedStock;
////
////        // 3. 用計算出來的總庫存進行比較
////        if (usableStock < requestDto.getQuantity()) {
////            throw new IllegalStateException("商品庫存不足，目前可下訂庫存為: " + usableStock);
////        }
//        // 如果您在 Product Entity 中新增了 getTotalStock() 方法，這裡也可以簡化為：
//        // if (product.getTotalStock() < requestDto.getQuantity()) {
//        //     throw new IllegalStateException("商品庫存不足，目前總庫存為: " + product.getTotalStock());
//        // }
//        // 查找購物車是否已存在此商品
////        Optional<CartDetail> existingDetailOpt = cart.getCartdetails().stream()
////                .filter(d -> d.getProduct().getProductid().equals(requestDto.getProductid()))
////                .findFirst();
//        Optional<CartDetail> existingDetail = cartDetailRepository.findByCartAndProduct(cart, product);
////        if (existingDetailOpt.isPresent()) {
////            // 如果存在，則更新數量
////            CartDetail existingDetail = existingDetailOpt.get();
////            int newQuantity = existingDetail.getQuantity() + requestDto.getQuantity();
//        if (existingDetail.isPresent()) {
//            CartDetail detail = existingDetail.get();
//            detail.setQuantity(detail.getQuantity() + requestDto.getQuantity());
//            int newQuantity = existingDetail.get().getQuantity() + requestDto.getQuantity(); //TODO(joshkuei): Make sure the`get().getQuantity()` it's right.
//
//            cartDetailRepository.save(detail);
////            if (newQuantity > usableStock) {
////            throw new IllegalStateException("商品庫存不足，累加後超過可下訂庫存: " + usableStock);
////        }
//            if (currentStock.compareTo(BigDecimal.valueOf(newQuantity)) < 0) {
//                throw new IllegalStateException("商品庫存不足，累加後超過可下訂庫存: " + currentStock);
//            }
//            existingDetail.get().setQuantity(newQuantity); //TODO(joshkuei): Make sure the `get().setQuantity()` it's right.
//            cartRepository.saveAndFlush(cart);
//        } else {
//            // 如果不存在，則新增一個 CartDetail
//            CartDetail newDetail = new CartDetail();
//            newDetail.setProduct(product);
//            newDetail.setQuantity(requestDto.getQuantity());
//            newDetail.setAddat(LocalDateTime.now());
//            newDetail.setCart(cart); // 明確設定它屬於哪個 cart
//            cartDetailRepository.saveAndFlush(newDetail);
//
////            CartDetail newDetail = new CartDetail();
////            newDetail.setCart(cart);
////            newDetail.setProduct(product);
////            newDetail.setQuantity(request.getQuantity());
////            cartDetailRepository.save(newDetail);
//        }
//
//        entityManager.refresh(cart);

//        return mapToCartViewDto(cart);
////        return getCartView(cart.getId());
//    }

        // === 新的庫存檢查邏輯 using InventoryService ===
        BigDecimal requestedQuantity = BigDecimal.valueOf(requestDto.getQuantity());
        // Get total available stock for the product across all warehouses.
        BigDecimal availableStock = inventoryService.getAvailableStock(product.getProductId());

        if (availableStock.compareTo(requestedQuantity) < 0) {
            throw new IllegalStateException("商品 [" + product.getName() + "] 庫存不足 (需求: " + requestedQuantity + ", 可用: " + availableStock + ")");
        }

        Optional<CartDetail> existingDetailOpt = cartDetailRepository.findByCartAndProduct(cart, product);

        if (existingDetailOpt.isPresent()) {
            CartDetail detail = existingDetailOpt.get();
            int newQuantityInt = detail.getQuantity() + requestDto.getQuantity();
            BigDecimal newQuantityBigDecimal = BigDecimal.valueOf(newQuantityInt);

            // Re-check available stock against the new total quantity for this item
            // This check should ideally be against the *remaining* available stock if multiple items are added,
            // but for a single item type addition/update, this check is against total available.
            if (availableStock.compareTo(newQuantityBigDecimal) < 0) {
                throw new IllegalStateException("商品 [" + product.getName() + "] 庫存不足，累加後超過可下訂庫存 (需求: " + newQuantityBigDecimal + ", 可用: " + availableStock + ")");
            }
            detail.setQuantity(newQuantityInt);
            cartDetailRepository.save(detail);
        } else {
            // If new item, initial stock check (already done above) is sufficient for this specific item.
            CartDetail newDetail = new CartDetail();
            newDetail.setProduct(product);
            newDetail.setQuantity(requestDto.getQuantity());
            newDetail.setAddat(LocalDateTime.now());
            newDetail.setCart(cart);
            cart.getCartdetails().add(newDetail); // Add to in-memory collection
            cartDetailRepository.save(newDetail); // Persist new detail
        }

        entityManager.flush(); // Ensure changes are written to DB before refresh
        entityManager.refresh(cart); // Refresh cart to get latest state of details
        return mapToCartViewDto(cart);
    }

    /**
     * 批次新增多個商品到購物車。
     * 這個方法經過優化，可以減少資料庫查詢次數。
     * @param customerId 顧客 ID
     * @param itemsToAdd 要新增的商品列表
     * @return 更新後的購物車視圖
     */
    @Transactional
    public CartViewDto addMultipleItemsToCart(Long customerId, List<AddItemRequestDto> itemsToAdd) {
        if (itemsToAdd == null || itemsToAdd.isEmpty()) {
            throw new IllegalArgumentException("商品列表不可為空");
        }

        // 1. 取得使用者的購物車
        Cart cart = findOrCreateCartByUser(customerId);

        // 2.【效能優化】一次性獲取所有請求的商品資訊
        List<Long> productIds = itemsToAdd.stream()
                .map(AddItemRequestDto::getProductid)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getProductId, product -> product));

        // 3.【效能優化】一次性獲取購物車中已存在的相關商品明細
        Map<Long, CartDetail> existingDetailsMap = cartDetailRepository.findByCartAndProductIn(cart, productMap.values()).stream()
                .collect(Collectors.toMap(detail -> detail.getProduct().getProductId(), detail -> detail));

        // 4.【庫存預先檢查】在進行任何修改前，先檢查所有商品的庫存是否充足
        for (AddItemRequestDto item : itemsToAdd) {
            Product product = productMap.get(item.getProductid());
            if (product == null) {
                throw new EntityNotFoundException("找不到商品 ID: " + item.getProductid());
            }

            int quantityToAdd = item.getQuantity();
            CartDetail existingDetail = existingDetailsMap.get(item.getProductid());
            int finalQuantity = (existingDetail != null) ? existingDetail.getQuantity() + quantityToAdd : quantityToAdd;

            BigDecimal availableStock = inventoryService.getAvailableStock(item.getProductid());
            if (availableStock.compareTo(BigDecimal.valueOf(finalQuantity)) < 0) {
                throw new IllegalStateException("商品 [" + product.getName() + "] 庫存不足 (需求總數: " + finalQuantity + ", 可用庫存: " + availableStock + ")");
            }
        }

        // 5.【執行更新】遍歷商品列表，更新或建立 CartDetail
        List<CartDetail> detailsToSave = new ArrayList<>();
        for (AddItemRequestDto item : itemsToAdd) {
            Product product = productMap.get(item.getProductid());
            CartDetail detail = existingDetailsMap.get(item.getProductid());

            if (detail != null) {
                // 如果商品已存在，更新數量
                detail.setQuantity(detail.getQuantity() + item.getQuantity());
                detailsToSave.add(detail);
            } else {
                // 如果是新商品，建立新的 CartDetail
                CartDetail newDetail = new CartDetail();
                newDetail.setCart(cart);
                newDetail.setProduct(product);
                newDetail.setQuantity(item.getQuantity());
                newDetail.setAddat(LocalDateTime.now());
                detailsToSave.add(newDetail);
            }
        }

        // 6.【效能優化】一次性將所有變更儲存到資料庫
        cartDetailRepository.saveAll(detailsToSave);

        // 7. 刷新並回傳最新的購物車視圖
        entityManager.flush();
        entityManager.refresh(cart);
        return mapToCartViewDto(cart);
    }

    /**
     * 更新購物車項目數量
     */
    @Transactional
    public CartViewDto updateItemQuantity(Long customerId, Long cartDetailId, int newQuantity) {
        Cart cart = findCartByUser(customerId);
//        CartDetail detail = findCartDetailInCart(cart, cartDetailId);
        CartDetail detailToUpdate = findCartDetailInCart(cart, cartDetailId); //TDOO(joshkuei): change name.
//        if (newQuantity <= 0) {
//            // 如果數量小於等於0，則移除該項目
//            removeItemFromCartInternal(cart, detail);
//        } else {
//
//            // 檢查庫存
//            // 1. 從購物車項目(detail)中取得對應的商品(Product)物件
//            Product product = detail.getProduct();
//
//            // 2. 加總該商品在所有庫存地點的庫存量
//            int totalStock = product.getInventories().stream()
//                    .mapToInt(Inventory::getUnitsinstock)
//                    .sum();
//
//            // 3. 比較總庫存與使用者想更新的數量
//            if (totalStock < newQuantity) {
//                throw new IllegalStateException("商品庫存不足，目前總庫存為: " + totalStock);
//            }
//
//            // 4. 如果庫存充足，才更新數量
//            detail.setQuantity(newQuantity);
//        }
//
//        Cart updatedCart = cartRepository.save(cart);
//        return mapToCartViewDto(updatedCart);
//    }
        if (newQuantity <= 0) {
            // removeItemFromCartInternal(cart, detailToUpdate); // Helper might be redundant
            cart.getCartdetails().remove(detailToUpdate); // Remove from managed collection
            cartDetailRepository.delete(detailToUpdate); // Explicitly delete
        } else {
            Product product = detailToUpdate.getProduct();
            BigDecimal requestedNewQuantity = BigDecimal.valueOf(newQuantity);
            // Get total available stock for the product across all warehouses.
            BigDecimal availableStock = inventoryService.getAvailableStock(product.getProductId());

            if (availableStock.compareTo(requestedNewQuantity) < 0) {
                throw new IllegalStateException("商品 [" + product.getName() + "] 庫存不足 (需求: " + requestedNewQuantity + ", 可用: " + availableStock + ")");
            }
            detailToUpdate.setQuantity(newQuantity);
            cartDetailRepository.save(detailToUpdate); // Save the updated detail
        }

        entityManager.flush();
        entityManager.refresh(cart); // Refresh to get updated cart details for DTO mapping
        return mapToCartViewDto(cart); // Corrected: use cart variable
    }


    /**
     * 從購物車移除單一項目
     */
    @Transactional
    public CartViewDto removeItemFromCart(Long customerId, Long cartDetailId) {
        Cart cart = findCartByUser(customerId);
        CartDetail detail = findCartDetailInCart(cart, cartDetailId);

        // 現在這個操作會觸發 orphanRemoval，直接刪除資料庫紀錄
        cart.getCartdetails().remove(detail);

        Cart updatedCart = cartRepository.save(cart);
        return mapToCartViewDto(updatedCart);
    }

    /**
     * 清空購物車
     */
    @Transactional
    public void clearCart(Long customerId) {
        Cart cart = findCartByUser(customerId);
        cart.getCartdetails().clear(); // 因為 orphanRemoval=true，這會刪除所有關聯的 CartDetail
        cartRepository.save(cart);
    }

    //----------以下為拉出來撰寫的方法
    private CartViewDto mapToCartViewDto(Cart cart) {
//        List<CartDetailDto> detailDtosDtos = cart.getCartdetails().stream()
//                .map(detail -> {
//                    List<ProductImg> imgs = detail.getProduct().getProductimgs();
//                    String ImgUrl;
//                    if (imgs != null && !imgs.isEmpty()) {
//                        ImgUrl = imgs.get(0).getImgurl();
//                    } else {
//                        ImgUrl = "/images/default_product.jpg";
//                    }
//
//                    return CartDetailDto.builder()
//                            .cartdetailid(detail.getCartdetailid())
//                            .productid(detail.getProduct().getProductid())
//                            .productname(detail.getProduct().getProductname())
//                            .productimgurl(ImgUrl)
//                            .unitprice(detail.getProduct().getUnitprice())
//                            .quantity(detail.getQuantity())
//                            .totalprice(detail.getProduct().getUnitprice() * detail.getQuantity())
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        double grandTotal = detailDtosDtos.stream().mapToDouble(CartDetailDto::getTotalprice).sum();
//        int totalItems = detailDtosDtos.stream().mapToInt(CartDetailDto::getQuantity).sum();
//
//        return CartViewDto.builder()
//                .cartid(cart.getCartid())
//                .cartdetails(detailDtosDtos)
//                .totalprice(grandTotal)
//                .quantity(totalItems)
//                .build();
//    }
        List<CartDetailDto> detailDtos = cart.getCartdetails().stream()
                .map(detail -> {
                    List<ProductImg> imgs = detail.getProduct().getProductimgs(); // Should now work
                    String imgUrl; // Variable name consistency
                    if (imgs != null && !imgs.isEmpty()) {
                        imgUrl = imgs.get(0).getImgurl();
                    } else {
                        imgUrl = "/images/default_product.jpg"; // Default image path
                    }

                    Product product = detail.getProduct();
                    return CartDetailDto.builder()
                            .cartdetailid(detail.getCartdetailid())
                            .productid(product.getProductId()) // Changed getProductid to getProductId
                            .productname(product.getName()) // Changed getProductname to getName
                            .productimgurl(imgUrl)
                            .unitprice(product.getBasePrice().doubleValue()) // Changed getUnitprice to getBasePrice().doubleValue()
                            .quantity(detail.getQuantity())
                            .totalprice(product.getBasePrice().doubleValue() * detail.getQuantity()) // Changed getUnitprice
                            .build();
                })
                .collect(Collectors.toList());

        double grandTotal = detailDtos.stream().mapToDouble(CartDetailDto::getTotalprice).sum();
        int totalItems = detailDtos.stream().mapToInt(CartDetailDto::getQuantity).sum(); // Corrected variable name

        return CartViewDto.builder()
                .cartid(cart.getCartid())
                .cartdetails(detailDtos) // Corrected variable name
                .totalprice(grandTotal)
                .quantity(totalItems)
                .build();
    }

    private CartViewDto getEmptyCart() {
        return CartViewDto.builder()
                .cartdetails(Collections.emptyList())
                .totalprice(0.0)
                .quantity(0)
                .build();
    }

    private Cart findOrCreateCartByUser(Long customerId) {
        return cartRepository.findByCCustomer_CustomerId(customerId)
                .orElseGet(() -> {
//                    CCustomer CCustomer = CCsutomerRepository.findById(customerId)
                    CCustomer cCustomer = cCustomerRepo.findById(customerId) //TODO(joshkuei): Correcte typo CCsutomerRepository to cCustomerRepo
                            .orElseThrow(() -> new EntityNotFoundException("找不到使用者 ID: " + customerId));
                    Cart newCart = new Cart();
                    newCart.setCCustomer(cCustomer); // TODO(joshkuei): Correcte variable name
                    newCart.setCreateat(LocalDateTime.now());
                    newCart.setUpdateat(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });
    }

    private Cart findCartByUser(Long customerId) {
        return cartRepository.findByCCustomer_CustomerId((long) customerId)
                .orElseThrow(() -> new EntityNotFoundException("使用者 " + customerId + " 尚無購物車"));
    }

    private CartDetail findCartDetailInCart(Cart cart, Long cartDetailId) {
        return cart.getCartdetails().stream()
                .filter(d -> d.getCartdetailid().equals(cartDetailId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("在購物車中找不到項目 ID: " + cartDetailId));
    }

    private void removeItemFromCartInternal(Cart cart, CartDetail detail) {
        cart.removeCartDetail(detail); // 使用輔助方法移除
    }

}

