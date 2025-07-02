package com.example.demo.controller;

import com.example.demo.dto.response.dashboard.DashboardDto;
import com.example.demo.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * GET /api/dashboard : 獲取儀表板所需的所有圖形化數據
     * @return 包含多個圖表和 KPI 數據的 DTO
     */
    @GetMapping
    public ResponseEntity<DashboardDto> getDashboard() {
        DashboardDto dashboardData = dashboardService.getDashboardData();
        return ResponseEntity.ok(dashboardData);
    }
}
