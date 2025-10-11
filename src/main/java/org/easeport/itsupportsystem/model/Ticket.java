package org.easeport.itsupportsystem.model;

import jakarta.persistence.*;
import org.easeport.itsupportsystem.model.ticketEnums.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject;
    @Column(nullable = false)
    private String name;

    @Column(name = "sender", nullable = false)
    private String from;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Queue queueType;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus status;
    @Column(nullable = true)
    private String answer;
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = true)
    private User employee;

    @Column(nullable = true)
    private String messageId;

    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Column(name = "closed_at", nullable = true)
    private LocalDateTime closedAt;

    public Ticket(Long id, String subject, String name, String from, String body, TicketType type, Queue queueType, Language language, Priority priority, TicketStatus status, String answer, User employee) {
        this.id = id;
        this.subject = subject;
        this.name = name;
        this.from = from;
        this.body = body;
        this.type = type;
        this.queueType = queueType;
        this.language = language;
        this.priority = priority;
        this.status = status;
        this.answer = answer;
        this.employee = employee;
    }

    public Ticket(String subject, String name, String from, String body, TicketType type, Queue queueType, Language language, Priority priority, TicketStatus status, String answer, User employee) {
        this.subject = subject;
        this.name = name;
        this.from = from;
        this.body = body;
        this.type = type;
        this.queueType = queueType;
        this.language = language;
        this.priority = priority;
        this.status = status;
        this.answer = answer;
        this.employee = employee;
    }

    public Ticket(String subject, String name, String from, String body, TicketType type, Queue queueType, Language language, Priority priority, TicketStatus status, String answer, User employee, String messageId, LocalDateTime createdAt) {
        this.subject = subject;
        this.name = name;
        this.from = from;
        this.body = body;
        this.type = type;
        this.queueType = queueType;
        this.language = language;
        this.priority = priority;
        this.status = status;
        this.answer = answer;
        this.employee = employee;
        this.messageId = messageId;
        this.createdAt = createdAt;
    }

    public Ticket() {

    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public Queue getQueueType() {
        return queueType;
    }

    public void setQueueType(Queue queueType) {
        this.queueType = queueType;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
