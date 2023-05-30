package ru.netology.cloudstorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.dto.authentication.AuthRequest;
import ru.netology.cloudstorage.dto.authentication.AuthResponse;
import ru.netology.cloudstorage.jwt.JwtService;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.impl.AuthorizationRepositoryImpl;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final AuthorizationRepositoryImpl authorizationRepository;
    private final JwtService jwtService;

    public AuthResponse login(AuthRequest request) {
        User user = userService.loadUserByUsername(request.getLogin());
        var jwtToken = jwtService.generateToken(user);
        authorizationRepository.save(user.getEmail(), jwtToken);
        return new AuthResponse(jwtToken);
    }

    public void logout(String authToken) {
        final String token = authToken.substring(7);
        authorizationRepository.delete(token);
    }
}
