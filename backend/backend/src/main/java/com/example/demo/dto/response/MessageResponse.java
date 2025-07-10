package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageResponse {
    private Long messageId;
    private String questionTitle;
    private Boolean isResolved;
    private LocalDateTime createdAt;
    private Long customerId;

    // --- NEW: Add customer specific fields for admin view ---
    private String customerAccount;
    private String customerName;
    private String customerEmail;

    // 新增預覽用欄位
    private String lastReplyContent;
    private LocalDateTime lastReplyTime;
    private String lastReplySenderType;
}
