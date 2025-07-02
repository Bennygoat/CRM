package com.example.demo.config.util;


import com.example.demo.dto.erp.ProductCreateDTO;
import com.example.demo.entity.ProductCategory;
import com.example.demo.entity.Supplier;
import com.example.demo.entity.Unit;
import com.example.demo.repository.ProductCategoryRepository;
import com.example.demo.repository.SupplierRepository;
import com.example.demo.repository.UnitRepository;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Component
public class ProductFaker {

    private final Faker faker;
    private final Random random = new Random();

    @Autowired
    private ProductCategoryRepository categoryRepository;
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private SupplierRepository supplierRepository;

    public ProductFaker() {
        this.faker = new Faker(new Locale("en-US")); // Products often have English names
    }

    public ProductCreateDTO generateFakeProductCreateDTO() {
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setProductCode(faker.commerce().promotionCode(6));
        dto.setName(faker.commerce().productName());
        dto.setDescription(faker.lorem().sentence());

        // Fetch or create dummy category, unit, supplier
        ProductCategory category = categoryRepository.findAll().stream().findAny()
                .orElseGet(() -> {
                    ProductCategory cat = new ProductCategory();
                    cat.setName(faker.commerce().department());
//                    cat.setDescription(faker.lorem().sentence());
                    return categoryRepository.save(cat);
                });
        Unit unit = unitRepository.findAll().stream().findAny()
                .orElseGet(() -> {
                    Unit u = new Unit();
                    u.setName(faker.letterify("pcs")); // common unit
//                    u.setDescription("Pieces");
//                    u.setCreatedAt(LocalDateTime.now());
//                    u.setUpdatedAt(LocalDateTime.now());
                    u.setCreatedBy(1L); // System user
                    u.setUpdatedBy(1L); // System user
                    return unitRepository.save(u);
                });
        Supplier supplier = supplierRepository.findAll().stream().findAny()
                .orElseGet(() -> {
                    Supplier sup = new Supplier();
                    sup.setName(faker.company().name());
                    sup.setSupplierCode("SUP-" + faker.number().digits(6)); // Generate a unique supplier code
//                    sup.setCreatedAt(LocalDateTime.now());
//                    sup.setCreatedBy(1L); // Assuming SYSTEM_USER_ID or a default
//                    sup.setActive(true); // Default as per entity, but good to be explicit
                    sup.setCreatedBy(1L); // Assuming SYSTEM_USER_ID or a default
                    sup.setActive(true);
                    return supplierRepository.save(sup);
                });

        dto.setCategoryId(category.getCategoryId());
        dto.setUnitId(unit.getUnitId());
//        dto.setPreferredSupplierId(supplier.getSupplierId());

//        dto.setIsPurchasable(true);
        dto.setIsSalable(true);
        dto.setBasePrice(BigDecimal.valueOf(faker.number().randomDouble(2, 10, 1000)));
        dto.setTaxType("VAT_EXCLUSIVE"); // Example tax type
        dto.setCostMethod("AVERAGE");
        dto.setSafetyStockQuantity(faker.number().numberBetween(5, 50));
        dto.setIsActive(true);
//        dto.setCreatedBy(1L); // System or default user ID
//        dto.setUpdatedBy(1L); // System or default user ID
        // CreatedAt and UpdatedAt are often handled by JPA auditing or @PrePersist

        return dto;
    }

    public List<ProductCreateDTO> generateFakeProductCreateDTOs(int count) {
        List<ProductCreateDTO> dtos = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            dtos.add(generateFakeProductCreateDTO());
        }
        return dtos;
    }
}
