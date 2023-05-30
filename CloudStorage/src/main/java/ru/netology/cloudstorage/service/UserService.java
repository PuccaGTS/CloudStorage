package ru.netology.cloudstorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.exception.BadRequestException;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(()->new BadRequestException("Bad credential for " + username +
                        ".Error input data"));
    }
}
