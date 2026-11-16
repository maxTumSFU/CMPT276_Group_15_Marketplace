package com.marketplace.sfu_marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageId;

    // Account that sent the message
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Account sender;

    // Account that receives the message
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Account receiver;

    // Listing related to this conversation
    @ManyToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @NotBlank(message = "Message cannot be empty")
    @Column(nullable = false, length = 2000)
    private String content;

    // Date and time when the message was sent
    private LocalDateTime sentAt;

    // Tracks whether the receiver has read this message
    // New messages are unread by default
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean read = false;

    public Message() {
    }

    // Automatically set the timestamp before saving
    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public Account getSender() {
        return sender;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    public Account getReceiver() {
        return receiver;
    }

    public void setReceiver(Account receiver) {
        this.receiver = receiver;
    }

    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    // Checks whether the message has been read
    public boolean isRead() {
        return read;
    }

    // Updates the message's read status
    public void setRead(boolean read) {
        this.read = read;
    }
}