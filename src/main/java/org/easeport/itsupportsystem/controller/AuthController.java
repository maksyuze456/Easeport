package org.easeport.itsupportsystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.easeport.itsupportsystem.logging.AuditEvent;
import org.easeport.itsupportsystem.logging.service.AuditLogger;
import org.easeport.itsupportsystem.repository.UserRepository;
import org.easeport.itsupportsystem.security.dto.LoginRequest;
import org.easeport.itsupportsystem.security.dto.MessageResponse;
import org.easeport.itsupportsystem.security.security_entity.UserPrincipal;
import org.easeport.itsupportsystem.security.utility.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@CrossOrigin(origins = "${allowed.origin}", allowCredentials = "true", maxAge = 3600)
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    AuditLogger auditLogger;

    @Value("${cookie.secure:true}")
    private boolean cookieSecure;


    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        if (authentication == null) {
            return ResponseEntity.status(401).body(new MessageResponse("Unathorized user!"));
        }


        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getAuthorities().stream().findFirst().get().getAuthority()
        ));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken((UserDetails) authentication.getPrincipal());

            ResponseCookie cookie = ResponseCookie.from("token", jwt)
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .sameSite(cookieSecure ? "None" : "Lax")
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60) // 7 days
                    .build();
            response.addHeader("Set-Cookie", cookie.toString());

            auditLogger.log(new AuditEvent(
                    loginRequest.getUsername(),
                    "LOGIN",
                    "SESSION",
                    "SUCCESS",
                    request.getHeader("X-Correlation-Id"),
                    request.getRemoteAddr()
            ));

            return ResponseEntity.ok()
                    .body("Logged in");

        } catch (AuthenticationException e) {
            auditLogger.log(new AuditEvent(
                    loginRequest.getUsername(),
                    "LOGIN",
                    "SESSION",
                    "FAILURE",
                    request.getHeader("X-Correlation-Id"),
                    request.getRemoteAddr()
            ));
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Authentication authentication) {
        String actor = (authentication != null && authentication.getPrincipal() instanceof UserPrincipal user)
                ? user.getUsername()
                : "UNKNOWN";

        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSecure ? "None" : "Lax")
                .path("/")
                .maxAge(0) // expires immediately
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        auditLogger.log(new AuditEvent(
                actor,
                "LOGOUT",
                "SESSION",
                "SUCCESS",
                request.getHeader("X-Correlation-Id"),
                request.getRemoteAddr()
        ));

        return ResponseEntity.ok("Logged out successfully");
    }


}
