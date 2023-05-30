package ru.netology.cloudstorage.repository.impl;

import org.springframework.stereotype.Repository;
import ru.netology.cloudstorage.repository.AuthorizationRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AuthorizationRepositoryImpl implements AuthorizationRepository {
    private final Map<String, String> tokenUserMap = new ConcurrentHashMap<>();

    public void save(String userEmail, String token){
        tokenUserMap.put(token, userEmail);
    }

    public void delete(String token){
        tokenUserMap.remove(token.substring(7));
    }

    public Optional<String> getUserEmail(String token){
        String subToken = token.substring(7);
        String email = tokenUserMap.get(subToken);
        return Optional.ofNullable(email);
    }
}
