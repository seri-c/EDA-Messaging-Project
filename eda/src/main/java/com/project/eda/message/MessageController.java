package com.project.eda.message;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.project.eda.message.dto.MessageResponse;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public List<MessageResponse> getMessages(@RequestParam(required = false) UUID senderId,
            @RequestParam(required = false) UUID recipientId) {
        return messageService.getMessages(senderId, recipientId);
    }

    @GetMapping("/{id}")
    public MessageResponse getMessageById(@PathVariable UUID id) {
        return messageService.getMessage(id);
    }

    @PatchMapping("/{id}/acknowledge")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acknowledgeMessage(@PathVariable UUID id) {
        messageService.acknowledgeMessage(id);
    }

    @PatchMapping("/{id}/acknowledge")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void readMessage(@PathVariable UUID id) {
        messageService.readMessage(id);
    }

}
