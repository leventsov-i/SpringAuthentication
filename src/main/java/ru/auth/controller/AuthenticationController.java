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
import ru.auth.service.UserActivateService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;


@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserActivateService userActivateService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService,
                                    UserActivateService userActivateService) {
        this.authenticationService = authenticationService;
        this.userActivateService = userActivateService;
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

    @GetMapping("/activate/{code}")
    public ResponseEntity activate(@PathVariable String code) {
        userActivateService.activateUser(code);
        return ResponseEntity.ok().build();
    }
}
