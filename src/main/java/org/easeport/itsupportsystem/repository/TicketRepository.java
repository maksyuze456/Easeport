package org.easeport.itsupportsystem.repository;


import org.easeport.itsupportsystem.model.Ticket;
import org.easeport.itsupportsystem.model.ticketEnums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {


    List<Ticket> findAllByStatus(TicketStatus status);

    List<Ticket> findAllByStatusAndEmployeeId(TicketStatus status, Long id);

    Ticket findByMessageId(String inReplyTo);


}
