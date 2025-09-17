package org.easeport.itsupportsystem.dto;

import org.easeport.itsupportsystem.model.ticketEnums.*;

public record TicketResponseDto(Long id, String subject, String body,
                                TicketType type, Queue queueType, Language language,
                                Priority priority, TicketStatus ticketStatus, String answer,
                                Long employeeId) {
}
