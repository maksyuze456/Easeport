package org.easeport.itsupportsystem.features.notifications.dto;

public class NotificationRequest {
    private String type;
    private String payload;
    private Boolean read;

    public NotificationRequest() {
    }

    public NotificationRequest(String type, String payload, Boolean read) {
        this.type = type;
        this.payload = payload;
        this.read = read;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
}
