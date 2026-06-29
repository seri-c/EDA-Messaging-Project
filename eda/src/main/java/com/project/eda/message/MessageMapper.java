package com.project.eda.message;

import java.util.stream.Collectors;

import com.project.eda.message.dto.CreateMessageRequest;
import com.project.eda.message.dto.MessageResponse;
import com.project.eda.message.entity.Label;
import com.project.eda.message.entity.Message;

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
