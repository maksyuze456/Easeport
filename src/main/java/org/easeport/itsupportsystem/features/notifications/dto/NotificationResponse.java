package org.easeport.itsupportsystem.features.notifications.dto;

import org.easeport.itsupportsystem.security.dto.MessageResponse;

public class NotificationResponse<T> extends MessageResponse {
    private T data;

    public NotificationResponse(String message, T data) {
        super(message);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
