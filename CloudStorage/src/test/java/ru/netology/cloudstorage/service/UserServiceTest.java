package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.cloudstorage.exception.BadRequestException;
import ru.netology.cloudstorage.model.Role;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.UserRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    private User testUser;

    @BeforeEach
    void setup(){
        testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("pass")
                .role(Role.USER)
                .build();
    }

    @Test
    void loadUserByUsernameTest(){
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.ofNullable(testUser));

        String username = "test@test.com";
        User user = userService.loadUserByUsername(username);

        Assertions.assertEquals(user, testUser);
    }

    @Test
    void loadUserByUsernameTestErrorEmail(){
        String username = "test@test.com";
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        Assertions.assertThrows(BadRequestException.class, () -> {
            userService.loadUserByUsername(username);
        });
    }
}
