package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    // 問題主題（選填）
    @Column(nullable = false)
    private String questionTitle;

    // 發起時間
    private LocalDateTime createdAt;

    // 是否結案
    private Boolean isResolved;

    // 留言的顧客
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CCustomer cCustomer;

    // 一對多關聯回覆
    @Builder.Default
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageReply> replies = new ArrayList<>();

    // 紀錄發問時間
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.isResolved = false;
    }
}
