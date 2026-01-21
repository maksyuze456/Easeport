package org.easeport.itsupportsystem.features.notifications.controller;

import org.easeport.itsupportsystem.exception.UserNotFoundException;
import org.easeport.itsupportsystem.features.notifications.dto.NotificationDto;
import org.easeport.itsupportsystem.features.notifications.dto.NotificationRequest;
import org.easeport.itsupportsystem.features.notifications.dto.NotificationResponse;
import org.easeport.itsupportsystem.features.notifications.exception.NotificationNotFoundException;
import org.easeport.itsupportsystem.features.notifications.model.Notification;
import org.easeport.itsupportsystem.features.notifications.service.NotificationsService;
import org.easeport.itsupportsystem.security.dto.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/{userId}/notifications")
@CrossOrigin(origins = "${allowed.origin}", allowCredentials = "true", maxAge = 3600)
public class NotificationsController {

    @Autowired
    NotificationsService notificationsService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> createNotification(@PathVariable("userId") Long userId,
                                                @RequestBody NotificationRequest request) {
        notificationsService.createNotification(userId, request);
        return ResponseEntity.ok()
                .body(new MessageResponse("Notification created for user " + userId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getNotifications(@PathVariable("userId") Long userId) {
        List<NotificationDto> notifications = notificationsService.getNotificationsByUser(userId)
                .stream()
                .map(NotificationDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok()
                .body(new NotificationResponse<>("Notifications retrieved successfully.", notifications));
    }

    @GetMapping("/{notificationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getNotification(@PathVariable("userId") Long userId,
                                             @PathVariable("notificationId") Long notificationId) {
        Notification notification = notificationsService.getNotification(userId, notificationId);
        NotificationDto response = NotificationDto.from(notification);
        return ResponseEntity.ok()
                .body(new NotificationResponse<>("Notification retrieved successfully.", response));
    }

    @PutMapping("/{notificationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> updateNotification(@PathVariable("userId") Long userId,
                                                @PathVariable("notificationId") Long notificationId,
                                                @RequestBody NotificationRequest request) {
        notificationsService.updateNotification(userId, notificationId, request);
        return ResponseEntity.ok()
                .body(new MessageResponse("Notification updated successfully."));
    }

    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> deleteNotification(@PathVariable("userId") Long userId,
                                                @PathVariable("notificationId") Long notificationId) {
        notificationsService.deleteNotification(userId, notificationId);
        return ResponseEntity.ok()
                .body(new MessageResponse("Notification deleted successfully."));
    }

    @ExceptionHandler({UserNotFoundException.class, NotificationNotFoundException.class, IllegalArgumentException.class})
    public ResponseEntity<MessageResponse> handleNotificationErrors(RuntimeException exception) {
        return ResponseEntity.badRequest()
                .body(new MessageResponse(exception.getMessage()));
    }
}
