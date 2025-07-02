package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_address")
@Getter
@Setter
public class CCustomerAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressid;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CCustomer CCustomer;

    private String name;
    private String phone;
    private String address; // 完整地址
//    private String street;
//    private String city;
//    private String district;
//    private String zipcode;
    private Boolean isdefault;
    private LocalDateTime createat;
    private LocalDateTime updateat;
}
