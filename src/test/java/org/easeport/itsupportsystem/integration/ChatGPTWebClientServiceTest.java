package org.easeport.itsupportsystem.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easeport.itsupportsystem.config.OpenAIConfig;
import org.easeport.itsupportsystem.dto.TicketRequestDto;
import org.easeport.itsupportsystem.model.aiRelated.ChatChoice;
import org.easeport.itsupportsystem.model.aiRelated.ChatMessage;
import org.easeport.itsupportsystem.model.aiRelated.ChatResponse;
import org.easeport.itsupportsystem.model.mailRelated.RawEmail;
import org.easeport.itsupportsystem.service.ChatGPTWebClientService;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;



import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ChatGPTWebClientServiceTest {

    @Test
    void testProcessTicketWithMockedAi() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        OpenAIConfig config = mock(OpenAIConfig.class);
        when(config.getApiKey()).thenReturn("fake-api-key");
        when(config.getModel()).thenReturn("fake-model");


        WebClient webClient = mock(WebClient.class);
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);


        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        String fakeJson = """
            {
              "subject": "Login issue",
              "name": "John Doe",
              "from": "john@example.com",
              "body": "I can't log in to my account.",
              "type": "Incident",
              "queueType": "Technical_Support",
              "language": "en",
              "priority": "High",
              "ticketStatus": "Open",
              "messageId": "1"
            }
            """;

        ChatMessage chatMessage = new ChatMessage("assistant", fakeJson);
        ChatChoice chatChoice = new ChatChoice(chatMessage);
        ChatResponse chatResponse = new ChatResponse(List.of(chatChoice));

        when(responseSpec.bodyToMono(ChatResponse.class)).thenReturn(Mono.just(chatResponse));

        ChatGPTWebClientService service = new ChatGPTWebClientService(webClient, config);

        RawEmail rawEmail = new RawEmail("Login issue", "John Doe", "I can't log in to my account", "1");

        TicketRequestDto requestDto = service.processTicket(rawEmail);

        assertEquals("Login issue", requestDto.subject());
        assertEquals("John Doe", requestDto.name());
        assertEquals("Incident", requestDto.type().name());
        assertEquals("Technical_Support", requestDto.queueType().name());
        assertEquals("en", requestDto.language().name());
        assertEquals("High", requestDto.priority().name());
        assertEquals("Open", requestDto.ticketStatus().name());
        assertEquals("1", requestDto.messageId());
        System.out.printf("""
    Expected vs Actual:
    subject: Login issue | %s
    name: John Doe | %s
    type: Incident | %s
    queueType: Technical_Support | %s
    language: en | %s
    priority: High | %s
    ticketStatus: Open | %s
    messageId: 1 | %s
    """,
                requestDto.subject(), requestDto.name(), requestDto.type().name(), requestDto.queueType().name(),
                requestDto.language().name(), requestDto.priority().name(), requestDto.ticketStatus().name(), requestDto.messageId()
        );



    }

}
