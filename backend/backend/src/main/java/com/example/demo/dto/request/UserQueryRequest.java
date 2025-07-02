package com.example.demo.dto.request;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryRequest {
    private String account;         // 帳號模糊查詢
    private String roleName;       // 精確
    private String authorityCode;  // 精確
    private Boolean isActive;      // 啟用狀態
    private LocalDate startDateFrom;
    private LocalDate startDateTo;
    private Integer page = 0;
    private Integer size = 10;
}
