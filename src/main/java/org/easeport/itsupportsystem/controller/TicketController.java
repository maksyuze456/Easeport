package org.easeport.itsupportsystem.controller;

import jakarta.websocket.server.PathParam;
import org.apache.coyote.Response;
import org.easeport.itsupportsystem.dto.AnswerDto;
import org.easeport.itsupportsystem.dto.TicketResponseDto;
import org.easeport.itsupportsystem.exception.TicketHasNoAssignedUserException;
import org.easeport.itsupportsystem.exception.TicketNotFoundException;
import org.easeport.itsupportsystem.exception.UserNotAssignedException;
import org.easeport.itsupportsystem.exception.UserNotFoundException;
import org.easeport.itsupportsystem.model.Ticket;
import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.model.ticketEnums.TicketStatus;
import org.easeport.itsupportsystem.security.dto.MessageResponse;
import org.easeport.itsupportsystem.security.security_entity.UserPrincipal;
import org.easeport.itsupportsystem.service.TicketService;
import org.easeport.itsupportsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true", maxAge = 3600)
public class TicketController {

    @Autowired
    TicketService ticketService;

    @Autowired
    UserService userService;


    @PostMapping("/assign/{ticketId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> assignTicket(@PathVariable("ticketId") Long ticketId, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String username = userPrincipal.getUsername();
            User user = userService.findByUsername(username);
            TicketResponseDto updatedTicket = ticketService.assignUserToTicket(ticketId, user);
            return new ResponseEntity<>(updatedTicket, HttpStatus.OK);

        } catch (TicketNotFoundException | UserNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/setAnswer/{ticketId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> setAnswerToTicket(@PathVariable("ticketId") Long ticketId, @RequestBody AnswerDto answerDto, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.findByUsername(userPrincipal.getUsername());

            TicketResponseDto updatedTicket = ticketService.setAnswer(ticketId, user, answerDto);

            return ResponseEntity.ok()
                    .body(new MessageResponse("Answer is set!"));

        }  catch (TicketNotFoundException | UserNotFoundException | UserNotAssignedException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }


    @GetMapping("/getAllByStatus/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getAllTicketsByStatus(@PathVariable("status")TicketStatus status) {
        if (!EnumSet.of(TicketStatus.Open, TicketStatus.Reviewing, TicketStatus.Closed)
                .contains(status)) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Wrong ticket status type!"));
        }

        return ResponseEntity.ok().
                body(ticketService.getAllTicketsByStatus(status));
    }

    @GetMapping("/{ticketId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getTicketById(@PathVariable("ticketId") Long id) {
        try {
            Ticket ticket = ticketService.findById(id);

            return ResponseEntity.ok()
                    .body(ticket);

        } catch(TicketNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }


    @GetMapping("/employeeTickets")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getAllTicketsByStatusAndEmployeeId(@RequestParam("status") TicketStatus status, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.findByUsername(userPrincipal.getUsername());

            List<TicketResponseDto> ticketList = ticketService.getAllTicketsByStatusAndEmployeeId(status, user.getId());

            return ResponseEntity.ok()
                    .body(ticketList);

        } catch(UserNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/close/{ticketId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> closeAndSendTicket(@PathVariable("ticketId") Long ticketId, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.findByUsername(userPrincipal.getUsername());
            ticketService.closeTicket(ticketId, user);

            return ResponseEntity.ok()
                    .body(new MessageResponse("Ticket is successfully closed and sent."));
        } catch(TicketNotFoundException | UserNotFoundException | UserNotAssignedException |
                TicketHasNoAssignedUserException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }

}
