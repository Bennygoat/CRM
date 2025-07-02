
package com.example.demo.repository;

import com.example.demo.entity.Cart;
import com.example.demo.entity.CartDetail;
import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    // 根據購物車ID和商品ID查找購物車項目
    Optional<CartDetail> findByCart_CartidAndProduct_ProductId(Long cartid, Long productId); //TODO(joshkuei): Rename productid to productId

    Optional<CartDetail> findByCartAndProduct(Cart cart, Product product); //TODO(joshkuei): Add for CartService.

    // ✨ 新增這個方法：根據購物車和一個商品列表，找出所有已存在的明細
    List<CartDetail> findByCartAndProductIn(Cart cart, Collection<Product> products);
}