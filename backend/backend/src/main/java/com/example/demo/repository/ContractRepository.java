package com.example.demo.repository;

import com.example.demo.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    /**
     * 檢查給定的商機是否已存在合約。
     * @param opportunityId 商機的 ID。
     * @return 如果合約已存在則返回 true，否則返回 false。
     */
//    boolean existsByOpportunityId(Long opportunityId);
    boolean existsByOpportunityOpportunityId(Long opportunityId);
}
