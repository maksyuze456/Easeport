package org.easeport.itsupportsystem.dto;

import org.easeport.itsupportsystem.model.ticketEnums.*;

import java.time.LocalDateTime;

public record TicketRequestDto(String subject, String name, String from, String body,
                               TicketType type, Queue queueType, Language language,
                               Priority priority, TicketStatus ticketStatus, String messageId, LocalDateTime createdAt) {
}
