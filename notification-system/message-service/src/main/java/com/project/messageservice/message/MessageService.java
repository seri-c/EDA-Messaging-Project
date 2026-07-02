package com.project.messageservice.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Set;

import com.project.messageservice.exception.MessageNotFoundException;
import com.project.messageservice.message.dto.CreateMessageRequest;
import com.project.messageservice.message.dto.MessageResponse;
import com.project.messageservice.message.entity.Label;
import com.project.messageservice.message.entity.Message;
import com.project.messageservice.message.repository.LabelRepository;
import com.project.messageservice.message.repository.MessageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;
    private final LabelRepository labelRepository;
    private final MessageMapper messageMapper;

    @Transactional
    public MessageResponse createMessage(CreateMessageRequest request){

        Message newMessage = messageMapper.toNewMessage(request);
        messageRepository.save(newMessage);

        log.info("New message %s created".formatted(newMessage.getId()));

        return messageMapper.toResponse(newMessage);

    }

    public List<MessageResponse> getMessages(UUID senderId, UUID recipientId) {

        List<Message> messages = findMessages(senderId, recipientId);

        return messages
                .stream()
                .map(messageMapper::toResponse)
                .toList();
    }

    public List<Message> findMessages(UUID senderId, UUID recipientId) {

        if (senderId != null && recipientId != null) {
            return messageRepository.findBySenderIdAndRecipientId(senderId, recipientId);
        }

        if (senderId != null) {
            return messageRepository.findBySenderId(senderId);
        }

        if (recipientId != null) {
            return messageRepository.findByRecipientId(recipientId);
        }

        return messageRepository.findAll();

    }
    

    public MessageResponse getMessage(UUID messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
        return messageMapper.toResponse(message);

    }

    @Transactional
    public void readMessage(UUID messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
        message.markAsRead();

    }

    @Transactional
    public void acknowledgeMessage(UUID messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
        message.markAsAcknowledged();

    }

    public Set<String> getMessageLabels(UUID messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
        return message.getLabels().stream()
                        .map(Label::getName)
                        .collect(Collectors.toSet());

    }

    @Transactional
    public void addLabel(UUID messageId, String labelName) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        Label label = labelRepository.findById(labelName)
                .orElseGet(() -> {
                    Label newLabel = new Label(labelName);
                    return labelRepository.save(newLabel);
                });

        message.addLabel(label);
    }

    @Transactional
    public void removeLabel(UUID messageId, String labelName) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        labelRepository.findById(labelName)
                .ifPresent(message::removeLabel);
    }

}
