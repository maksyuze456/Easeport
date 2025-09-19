package org.easeport.itsupportsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.easeport.itsupportsystem.dto.TicketRequestDto;
import org.easeport.itsupportsystem.model.mailRelated.RawEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailProcessingService {

    @Autowired
    TicketService ticketService;

    @Autowired
    ChatGPTWebClientService chatGPTWebClientService;

    public void processRawMailThroughAi(RawEmail rawEmail) throws JsonProcessingException {
        TicketRequestDto ticketRequestDto = chatGPTWebClientService.processTicket(rawEmail);
        ticketService.addTicket(ticketRequestDto);
    }

}
