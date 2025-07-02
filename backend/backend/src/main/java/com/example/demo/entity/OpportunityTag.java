package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "opportunity_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "opportunities")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OpportunityTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long tagId;

    @Column(nullable = false, unique = true, length = 100)
    private String tagName;

    @Column(length = 100)
    private String color;

    // ----- 多對多關聯：一個商機標籤可以關聯多個商機 -----
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Opportunity> opportunities = new HashSet<>();

    // 添加商機關聯的輔助方法
    public void addOpportunity(Opportunity opportunity) {
        this.opportunities.add(opportunity);
        opportunity.getTags().add(this);
    }

    // 移除商機關聯的輔助方法
    public void removeOpportunity(Opportunity opportunity) {
        this.opportunities.remove(opportunity);
        opportunity.getTags().remove(this);
    }
}

