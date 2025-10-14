package org.easeport.itsupportsystem.config;

import org.easeport.itsupportsystem.model.Role;
import org.easeport.itsupportsystem.model.Ticket;
import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.model.ticketEnums.*;
import org.easeport.itsupportsystem.repository.TicketRepository;
import org.easeport.itsupportsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class InitData implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    String adminUsername;

    @Value("${admin.pass}")
    String adminPassword;

    @Value("${admin.email}")
    String adminEmail;

    @Override
    public void run(String... args) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User(adminUsername, passwordEncoder.encode(adminPassword), Role.ADMIN, adminEmail);

            userRepository.save(admin);
        }
        User u1 = new User("maksy123", passwordEncoder.encode("maks456"), Role.USER, "maksy123@gmail.com");
        User u2 = new User("gus345", passwordEncoder.encode("gus543"), Role.USER, "gus345@gmail.com");
        User u3 = new User("dan564", passwordEncoder.encode("dan465"), Role.USER, "dan564@gmail.com");
        User u4 = new User("rus012", passwordEncoder.encode("rus210"), Role.USER, "rus012@gmail.com");
        userRepository.save(u1);
        userRepository.save(u2);
        userRepository.save(u3);
        userRepository.save(u4);

        List<Ticket> tickets = Arrays.asList(
                new Ticket("Printer not working", "Alice", "alice@example.com",
                        "My office printer is not responding.", TicketType.Problem, Queue.It_Support,
                        Language.en, Priority.Medium, TicketStatus.Open, null, null),

                new Ticket("VPN issue", "Bob", "bob@example.com",
                        "Unable to connect to the VPN.", TicketType.Problem, Queue.It_Support,
                        Language.en, Priority.High, TicketStatus.Open, null, null),

                new Ticket("Password reset", "Charlie", "charlie@example.com",
                        "I forgot my password, please reset.", TicketType.Request, Queue.Service_Outages_And_Maintenance,
                        Language.en, Priority.Low, TicketStatus.Open, null, null),

                new Ticket("Email not syncing", "Diana", "diana@example.com",
                        "Outlook is not syncing with the server.", TicketType.Incident, Queue.It_Support,
                        Language.en, Priority.High, TicketStatus.Open, null, null),

                new Ticket("Software installation", "Ethan", "ethan@example.com",
                        "Need Adobe Photoshop installed.", TicketType.Request, Queue.Service_Outages_And_Maintenance,
                        Language.en, Priority.Medium, TicketStatus.Open, null, null),

                new Ticket("Laptop overheating", "Fiona", "fiona@example.com",
                        "My laptop gets too hot after 10 minutes.", TicketType.Incident, Queue.It_Support,
                        Language.en, Priority.High, TicketStatus.Open, null, null),

                new Ticket("New account setup", "George", "george@example.com",
                        "Please create a new user account for HR intern.", TicketType.Request, Queue.Service_Outages_And_Maintenance,
                        Language.en, Priority.Medium, TicketStatus.Open, null, null),

                new Ticket("Slow WiFi", "Helen", "helen@example.com",
                        "WiFi connection is very slow in the meeting room.", TicketType.Incident, Queue.It_Support,
                        Language.en, Priority.Medium, TicketStatus.Open, null, null),

                new Ticket("Access denied", "Ian", "ian@example.com",
                        "Cannot access the shared drive.", TicketType.Incident, Queue.It_Support,
                        Language.en, Priority.High, TicketStatus.Open, null, null)

        );

        ticketRepository.saveAll(tickets);
    }
}
