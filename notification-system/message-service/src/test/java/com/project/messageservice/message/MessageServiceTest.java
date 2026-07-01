package com.project.messageservice.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.messageservice.exception.MessageNotFoundException;
import com.project.messageservice.message.dto.MessageResponse;
import com.project.messageservice.message.entity.Label;
import com.project.messageservice.message.entity.Message;
import com.project.messageservice.message.repository.LabelRepository;
import com.project.messageservice.message.repository.MessageRepository;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    private static final UUID senderId = UUID.fromString("df3c5cd0-12ea-4e32-aa17-6f70289b5dd1");
    private static final UUID recipientId = UUID.fromString("fab6e5df-15e7-4020-acb8-f3274ec5871e");
    private static final String body = "Automated test message";

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private MessageService messageService;

    @Test
    void getMessage_returnsMessage_whenMessageExists() {

        UUID messageId = UUID.randomUUID();

        Message message = new Message(senderId, recipientId, body);
        MessageResponse expectedResponse = new MessageResponse(messageId, senderId, recipientId, body, false, false,
                null);

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(messageMapper.toResponse(message)).thenReturn(expectedResponse);

        MessageResponse messageResponse = messageService.getMessage(messageId);

        verify(messageMapper).toResponse(message);

        assertNotNull(messageResponse);

        assertEquals(senderId, messageResponse.senderId());
        assertEquals(recipientId, messageResponse.recipientId());
        assertEquals(body, messageResponse.body());

        verify(messageRepository).findById(messageId);
    }

    @Test
    void getMessage_whenMessageDoesNotExist_throwsException() {

        UUID messageId = UUID.randomUUID();

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        assertThrows(MessageNotFoundException.class, () -> {
            messageService.getMessage(messageId);
        });

        verify(messageRepository).findById(messageId);
    }

    @Test
    void getMessages_whenNoMessages_returnsEmptyList() {

        when(messageRepository.findAll()).thenReturn(List.of());

        List<MessageResponse> messageList = messageService.getMessages(null, null);

        assertTrue(messageList.isEmpty());

        verify(messageRepository).findAll();
    }

    @Test
    void readMessage_whenMessageDoesNotExist_throwsException() {

        UUID messageId = UUID.randomUUID();

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        assertThrows(MessageNotFoundException.class, () -> {
            messageService.readMessage(messageId);
        });

        verify(messageRepository).findById(messageId);
    }

    @Test
    void readMessage_marksMessageAsRead() {

        UUID messageId = UUID.randomUUID();
        Message message = new Message(senderId, recipientId, body);
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        messageService.readMessage(messageId);

        verify(messageRepository).findById(messageId);
        assertTrue(message.isRead());

    }

    @Test
    void acknowledgeMessage_whenMessageDoesNotExistThrowsException() {

        UUID messageId = UUID.randomUUID();

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        assertThrows(MessageNotFoundException.class, () -> {
            messageService.acknowledgeMessage(messageId);
        });

        verify(messageRepository).findById(messageId);
    }

    @Test
    void acknowledgeMessage_marksMessageAsAcknowledged() {

        UUID messageId = UUID.randomUUID();
        Message message = new Message(senderId, recipientId, body);
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        messageService.acknowledgeMessage(messageId);

        verify(messageRepository).findById(messageId);
        assertTrue(message.isAcknowledged());

    }

    @Test
    void getMessageLabels_whenMessageDoesNotExistThrowsException() {

        UUID messageId = UUID.randomUUID();

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        assertThrows(MessageNotFoundException.class, () -> {
            messageService.getMessageLabels(messageId);
        });

        verify(messageRepository).findById(messageId);
    }

    @Test
    void addLabel_whenMessageDoesNotExistThrowsException() {

        UUID messageId = UUID.randomUUID();

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        assertThrows(MessageNotFoundException.class, () -> {
            messageService.addLabel(messageId, "Test Label");
        });

        verify(messageRepository).findById(messageId);
    }

    @Test
    void addLabel_whenLabelExists_addsExistingLabelToMessage() {

        UUID messageId = UUID.randomUUID();
        Message message = new Message(senderId, recipientId, body);

        String labelName = "Test Label";
        Label label = new Label(labelName);

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(labelRepository.findById(labelName)).thenReturn(Optional.of(label));

        messageService.addLabel(messageId, labelName);

        assertTrue(message.getLabels().contains(label));

        verify(messageRepository).findById(messageId);
        verify(labelRepository).findById(labelName);

    }

    @Test
    void addLabel_whenLabelDoesNotExist_addsNewLabelToMessage() {

        UUID messageId = UUID.randomUUID();
        Message message = new Message(senderId, recipientId, body);

        String labelName = "Test Label";
        Label label = new Label(labelName);

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(labelRepository.findById(labelName)).thenReturn(Optional.empty());

        when(labelRepository.save(Mockito.any(Label.class))).thenReturn(label);

        messageService.addLabel(messageId, labelName);

        verify(labelRepository).save(Mockito.any(Label.class));
        assertTrue(message.getLabels().contains(label));

        verify(messageRepository).findById(messageId);
        verify(labelRepository).findById(labelName);

    }

    @Test
    void removeLabel_whenMessageDoesNotExistThrowsException() {

        UUID messageId = UUID.randomUUID();

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        assertThrows(MessageNotFoundException.class, () -> {
            messageService.removeLabel(messageId, "Test Label");
        });

        verify(messageRepository).findById(messageId);
    }

}
