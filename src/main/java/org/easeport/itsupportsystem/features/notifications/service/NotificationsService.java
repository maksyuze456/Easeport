package org.easeport.itsupportsystem.features.notifications.service;

import org.easeport.itsupportsystem.exception.UserNotFoundException;
import org.easeport.itsupportsystem.features.notifications.dto.NotificationRequest;
import org.easeport.itsupportsystem.features.notifications.exception.NotificationNotFoundException;
import org.easeport.itsupportsystem.features.notifications.model.Notification;
import org.easeport.itsupportsystem.features.notifications.repository.NotificationsRepository;
import org.easeport.itsupportsystem.features.notifications.ws.WebSocketNotificationService;
import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationsService {

    @Autowired
    NotificationsRepository notificationsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSocketNotificationService webSocketNotificationService;

    public Notification createNotification(Long userId, NotificationRequest request) {

        User user = findUserById(userId);
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(request.getType());
        notification.setPayload(request.getPayload());
        notification.setRead(request.getRead());
        webSocketNotificationService.newNotification(user.getUsername(), request.getType());
        return notificationsRepository.save(notification);
    }

    public Notification createNotification(String username, NotificationRequest request) {

        User user = findUserByUsername(username);
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(request.getType());
        notification.setPayload(request.getPayload());
        notification.setRead(request.getRead());
        webSocketNotificationService.newNotification(user.getUsername(), request.getType());
        return notificationsRepository.save(notification);
    }

    public List<Notification> getNotificationsByUser(Long userId) {

        return notificationsRepository.findAllByUser_Id(userId);
    }

    public Notification getNotification(Long userId, Long notificationId) {
        return notificationsRepository.findByIdAndUser_Id(notificationId, userId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId, userId));
    }

    public Notification updateNotification(Long userId, Long notificationId, NotificationRequest request) {
        Notification notification = getNotification(userId, notificationId);
        if (request.getType() != null) {
            notification.setType(request.getType());
        }
        if (request.getPayload() != null) {
            notification.setPayload(request.getPayload());
        }
        if (request.getRead() != null) {
            notification.setRead(request.getRead());
        }
        return notificationsRepository.save(notification);
    }

    public void deleteNotification(Long userId, Long notificationId) {
        Notification notification = getNotification(userId, notificationId);
        notificationsRepository.delete(notification);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

}
