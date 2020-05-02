package ru.auth.controller;
import static org.springframework.http.ResponseEntity.ok;

import ru.auth.dto.AuthenticationBodyDTO;
import ru.auth.dto.NewUserDTO;
import ru.auth.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;


@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @SuppressWarnings("rawtypes")
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationBodyDTO data) {
        return ok(authenticationService.login(data));
    }

    @SuppressWarnings("rawtypes")
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody NewUserDTO newUser) {
        authenticationService.registration(newUser);
        return ok(HttpStatus.CREATED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity handleAuthenticationException(Exception ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid login or password");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleLengthUsernameOrPassword(Exception ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid length password or username. Should be 7 chars.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }
}
