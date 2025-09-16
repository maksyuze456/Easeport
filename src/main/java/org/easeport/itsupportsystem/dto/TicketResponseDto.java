package org.easeport.itsupportsystem.dto;

public record TicketResponseDto(Long id, String subject, String body,
                                String type, String queueType, String language,
                                String priority, String ticketStatus, String answer,
                                Long employeeId) {
}
