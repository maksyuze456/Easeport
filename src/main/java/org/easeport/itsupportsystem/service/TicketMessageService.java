package org.easeport.itsupportsystem.service;

import org.easeport.itsupportsystem.model.mailRelated.RawEmail;
import org.easeport.itsupportsystem.model.mailRelated.TicketMessage;
import org.easeport.itsupportsystem.repository.TicketMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


}
