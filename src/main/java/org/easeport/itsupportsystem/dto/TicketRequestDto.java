package org.easeport.itsupportsystem.dto;

public record TicketRequestDto(String subject, String body,
                               String type, String queueType, String language,
                               String priority, String ticketStatus) {
}
