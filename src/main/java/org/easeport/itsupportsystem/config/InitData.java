package org.easeport.itsupportsystem.config;

import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitData implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User u1 = new User("maksyuze456", encoder.encode("fess36"), "maksyuze456@gmail.com");
        userRepository.save(u1);
    }
}
