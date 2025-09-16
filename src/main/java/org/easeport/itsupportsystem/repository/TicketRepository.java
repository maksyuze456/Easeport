package org.easeport.itsupportsystem.repository;


import org.easeport.itsupportsystem.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {


}
