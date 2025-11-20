package org.easeport.itsupportsystem.service;

import org.easeport.itsupportsystem.security.dto.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketTicketService {

    @Autowired
    SimpMessagingTemplate messagingTemplate;


    public void newTicket() {
        System.out.println("Broadcasting new ticket...");
        messagingTemplate.convertAndSend(
                "/topic",
                new MessageResponse("New ticket arrived!")
        );
    }

    public void newTicketMessage(String username, Long ticketId) {
        System.out.println("Sending ticket message to user: " + username);
        messagingTemplate.convertAndSendToUser(
                username,
                "/ticket-messages",
                new MessageResponse("New ticket message in ticket with id " + ticketId)
        );
    }

    public void newAssign() {
        System.out.println("Ticket got assigned, sending...");
        messagingTemplate.convertAndSend("/topic/assign", new MessageResponse("Ticket assigned to someone"));
    }


}
