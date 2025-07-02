package com.example.demo.repository;

import com.example.demo.entity.CCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CCustomerRepo extends JpaRepository<CCustomer, Long> , JpaSpecificationExecutor<CCustomer> {
    Optional<CCustomer> findByAccount(String account);
    Optional<CCustomer> findByEmail(String email);
    boolean existsByEmail(String email); // 是否已存在某信箱帳號
}
