package org.easeport.itsupportsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.easeport.itsupportsystem.dto.TicketRequestDto;
import org.easeport.itsupportsystem.model.Ticket;
import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.model.mailRelated.QueuedEmail;
import org.easeport.itsupportsystem.model.mailRelated.RawEmail;
import org.easeport.itsupportsystem.model.mailRelated.TicketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailProcessingService {

    @Autowired
    TicketService ticketService;

    @Autowired
    TicketMessageService ticketMessageService;

    @Autowired
    ChatGPTWebClientService chatGPTWebClientService;

    public void processRawMailThroughAi(QueuedEmail queuedEmail) throws JsonProcessingException {
        RawEmail rawEmail = queuedEmail.getRawEmail();
        String inReplyTo = queuedEmail.getInReplyTo();

        if(inReplyTo != null) {
            TicketMessage parentMessage = ticketMessageService.findByMessageId(inReplyTo);

            if (parentMessage != null) {
                Long ticketId = parentMessage.getTicketId();
                ticketMessageService.saveNewMessage(ticketId, rawEmail, inReplyTo);
                return;
            }

            Ticket parentTicket = ticketService.findByMessageId(inReplyTo);

            if(parentTicket != null) {
                ticketMessageService.saveNewMessage(parentTicket.getId(), rawEmail, inReplyTo);
                return;
            }
        }

        TicketRequestDto ticketRequestDto = chatGPTWebClientService.processTicket(rawEmail);
        ticketService.addTicket(ticketRequestDto);
    }

}
