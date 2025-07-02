package com.example.demo.controller.erp;


import com.example.demo.dto.erp.dashboard.ErpDashboardDTO;
import com.example.demo.service.erp.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name="儀表板API(Dashboard)",
        description = "五種數據呈現，包含關鍵指標、TOP5熱銷產品、年度同比銷售數據、存貨總額同比數據、低水位商品清單")
public class ERPDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/erp-summary")
    @Operation(summary = "有五種數據呈現",
            description = "1. 關鍵指標： a. 本月銷售總額(會顯示金額)\n" +
                    " b. 未出貨訂單(會顯示數量)\n" +
                    " c. 低於安全庫存的商品(會顯示數量)\n"+
                    "2. 熱銷商品(會顯示五筆商品)\n"+
                    "3. 年度同比銷售數據(X軸顯示月份、Y軸顯示金額)\n"+
                    "4. 存貨總額同比數據(X軸顯示月份、Y軸顯示金額)\n"+
                    "5. 低於安全庫存的商品清單"


    )
    public ResponseEntity<ErpDashboardDTO> getErpDashboardSummary() {
        ErpDashboardDTO summary = dashboardService.getErpDashboardSummary();
        return ResponseEntity.ok(summary);
    }
}
