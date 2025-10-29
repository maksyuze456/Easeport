package org.easeport.itsupportsystem.controller;


import org.easeport.itsupportsystem.model.mailRelated.TicketMessage;
import org.easeport.itsupportsystem.service.TicketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticketMessages")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true", maxAge = 3600)
public class TicketMessageController {


    @Autowired
    TicketMessageService ticketMessageService;


    @GetMapping("/getConversation/{ticketId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getConversationByTicketId(@PathVariable("ticketId") Long ticketId) {

        List<TicketMessage> conversationList = ticketMessageService.getTimeConversationByTicketId(ticketId);

        return ResponseEntity.ok()
                .body(conversationList);

    }





}
