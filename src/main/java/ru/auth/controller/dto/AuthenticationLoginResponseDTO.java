package ru.auth.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthenticationLoginResponseDTO {
    private final String token;
}
