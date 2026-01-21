package org.easeport.itsupportsystem.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String username) {
        super("User with username " + username + " not found");
    }

    public UserNotFoundException(Long userId) {
        super("User with id " + userId + " not found");
    }
}
