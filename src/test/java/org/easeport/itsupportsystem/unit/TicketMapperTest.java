package org.easeport.itsupportsystem.unit;

import org.easeport.itsupportsystem.dto.TicketRequestDto;
import org.easeport.itsupportsystem.dto.TicketResponseDto;
import org.easeport.itsupportsystem.mappers.TicketMapper;
import org.easeport.itsupportsystem.model.Ticket;
import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.model.ticketEnums.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class TicketMapperTest {

    TicketMapper ticketMapper;

    public TicketMapperTest() {
        this.ticketMapper = new TicketMapper();
    }

    @Test
    void checkIfRequestDtoToEntityMapsCorrect() {
        TicketRequestDto requestDto = new TicketRequestDto(
                "Login issue",
                "John Doe",
                "john@example.com",
                "I can't log in to my account.",
                TicketType.Incident,
                Queue.Technical_Support,
                Language.en,
                Priority.High,
                TicketStatus.Open,
                "1"
        );
        Ticket ticket = ticketMapper.requestDtoToEntity(requestDto);

        System.out.printf("""
    Expected vs Actual:
    subject: Login issue | %s
    name: John Doe | %s
    type: Incident | %s
    queueType: Technical_Support | %s
    language: en | %s
    priority: High | %s
    ticketStatus: Open | %s
    messageId: 1 | %s
    """,
                ticket.getSubject(),
                ticket.getName(),
                ticket.getType(),
                ticket.getQueueType(),
                ticket.getLanguage(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getMessageId()
        );
        assertEquals("Incident", ticket.getType().name());
        assertEquals("Technical_Support", ticket.getQueueType().name());
        assertEquals("en", ticket.getLanguage().name());
        assertEquals("High", ticket.getPriority().name());
        assertEquals("Open", ticket.getStatus().name());

    }
    @Test
    void checkIfEntityToResponseDtoMapsCorrect() {
        Ticket ticket = new Ticket(2L, "Login issue", "John Doe", "john@example.com", "I can't log in to my account.",
                TicketType.Incident,
                Queue.Technical_Support,
                Language.en,
                Priority.High,
                TicketStatus.Open,
                null,
                null
        );
        TicketResponseDto responseDto = ticketMapper.entityToResponseDto(ticket);
        System.out.printf("""
                        Expected vs Actual:
                        id: 2 | %s
                        subject: Login issue | %s
                        name: John Doe | %s
                        type: Incident | %s
                        queueType: Technical_Support | %s
                        language: en | %s
                        priority: High | %s
                        ticketStatus: Open | %s
                        answer: null | %s
                        employeeId: null | %s
                        """, responseDto.id(), responseDto.subject(), responseDto.name(), responseDto.type(), responseDto.queueType(),
                             responseDto.language(), responseDto.priority(), responseDto.status(), responseDto.answer(), responseDto.employeeId()
        );
        assertEquals("Incident", responseDto.type().name());
        assertEquals("Technical_Support", responseDto.queueType().name());
        assertEquals("en", responseDto.language().name());
        assertNull(responseDto.answer());
        assertNull(responseDto.employeeId());

    }
    @Test
    void checkIfEntityToResponseDtoMapsEmployeeIdCorrect() {
        User employee = new User();
        employee.setId(3L);
        Ticket ticket = new Ticket(
                2L, "Login issue", "John Doe", "john@example.com", "I can't log in to my account.",
                TicketType.Incident,
                Queue.Technical_Support,
                Language.en,
                Priority.High,
                TicketStatus.Open,
                null,
                employee
        );
        TicketResponseDto responseDto = ticketMapper.entityToResponseDto(ticket);

        System.out.printf("""
                        Expected vs Actual:
                        employeeId: 3 | %s
                        """,
                responseDto.employeeId()
        );
        assertEquals(3L, responseDto.employeeId());
    }

    @Test
    void entityListToResponseDtoList_checkIfListIsProduced() {
        List<Ticket> tickets = Arrays.asList(
                new Ticket("Printer not working", "Alice", "alice@example.com",
                        "My office printer is not responding.", TicketType.Problem, Queue.It_Support,
                        Language.en, Priority.Medium, TicketStatus.Open, null, null),

                new Ticket("VPN issue", "Bob", "bob@example.com",
                        "Unable to connect to the VPN.", TicketType.Problem, Queue.It_Support,
                        Language.en, Priority.High, TicketStatus.Open, null, null),

                new Ticket("Password reset", "Charlie", "charlie@example.com",
                        "I forgot my password, please reset.", TicketType.Request, Queue.Service_Outages_And_Maintenance,
                        Language.en, Priority.Low, TicketStatus.Open, null, null)
        );

        List<TicketResponseDto> ticketResponseDtoList = ticketMapper.entityListToResponseDtoList(tickets);

        assertEquals(ticketResponseDtoList.get(1).name(), "Bob");
        assertEquals(ticketResponseDtoList.get(0).getClass(), TicketResponseDto.class);

    }

    @Test
    void entityListToResponseDtoList_ShouldReturnEmptyListIfEmptyListPassed() {

        List<Ticket> tickets = Collections.emptyList();

        List<TicketResponseDto> ticketResponseDtoList = ticketMapper.entityListToResponseDtoList(tickets);

        assertEquals(Collections.emptyList(), ticketResponseDtoList);


    }
}
