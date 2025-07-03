package com.example.demo.repository;

import com.example.demo.entity.Authority;
import com.example.demo.entity.CCustomer;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    // 帳號查詢
    Optional<User> findByAccount(String account);
    // 信箱查詢
    Optional<User> findByEmail(String email);
    // 權限查詢
    List<User> findByAuthorities_Code(String code);
    // 腳色查詢
    List<User> findByRoleName(String roleName);
    // 有無存在腳色
    boolean existsByAccount(String account);
}
