package com.project.eda.exception;

import java.util.UUID;


public class MessageNotFoundException extends RuntimeException {

    public MessageNotFoundException(String message) {
        super(message);
    }

    public MessageNotFoundException(UUID messageId) {
        super("Message with id '%s' was not found.".formatted(messageId));
    }


}
