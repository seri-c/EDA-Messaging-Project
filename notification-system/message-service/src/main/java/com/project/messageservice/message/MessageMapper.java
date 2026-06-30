package com.project.messageservice.message;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.project.messageservice.message.dto.CreateMessageRequest;
import com.project.messageservice.message.dto.MessageResponse;
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

    public Message toNewMessage(CreateMessageRequest request){
        return new Message(request.senderId(), request.recipientId(), request.body());
    }
}
