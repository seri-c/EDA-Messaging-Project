package com.project.messageservice.message.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.messageservice.message.entity.Message;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findBySenderId(UUID senderId);

    List<Message> findByRecipientId(UUID recipientId);

    List<Message> findBySenderIdAndRecipientId(
            UUID senderId,
            UUID recipientId);
}
