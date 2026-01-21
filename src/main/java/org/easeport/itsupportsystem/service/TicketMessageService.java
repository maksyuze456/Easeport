package org.easeport.itsupportsystem.service;

import org.easeport.itsupportsystem.features.notifications.dto.NotificationRequest;
import org.easeport.itsupportsystem.features.notifications.service.NotificationsService;
import org.easeport.itsupportsystem.model.mailRelated.RawEmail;
import org.easeport.itsupportsystem.model.mailRelated.TicketMessage;
import org.easeport.itsupportsystem.repository.TicketMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketMessageService {

    @Autowired
    TicketMessageRepository ticketMessageRepository;

    @Autowired
    WebSocketTicketService webSocketTicketService;

    @Autowired
    NotificationsService notificationsService;



    public TicketMessage findByMessageId(String inReplyTo) {
        return ticketMessageRepository.findByEmailMessageId(inReplyTo);
    }

    public TicketMessage saveNewMessage(Long ticketId, RawEmail rawEmail, String inReplyTo, String employeeUsername) {
        TicketMessage ticketMessage = new TicketMessage(ticketId, rawEmail.getFrom(), rawEmail.getContent(), rawEmail.getLocalDateTime(), inReplyTo, rawEmail.getMessageId());
        TicketMessage savedMessage = ticketMessageRepository.save(ticketMessage);
        NotificationRequest notificationRequest = new NotificationRequest("Message", "Customer has replied in ticket with id " + ticketId, false);
        notificationsService.createNotification(employeeUsername, notificationRequest);
        webSocketTicketService.newTicketMessage(employeeUsername, ticketId);

        return savedMessage;
    }

    public TicketMessage saveMessage(String username, Long ticketId, TicketMessage ticketMessage) {
        webSocketTicketService.newTicketMessage(username, ticketId);
        return ticketMessageRepository.save(ticketMessage);
    }

    public TicketMessage findByTicketMessageId(Long ticketMessageId) {
        return ticketMessageRepository.findById(ticketMessageId).orElseThrow();
    }
    public List<TicketMessage> getTimeConversationByTicketId(Long ticketId) {
        List<TicketMessage> ticketMessageList = ticketMessageRepository.findAllByTicketId(ticketId);

        if(ticketMessageList.isEmpty()) {
            return ticketMessageList;
        }

        return ticketMessageList.stream()
                .sorted(Comparator.comparing(TicketMessage::getLocalDateTime))
                .collect(Collectors.toList());
    }


}
