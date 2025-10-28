package org.easeport.itsupportsystem.exception;

public class TicketHasNoAssignedUserException extends RuntimeException{
    public TicketHasNoAssignedUserException(Long id) {
        super("Ticket with id " + id + " has no assigned user.");
    }
}
