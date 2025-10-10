package org.easeport.itsupportsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easeport.itsupportsystem.config.OpenAIConfig;
import org.easeport.itsupportsystem.dto.TicketRequestDto;
import org.easeport.itsupportsystem.model.aiRelated.ChatMessage;
import org.easeport.itsupportsystem.model.aiRelated.ChatRequest;
import org.easeport.itsupportsystem.model.aiRelated.ChatResponse;
import org.easeport.itsupportsystem.model.mailRelated.RawEmail;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ChatGPTWebClientService {

    private final WebClient webClient;
    private final OpenAIConfig config;

    public ChatGPTWebClientService(WebClient webClient, OpenAIConfig config) {
        this.webClient = webClient;
        this.config = config;
    }

    public TicketRequestDto processTicket(RawEmail rawEmail) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ChatMessage message = new ChatMessage("user", mapper.writeValueAsString(rawEmail));
        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRole("system");
        systemMessage.setContent(
                "You are an AI assistant that categorizes support tickets. " +
                        "For each email, extract the subject, name and email (name and email is seen in field named `from` which includes name and mail, extract mail from <> brackets), body, and messageId (including the <>) and assign the best-fitting values for the following fields: " +
                        "type (Incident, Request, Problem, Change), " +
                        "queueType (Technical_Support, Returns_And_Exchanges, Billing_And_Payments, Sales_And_Pre_Sales, Service_Outages_And_Maintenance, Product_Support, It_Support, Customer_Service, Human_Resources, General_Inquiry), " +
                        "language (en or da), " +
                        "priority (High, Medium, Low), " +
                        "ticketStatus (always Open). " +
                        "Return the result strictly as a JSON object with the following exact fields: " +
                        "`subject`, `name`, `from`, `body`, `type`, `queueType`, `language`, `priority`, `ticketStatus`. `messageId` " +
                        "Do not include any extra text or explanation."
        );

        ChatRequest request = new ChatRequest();
        request.setModel(config.getModel());
        request.setMessages(List.of(systemMessage, message));

        System.out.println("=== REQUEST JSON ===");
        System.out.println(mapper.writeValueAsString(request));

        TicketRequestDto requestDto = webClient.post()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + config.getApiKey())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .map(chatResponse -> {
                    String json = chatResponse.getChoices().get(0).getMessage().getContent();
                    System.out.println(json);
                    try {
                        TicketRequestDto ticketRequestDto = mapper.readValue(json, TicketRequestDto.class);
                        return ticketRequestDto;
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .block();
        return requestDto;
    }

}
