package org.easeport.itsupportsystem.controller;

import jakarta.websocket.server.PathParam;
import org.easeport.itsupportsystem.model.Ticket;
import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.security.security_entity.UserPrincipal;
import org.easeport.itsupportsystem.service.TicketService;
import org.easeport.itsupportsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true", maxAge = 3600)
public class TicketController {

    @Autowired
    TicketService ticketService;

    @Autowired
    UserService userService;


    @PostMapping("/assign?id={ticketId}")
    public ResponseEntity<?> assignTicket(@PathParam("ticketId") Long ticketId, Authentication authentication) {
        try {
            Ticket ticketToAssign = ticketService.findById(ticketId);
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String username = userPrincipal.getUsername();
            User user = userService.findByUsername(username);
            return new ResponseEntity<>(ticketService.assignUserToTicket(ticketToAssign, user), HttpStatus.OK);

        } catch (NullPointerException e) {
            return new ResponseEntity<>("Ticket not found.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Authentication problem, signed in user not found.", HttpStatus.FORBIDDEN);
        }
    }

}
