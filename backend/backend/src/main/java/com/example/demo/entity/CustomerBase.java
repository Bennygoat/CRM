package com.example.demo.entity;

import com.example.demo.enums.CustomerType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_base")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "customer_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString(of = {"customerId", "customerName"}) //TODO(josh): Added for BCustomer
@EqualsAndHashCode(onlyExplicitlyIncluded = true) //TODO(josh): Added for BCustomer
public abstract class CustomerBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include //TODO(josh): Added for BCustomer
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_code", unique = true, nullable = true, length = 50)
    private String customerCode;

    @Column(name = "name", nullable = false)
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false, length = 10, insertable = false, updatable = false)
    private CustomerType customerType;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;


    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "email")
    private String email;

    @Column(name = "customertel")
    private String tel;

    @Column(name="spending")
    private Long spending;

    @CreatedBy
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public boolean isAvailable() {
        return this.isActive && !this.isDeleted;
    }




}
