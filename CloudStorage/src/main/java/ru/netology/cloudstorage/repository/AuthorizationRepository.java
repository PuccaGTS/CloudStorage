package ru.netology.cloudstorage.repository;

import java.util.Optional;

public interface AuthorizationRepository {
    void save(String userEmail, String token);
    void delete(String token);
    Optional<String> getUserEmail(String token);
}
