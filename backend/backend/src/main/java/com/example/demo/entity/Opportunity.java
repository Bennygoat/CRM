package com.example.demo.entity;


import com.example.demo.enums.OpportunityStage;
import com.example.demo.enums.OpportunityStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "opportunities")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"bCustomer", "contact"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long opportunityId;

    @Column(nullable = false, length = 255)
    private String opportunityName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 14, scale = 2)
    private BigDecimal expectedValue;

    private LocalDate closeDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private OpportunityStage stage;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private OpportunityStatus status;

    // ----- 多對一關聯：商機所屬的客戶 -----
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private BCustomer bCustomer;

    // ----- 多對一關聯：商機所屬的聯絡人 -----
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

//    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 評分總和
    // 初始值為 0
    @Builder.Default
    @Column(name = "total_rating_sum", nullable = false)
    private Long totalRatingSum = 0L;

    // 評分
    // 初始值為 0
    @Builder.Default
    @Column(name = "number_of_ratings", nullable = false)
    private Integer numberOfRatings = 0;

    // 星級
    @Column(name = "priority", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int priority; // 0: 無星級, 1: 一星, 2: 二星, 3: 三星

    // ----- 多對多關聯：一個商機可以有多個標籤 ----
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "opportunity_tags_link",
            joinColumns = @JoinColumn(name = "opportunity_id"),
            inverseJoinColumns = @JoinColumn(name = "opportunity_tag_id")
    )
    private Set<OpportunityTag> tags = new HashSet<>();

    public void addTag(OpportunityTag tag) {
        this.tags.add(tag);
        tag.getOpportunities().add(this);
    }

    public void removeTag(OpportunityTag tag) {
        this.tags.remove(tag);
        tag.getOpportunities().remove(this);
    }
}
