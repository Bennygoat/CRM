package com.example.demo.service;

import com.example.demo.entity.VIPLevel;
import com.example.demo.repository.CCustomerRepo;
import com.example.demo.repository.VIPLevelRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class VIPLevelService {
    private final VIPLevelRepo vipLevelRepository;

    public VIPLevelService(VIPLevelRepo vipLevelRepository, CCustomerRepo cCustomerRepo) {
        this.vipLevelRepository = vipLevelRepository;
    }


    public VIPLevel findLevelBySpending(Long spending) {
        return vipLevelRepository.findAllByOrderByUpgradeThresholdAsc().stream()
                .filter(level -> spending >= level.getUpgradeThreshold())
                .reduce((first, second) -> second) // 取符合的最後一個（最高等級）
                .orElseThrow(() -> new RuntimeException("找不到符合的 VIP 等級"));
    }

    public Optional<VIPLevel> downgradeOneLevel(VIPLevel currentLevel) {
        List<VIPLevel> levels = vipLevelRepository.findAllByOrderByUpgradeThresholdAsc();
        int index = IntStream.range(0, levels.size())
                .filter(i -> levels.get(i).equals(currentLevel))
                .findFirst().orElse(-1);

        if (index > 0) {
            return Optional.of(levels.get(index - 1));
        } else {
            return Optional.empty();
        }
    }

    public List<VIPLevel> getAllLevels() {
        return vipLevelRepository.findAllByOrderByUpgradeThresholdAsc();
    }
}