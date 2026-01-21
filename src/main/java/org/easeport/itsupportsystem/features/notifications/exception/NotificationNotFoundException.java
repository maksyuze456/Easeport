package org.easeport.itsupportsystem.features.notifications.exception;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(Long notificationId, Long userId) {
        super("Notification with id " + notificationId + " not found for user " + userId);
    }
}
