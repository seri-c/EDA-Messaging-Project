package com.project.messageservice.message;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.project.messageservice.message.dto.MessageEvent.EventType;
import org.springframework.stereotype.Component;

import com.project.messageservice.message.dto.CreateMessageRequest;
import com.project.messageservice.message.dto.MessageEvent;
import com.project.messageservice.message.dto.MessageResponse;
import com.project.messageservice.message.dto.MessageEvent.MessageEventBuilder;
import com.project.messageservice.message.entity.Label;
import com.project.messageservice.message.entity.Message;

@Component
public class MessageMapper {

    public MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getSenderId(),
                message.getRecipientId(),
                message.getBody(),
                message.isRead(),
                message.isAcknowledged(),
                message.getLabels()
                        .stream()
                        .map(Label::getName)
                        .collect(Collectors.toSet()));

    }

    public MessageEvent toEvent(Message message, EventType eventType) {

        UUID eventId = UUID.randomUUID();

        Set<String> labels = message.getLabels().stream()
                .map(Label::getName)
                .collect(Collectors.toSet());

        MessageEvent event = new MessageEventBuilder()
                .eventId(eventId)
                .eventType(eventType)
                .occurredAt(Instant.now())
                .messageId(message.getId())
                .senderId(message.getSenderId())
                .recipientId(message.getRecipientId())
                .labels(labels)
                .build();

        return event;
    }

    public Message toNewMessage(CreateMessageRequest request) {
        return new Message(request.senderId(), request.recipientId(), request.body());
    }
}
