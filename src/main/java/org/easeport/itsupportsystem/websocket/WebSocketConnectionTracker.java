package org.easeport.itsupportsystem.websocket;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tags;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class WebSocketConnectionTracker
        implements ApplicationListener<AbstractSubProtocolEvent> {

    private final AtomicInteger activeConnections = new AtomicInteger();
    private final MeterRegistry registry;

    // sessionId -> username
    private final ConcurrentHashMap<String, String> sessionUsers = new ConcurrentHashMap<>();
    // sessionId -> connect timestamp (epoch millis)
    private final ConcurrentHashMap<String, Long> sessionConnectTime = new ConcurrentHashMap<>();

    private final MultiGauge activeUsersGauge;
    private final MultiGauge sessionDurationGauge;

    public WebSocketConnectionTracker(MeterRegistry registry) {
        this.registry = registry;

        Gauge.builder("websocket.connections.active",
                      activeConnections,
                      AtomicInteger::get)
             .description("Number of active WebSocket STOMP connections")
             .register(registry);

        Gauge.builder("websocket.users.active",
                      sessionUsers,
                      map -> map.values().stream().distinct().count())
             .description("Number of distinct active WebSocket users")
             .register(registry);

        this.activeUsersGauge = MultiGauge.builder("websocket.user.connected")
                .description("Whether a user is currently connected (1 = connected)")
                .register(registry);

        this.sessionDurationGauge = MultiGauge.builder("websocket.user.session.duration.seconds")
                .description("How long each user has been connected in seconds")
                .register(registry);
    }

    @Override
    public void onApplicationEvent(AbstractSubProtocolEvent event) {
        if (event instanceof SessionConnectedEvent) {
            activeConnections.incrementAndGet();
            String sessionId = getSessionId(event);
            String username = getUsername(event);
            if (sessionId != null && username != null) {
                sessionUsers.put(sessionId, username);
                sessionConnectTime.put(sessionId, System.currentTimeMillis());
            }
            updateMultiGauges();
        } else if (event instanceof SessionDisconnectEvent) {
            activeConnections.decrementAndGet();
            String sessionId = getSessionId(event);
            if (sessionId != null) {
                sessionUsers.remove(sessionId);
                sessionConnectTime.remove(sessionId);
            }
            updateMultiGauges();
        }
    }

    @Scheduled(fixedRate = 5000)
    public void refreshDurationGauges() {
        long now = System.currentTimeMillis();
        sessionDurationGauge.register(
            sessionConnectTime.entrySet().stream()
                .filter(e -> sessionUsers.containsKey(e.getKey()))
                .map(e -> {
                    String username = sessionUsers.get(e.getKey());
                    double seconds = (now - e.getValue()) / 1000.0;
                    return MultiGauge.Row.of(Tags.of("username", username, "session_id", e.getKey()), seconds);
                })
                .collect(Collectors.toList()),
            true
        );
    }

    private void updateMultiGauges() {
        activeUsersGauge.register(
            sessionUsers.values().stream()
                .distinct()
                .map(username -> MultiGauge.Row.of(Tags.of("username", username), 1))
                .collect(Collectors.toList()),
            true
        );
    }

    private String getSessionId(AbstractSubProtocolEvent event) {
        return (String) event.getMessage().getHeaders().get("simpSessionId");
    }

    private String getUsername(AbstractSubProtocolEvent event) {
        Principal user = event.getUser();
        if (user != null) {
            return user.getName();
        }
        return null;
    }
}
