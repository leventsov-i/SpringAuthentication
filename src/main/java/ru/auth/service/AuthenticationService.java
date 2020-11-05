package ru.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.auth.controller.dto.AuthenticationLoginRequestDTO;
import ru.auth.controller.dto.AuthenticationLoginResponseDTO;
import ru.auth.controller.dto.NewUserDTO;
import ru.auth.entity.User;
import ru.auth.jwt.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.auth.repository.UserRepository;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserActivateService userActivateService;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager,
                                 JwtTokenProvider jwtTokenProvider,
                                 UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 UserActivateService userActivateService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userActivateService = userActivateService;
    }

    public void registration(NewUserDTO newUser) {
        Optional<User> userExists = userRepository.findByEmail(newUser.getEmail());
        if (userExists.isPresent()) {
            throw new BadCredentialsException("User with username: " + newUser.getEmail() + " already exists");
        }
        User user = User.builder()
                .email(newUser.getEmail())
                .password(passwordEncoder.encode(newUser.getPassword()))
                .isEnabled(false)
                .isCredentialsExpired(false)
                .isLocked(false)
                .build();

        userRepository.save(user);
        userActivateService.sentActivateMessage(user.getEmail(), user.getId());
    }

    public AuthenticationLoginResponseDTO login(AuthenticationLoginRequestDTO loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        User user = userRepository
                .findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("User with username: " + loginRequest.getEmail() + " not found"));

        return AuthenticationLoginResponseDTO.builder()
                .token(jwtTokenProvider.createToken(user))
                .build();
    }
}
