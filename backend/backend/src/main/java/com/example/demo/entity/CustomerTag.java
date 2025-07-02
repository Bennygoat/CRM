package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customer_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "bCustomers")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CustomerTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long tagId;

    @Column(nullable = false, unique = true, length = 100)
    private String tagName;

    @Column(length = 100)
    private String color;

    // ----- 多對多關聯：一個客戶標籤可以關聯多個客戶 -----
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<BCustomer> bCustomers = new HashSet<>();
}
