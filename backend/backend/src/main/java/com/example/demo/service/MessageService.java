package com.example.demo.service;

import com.example.demo.dto.request.MessageCreateRequest;
import com.example.demo.dto.request.MessageReplyCreateRequest;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.entity.CCustomer;
import com.example.demo.entity.Message;
import com.example.demo.entity.MessageReply;
import com.example.demo.entity.User;
import com.example.demo.enums.SenderType;
import com.example.demo.repository.CCustomerRepo;
import com.example.demo.repository.MessageReplyRepo;
import com.example.demo.repository.MessageRepo;
import com.example.demo.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepo messageRepo;
    private final MessageReplyRepo messageReplyRepo;
    private final CCustomerRepo cCustomerRepo;
    private final UserRepo userRepo;

    public MessageService(MessageRepo messageRepo, MessageReplyRepo messageReplyRepo, CCustomerRepo cCustomerRepo, UserRepo userRepo) {
        this.messageRepo = messageRepo;
        this.messageReplyRepo = messageReplyRepo;
        this.cCustomerRepo = cCustomerRepo;
        this.userRepo = userRepo;
    }


//    - 查詢留言列表
//    - 標記已解決、關閉主題等

    // 建立開始提問/留言主題
    @Transactional
    public MessageResponse createMessage(Long customerId, MessageCreateRequest req) {
        // 拉是哪個客戶的留言
        CCustomer customer = cCustomerRepo.findById(customerId).orElseThrow(
                () -> new RuntimeException("查無此客戶"));

        Message message = Message.builder()
                .cCustomer(customer)
                .questionTitle(req.getQuestionTitle())
                .build();

        Message savedMessage = messageRepo.save(message);

        // 建立第一筆留言
        MessageReply firstReply = MessageReply.builder()
                .message(savedMessage)
                .cCustomer(customer)
                .senderType(SenderType.CUSTOMER)
                .content(req.getContent())
                .build();
        messageReplyRepo.save(firstReply);

//        return new MessageResponse(
//                savedMessage.getMessageId(),
//                savedMessage.getQuestionTitle(),
//                savedMessage.getIsResolved(),
//                savedMessage.getCreatedAt(),
//                savedMessage.getCCustomer().getCustomerId(),
//                savedMessage.getCCustomer().getAccount(),
//                savedMessage.getCCustomer().getCustomerName(),
//                savedMessage.getCCustomer().getEmail(),
//                firstReply.getContent(),
//                firstReply.getSentAt()
//        );
        return toResponse(savedMessage);
    }

    // 查詢該顧客所有留言
    public List<MessageResponse> getMessagesByCustomer(Long customerId) {
        List<Message> messages = messageRepo.findBycCustomer_CustomerId(customerId);
        return messages.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // 依留言id查詢留言
    public Optional<Message> getMessageById(Long messageId) {
        return messageRepo.findById(messageId);
    }

    private MessageResponse toResponse(Message message) {
        MessageResponse resp = new MessageResponse();
        resp.setMessageId(message.getMessageId());
        resp.setQuestionTitle(message.getQuestionTitle());
        resp.setCreatedAt(message.getCreatedAt());
        resp.setIsResolved(message.getIsResolved());
//        resp.setCustomerId(message.getCCustomer().getCustomerId());

        // Populate customer-specific fields
        if (message.getCCustomer() != null) {
            resp.setCustomerId(message.getCCustomer().getCustomerId());
            resp.setCustomerAccount(message.getCCustomer().getAccount());
            resp.setCustomerName(message.getCCustomer().getCustomerName());
            resp.setCustomerEmail(message.getCCustomer().getEmail()); // Populate email
        }

//         可以選擇只取最後一筆回覆當 summary（如果要）
        List<MessageReply> replies = message.getReplies();
        if (replies != null && !replies.isEmpty()) {
            // 找到最新的一條回覆
            MessageReply lastReply = replies.stream()
                    .max(Comparator.comparing(MessageReply::getSentAt))
                    .orElse(null);
            if (lastReply != null) {
                resp.setLastReplyContent(lastReply.getContent());
                resp.setLastReplyTime(lastReply.getSentAt());
                resp.setLastReplySenderType(lastReply.getSenderType().name()); // 填充發送者類型
            }
        }

        return resp;
    }

    // 檢視有無查看問題的權限
    public boolean isMessageAccessibleByAccount(Long messageId, String account) {
        Optional<Message> optionalMessage = messageRepo.findById(messageId);
        if (optionalMessage.isEmpty()) return false;

        Message message = optionalMessage.get();
        String messageOwnerAccount = message.getCCustomer().getAccount();

        // 如果是留言的客戶return true
        if (messageOwnerAccount.equals(account)) return true;

        // 如果是客服return true
        Optional<User> optionalUser = userRepo.findByAccount(account);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return user.getAuthorities().stream()
                    .anyMatch(auth -> auth.getCode().equals("CUSTOMER_SUPPORT"));
        }

        return false;
    }

    // 查詢所有客戶問題的清單
    public List<MessageResponse> getAllMessagesForAdmin() {
        List<Message> messages = messageRepo.findAllMessagesWithCustomerInfo();
        return messages.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // --- NEW METHOD: Mark Message as Resolved (Admin Action) ---
    @Transactional
    public void markMessageAsResolved(Long messageId) {
        Message message = messageRepo.findById(messageId)
                .orElseThrow(() -> new RuntimeException("找不到留言: " + messageId));
        message.setIsResolved(true);
        messageRepo.save(message);
    }

}
