package org.easeport.itsupportsystem.service;

import org.easeport.itsupportsystem.model.Role;
import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User findByUsername(String username) throws Exception {
        return userRepository.findByUsername(username).orElseThrow(Exception::new);
    }
    public List<User> findAllByRoleUser() {
        return userRepository.findAllByRole(Role.USER).orElseThrow(Error::new);
    }

}
