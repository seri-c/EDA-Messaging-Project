package com.project.eda.message.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMessageRequest(

        @NotBlank(message = "Sender ID cannot be blank")
        UUID senderId,

        @NotBlank(message = "Recipient ID cannot be blank")
        UUID recipientId,

        @NotBlank(message = "Message body cannot be blank")
        @Size(min = 3, max = 200, message = "Message body must be between 3 and 200 characters")
        String body

) {

};