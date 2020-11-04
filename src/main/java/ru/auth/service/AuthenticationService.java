package ru.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.auth.controller.dto.AuthenticationLoginRequestDTO;
import ru.auth.controller.dto.AuthenticationLoginResponseDTO;
import ru.auth.controller.dto.NewUserDTO;
import ru.auth.entity.Role;
import ru.auth.entity.User;
import ru.auth.jwt.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.auth.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager,
                                 JwtTokenProvider jwtTokenProvider,
                                 UserRepository userRepository,
                                 PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registration(NewUserDTO newUser) {
        Optional<User> userExists = userRepository.findByUsername(newUser.getUsername());
        if (userExists.isPresent()) {
            throw new BadCredentialsException("User with username: " + newUser.getUsername() + " already exists");
        }
        User user = User.builder()
                .username(newUser.getUsername())
                .password(passwordEncoder.encode(newUser.getPassword()))
                .dateChangePassword(System.currentTimeMillis())
                .roles(Collections.singleton(new Role(1L, "ROLE_USER")))
                .build();

        userRepository.save(user);
    }

    public AuthenticationLoginResponseDTO login(AuthenticationLoginRequestDTO loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        User user = userRepository
                .findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BadCredentialsException("User with username: " + loginRequest.getUsername() + " not found"));

        return AuthenticationLoginResponseDTO.builder()
                .token(jwtTokenProvider.createToken(user))
                .build();
    }
}
