package org.easeport.itsupportsystem.service;

import org.easeport.itsupportsystem.exception.UserNotFoundException;
import org.easeport.itsupportsystem.model.Role;
import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.repository.UserRepository;
import org.easeport.itsupportsystem.security.dto.UpdateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }
    public List<User> findAllByRoleUser() {
        return userRepository.findAllByRole(Role.USER).orElseThrow(Error::new);
    }

    public void updateUser(UpdateUserRequest updateUserRequest) {


    }


}
