package ru.auth.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AuthenticationLoginRequestDTO {
    private String email;
    private String password;
}
