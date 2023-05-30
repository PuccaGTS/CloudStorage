package ru.netology.cloudstorage.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    @NotBlank(message = "Login can not be null")
    private String login;
    @NotBlank(message = "Password can not be null")
    private String password;
}
