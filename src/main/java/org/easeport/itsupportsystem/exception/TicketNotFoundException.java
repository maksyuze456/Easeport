package org.easeport.itsupportsystem.exception;

public class TicketNotFoundException extends RuntimeException{
    public TicketNotFoundException(Long id) {
        super("Ticket with id " + id + " not found");
    }
}
