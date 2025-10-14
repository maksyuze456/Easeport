package org.easeport.itsupportsystem.unit;

import org.easeport.itsupportsystem.model.Role;
import org.easeport.itsupportsystem.model.User;
import org.easeport.itsupportsystem.repository.UserRepository;
import org.easeport.itsupportsystem.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setRole(Role.USER);
    }

    @Test
    void createUser_ShouldSaveAndReturnUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = adminService.createUser(user);

        assertEquals("alice", result.getUsername());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenExists() throws Exception {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        User result = adminService.findByUsername("alice");

        assertEquals("alice@example.com", result.getEmail());
    }

    @Test
    void findByUsername_ShouldThrowException_WhenNotFound() {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> adminService.findByUsername("bob"));
    }

    @Test
    void findAllByRoleUser_ShouldReturnListOfUsers_WhenExists() {
        List<User> users = Arrays.asList(user, new User("maksyuze", "bob", Role.USER.name()));
        when(userRepository.findAllByRole(Role.USER)).thenReturn(Optional.of(users));

        List<User> result = adminService.findAllByRoleUser();

        assertEquals(2, result.size());
        assertEquals("alice", result.get(0).getUsername());
    }

    @Test
    void findAllByRoleUser_ShouldThrowError_WhenNoUsersFound() {
        when(userRepository.findAllByRole(Role.USER)).thenReturn(Optional.empty());

        assertThrows(Error.class, () -> adminService.findAllByRoleUser());
    }

    @Test
    void updateUser_ShouldSaveAndReturnUpdatedUser() {
        user.setEmail("newalice@example.com");
        when(userRepository.save(user)).thenReturn(user);

        User result = adminService.updateUser(user);

        assertEquals("newalice@example.com", result.getEmail());
    }
}
