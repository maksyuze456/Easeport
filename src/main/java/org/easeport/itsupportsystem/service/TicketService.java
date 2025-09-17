package org.easeport.itsupportsystem.service;

import org.easeport.itsupportsystem.dto.TicketRequestDto;
import org.easeport.itsupportsystem.dto.TicketResponseDto;
import org.easeport.itsupportsystem.mappers.TicketMapper;
import org.easeport.itsupportsystem.model.Ticket;
import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.model.ticketEnums.TicketStatus;
import org.easeport.itsupportsystem.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    TicketMapper ticketMapper;


    public TicketResponseDto addTicket(TicketRequestDto requestDto) {

        Ticket ticket = ticketMapper.requestDtoToEntity(requestDto);

        return ticketMapper.entityToResponseDto(ticketRepository.save(ticket));
    }

    public Ticket findById(Long ticketId) {
        return ticketRepository.findById(ticketId).orElseThrow(NullPointerException::new);
    }

    public TicketResponseDto assignUserToTicket(Ticket ticket, User user) {
        ticket.setEmployee(user);
        ticket.setStatus(TicketStatus.Reviewing);
        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.entityToResponseDto(updatedTicket);
    }



}
