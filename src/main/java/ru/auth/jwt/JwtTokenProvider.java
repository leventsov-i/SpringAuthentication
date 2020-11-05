package ru.auth.jwt;

import org.apache.tomcat.websocket.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import ru.auth.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenProvider {
    private final String BEARER = "Bearer ";
    private final String secretKey;
    private final long validityInMilliseconds;
    private final UserDetailsService userService;

    @Autowired
    public JwtTokenProvider(
            @Value("${security.jwt.token.secret-key}") String secretKey,
            @Value("${security.jwt.token.expire-length}") long validityInMilliseconds,
            UserDetailsService userService
    ) {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.validityInMilliseconds = validityInMilliseconds;
        this.userService = userService;
    }

    public String createToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Optional<String> resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(Constants.AUTHORIZATION_HEADER_NAME);
        if (bearerToken != null && bearerToken.startsWith(BEARER)) {
            return Optional.of(bearerToken.substring(7));
        }
        return Optional.empty();
    }

    public Optional<String> validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            if (claims.getBody().getExpiration().before(new Date())) {
                return Optional.empty();
            }
            return Optional.ofNullable(claims.getBody().getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }

    }
}
