package ru.netology.cloudstorage.controller;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.netology.cloudstorage.dto.authentication.AuthRequest;
import ru.netology.cloudstorage.dto.authentication.AuthResponse;
import ru.netology.cloudstorage.jwt.JwtService;
import ru.netology.cloudstorage.model.Role;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.impl.AuthorizationRepositoryImpl;
import ru.netology.cloudstorage.service.AuthenticationService;
import ru.netology.cloudstorage.service.UserService;


import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {
    @InjectMocks
    private AuthenticationService authenticationService;
    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;
    @Mock
    AuthorizationRepositoryImpl authorizationRepository;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    private static final String TEST_USERNAME = "test@test.com";
    private static final String TEST_PASSWORD = "password";
    private static final Long TEST_ID = 1L;
    private static final Role TEST_ROLE = Role.USER;
    private static final String TEST_JWT_TOKEN = "TEST_TOKEN";
    @Test
    void loginTest(){
        AuthRequest authRequest = buildAuthRequest();
        User user = buildUser();
        when(userService.loadUserByUsername(TEST_USERNAME)).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(TEST_JWT_TOKEN);

        AuthResponse responseResult = authenticationService.login(authRequest);
        Assertions.assertNotNull(responseResult);
        Assertions.assertEquals("TEST_TOKEN", responseResult.getToken());

    }

    private AuthRequest buildAuthRequest(){
        return new AuthRequest(TEST_USERNAME, TEST_PASSWORD);
    }

    private User buildUser(){
        return new User(TEST_ID, TEST_USERNAME, TEST_PASSWORD, TEST_ROLE);
    }
}
