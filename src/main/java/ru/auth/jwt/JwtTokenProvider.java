package ru.auth.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import ru.auth.entity.User;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenProvider {
    private static final String KEY_DATE_CHANGE_PASSWORD = "dateChangePassword";

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
        claims.put(KEY_DATE_CHANGE_PASSWORD, user.getDateChangePassword());
        claims.put("roles", user.getRoles());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public Optional<String> resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return Optional.of(bearerToken.substring(7));
        }
        return Optional.empty();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            User user = (User) userService.loadUserByUsername(claims.getBody().getSubject());
            if (claims.getBody().getExpiration().before(new Date())) {
                return false;
            }

            Number dateFromToken = claims.getBody().get(KEY_DATE_CHANGE_PASSWORD, Number.class);
            //если пользователь сменил пароль, то скинуть токен
            return !userChangePassword(user.getDateChangePassword(), dateFromToken);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean userChangePassword(Long dateChangePassFromUser, Number dateChangePassFromToken) {
        return dateChangePassFromUser == dateChangePassFromToken.longValue();
    }
}
