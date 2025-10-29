package org.easeport.itsupportsystem.service;

import org.easeport.itsupportsystem.dto.AnswerDto;
import org.easeport.itsupportsystem.dto.TicketRequestDto;
import org.easeport.itsupportsystem.dto.TicketResponseDto;
import org.easeport.itsupportsystem.exception.TicketHasNoAssignedUserException;
import org.easeport.itsupportsystem.exception.TicketNotFoundException;
import org.easeport.itsupportsystem.exception.UserNotAssignedException;
import org.easeport.itsupportsystem.mappers.TicketMapper;
import org.easeport.itsupportsystem.model.Ticket;
import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.model.mailRelated.TicketMessage;
import org.easeport.itsupportsystem.model.ticketEnums.TicketStatus;
import org.easeport.itsupportsystem.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    public TicketMapper ticketMapper;

    @Autowired
    TicketMessageService ticketMessageService;

    @Autowired
    EmailSenderService emailSenderService;


    public TicketResponseDto addTicket(TicketRequestDto requestDto) {

        Ticket ticket = ticketMapper.requestDtoToEntity(requestDto);

        Ticket savedTicket = ticketRepository.save(ticket);


        String messageId = savedTicket.getMessageId();
        System.out.println("MessageId: " + messageId);



        TicketMessage ticketMessage = new TicketMessage(savedTicket.getId(), savedTicket.getFrom(), savedTicket.getBody(), savedTicket.getCreatedAt(), null, messageId);
        ticketMessageService.saveMessage(ticketMessage);

        return ticketMapper.entityToResponseDto(savedTicket);
    }

    public Ticket findById(Long ticketId) {
        return ticketRepository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException(ticketId));
    }

    public TicketResponseDto getTicketResponseDtoById(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException(ticketId));
        TicketResponseDto responseDto = ticketMapper.entityToResponseDto(ticket);
        return responseDto;
    }

    public TicketResponseDto setAnswer(Long ticketId, User user, AnswerDto answerDto) {
        Ticket ticket = findById(ticketId);
        User userAssignedToTicket = ticket.getEmployee();
        if (userAssignedToTicket == null) {
            throw new TicketHasNoAssignedUserException(ticketId);
        }
        if (!userAssignedToTicket.getId().equals(user.getId())) throw new UserNotAssignedException(user.getUsername(), ticketId);
        LocalDateTime updatedAt = LocalDateTime.now(ZoneId.systemDefault());
        ticket.setUpdatedAt(updatedAt);
        ticket.setAnswer(answerDto.getMessage());

        Ticket updatedTicket = ticketRepository.save(ticket);
        TicketResponseDto responseDto = ticketMapper.entityToResponseDto(updatedTicket);

        return responseDto;
    }

    public TicketResponseDto assignUserToTicket(Long ticketId, User user) {
        Ticket ticketToAssign = findById(ticketId);
        ticketToAssign.setEmployee(user);
        ticketToAssign.setStatus(TicketStatus.Reviewing);
        LocalDateTime updatedAt = LocalDateTime.now(ZoneId.systemDefault());
        ticketToAssign.setUpdatedAt(updatedAt);
        Ticket updatedTicket = ticketRepository.save(ticketToAssign);
        return ticketMapper.entityToResponseDto(updatedTicket);
    }

    public List<TicketResponseDto> getAllTicketsByStatus(TicketStatus status) {
        List<Ticket> ticketList = ticketRepository.findAllByStatus(status);
        return ticketMapper.entityListToResponseDtoList(ticketList);
    }

    public List<TicketResponseDto> getAllTicketsByStatusAndEmployeeId(TicketStatus status, Long id) {
        List<Ticket> ticketList = ticketRepository.findAllByStatusAndEmployeeId(status, id);
        return ticketMapper.entityListToResponseDtoList(ticketList);
    }

    public void closeTicket(Long ticketId, User user) {
        try {
            Ticket ticket = findById(ticketId);
            User userAssignedToTicket = ticket.getEmployee();
            if (userAssignedToTicket == null) {
                throw new TicketHasNoAssignedUserException(ticketId);
            }
            if (!userAssignedToTicket.getId().equals(user.getId())) throw new UserNotAssignedException(user.getUsername(), ticketId);

            ticket.setStatus(TicketStatus.Closed);
            LocalDateTime closedAt = LocalDateTime.now(ZoneId.systemDefault());
            ticket.setClosedAt(closedAt);
            Ticket updatedTicket = ticketRepository.save(ticket);
            emailSenderService.sendMail(updatedTicket);
        } catch (TicketNotFoundException | UserNotAssignedException | TicketHasNoAssignedUserException e) {
            throw e;
        }


    }

    public Ticket findByMessageId(String inReplyTo) {
        return ticketRepository.findByMessageId(inReplyTo);
    }


    public boolean sendAnswer(User user, Long ticketId, Long ticketMessageId) {
        TicketMessage ticketMessage = ticketMessageService.findByTicketMessageId(ticketMessageId);
        Ticket ticket = findById(ticketId);
        User userAssignedToTicket = ticket.getEmployee();

        if (userAssignedToTicket == null) {
            throw new TicketHasNoAssignedUserException(ticketId);
        }
        if (!userAssignedToTicket.getId().equals(user.getId())) throw new UserNotAssignedException(user.getUsername(), ticketId);

        LocalDateTime updatedAt = LocalDateTime.now(ZoneId.systemDefault());
        ticket.setUpdatedAt(updatedAt);

        TicketMessage employeeAnswer = new TicketMessage(ticket.getId(), user.getEmail(), ticket.getAnswer(), updatedAt, ticketMessage.getEmailMessageId(), null);

        return processSendAnswer(ticket, employeeAnswer);
    }

    public boolean sendAnswer(User user, Long ticketId) {
        Ticket ticket = findById(ticketId);
        User userAssignedToTicket = ticket.getEmployee();

        if (userAssignedToTicket == null) {
            throw new TicketHasNoAssignedUserException(ticketId);
        }
        if (!userAssignedToTicket.getId().equals(user.getId())) throw new UserNotAssignedException(user.getUsername(), ticketId);

        LocalDateTime updatedAt = LocalDateTime.now(ZoneId.systemDefault());
        ticket.setUpdatedAt(updatedAt);

        TicketMessage employeeMessage = new TicketMessage(ticket.getId(), user.getEmail(), ticket.getAnswer(), updatedAt, null, null);

        return processSendAnswer(ticket, employeeMessage);
    }

    private boolean processSendAnswer(Ticket ticket, TicketMessage employeeMessage) {
        try {
            String messageId = emailSenderService.sendAnswer(employeeMessage, ticket);
            employeeMessage.setEmailMessageId(messageId);
            ticket.setAnswer("");
            ticketRepository.save(ticket);
            TicketMessage savedEmployeeMessage = ticketMessageService.saveMessage(employeeMessage);

            return savedEmployeeMessage != null;
        } catch(RuntimeException e) {
            throw e;
        }
    }

}
