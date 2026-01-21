package org.easeport.itsupportsystem.features.notifications.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.easeport.itsupportsystem.security.dto.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    ObjectMapper objectMapper;


    public void newNotification(String username, String type) {
        System.out.println("Sending ticket message to user: " + username);
        try {
            String jsonMessage = objectMapper.writeValueAsString(new MessageResponse(type));
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/notifications",
                    jsonMessage
            );
        } catch (Exception e) {

        }

    }

}
