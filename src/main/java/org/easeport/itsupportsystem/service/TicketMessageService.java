package org.easeport.itsupportsystem.service;

import org.easeport.itsupportsystem.exception.TicketHasNoAssignedUserException;
import org.easeport.itsupportsystem.exception.UserNotAssignedException;
import org.easeport.itsupportsystem.model.Ticket;
import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.model.mailRelated.RawEmail;
import org.easeport.itsupportsystem.model.mailRelated.TicketMessage;
import org.easeport.itsupportsystem.repository.TicketMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TicketMessageService {

    @Autowired
    TicketMessageRepository ticketMessageRepository;


    public TicketMessage findByMessageId(String inReplyTo) {
        return ticketMessageRepository.findByEmailMessageId(inReplyTo);
    }

    public TicketMessage saveNewMessage(Long ticketId, RawEmail rawEmail, String inReplyTo) {
        TicketMessage ticketMessage = new TicketMessage(ticketId, rawEmail.getFrom(), rawEmail.getContent(), rawEmail.getLocalDateTime(), inReplyTo, rawEmail.getMessageId());
        TicketMessage savedMessage = ticketMessageRepository.save(ticketMessage);
        return savedMessage;
    }

    public TicketMessage saveMessage(TicketMessage ticketMessage) {

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
