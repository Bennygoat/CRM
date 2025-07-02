package com.example.demo.controller.erp;

import com.example.demo.dto.erp.*;
import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//import com.example.demo.entity.Product;
import com.example.demo.service.erp.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "產品管理API(Product Management)", description = "包含產品建立、多種查詢、簡易產品清單、更新、刪除")
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "新增產品", description = "新增單筆產品或多筆產品")
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductCreateDTO productDTO) {
        Long currentUserId = 1L;

        ProductResponseDTO createdProduct = productService.createProductFromDTO(productDTO, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @Operation(summary = "查詢產品", description = "根據條件，查詢產品列表")
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> searchProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isSalable,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "productId") Pageable pageable) {
        
        
        
        Page<ProductResponseDTO> productPage = productService.searchProducts(
                categoryId, isSalable, keyword, pageable);
        
        return ResponseEntity.ok(productPage);
    }

    @Operation(summary = "查詢單筆產品", description = "根據產品ID，查詢單筆產品")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id){
        ProductResponseDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "獲取所有產品的簡易列表", description = "只返回所有產品的ID和名稱，用於下拉選單等場景")
    @GetMapping("/simple-list")
    public ResponseEntity<List<ProductSimpleDTO>> getAllProductsSimple() {
        List<ProductSimpleDTO> products = productService.getAllProductsSimple();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "更新產品", description = "根據產品ID，更新產品")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody ProductUpdateDTO productDTO) {
        
        Long currentUserId = 1L;
        
        ProductResponseDTO updatedProduct = productService.updateProductFromDTO(id, productDTO, currentUserId);
        
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "刪除產品", description = "根據產品ID，刪除產品")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Long currentUserId = 1L;
        
        productService.deleteProduct(id, currentUserId);
        
        
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "復原產品", description = "根據產品ID，復原產品")
    @PostMapping("/{id}/restore")
    public ResponseEntity<ProductResponseDTO> restoreProduct(@PathVariable Long id) {
        Long currentUserId = 1L;

        ProductResponseDTO restoredProduct = productService.restoreProduct(id, currentUserId);
        
        
        return ResponseEntity.ok(restoredProduct);
    }


    /**
     * 【新增】提供給首頁獲取所有商品的 API 端點
     * @return 返回包含所有商品的 JSON 陣列
     */
    @GetMapping("/homepage")
    public ResponseEntity<List<ProductHomepageDto>> getAllProductsForHomepage() {
        List<ProductHomepageDto> products = productService.getAllProductsForHomepage();
        return ResponseEntity.ok(products);
    }

    /**
     * 【全新增加】提供給前台，根據分類 ID 獲取商品列表的專用 API。
     * 使用 @PathVariable 將 URL 路徑中的一部分作為參數。
     * @param categoryId 商品分類的 ID，來自 URL 路徑，例如 /api/products/category/1 中的 1
     * @return 返回該分類下的商品列表
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductHomepageDto>> getProductsByCategoryId(@PathVariable Long categoryId) {
        List<ProductHomepageDto> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    /**
     * 【全新增加】建立新商品的 API 端點 (包含圖片上傳)
     * 使用 @RequestPart 來同時接收 JSON 和檔案
     */
    @PostMapping("/add")
    public ResponseEntity<?> createProduct(
            @RequestPart("productData") ProductCreateDTO createDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        try {
            Product newProduct = productService.createProductWithImages(createDTO, files);
            // 這裡可以回傳一個更詳細的 DTO，但為了簡單起見，我們先回傳 ID
            return ResponseEntity.ok(Map.of(
                    "message", "商品建立成功",
                    "productId", newProduct.getProductId()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("圖片儲存失敗: " + e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @Operation(summary = "取得單一產品(簡易版)", description = "取得單一產品的少量必要資訊，用於商品詳情頁")
    @GetMapping("/details/{id}")
    public ResponseEntity<ProductHomepageDto> getProductDetailsById(@PathVariable Integer id) {
        ProductHomepageDto productDetails = productService.getProductDetailsById(id);
        return ResponseEntity.ok(productDetails);
    }

}
