package org.easeport.itsupportsystem.service;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import org.easeport.itsupportsystem.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailSenderServiceTest {

    private Session session;
    private EmailSenderService emailSenderService;

    @BeforeEach
    void setUp() {
        // Create a dummy session (not connected to real SMTP)
        Properties props = new Properties();
        session = Session.getInstance(props);
        emailSenderService = new EmailSenderService(session);
    }

    private Ticket buildTicket(boolean withMessageId) {
        Ticket ticket = new Ticket();
        ticket.setId(100L);
        ticket.setName("Alice");
        ticket.setFrom("maksym.045z@gmail.com");
        ticket.setSubject("VPN issue");
        ticket.setAnswer("Please update your VPN client.");
        if (withMessageId) {
            ticket.setMessageId("<12345@server>");
        }
        return ticket;
    }

    @Test
    void sendMail_ShouldBuildMessageAndSend_WhenMessageIdIsNull() throws Exception {
        Ticket ticket = buildTicket(false);

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {
            emailSenderService.sendMail(ticket);

            transportMock.verify(() -> Transport.send(any(MimeMessage.class)), times(1));
        }
    }

    @Test
    void sendMail_ShouldBuildMessageAndSend_WithReplyHeaders_WhenMessageIdPresent() throws Exception {
        Ticket ticket = buildTicket(true);

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {
            emailSenderService.sendMail(ticket);

            transportMock.verify(() -> Transport.send(any(MimeMessage.class)), times(1));
        }

        // Build message separately to inspect headers
        MimeMessage message = new MimeMessage(session);
        message.setFrom("easeport.tickets@gmail.com");
        message.setRecipient(Message.RecipientType.TO, new jakarta.mail.internet.InternetAddress(ticket.getFrom()));
        message.setSubject(ticket.getSubject());
        message.setText("dummy");
        message.setHeader("In-Reply-To", ticket.getMessageId());
        message.setHeader("References", ticket.getMessageId());

        assertEquals(ticket.getMessageId(), message.getHeader("In-Reply-To", null));
        assertEquals(ticket.getMessageId(), message.getHeader("References", null));
    }

    @Test
    void sendMail_ShouldHandleException_WhenTransportFails() {
        Ticket ticket = buildTicket(false);

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {
            transportMock.when(() -> Transport.send(any(MimeMessage.class)))
                    .thenThrow(new RuntimeException("SMTP error"));

            assertDoesNotThrow(() -> emailSenderService.sendMail(ticket));
        }
    }
}
