package ru.netology.cloudstorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.dto.authentication.AuthRequest;
import ru.netology.cloudstorage.dto.authentication.AuthResponse;
import ru.netology.cloudstorage.jwt.JwtService;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.impl.AuthorizationRepositoryImpl;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserService userService;
    private final AuthorizationRepositoryImpl authorizationRepository;
    private final JwtService jwtService;

    public AuthResponse login(AuthRequest request) {
        User user = userService.loadUserByUsername(request.getLogin());
        log.info("Get user: " + user.getEmail());
        var jwtToken = jwtService.generateToken(user);
        log.info("Generate token for user: " + user.getEmail());
        authorizationRepository.save(user.getEmail(), jwtToken);
        log.info("User was saved in auth repository " + user.getEmail());
        return new AuthResponse(jwtToken);
    }

    public void logout(String authToken) {
        final String token = authToken.substring(7);
        authorizationRepository.delete(token);
        log.info("User logout: " + authorizationRepository.getUserEmail(token));
    }
}
