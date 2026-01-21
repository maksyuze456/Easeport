package org.easeport.itsupportsystem.features.notifications.dto;

import org.easeport.itsupportsystem.features.notifications.model.Notification;

import java.time.Instant;

public class NotificationDto {
    private Long id;
    private Long userId;
    private String type;
    private String payload;
    private Instant createdAt;
    private boolean read;

    public NotificationDto() {
    }

    public NotificationDto(Long id, Long userId, String type, String payload, Instant createdAt, boolean read) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.payload = payload;
        this.createdAt = createdAt;
        this.read = read;
    }

    public static NotificationDto from(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getUserId(),
                notification.getType(),
                notification.getPayload(),
                notification.getCreatedAt(),
                notification.isRead()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
