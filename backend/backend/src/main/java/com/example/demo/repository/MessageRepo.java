package com.example.demo.repository;

import com.example.demo.entity.CCustomer;
import com.example.demo.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepo extends JpaRepository<Message, Long> {
    // 可依顧客ID取得留言
    List<Message> findBycCustomer(CCustomer customer);

    List<Message> findBycCustomer_CustomerId(Long customerId);

    // 查詢某個留言所屬的客戶帳號
    @Query("SELECT m.cCustomer.account FROM Message m WHERE m.messageId = :messageId")
    Optional<String> findCustomerAccountByMessageId(@Param("messageId") Long messageId);

}
