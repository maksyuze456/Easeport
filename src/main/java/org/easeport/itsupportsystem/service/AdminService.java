package org.easeport.itsupportsystem.service;

import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.repository.UserRepository;
import org.easeport.itsupportsystem.security.dto.CreateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    UserRepository userRepository;


    public User createUser(User user) {

        User savedUser = userRepository.save(user);

        return savedUser;

    }

}
