package org.easeport.itsupportsystem.unit;

import org.easeport.itsupportsystem.dto.AnswerDto;
import org.easeport.itsupportsystem.dto.TicketRequestDto;
import org.easeport.itsupportsystem.dto.TicketResponseDto;
import org.easeport.itsupportsystem.exception.TicketHasNoAssignedUserException;
import org.easeport.itsupportsystem.exception.UserNotAssignedException;
import org.easeport.itsupportsystem.mappers.TicketMapper;
import org.easeport.itsupportsystem.model.Ticket;
import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.model.ticketEnums.*;
import org.easeport.itsupportsystem.repository.TicketRepository;
import org.easeport.itsupportsystem.service.EmailSenderService;
import org.easeport.itsupportsystem.service.TicketService;
import org.easeport.itsupportsystem.service.WebSocketTicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;


    private TicketMapper ticketMapper = new TicketMapper();

    @Mock
    EmailSenderService emailSenderService;

    @Mock
    WebSocketTicketService webSocketTicketService;

    @InjectMocks
    private TicketService ticketService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ticketService.ticketMapper = ticketMapper;
    }

    @Test
    public void closeTicket_ShouldSetStatusClosed_AndSendMail() {

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setSubject("Problem with payment");
        ticket.setName("Jason Brody");
        ticket.setFrom("jason.brody@gmail.com");
        ticket.setBody("My payment didn't went through, but money was took from my bank account.");
        ticket.setType(TicketType.Problem);
        ticket.setQueueType(Queue.Billing_And_Payments);
        ticket.setLanguage(Language.en);
        ticket.setPriority(Priority.High);
        User employee = new User();
        employee.setId(2L);
        ticket.setEmployee(employee);
        ticket.setAnswer("Can you provide me your transaction id?");
        ticket.setStatus(TicketStatus.Reviewing);

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        ticketService.closeTicket(1L, employee);

        assertEquals(TicketStatus.Closed, ticket.getStatus());
        verify(ticketRepository).save(ticket);
        verify(emailSenderService).sendMail(ticket);
        System.out.printf("""
                Expected vs Actual:
                status: Closed | %s
                """,
                ticket.getStatus());

    }

    @Test
    public void closeTicket_ShouldThrowExceptionIfTicketUserIsNull() {

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setSubject("Problem with payment");
        ticket.setName("Jason Brody");
        ticket.setFrom("jason.brody@gmail.com");
        ticket.setBody("My payment didn't went through, but money was took from my bank account.");
        ticket.setType(TicketType.Problem);
        ticket.setQueueType(Queue.Billing_And_Payments);
        ticket.setLanguage(Language.en);
        ticket.setPriority(Priority.High);
        ticket.setStatus(TicketStatus.Open);

        User user = new User();

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThrows(TicketHasNoAssignedUserException.class, () -> {
            ticketService.closeTicket(1L, user);
        });

        verify(ticketRepository, never()).save(any());
        verifyNoInteractions(emailSenderService);

    }
    @Test
    public void closeTicket_ShouldThrowExceptionIfUserIsNotAssignedToTicket() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setSubject("Problem with payment");
        ticket.setName("Jason Brody");
        ticket.setFrom("jason.brody@gmail.com");
        ticket.setBody("My payment didn't went through, but money was took from my bank account.");
        ticket.setType(TicketType.Problem);
        ticket.setQueueType(Queue.Billing_And_Payments);
        ticket.setLanguage(Language.en);
        ticket.setPriority(Priority.High);
        User employee = new User();
        employee.setId(2L);
        ticket.setEmployee(employee);
        ticket.setAnswer("Can you provide me your transaction id?");
        ticket.setStatus(TicketStatus.Reviewing);

        User signedInUser = new User();
        signedInUser.setId(4L);
        signedInUser.setUsername("henrik34");

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThrows(UserNotAssignedException.class, () -> {
           ticketService.closeTicket(1L, signedInUser);
        });

    }

    @Test
    public void getAllTicketsByStatus_ShouldReturnTicketResponseDtoList() {

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

        when(ticketRepository.findAllByStatus(TicketStatus.Open)).thenReturn(tickets);

        List<TicketResponseDto> result = ticketService.getAllTicketsByStatus(TicketStatus.Open);

        assertEquals(3, result.size());
        assertEquals("Printer not working", result.get(0).subject());

    }

    @Test
    public void assignUserToTicket_shouldAssignUserToTicket_AndSetStatusReviewing(){
        User user = new User();
        user.setId(1L);
        Ticket ticket = new Ticket(2L,"VPN issue", "Bob", "bob@example.com",
                "Unable to connect to the VPN.", TicketType.Problem, Queue.It_Support,
                Language.en, Priority.High, TicketStatus.Open, null, null);

        when(ticketRepository.findById(2L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        TicketResponseDto responseDto = ticketService.assignUserToTicket(2L, user);

        assertEquals(responseDto.status(), TicketStatus.Reviewing);
        assertEquals(responseDto.employeeId(), 1L);
    }

    @Test
    public void setAnswer_ShouldSetAnswer_AndUpdateTicket(){

        User user = new User();
        user.setId(1L);
        Ticket ticket = new Ticket(2L,"VPN issue", "Bob", "bob@example.com",
                "Unable to connect to the VPN.", TicketType.Problem, Queue.It_Support,
                Language.en, Priority.High, TicketStatus.Reviewing, null, user);



        when(ticketRepository.save(ticket)).thenReturn(ticket);
        when(ticketRepository.findById(2L)).thenReturn(Optional.of(ticket));

        AnswerDto answerDto = new AnswerDto("What version of the app are you currently using?");

        TicketResponseDto responseDto = ticketService.setAnswer(2L, user, answerDto);

        assertEquals(responseDto.answer(), "What version of the app are you currently using?");


    }

    @Test
    public void setAnswer_ShouldThrowException_IfSignedInUserNotAssignedToTicket() {

        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        Ticket ticket = new Ticket(2L,"VPN issue", "Bob", "bob@example.com",
                "Unable to connect to the VPN.", TicketType.Problem, Queue.It_Support,
                Language.en, Priority.High, TicketStatus.Reviewing, null, user1);

        when(ticketRepository.findById(2L)).thenReturn(Optional.of(ticket));

        AnswerDto answerDto = new AnswerDto("What version of the app are you currently using?");

        assertThrows(UserNotAssignedException.class, () -> {
            ticketService.setAnswer(2L, user2, answerDto);
        });

    }

    @Test
    public void setAnswer_ShouldThrowException_IfTicketHasNoAssignedUser() {
        User user1 = new User();
        user1.setId(1L);


        Ticket ticket = new Ticket(2L,"VPN issue", "Bob", "bob@example.com",
                "Unable to connect to the VPN.", TicketType.Problem, Queue.It_Support,
                Language.en, Priority.High, TicketStatus.Open, null, null);

        when(ticketRepository.findById(2L)).thenReturn(Optional.of(ticket));
        AnswerDto answerDto = new AnswerDto("What version of the app are you currently using?");

        assertThrows(TicketHasNoAssignedUserException.class, () -> {
            ticketService.setAnswer(2L, user1, answerDto);
        });
    }

    @Test
    public void addTicket_ShouldReturnResponseDtoAfterMappingRequestDto() {

        TicketRequestDto ticketRequestDto = new TicketRequestDto("VPN issue", "Bob",
                "bob@example.com", "Unable to connect to VPN", TicketType.Problem, Queue.It_Support,
                Language.en, Priority.High, TicketStatus.Open, "<TestMessageId>", LocalDateTime.now());

        Ticket ticket = ticketMapper.requestDtoToEntity(ticketRequestDto);

        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        TicketResponseDto responseDto = ticketService.addTicket(ticketRequestDto);

        assertEquals(responseDto.from(), "bob@example.com");

    }


}
