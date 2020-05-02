package ru.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    public boolean registration(NewUserDTO newUser) {
        User userExists = userService.findByUsername(newUser.getUsername());
        if (userExists != null) {
            throw new BadCredentialsException("User with username: " + newUser.getUsername() + " already exists");
        }
        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setPassword(newUser.getPassword());
        user.setDateChangePassword(System.currentTimeMillis());
        userService.saveUser(user);
        Map<Object, Object> model = new HashMap<>();
        model.put("message", "User registered successfully");
        return true;
    }

    public Map<String, String> login(AuthenticationBodyDTO data) {
            String username = data.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            String token = jwtTokenProvider.createToken(userService.findByUsername(username));
            Map<String, String> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);
            return model;
    }
}
