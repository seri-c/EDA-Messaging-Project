package com.project.messageservice.message.dto;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record MessageEvent(
        UUID eventId,
        EventType eventType,
        Instant occurredAt,
        UUID messageId,
        UUID senderId,
        UUID recipientId,
        Set<String> labels) {

     public enum EventType {
        MESSAGE_CREATED, MESSAGE_READ, MESSAGE_ACKNOWLEDGED, LABEL_ADDED, LABEL_REMOVED
    }

    public MessageEvent {
        Objects.requireNonNull(eventId);
        Objects.requireNonNull(eventType);
        Objects.requireNonNull(occurredAt);
        Objects.requireNonNull(messageId);
        Objects.requireNonNull(senderId);
        Objects.requireNonNull(recipientId);

        labels = labels == null ? Set.of() : Set.copyOf(labels);
    }

   

    public static class MessageEventBuilder {
        private UUID eventId;
        private EventType eventType;
        private Instant occurredAt;
        private UUID messageId;
        private UUID senderId;
        private UUID recipientId;
        private Set<String> labels;

        public MessageEventBuilder() {
        }

        public MessageEventBuilder eventId(UUID eventId) {
            this.eventId = eventId;
            return this;
        }

        public MessageEventBuilder eventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public MessageEventBuilder occurredAt(Instant occurredAt) {
            this.occurredAt = occurredAt;
            return this;
        }

        public MessageEventBuilder messageId(UUID messageId) {
            this.messageId = messageId;
            return this;
        }

        public MessageEventBuilder senderId(UUID senderId) {
            this.senderId = senderId;
            return this;
        }

        public MessageEventBuilder recipientId(UUID recipientId) {
            this.recipientId = recipientId;
            return this;
        }

        public MessageEventBuilder labels(Set<String> labels){
            this.labels = labels;
            return this;
        }

        public MessageEvent build(){
            return new MessageEvent(eventId, eventType, occurredAt, messageId, senderId, recipientId, labels);
        }

    }
}
