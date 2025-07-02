package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCustomerRegisterRequest extends RegisterRequest {
    private String customerName;
    private String address;
    private LocalDate birthday;
    private String customerTel;
}
