package org.easeport.itsupportsystem.websocket;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.easeport.itsupportsystem.security.utility.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    @Autowired
    JwtUtils jwtUtils;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            String jwt = parseJwtFromRequest(httpRequest);

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUsernameFromJwtToken(jwt);

                attributes.put("username", username);
                System.out.println("WebSocket handshake authenticated for user: " + username);
                return true;
            }

            System.out.println("WebSocket handshake rejected: No valid JWT token");
            return false; // Reject handshake
        }

        return false;
    }

    /**
     * Extracts JWT token from the WebSocket handshake request.
     * Supports:
     * 1. Query parameter 'token' - for mobile apps (React Native Expo)
     *    Example: ws://server/ws?token=eyJhbGciOiJIUzUxMiJ9...
     * 2. HTTP-only cookies - for web browsers
     *
     * Query parameter takes precedence if both are present.
     */
    private String parseJwtFromRequest(HttpServletRequest request) {
        // First, try to get JWT from query parameter (mobile apps)
        String tokenParam = request.getParameter("token");
        if (tokenParam != null && !tokenParam.isEmpty()) {
            return tokenParam;
        }

        // Fallback to cookie-based auth (web browsers)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(c -> c.getName().equals("token"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
