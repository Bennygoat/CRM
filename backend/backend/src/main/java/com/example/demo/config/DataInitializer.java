package com.example.demo.config;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import com.example.demo.config.util.InventoryFaker;
import com.example.demo.entity.*;
import com.example.demo.enums.AuthorityCode;
import com.example.demo.enums.VIPLevelEnum;
import com.example.demo.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.github.javafaker.Faker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
@Slf4j
@RequiredArgsConstructor // ✨ 3. 使用 Lombok 簡化建構子
public class DataInitializer {

    // ✨ 4. 將所有依賴改為 final，並使用建構子注入
    private final ProductRepository productRepository;
    private final UnitRepository unitRepository;
    private final ProductCategoryRepository categoryRepository;
    private final UserRepo userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthorityRepo authorityRepo;
    private final VIPLevelRepo vipLevelRepo;
    private final WarehouseRepository warehouseRepository; // ✨ 5. 新增 WarehouseRepository
    private final InventoryRepository inventoryRepository; // ✨ 6. 新增 InventoryRepository
    private final InventoryFaker inventoryFaker;         // ✨ 7. 注入 InventoryFaker

    private static final Long SYSTEM_USER_ID = 1L;

    // ... (其他常數不變)
    private static final String[] ICE_CREAM_PREFIXES = {"經典", "純濃", "雪藏", "夏日", "莊園", "極致", "鮮果"};
    private static final String[] ICE_CREAM_NAMES = {"香草", "巧克力","藍莓","花生","咖啡", "香蕉","薄荷巧克力","OREO","草莓", "抹茶", "蘭姆葡萄", "海鹽焦糖", "芒果優格", "豆乳芝麻", "燕麥奶"};
    private static final String[] ICE_CREAM_SUFFIXES = {"冰淇淋", "雪酪", "聖代", "冰棒", "雪糕"};


    @PostConstruct
    public void initCriticalData() {
        initAuthoritiesAndAdmin();
        createVipLevelsIfNotExist();
    }

