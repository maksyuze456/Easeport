package org.easeport.itsupportsystem.controller;

import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.security.dto.CreateUserRequest;
import org.easeport.itsupportsystem.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {

        User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()), request.getEmail());

        User savedUser = adminService.createUser(user);

        return new ResponseEntity<>(user, HttpStatus.CREATED);

    }
}
