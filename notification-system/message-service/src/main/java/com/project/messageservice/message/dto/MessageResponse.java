package com.project.messageservice.message.dto;

import java.util.Set;
import java.util.UUID;

public record MessageResponse(
    UUID id,
    UUID senderId,
    UUID recipientId,
    String body,
    boolean read,
    boolean acknowledged,
    Set<String> labels
) {}
