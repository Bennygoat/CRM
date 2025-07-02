package com.example.demo.service.erp;

import com.example.demo.dto.erp.ProductHomepageDto;
import com.example.demo.dto.erp.ProductResponseDTO;
import com.example.demo.entity.*;
import com.example.demo.dto.erp.ProductSimpleDTO;
import com.example.demo.entity.ProductCategory;
import com.example.demo.entity.Unit;
import com.example.demo.exception.DataConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProductCategoryRepository;
import com.example.demo.repository.ProductImgRepository;
import com.example.demo.repository.UnitRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.dto.erp.ProductCreateDTO;
import com.example.demo.dto.erp.ProductUpdateDTO;
import com.example.demo.repository.ProductRepository;
import com.example.demo.specification.ProductSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final UnitRepository unitRepository;
    private final ProductImgRepository productImgRepository;

    // 定義圖片儲存的根目錄
    private final Path rootLocation = Paths.get("src/main/resources/static/images/products");
    
    public ProductResponseDTO createProductFromDTO(ProductCreateDTO dto, Long userId) {


        productRepository.findByProductCode(dto.getProductCode()).ifPresent(product->{
            throw new DataConflictException("商品代號'"+dto.getProductCode()+"'已存在");
        });

        ProductCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(()-> new ResourceNotFoundException("找不到商品分類ID為："+dto.getCategoryId()+"的分類"));

        Unit unit = unitRepository.findById(dto.getUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到單位ID為：" + dto.getUnitId()+"的單位"));

        Product product = new Product();
        product.setProductCode(dto.getProductCode());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setBasePrice(dto.getBasePrice());
        product.setTaxType(dto.getTaxType());
        product.setIsActive(true);
        product.setCreatedBy(userId);
        product.setUpdatedBy(userId);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setCategory(category);
        product.setUnit(unit);
        product.setCostMethod(dto.getCostMethod());
        product.setSafetyStockQuantity(0);

        Product savedProduct = productRepository.save(product);
        return ProductResponseDTO.fromEntity(savedProduct);
    }

    public Page<ProductResponseDTO> searchProducts(Long categoryId, Boolean isSalable,
                                                   String keyword, Pageable pageable) {
        
        Specification<Product> spec = ProductSpecification.findByCriteria(categoryId, isSalable, keyword);

        Page<Product> productPage = productRepository.findAll(spec, pageable);
        return productPage.map(ProductResponseDTO::fromEntity);
    }


    public List<ProductSimpleDTO> getAllProductsSimple() {

        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(ProductSimpleDTO::fromEntity)
                .collect(Collectors.toList());
    }
//    public Product findByProductIdAndProductName(Long productId, String productName){
//
//        Product product = productRepository.findById(productId);
//
//        if(product == null){
//            throw new ResourceNotFoundException("找不到商品ID為"+productId+"的商品");
//        }
//        if(!product.getName().equals(productName)){
//
//        }
//    }

    public ProductResponseDTO getProductById(Long productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("找不到商品ID"+productId+"的商品"));
        return ProductResponseDTO.fromEntity(product);
    }
    
    public ProductResponseDTO updateProductFromDTO(Long productId, ProductUpdateDTO updatedInfo, Long userId) {
        
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到商品ID為 " + productId + " 的商品"));

        ProductCategory category = categoryRepository.findById(updatedInfo.getCategoryId())
                        .orElseThrow(()->new ResourceNotFoundException("找不到商品分類ID為"+updatedInfo.getCategoryId()+"的分類"));
        Unit unit = unitRepository.findById(updatedInfo.getUnitId())
                        .orElseThrow(()->new ResourceNotFoundException("找不到單位iD為"+updatedInfo.getUnitId()+"的單位"));

        existingProduct.setName(updatedInfo.getName());
        existingProduct.setDescription(updatedInfo.getDescription());
        existingProduct.setCategory(category);
        existingProduct.setUnit(unit);
        existingProduct.setBasePrice(updatedInfo.getBasePrice());
//        existingProduct.setTaxType(updatedInfo.getTaxType());
        existingProduct.setIsSalable(updatedInfo.getIsSalable());
        existingProduct.setUpdatedBy(userId);

        Product updatedProduct = productRepository.save(existingProduct);
        return ProductResponseDTO.fromEntity(updatedProduct);
    }


    public void deleteProduct(Long productId, Long userId) {
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("找不到商品ID 為 " + productId + " 的產品"));

        product.setIsActive(false);
        product.setUpdatedBy(userId);

        
        productRepository.save(product);
    }

    public ProductResponseDTO restoreProduct(Long productId, Long userId) {
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到商品ID 為 " + productId + " 的產品"));

        
        product.setIsActive(true);
        product.setUpdatedBy(userId);

        Product restoredProduct = productRepository.save(product);
        return ProductResponseDTO.fromEntity(restoredProduct);
}

    /**
     * 【修改】獲取所有商品，並轉換為用於首頁顯示的 DTO 列表 (支援多張圖片)
     * @return List<ProductHomepageDto>
     */
    public List<ProductHomepageDto> getAllProductsForHomepage() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(product -> {
            ProductHomepageDto dto = new ProductHomepageDto();
            dto.setProductId(product.getProductId());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getBasePrice());

            // 【修改】處理商品圖片，撈出最多兩張圖的 URL
            List<String> twoImageUrls = product.getProductimgs().stream()
                    .map(ProductImg::getImgurl) // 將 ProductImg 物件轉換為其 URL 字串
                    .limit(2) // 最多只取 2 張
                    .collect(Collectors.toList()); // 收集成一個 List

            dto.setImageUrls(twoImageUrls);
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 【全新增加】根據分類ID獲取商品，並轉換為 DTO 列表
     * @param categoryId 商品分類的 ID
     * @return List<ProductHomepageDto>
     */
    @Transactional(readOnly = true)
    public List<ProductHomepageDto> getProductsByCategory(Long categoryId) {
        // 1. 呼叫 Repository 的新方法，直接從資料庫撈出篩選後的商品
        List<Product> products = productRepository.findByCategory_CategoryId(categoryId);

        // 2. 後續的轉換邏輯與 getAllProductsForHomepage 方法完全相同
        return products.stream().map(product -> {
            ProductHomepageDto dto = new ProductHomepageDto();
            dto.setProductId(product.getProductId());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getBasePrice());

            List<String> twoImageUrls = product.getProductimgs().stream()
                    .map(ProductImg::getImgurl)
                    .limit(2)
                    .collect(Collectors.toList());

            dto.setImageUrls(twoImageUrls);
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 【全新增加】儲存上傳的商品圖片
     * @param productId 商品 ID
     * @param file 上傳的圖片檔案
     * @return 返回儲存後的 ProductImg 物件
     * @throws IOException 檔案儲存失敗時拋出
     */
    @Transactional
    public ProductImg saveProductImage(Long productId, MultipartFile file) throws IOException {
        // 1. 檢查商品是否存在
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到 ID 為 " + productId + " 的商品"));

        if (file.isEmpty()) {
            throw new IllegalArgumentException("無法儲存空檔案");
        }

        // 2. 產生一個獨一無二的檔案名稱，避免重複
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // 3. 將檔案儲存到我們指定的目錄下
        try (InputStream inputStream = file.getInputStream()) {
            Path destinationFile = this.rootLocation.resolve(uniqueFilename)
                    .normalize().toAbsolutePath();
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }

        // 4. 產生前端可以存取的公開 URL
        String imageUrl = "/images/products/" + uniqueFilename;

        // 5. 建立新的 ProductImg 實體並存入資料庫
        ProductImg newProductImg = new ProductImg();
        newProductImg.setProduct(product);
        newProductImg.setImgurl(imageUrl); // 使用您的實體中正確的 setter 方法

        return productImgRepository.save(newProductImg);
    }

    /**
     * 【全新增加】建立一個新商品，並同時處理圖片上傳
     * @param createDTO 包含商品基本資料的 DTO
     * @param files 上傳的圖片檔案列表
     * @return 建立完成的 Product 物件
     * @throws IOException 檔案儲存失敗時拋出
     */
    @Transactional
    public Product createProductWithImages(ProductCreateDTO createDTO, List<MultipartFile> files) throws IOException {
        // 1. 建立並儲存 Product 物件本身
        Product newProduct = new Product();
        newProduct.setProductCode(createDTO.getProductCode());
        newProduct.setName(createDTO.getName());
        newProduct.setDescription(createDTO.getDescription());
        newProduct.setBasePrice(createDTO.getBasePrice());
        newProduct.setSafetyStockQuantity(createDTO.getSafetyStockQuantity());

        // 根據 ID 找到關聯的 Entity 並設定
        ProductCategory category = categoryRepository.findById(createDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到分類 ID: " + createDTO.getCategoryId()));
        Unit unit = unitRepository.findById(createDTO.getUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到單位 ID: " + createDTO.getUnitId()));

        newProduct.setCategory(category);
        newProduct.setUnit(unit);

        // 先儲存一次 Product，這樣才能獲得 generated productId
        Product savedProduct = productRepository.save(newProduct);

        // 2. 處理並儲存圖片
        if (files != null && !files.isEmpty()) {
            List<ProductImg> productImgs = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    // 呼叫我們之前寫好的儲存單一圖片的邏輯
                    ProductImg savedImg = this.saveProductImage(savedProduct.getProductId(), file);
                    productImgs.add(savedImg);
                }
            }
            // 將圖片列表設定回 Product 物件 (如果您的 Product entity 有 Set<ProductImg> 的話)
            // savedProduct.setProductimgs(new HashSet<>(productImgs));
        }

        return savedProduct;
    }

    /**
     * 根據 ID 查找商品，並回傳用於商品詳情頁的簡易版 DTO
     */
    public ProductHomepageDto getProductDetailsById(Integer id) {
        Product product = productRepository.findById((long)id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到產品 ID: " + id));
        return convertToHomepageDto(product);
    }

    /**
     * 一個新的輔助方法，用於將 Product 實體轉換為 ProductHomepageDto
     */
    private ProductHomepageDto convertToHomepageDto(Product product) {
        ProductHomepageDto dto = new ProductHomepageDto();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getBasePrice()); // 假設 price 是從 basePrice 來的

        // 處理圖片 URL 列表
        if (product.getProductimgs() != null) {
            List<String> imageUrls = product.getProductimgs().stream()
                    .map(ProductImg::getImgurl)
                    .collect(Collectors.toList());
            dto.setImageUrls(imageUrls);
        } else {
            dto.setImageUrls(Collections.emptyList()); // 如果沒有圖片，給一個空列表
        }

        return dto;
    }

}
