package ru.auth.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AuthenticationLoginRequestDTO {
    private String username;
    private String password;

}
