package org.easeport.itsupportsystem.security.dto;

import org.easeport.itsupportsystem.model.Role;

public record UpdateUserRequest(String username, String email, String password, Role role) {
}
