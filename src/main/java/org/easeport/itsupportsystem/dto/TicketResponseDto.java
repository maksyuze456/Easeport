package org.easeport.itsupportsystem.dto;

import org.easeport.itsupportsystem.model.ticketEnums.*;

import java.time.LocalDateTime;

public record TicketResponseDto(Long id, String subject, String name, String from, String body,
                                TicketType type, Queue queueType, Language language,
                                Priority priority, TicketStatus status, String answer,
                                Long employeeId, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime closedAt) {
}
