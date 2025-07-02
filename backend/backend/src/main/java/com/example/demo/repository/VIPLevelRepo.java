package com.example.demo.repository;

import com.example.demo.entity.VIPLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VIPLevelRepo extends JpaRepository<VIPLevel, String> {
    List<VIPLevel> findAllByOrderByUpgradeThresholdAsc(); // 依升級門檻由小到大排序
}