    @Bean
    @Profile("dev")
    public CommandLineRunner initDatabase() {
        return args -> {
            if (productRepository.count() > 0) {
                log.info("資料庫中已有商品，跳過 dev profile 的假資料產生程序。");
                return;
            }
            log.info("偵測到為 dev 環境且無商品資料，開始產生基礎假資料...");

            try {
                // ... (createSystemUser, createAndSaveUnits, createAndSaveCategories 的邏輯不變)
                List<Unit> savedUnits = createAndSaveUnits();
                List<ProductCategory> savedCategories = createAndSaveCategories();
                List<Warehouse> savedWarehouses = createAndSaveWarehouses(); // ✨ 8. 建立倉庫

                // --- 產生商品 ---
                log.info("開始產生20筆商品假資料...");
                Faker faker = new Faker(Locale.TAIWAN);
                List<Product> productList = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    // ... (產生 Product 的邏輯不變)
                    Product product = new Product();
                    String name = ICE_CREAM_PREFIXES[ThreadLocalRandom.current().nextInt(ICE_CREAM_PREFIXES.length)] +
                            ICE_CREAM_NAMES[ThreadLocalRandom.current().nextInt(ICE_CREAM_NAMES.length)] +
                            ICE_CREAM_SUFFIXES[ThreadLocalRandom.current().nextInt(ICE_CREAM_SUFFIXES.length)];
                    String uniqueCode = "P" + String.format("%06d", i + 1);
                    product.setProductCode(uniqueCode);
                    product.setName(name + " " + uniqueCode);
                    product.setDescription(faker.lorem().paragraph(2));
                    Unit randomUnit = savedUnits.get(ThreadLocalRandom.current().nextInt(savedUnits.size()));
                    ProductCategory randomCategory = savedCategories.get(ThreadLocalRandom.current().nextInt(savedCategories.size()));
                    product.setUnit(randomUnit);
                    product.setCategory(randomCategory);
                    double price = ThreadLocalRandom.current().nextDouble(45.0, 350.0);
                    product.setBasePrice(BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP));
                    product.setCostMethod("AVERAGE");
                    product.setTaxType("TAXABLE");
                    product.setSafetyStockQuantity(ThreadLocalRandom.current().nextInt(10, 100));
                    product.setCreatedBy(SYSTEM_USER_ID);
                    product.setUpdatedBy(SYSTEM_USER_ID);
                    productList.add(product);
                }
                List<Product> savedProducts = productRepository.saveAll(productList);
                log.info("20筆商品假資料已成功寫入資料庫！");

                // ✨✨✨ 9. 【全新增加的邏輯】為每個商品在每個倉庫建立初始庫存 ✨✨✨
                log.info("開始為商品建立初始庫存記錄...");
                List<Inventory> inventoryList = new ArrayList<>();
                for (Product product : savedProducts) {
                    for (Warehouse warehouse : savedWarehouses) {
                        // 呼叫 InventoryFaker 來建立一個庫存物件
                        Inventory inventory = inventoryFaker.createFakeInventory(product, warehouse);
                        // 確保初始庫存至少有 50
                        if (inventory.getCurrentStock().compareTo(new BigDecimal("50")) < 0) {
                            inventory.setCurrentStock(BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(50, 151)));
                        }
                        inventoryList.add(inventory);
                    }
                }
                inventoryRepository.saveAll(inventoryList);
                log.info("已成功為 {} 個商品在 {} 個倉庫中建立共 {} 筆庫存記錄。", savedProducts.size(), savedWarehouses.size(), inventoryList.size());
                // ✨✨✨ 邏輯結束 ✨✨✨

            } catch (Exception e) {
                log.error("Data initialization failed", e);
                throw e;
            }
        };
    }

    // ✨ 10. 新增一個建立倉庫的方法
    private List<Warehouse> createAndSaveWarehouses() {
        if (warehouseRepository.count() > 0) {
            return warehouseRepository.findAll();
        }
        log.info("建立倉庫資料...");
        List<Warehouse> warehouses = new ArrayList<>();
        Arrays.asList("台北總倉", "台中冷凍倉", "高雄物流中心").forEach(name -> {
            Warehouse warehouse = new Warehouse();
            warehouse.setName(name);
            warehouse.setCreatedBy(SYSTEM_USER_ID);
            warehouse.setCreatedAt(LocalDateTime.now());
            warehouses.add(warehouse);
        });
        return warehouseRepository.saveAll(warehouses);
    }

    // ... (其他 createAndSaveUnits, initAuthoritiesAndAdmin 等方法不變)
    private List<Unit> createAndSaveUnits() {
        if (unitRepository.count() > 0) {
            return unitRepository.findAll();
        }
        System.out.println("建立商品單位...");
        List<Unit> units = new ArrayList<>();
        Arrays.asList("個", "箱", "打", "公克","支","杯","盒").forEach(name -> {
            Unit unit = new Unit();
            unit.setName(name);

            unit.setCreatedBy(SYSTEM_USER_ID);
            unit.setUpdatedBy(SYSTEM_USER_ID);
            units.add(unit);
        });
        return unitRepository.saveAll(units);
    }
    private List<ProductCategory> createAndSaveCategories() {
        if (categoryRepository.count() > 0) {
            return categoryRepository.findAll();
        }
        System.out.println("建立商品分類...");
        List<ProductCategory> categories = new ArrayList<>();
        Arrays.asList("經典冰淇淋", "水果雪酪","雪糕系列","巧酥雪糕系列", "季節限定", "純素系列", "品牌聯名系列","週邊商品").forEach(name -> {
            ProductCategory category = new ProductCategory();
            category.setName(name);

            category.setCreatedBy(SYSTEM_USER_ID);
            category.setUpdatedBy(SYSTEM_USER_ID);
            categories.add(category);
        });
        return categoryRepository.saveAll(categories);
    }
    private void initAuthoritiesAndAdmin(){
        // 1. 建立所有 Enum 權限
        for (AuthorityCode code : AuthorityCode.values()) {
            if (!authorityRepo.existsByCode(code.getCode())) {
                authorityRepo.save(code.toAuthorityEntity());
            }
        }

        // 2. 建立 admin 使用者
        if (userRepository.findByAccount("admin").isEmpty()) {
            List<Authority> allAuthorities = authorityRepo.findAll();
            User admin = User.builder()
                    .account("admin")
                    .userName("超級管理員")
                    .password(passwordEncoder.encode("Admin123!@#"))
                    .email("admin@system.com")
                    .roleName("ADMIN")
                    .authorities(allAuthorities)
                    .isActive(true)
                    .isDeleted(false)
                    .accessStartDate(LocalDate.now())
                    .accessEndDate(LocalDate.now().plusYears(10))
                    .build();
            userRepository.save(admin);
        }
    }
    private void createVipLevelsIfNotExist() {
        for (VIPLevelEnum levelEnum : VIPLevelEnum.values()) {
            if (!vipLevelRepo.existsById(levelEnum.name())) {
                vipLevelRepo.save(levelEnum.toEntity());
            }
        }
    }
}

