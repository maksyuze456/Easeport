package org.easeport.itsupportsystem.config;

import org.easeport.itsupportsystem.model.Role;
import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitData implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User("admin", passwordEncoder.encode("admin123"), Role.ADMIN, "admin@gmail.com");

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
    }
}
