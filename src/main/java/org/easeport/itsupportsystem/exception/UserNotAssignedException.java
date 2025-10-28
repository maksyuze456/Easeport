package org.easeport.itsupportsystem.exception;

public class UserNotAssignedException extends RuntimeException{
    public UserNotAssignedException(String username, Long ticketId) {
        super("User " + username + " is not assigned to ticket " + ticketId);
    }
}
