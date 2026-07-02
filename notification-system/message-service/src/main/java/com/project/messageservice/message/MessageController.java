package com.project.messageservice.message;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.project.messageservice.message.dto.CreateMessageRequest;
import com.project.messageservice.message.dto.MessageResponse;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createMessage(@Valid @RequestBody CreateMessageRequest request) {
        return messageService.createMessage(request);
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

    @PatchMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void readMessage(@PathVariable UUID id) {
        messageService.readMessage(id);
    }

    @PatchMapping("/{id}/acknowledge")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acknowledgeMessage(@PathVariable UUID id) {
        messageService.acknowledgeMessage(id);
    }

    @GetMapping("/{id}/labels")
    public Set<String> getMessageLabels(@PathVariable UUID id) {
        return messageService.getMessageLabels(id);
    }

    @PutMapping("/{messageId}/labels/{labelName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLabel(@PathVariable UUID messageId, @PathVariable String labelName) {
        messageService.addLabel(messageId, labelName);
    }

    @DeleteMapping("/{messageId}/labels/{labelName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLabel(@PathVariable UUID messageId, @PathVariable String labelName) {
        messageService.removeLabel(messageId, labelName);
    }

}
