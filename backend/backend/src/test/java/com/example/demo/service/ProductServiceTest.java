package com.example.demo.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.demo.dto.erp.ProductCreateDTO;
import com.example.demo.dto.erp.ProductResponseDTO;
import com.example.demo.dto.erp.ProductUpdateDTO;
import com.example.demo.entity.ProductCategory;
import com.example.demo.entity.Unit;
import com.example.demo.repository.ProductCategoryRepository;
import com.example.demo.repository.UnitRepository;
import com.example.demo.service.erp.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository categoryRepository;

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private ProductService productService;


    @Test
    void testCreateProductSuccess(){
         // 1. 準備 (Arrange)
        // 模擬一個要被儲存的 Product 物件
        ProductCreateDTO productCreateDTO = new ProductCreateDTO();
        productCreateDTO.setProductCode("TEST001");
        productCreateDTO.setName("測試商品");
        productCreateDTO.setCategoryId(1L);
        productCreateDTO.setUnitId(1L);
        productCreateDTO.setBasePrice(new BigDecimal("100.00"));
        productCreateDTO.setTaxType("TAXABLE");
        productCreateDTO.setCostMethod("AVERAGE");


        Unit mockUnit = new Unit();
        mockUnit.setUnitId(1L);
        mockUnit.setName("個");

        ProductCategory mockCategory = new ProductCategory();
        mockCategory.setCategoryId(1L);
        mockCategory.setName("測試分類");


        // 模擬儲存成功後，資料庫回傳的 Product 物件 (多了 ID 和時間)
        Product savedProduct = new Product();
        savedProduct.setProductId(1L);
        savedProduct.setProductCode("TEST001");
        savedProduct.setName("測試商品");
        savedProduct.setUnit(mockUnit);
        savedProduct.setCategory(mockCategory);


        // 設定 Mockito：當 productRepository.save() 方法被呼叫時，就回傳我們上面準備好的 savedProduct
        when(productRepository.findByProductCode("TEST001")).thenReturn(Optional.empty());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(unitRepository.findById(1L)).thenReturn(Optional.of(mockUnit));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);


        // 2. 執行 (Act)
        // 實際呼叫我們「即將要開發」的 createProduct 方法
        ProductResponseDTO result = productService.createProductFromDTO(productCreateDTO, 1L);


        // 3. 斷言 (Assert)
        // 驗證回傳的結果是否如我們預期
        assertNotNull(result);
        assertEquals("TEST001", result.getProductCode());
        assertEquals("測試商品", result.getName());

    }

    @Test
    void testSearchProducts_shouldReturnPagedResult() {
        // 1. 準備 (Arrange)
        // 模擬查詢條件
        Long categoryId = 1L;
        Boolean isSalable = true;
        String keyword = "測試";
        Pageable pageable = PageRequest.of(0, 10); // 查詢第 0 頁，每頁 10 筆

        // 模擬資料庫中符合條件的產品
        Product product = new Product();
        product.setProductId(1L);
        product.setName("測試商品");
        List<Product> productList = Collections.singletonList(product);

        // 模擬一個分頁結果物件
        Page<Product> expectedPage = new PageImpl<>(productList, pageable, 1);

        // 設定 Mockito：當 repository 被呼叫時 (使用任何 Specification 和 Pageable)，就回傳我們預設的 Page 物件
        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        // 2. 執行 (Act)
        // 呼叫我們即將開發的 searchProducts 方法
        Page<ProductResponseDTO> actualPage = productService.searchProducts(categoryId, isSalable, keyword, pageable);

        // 3. 斷言 (Assert)
        // 驗證回傳的分頁結果是否符合預期
        assertNotNull(actualPage);
        assertEquals(1, actualPage.getTotalElements()); // 總筆數為 1
        assertEquals(1, actualPage.getTotalPages()); // 總頁數為 1
        assertEquals("測試商品", actualPage.getContent().get(0).getName()); // 內容正確
    }

    @Test
    void testUpdateProduct_Success() {
        // 1. 準備 (Arrange)
        Long productId = 1L;
        Product existingProduct = new Product(); // 模擬資料庫中已存在的產品
        existingProduct.setProductId(productId);
        existingProduct.setName("舊品名");

        ProductUpdateDTO updateDTO = new ProductUpdateDTO();
        updateDTO.setName("新品名");
        updateDTO.setBasePrice(new BigDecimal("150.00"));
        updateDTO.setCategoryId(1L);
        updateDTO.setUnitId(1L);
        updateDTO.setTaxType("TAXABLE");
        updateDTO.setIsSalable(true);

        Unit mockUnit = new Unit();
        mockUnit.setUnitId(1L);
        mockUnit.setName("個");

        ProductCategory mockCategory = new ProductCategory();
        mockCategory.setCategoryId(1L);
        mockCategory.setName("測試分類");

        Product updatedProduct = new Product();
        updatedProduct.setProductId(productId);
        updatedProduct.setName("新品名");
        updatedProduct.setBasePrice(new BigDecimal("150.00"));

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(unitRepository.findById(1L)).thenReturn(Optional.of(mockUnit));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // 2. 執行 (Act)
        ProductResponseDTO result = productService.updateProductFromDTO(productId, updateDTO, 1L);


        // 3. 斷言 (Assert)
        assertNotNull(result);
        assertEquals("新品名", result.getName()); // 驗證品名是否已更新
        assertEquals(0, new BigDecimal("150.00").compareTo(result.getBasePrice())); // 驗證價格是否已更新
    }

    // 測試更新不存在的產品
    @Test
    void testUpdateProduct_NotFound_shouldThrowException() {
        // 1. 準備 (Arrange)
        Long nonExistentProductId = 99L;
        ProductUpdateDTO updateDTO = new ProductUpdateDTO();
        updateDTO.setName("新品名");
        updateDTO.setCategoryId(1L);
        updateDTO.setUnitId(1L);
        updateDTO.setTaxType("TAXABLE");
        updateDTO.setIsSalable(true);


        // 設定 Mockito：當 findById 被呼叫時，回傳一個空的 Optional，表示找不到
        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());


        // 2. 執行與斷言 (Act & Assert)
        // 驗證當呼叫 updateProduct 時，會拋出 RuntimeException (或更精確的自訂例外)
        assertThrows(RuntimeException.class, () -> {
            productService.updateProductFromDTO(nonExistentProductId, updateDTO, 1L);
        });
    }


    @Test
    void testDeleteProduct_shouldSetIsActiveToFalse() {
        // 1. 準備 (Arrange)
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setProductId(productId);
        existingProduct.setIsActive(true); // 產品原本是啟用的

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // 2. 執行 (Act)
        productService.deleteProduct(productId, 1L); // 由 user 1 刪除

        // 3. 斷言 (Assert)
        // 我們要驗證 'save' 方法被呼叫時，傳入的 product 物件其 is_active 是 false

        // ArgumentCaptor 可以捕獲方法被呼叫時傳入的參數
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        // 驗證 save 方法確實被以某個 Product 物件呼叫了
        verify(productRepository).save(productCaptor.capture());

        // 取得被傳入的 Product 物件
        Product savedProduct = productCaptor.getValue();

        // 斷言它的 is_active 狀態是 false
        assertFalse(savedProduct.getIsActive());
    }

}
