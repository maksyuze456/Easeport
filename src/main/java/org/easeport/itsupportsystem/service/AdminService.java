package org.easeport.itsupportsystem.service;

import org.easeport.itsupportsystem.model.Role;
import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    UserRepository userRepository;


    public User createUser(User user) {

        User savedUser = userRepository.save(user);

        return savedUser;

    }

    public List<User> findAllByRoleUser() {
        return userRepository.findAllByRole(Role.USER).orElseThrow(Error::new);
    }

}
