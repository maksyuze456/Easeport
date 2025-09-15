/*
package org.easeport.itsupportsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.easeport.itsupportsystem.model.ticketEnums.Language;
import org.easeport.itsupportsystem.model.ticketEnums.Priority;
import org.easeport.itsupportsystem.model.ticketEnums.Queue;
import org.easeport.itsupportsystem.model.ticketEnums.TicketType;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String subject;
    private String body;
    private TicketType type;
    private Queue queue;
    private Language language;
    private Priority priority;

}
*/