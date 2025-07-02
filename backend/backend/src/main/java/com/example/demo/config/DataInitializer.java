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

import com.example.demo.config.util.CCustomerFaker;
import com.example.demo.config.util.InventoryFaker;
import com.example.demo.dto.request.CCustomerRegisterRequest;
import com.example.demo.entity.*;
import com.example.demo.enums.AuthorityCode;
import com.example.demo.enums.VIPLevelEnum;
import com.example.demo.repository.*;
import com.example.demo.service.CCustomerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.github.javafaker.Faker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Locale;
//import java.util.concurrent.ThreadLocalRandom;

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
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryFaker inventoryFaker;
    private final CCustomerRepo cCustomerRepo;
    private final CCustomerService cCustomerService;
    private final CCustomerFaker cCustomerFaker;
    private final InventoryMovementRepository inventoryMovementRepository;

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
    @Transactional
    public CommandLineRunner initDatabase() {
        return args -> {
            // --- 產生基礎資料 (產品、客戶、當前庫存等) ---
            if (productRepository.count() == 0) {
                log.info("偵測到為 dev 環境且無商品資料，開始產生基礎假資料...");
                // ... (您現有的所有產生 Product, Inventory, CCustomer 的邏輯都維持不變)
                List<Unit> savedUnits = createAndSaveUnits();
                List<ProductCategory> savedCategories = createAndSaveCategories();
                List<Warehouse> savedWarehouses = createAndSaveWarehouses();
                List<Product> savedProducts = createAndSaveProducts(savedUnits, savedCategories);
                createInventories(savedProducts, savedWarehouses);
                createCCustomers();
            } else {
                log.info("資料庫中已有商品，跳過基礎假資料產生程序。");
            }

            // --- [核心修改] 在所有基礎資料都產生完畢後，才執行歷史庫存異動的產生 ---
            seedHistoricalMovements();

            log.info("--- [DEV PROFILE] 所有假資料初始化程序完成 ---");
        };
    }

    // =================================================================
    // [新增] 產生歷史庫存異動紀錄的私有方法
    // =================================================================
    private void seedHistoricalMovements() {
        if (inventoryMovementRepository.count() > 0) {
            log.warn("資料庫中已有庫存異動紀錄，跳過歷史資料產生程序。");
            return;
        }
        log.info("偵測到無庫存異動紀錄，開始執行 HistoricalMovement Faker...");

        List<Product> products = productRepository.findAll();
        List<Warehouse> warehouses = warehouseRepository.findAll();
        User systemUser = userRepository.getReferenceById(SYSTEM_USER_ID);

        if (products.isEmpty() || warehouses.isEmpty()) {
            log.error("無法產生歷史庫存異動，因為產品或倉庫資料不存在！");
            return;
        }

        List<InventoryMovement> movements = new ArrayList<>();
        LocalDate startDate = LocalDate.now().minusYears(2).withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().minusMonths(1);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (ThreadLocalRandom.current().nextInt(10) > 3) continue;

            int transactionsPerDay = ThreadLocalRandom.current().nextInt(1, 4);
            for (int i = 0; i < transactionsPerDay; i++) {
                Product randomProduct = products.get(ThreadLocalRandom.current().nextInt(products.size()));
                Warehouse randomWarehouse = warehouses.get(ThreadLocalRandom.current().nextInt(warehouses.size()));
                LocalDateTime movementDate = date.atTime(
                        ThreadLocalRandom.current().nextInt(9, 19),
                        ThreadLocalRandom.current().nextInt(0, 60)
                );

                if (ThreadLocalRandom.current().nextBoolean()) {
                    BigDecimal quantity = BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(50, 201));
                    BigDecimal unitCost = randomProduct.getBasePrice().multiply(new BigDecimal("0.5"));
                    movements.add(createMovement(randomProduct, randomWarehouse, "IN_PURCHASE", quantity, unitCost, movementDate, systemUser));
                } else {
                    BigDecimal quantity = BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(1, 21));
                    BigDecimal unitCost = randomProduct.getBasePrice().multiply(new BigDecimal("0.5"));
                    movements.add(createMovement(randomProduct, randomWarehouse, "OUT_SALE", quantity.negate(), unitCost, movementDate, systemUser));
                }
            }
        }

        log.info("共產生 {} 筆歷史庫存異動紀錄，正在存入資料庫...", movements.size());
        inventoryMovementRepository.saveAll(movements);
        log.info("歷史庫存異動紀錄產生完畢！");
    }

    private InventoryMovement createMovement(Product product, Warehouse warehouse, String type, BigDecimal qty, BigDecimal cost, LocalDateTime date, User user) {
        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(product);
        movement.setWarehouse(warehouse);
        movement.setMovementType(type);
        movement.setQuantityChange(qty);
        movement.setUnitCostAtMovement(cost);
        movement.setTotalCostChange(cost.multiply(qty));
        movement.setMovementDate(date);
        movement.setRecordedBy(user);
        movement.setCurrentStockAfterMovement(BigDecimal.ZERO); // 在此情境下不需精確計算
        return movement;
    }


    // =================================================================
    // 將您原有的邏輯稍微重構成私有方法，提高可讀性
    // =================================================================
    private List<Product> createAndSaveProducts(List<Unit> units, List<ProductCategory> categories) {
        log.info("開始產生20筆商品假資料...");
        Faker faker = new Faker(Locale.TAIWAN);
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Product product = new Product();
            String name = ICE_CREAM_PREFIXES[ThreadLocalRandom.current().nextInt(ICE_CREAM_PREFIXES.length)] +
                    ICE_CREAM_NAMES[ThreadLocalRandom.current().nextInt(ICE_CREAM_NAMES.length)] +
                    ICE_CREAM_SUFFIXES[ThreadLocalRandom.current().nextInt(ICE_CREAM_SUFFIXES.length)];
            String uniqueCode = "P" + String.format("%06d", i + 1);
            product.setProductCode(uniqueCode);
            product.setName(name + " " + uniqueCode);
            product.setDescription(faker.lorem().paragraph(2));
            product.setUnit(units.get(ThreadLocalRandom.current().nextInt(units.size())));
            product.setCategory(categories.get(ThreadLocalRandom.current().nextInt(categories.size())));
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
        return savedProducts;
    }

    private void createInventories(List<Product> products, List<Warehouse> warehouses) {
        log.info("開始為商品建立初始庫存記錄...");
        List<Inventory> inventoryList = new ArrayList<>();
        for (Product product : products) {
            for (Warehouse warehouse : warehouses) {
                Inventory inventory = inventoryFaker.createFakeInventory(product, warehouse);
                if (inventory.getCurrentStock().compareTo(new BigDecimal("50")) < 0) {
                    inventory.setCurrentStock(BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(50, 151)));
                }
                inventoryList.add(inventory);
            }
        }
        inventoryRepository.saveAll(inventoryList);
        log.info("已成功為 {} 個商品在 {} 個倉庫中建立共 {} 筆庫存記錄。", products.size(), warehouses.size(), inventoryList.size());
    }

    private void createCCustomers() {
        if (cCustomerRepo.count() > 0) {
            log.info("B2C customers already exist. Skipping CCustomer generation.");
            return;
        }
        log.info("Database has no B2C customers. Starting CCustomer generation...");
        try {
            int numberOfCustomers = 25;
            List<CCustomerRegisterRequest> fakeRequests = cCustomerFaker.generateFakeCCustomerRequests(numberOfCustomers);
            int successCount = 0;
            for (CCustomerRegisterRequest request : fakeRequests) {
                try {
                    cCustomerService.register(request.getAccount(), request.getCustomerName(), request.getPassword(), request.getEmail(), request.getCustomerTel(), request.getAddress(), request.getBirthday());
                    successCount++;
                } catch (Exception e) {
                    log.error("創建 B2C 假客戶失敗，帳號 [{}]: {}", request.getAccount(), e.getMessage());
                }
            }
            log.info("--- B2C 客戶假資料產生完畢。成功創建 {} / {} 位客戶。 ---", successCount, numberOfCustomers);
        } catch (Exception e) {
            log.error("CCustomer data initialization failed", e);
        }
    }


    // ✨ 10. 新增一個建立倉庫的方法
    private List<Warehouse> createAndSaveWarehouses() {
        if (warehouseRepository.count() > 0) {
            return warehouseRepository.findAll();
        }
        log.info("建立倉庫資料...");
        List<Warehouse> warehouses = new ArrayList<>();
        Arrays.asList("零售出貨倉", "成品倉", "超商暫存倉").forEach(name -> {
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

