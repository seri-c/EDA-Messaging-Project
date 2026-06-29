package com.project.eda.message.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private UUID senderId;

    private UUID recipientId;

    @Column(name = "body", nullable = false, length = 200)
    private String body;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean read = false;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean acknowledged = false;

    @ManyToMany
    @JoinTable(name = "message_label", joinColumns = @JoinColumn(name = "message_id"), inverseJoinColumns = @JoinColumn(name = "label_name"))
    private Set<Label> messageLabels = new HashSet<>();

    public Message(UUID senderId, UUID recipientId, String body, boolean read, boolean acknowledged) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.body = body;
        this.read = read;
        this.acknowledged = acknowledged;
    }

    public Message(UUID senderId,
            UUID recipientId,
            String body) {

        this.senderId = senderId;
        this.recipientId = recipientId;
        this.body = body;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getRecipientId() {
        return recipientId;
    }

    public Set<Label> getLabels() {
        return messageLabels;
    }

    public String getBody() {
        return body;
    }

    public boolean isRead() {
        return read;
    }

    public void markAsRead() {
        this.read = true;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void markAsAcknowledged() {
        this.acknowledged = true;
    }

    public boolean addLabel(Label label) {
        return messageLabels.add(label);
    }

    public void removeLabel(Label label) {
        messageLabels.remove(label);
    }

}
