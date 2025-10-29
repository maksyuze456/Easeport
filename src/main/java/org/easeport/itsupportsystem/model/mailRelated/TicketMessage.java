package org.easeport.itsupportsystem.model.mailRelated;

import jakarta.persistence.*;
import org.easeport.itsupportsystem.model.Ticket;

import java.time.LocalDateTime;

@Entity
public class TicketMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketMessageId;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;
    private String sender;
    private String body;
    private LocalDateTime localDateTime;
    private String emailMessageId;
    private String inReplyTo;


    public TicketMessage(Long ticketId, String sender, String body, LocalDateTime localDateTime, String inReplyTo, String emailMessageId) {
        this.ticketId = ticketId;
        this.sender = sender;
        this.body = body;
        this.localDateTime = localDateTime;
        this.inReplyTo = inReplyTo;
        this.emailMessageId = emailMessageId;
    }

    public TicketMessage() {
    }

    public Long getTicketMessageId() {
        return ticketMessageId;
    }

    public void setTicketMessageId(Long ticketMessageId) {
        this.ticketMessageId = ticketMessageId;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getInReplyTo() {
        return inReplyTo;
    }

    public void setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
    }

    public String getEmailMessageId() {
        return emailMessageId;
    }

    public void setEmailMessageId(String emailMessageId) {
        this.emailMessageId = emailMessageId;
    }
}
