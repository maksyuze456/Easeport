package org.easeport.itsupportsystem.features.notifications.repository;

import org.easeport.itsupportsystem.features.notifications.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationsRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUser_Id(Long userId);

    Optional<Notification> findByIdAndUser_Id(Long notificationId, Long userId);
}
