package ru.auth.controller;

import org.apache.tomcat.websocket.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import ru.auth.controller.dto.AuthenticationLoginRequestDTO;
import ru.auth.controller.dto.NewUserDTO;
import ru.auth.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;


@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationLoginRequestDTO data) {
        return ResponseEntity.ok()
                .header(Constants.AUTHORIZATION_HEADER_NAME, authenticationService.login(data).getToken())
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody NewUserDTO newUser) {
        authenticationService.registration(newUser);
        return ResponseEntity.ok().build();
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

    //@ExceptionHandler(Exception.class)
    //public ResponseEntity handleException(Exception ex, HttpServletRequest request) {
    //    return ResponseEntity
    //            .status(HttpStatus.INTERNAL_SERVER_ERROR)
    //            .build();
    //}
}
