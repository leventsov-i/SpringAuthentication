package ru.auth.service;

import ru.auth.dto.AuthenticationBodyDTO;
import ru.auth.dto.NewUserDTO;
import ru.auth.entity.User;
import ru.auth.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    public boolean registration(NewUserDTO newUser) {
        User userExists = userService.findByUsername(newUser.getUsername());
        if (userExists != null) {
            throw new BadCredentialsException("User with username: " + newUser.getUsername() + " already exists");
        }
        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setPassword(newUser.getPassword());
        userService.saveUser(user);
        Map<Object, Object> model = new HashMap<>();
        model.put("message", "User registered successfully");
        return true;
    }

    public Map<String, String> login(AuthenticationBodyDTO data) {
            String username = data.getEmail();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            String token = jwtTokenProvider.createToken(username, userService.findByUsername(username).getRoles());
            Map<String, String> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);
            return model;
    }
}