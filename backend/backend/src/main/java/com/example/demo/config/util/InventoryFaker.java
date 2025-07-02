package com.example.demo.config.util;

import com.example.demo.entity.Inventory;
import com.example.demo.entity.Product;
import com.example.demo.entity.Warehouse;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

@Component
public class InventoryFaker {

    private final Faker faker;
    private final Random random = new Random();

    public InventoryFaker() {
        this.faker = new Faker();
    }

    public Inventory createFakeInventory(Product product, Warehouse warehouse) {
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        // Initial stock for a new inventory item can be 0 or a small random amount
        // The DataInitializer will then ensure it's at least 50.
        inventory.setCurrentStock(BigDecimal.valueOf(random.nextInt(25))); // e.g., 0-24 initial units
        inventory.setAverageCost(product.getBasePrice() != null ?
                product.getBasePrice().multiply(BigDecimal.valueOf(0.7 + (random.nextDouble() * 0.2))) : // 70-90% of basePrice
                BigDecimal.valueOf(faker.number().randomDouble(2, 5, 500))); // Fallback if no basePrice
        inventory.setUnitsOnOrder(BigDecimal.ZERO);
        inventory.setUnitsAllocated(BigDecimal.ZERO);

        // Assuming createdBy and updatedBy are Long user IDs
        // These should ideally come from the security context or a system user ID
        Long systemUserId = 1L; // Placeholder for a system user ID
        inventory.setCreatedBy(systemUserId);
        inventory.setUpdatedBy(systemUserId);

        // createdAt and updatedAt are typically handled by @CreatedDate, @LastModifiedDate with Auditing
        // If not, set them manually:
        // inventory.setCreatedAt(LocalDateTime.now());
        // inventory.setUpdatedAt(LocalDateTime.now());

        return inventory;
    }
}

