package org.easeport.itsupportsystem.mappers;

import org.easeport.itsupportsystem.dto.TicketRequestDto;
import org.easeport.itsupportsystem.dto.TicketResponseDto;
import org.easeport.itsupportsystem.model.Ticket;
import org.easeport.itsupportsystem.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class TicketMapper {

    public Ticket requestDtoToEntity(TicketRequestDto requestDto) {
        Ticket ticket = new Ticket(requestDto.subject(), requestDto.name(), requestDto.from(), requestDto.body(), requestDto.type(),
                                    requestDto.queueType(), requestDto.language(), requestDto.priority(),
                                    requestDto.ticketStatus(), null, null, requestDto.messageId(), requestDto.createdAt().truncatedTo(ChronoUnit.SECONDS));
        return ticket;
    }

    public TicketResponseDto entityToResponseDto(Ticket ticket) {
        User user = ticket.getEmployee();

        LocalDateTime createdAt = ticket.getCreatedAt() != null ? ticket.getCreatedAt().truncatedTo(ChronoUnit.SECONDS) : null;
        LocalDateTime updatedAt = ticket.getUpdatedAt() != null ? ticket.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS) : null;
        LocalDateTime closedAt  = ticket.getClosedAt()  != null ? ticket.getClosedAt().truncatedTo(ChronoUnit.SECONDS)  : null;

        return new TicketResponseDto(
                ticket.getId(),
                ticket.getSubject(),
                ticket.getName(),
                ticket.getFrom(),
                ticket.getBody(),
                ticket.getType(),
                ticket.getQueueType(),
                ticket.getLanguage(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getAnswer(),
                user != null ? user.getId() : null,
                createdAt,
                updatedAt,
                closedAt
        );
    }


    public List<TicketResponseDto> entityListToResponseDtoList(List<Ticket> ticketList) {
        return ticketList.stream()
                .map(this::entityToResponseDto)
                .toList();
    }



}
