package org.easeport.itsupportsystem.controller;

import org.easeport.itsupportsystem.model.Role;
import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.security.dto.CreateUserRequest;
import org.easeport.itsupportsystem.security.security_entity.UserPrincipal;
import org.easeport.itsupportsystem.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true", maxAge = 3600)
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {

        User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()), request.getEmail());
        user.setRole(Role.USER);

        User savedUser = adminService.createUser(user);

        return new ResponseEntity<>(user, HttpStatus.CREATED);

    }
}
