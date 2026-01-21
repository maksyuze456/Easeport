package org.easeport.itsupportsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easeport.itsupportsystem.security.dto.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketTicketService {

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    ObjectMapper objectMapper;


    public void init() throws JsonProcessingException {
        System.out.println("Init...");
        String jsonMessage = objectMapper.writeValueAsString(new MessageResponse("Starting init"));
        messagingTemplate.convertAndSend(
                "/topic",
                jsonMessage
        );
    }


    public void newTicket() throws JsonProcessingException {
        System.out.println("Broadcasting new ticket...");
        String jsonMessage = objectMapper.writeValueAsString(new MessageResponse("New ticket arrived!"));
        messagingTemplate.convertAndSend(
                "/topic/new-ticket",
                jsonMessage
        );
    }

    public void updateUserTicket(String username) throws JsonProcessingException {
        System.out.println("User ticket update");
        String jsonMessage = objectMapper.writeValueAsString(new MessageResponse("Updates"));
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/ticket",
                jsonMessage
        );
    }

    public void newTicketMessage(String username, Long ticketId) {
        System.out.println("Sending ticket message to user: " + username);
        try {
            String jsonMessage = objectMapper.writeValueAsString(new MessageResponse("" + ticketId));
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/ticket-messages",
                    jsonMessage
            );
            System.out.println(jsonMessage);
        } catch (Exception e) {

        }

    }

    public void newAssign() throws JsonProcessingException {
        System.out.println("Ticket got assigned, sending...");
        String jsonMessage = objectMapper.writeValueAsString(new MessageResponse("New assign"));
        System.out.println("Active sub: " + messagingTemplate.getMessageConverter());
        messagingTemplate.convertAndSend("/topic/assign", jsonMessage);
    }

    public void newAssignToUser(String username) {
        System.out.println("New assigned ticket for user: " + username);
        try {
            String jsonMessage = objectMapper.writeValueAsString(new MessageResponse("New assigned ticket for user " + username));
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/assign",
                    jsonMessage
            );
        } catch (Exception e) {

        }

    }


}
