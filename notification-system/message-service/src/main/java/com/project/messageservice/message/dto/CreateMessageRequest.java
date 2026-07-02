package com.project.messageservice.message.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateMessageRequest(

                @NotNull(message = "Sender ID cannot be null") UUID senderId,

                @NotNull(message = "Recipient ID cannot be null") UUID recipientId,

                @NotBlank(message = "Message body cannot be blank") @Size(min = 3, max = 200, message = "Message body must be between 3 and 200 characters") String body

) {

        public class CreateMessageRequestBuilder {
                private UUID senderId;
                private UUID recipientId;
                private String body;

                public CreateMessageRequestBuilder(UUID senderId, UUID recipientId, String body) {
                        this.senderId = senderId;
                        this.recipientId = recipientId;
                        this.body = body;
                }

                public CreateMessageRequestBuilder senderId(UUID senderId) {
                        this.senderId = senderId;
                        return this;
                }

                public CreateMessageRequestBuilder recipientId(UUID recipientId) {
                        this.recipientId = recipientId;
                        return this;
                }

                public CreateMessageRequestBuilder body(String body) {
                        this.body = body;
                        return this;
                }

                public CreateMessageRequest build() {
                        return new CreateMessageRequest(senderId, recipientId, body);
                }

        }
};