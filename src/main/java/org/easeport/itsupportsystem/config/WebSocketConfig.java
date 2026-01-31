package org.easeport.itsupportsystem.config;


import org.easeport.itsupportsystem.websocket.JwtHandshakeInterceptor;
import org.easeport.itsupportsystem.websocket.WebSocketAuthChannelInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Autowired
    private WebSocketAuthChannelInterceptor webSocketAuthChannelInterceptor;


    @Value("${allowed.origin}")
    String allowedOrigin;
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        System.out.println("=== Configuring Message Broker ===");
        config.enableSimpleBroker("/topic", "/queue")
                .setTaskScheduler(taskScheduler())
                .setHeartbeatValue(new long[]{10000, 10000}); // Send heartbeat every 10s, expect from client every 10s
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Bean
    public org.springframework.scheduling.TaskScheduler taskScheduler() {
        org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler scheduler =
            new org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler();
        scheduler.setPoolSize(8);
        scheduler.setThreadNamePrefix("ws-heartbeat-");
        scheduler.initialize();
        return scheduler;
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Raw WebSocket endpoint (preferred for modern browsers and mobile)
        registry.addEndpoint("/ws")
                .setAllowedOrigins(allowedOrigin.split(","))
                .addInterceptors(jwtHandshakeInterceptor);

        // SockJS fallback endpoint
        registry.addEndpoint("/ws-sockjs")
                .setAllowedOrigins(allowedOrigin.split(","))
                .addInterceptors(jwtHandshakeInterceptor)
                .withSockJS()
                .setHeartbeatTime(10000);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthChannelInterceptor);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }
}
