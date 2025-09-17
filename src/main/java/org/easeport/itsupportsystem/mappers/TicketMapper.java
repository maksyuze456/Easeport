package org.easeport.itsupportsystem.mappers;

import org.easeport.itsupportsystem.dto.TicketRequestDto;
import org.easeport.itsupportsystem.dto.TicketResponseDto;
import org.easeport.itsupportsystem.model.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public Ticket requestDtoToEntity(TicketRequestDto requestDto) {
        Ticket ticket = new Ticket(requestDto.subject(), requestDto.body(), requestDto.type(),
                                    requestDto.queueType(), requestDto.language(), requestDto.priority(),
                                    requestDto.ticketStatus(), null, null);
        return ticket;
    }

    public TicketResponseDto entityToResponseDto(Ticket ticket) {
        TicketResponseDto responseDto = new TicketResponseDto(ticket.getId(), ticket.getSubject(), ticket.getBody(),
                                                              ticket.getType(), ticket.getQueueType(), ticket.getLanguage(),
                                                              ticket.getPriority(), ticket.getStatus(), ticket.getAnswer(), ticket.getEmployee().getId());
        return responseDto;
    }



}
