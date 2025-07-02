package com.example.demo.service;

import com.example.demo.dto.response.MessageReplyResponse;
import com.example.demo.entity.CCustomer;
import com.example.demo.entity.Message;
import com.example.demo.entity.MessageReply;
import com.example.demo.entity.User;
import com.example.demo.enums.SenderType;
import com.example.demo.repository.CCustomerRepo;
import com.example.demo.repository.MessageReplyRepo;
import com.example.demo.repository.MessageRepo;
import com.example.demo.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageReplyService {

    private final MessageRepo messageRepo;
    private final MessageReplyRepo messageReplyRepo;
    private final CCustomerRepo cCustomerRepo;
    private final UserRepo userRepo;

    public MessageReplyService(
            MessageRepo messageRepo,
            MessageReplyRepo messageReplyRepo,
            CCustomerRepo cCustomerRepo,
            UserRepo userRepo
    ) {
        this.messageRepo = messageRepo;
        this.messageReplyRepo = messageReplyRepo;
        this.cCustomerRepo = cCustomerRepo;
        this.userRepo = userRepo;
    }


//    - 查詢某留言主題下的所有回覆
//    - 編輯、刪除回覆（如有）

    // 客戶回覆
    public MessageReplyResponse replyByCustomer(Long customerId, Long messageId, String content) {
        CCustomer customer = cCustomerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("客戶不存在"));

        Message message = messageRepo.findById(messageId)
                .orElseThrow(() -> new RuntimeException("留言不存在"));

        MessageReply reply = MessageReply.builder()
                .message(message)
                .senderType(SenderType.CUSTOMER)
                .content(content)
                .cCustomer(customer)
                .content(content)
                .build();

        MessageReply savedReply = messageReplyRepo.save(reply);

        return toResponse(savedReply);
    }

    // 使用者回覆
    public MessageReplyResponse replyByUser(Long userId, Long messageId, String content) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Message message = messageRepo.findById(messageId)
                .orElseThrow(() -> new RuntimeException("留言不存在"));

        MessageReply reply = MessageReply.builder()
                .message(message)
                .senderType(SenderType.STAFF)
                .content(content)
                .user(user)
                .content(content)
                .build();

        MessageReply savedReply = messageReplyRepo.save(reply);
        return toResponse(savedReply);
    }

    // 查詢某留言下所有回覆
    public List<MessageReplyResponse> getRepliesForMessage(Long messageId) {
        List<MessageReply> replies = messageReplyRepo.findByMessage_MessageId(messageId);
        return replies.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // 將 Entity 轉為 DTO
    private MessageReplyResponse toResponse(MessageReply reply) {
        String senderName = null;

        if (reply.getSenderType() == SenderType.CUSTOMER && reply.getCCustomer() != null) {
            senderName = reply.getCCustomer().getCustomerName(); //TODO(joshkuei): Change getCustomerName() to getName()
        } else if (reply.getSenderType() == SenderType.STAFF && reply.getUser() != null) {
            senderName = reply.getUser().getUserName();
        }

        return new MessageReplyResponse(
                reply.getReplyId(),
                reply.getContent(),
                reply.getSenderType().name(),
                senderName,
                reply.getSentAt()
        );
    }

}
