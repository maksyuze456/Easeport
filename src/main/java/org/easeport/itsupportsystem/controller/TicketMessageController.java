package org.easeport.itsupportsystem.controller;


import org.easeport.itsupportsystem.exception.TicketNotFoundException;
import org.easeport.itsupportsystem.exception.UserNotAssignedException;
import org.easeport.itsupportsystem.exception.UserNotFoundException;
import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.model.mailRelated.TicketMessage;
import org.easeport.itsupportsystem.security.dto.MessageResponse;
import org.easeport.itsupportsystem.security.security_entity.UserPrincipal;
import org.easeport.itsupportsystem.service.TicketMessageService;
import org.easeport.itsupportsystem.service.TicketService;
import org.easeport.itsupportsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticketMessages")
@CrossOrigin(origins = "${allowed.origin}", allowCredentials = "true", maxAge = 3600)
public class TicketMessageController {


    @Autowired
    TicketMessageService ticketMessageService;
    @Autowired
    TicketService ticketService;

    @Autowired
    UserService userService;

    @GetMapping("/getConversation/{ticketId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getConversationByTicketId(@PathVariable("ticketId") Long ticketId) {

        List<TicketMessage> conversationList = ticketMessageService.getTimeConversationByTicketId(ticketId);

        return ResponseEntity.ok()
                .body(conversationList);

    }

    @PostMapping("/sendAnswer/{ticketId}/reply/{ticketMessageId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> sendAnswer(@PathVariable("ticketId") Long ticketId, @PathVariable("ticketMessageId") Long ticketMessageId,  Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.findByUsername(userPrincipal.getUsername());

            boolean sent = ticketService.sendAnswer(user, ticketId, ticketMessageId);

            if(sent) {
                return ResponseEntity.ok()
                        .body(new MessageResponse("Answer was sent!"));
            } else {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Answer couldn't be sent"));
            }

        } catch(TicketNotFoundException | UserNotFoundException | UserNotAssignedException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }





}
