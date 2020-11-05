package ru.auth.jwt;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import ru.auth.entity.Role;
import ru.auth.service.UserService;

@Component
public class JwtTokenFilter extends GenericFilterBean {
    private final Set<Role> ONE_ROLE = Collections.singleton(new Role(1L, "ROLE_USER"));
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Autowired
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        jwtTokenProvider.resolveToken((HttpServletRequest) req)
                .flatMap(jwtTokenProvider::validateToken)
                .ifPresent(username -> {
                    UserDetails user = userService.loadUserByUsername(username);
                    if (user.isCredentialsNonExpired() && user.isAccountNonLocked() && user.isEnabled()) {
                        Authentication auth = getAuthentication(user);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                });
        filterChain.doFilter(req, res);
    }

    private Authentication getAuthentication(UserDetails user) {
        return new UsernamePasswordAuthenticationToken(user, "", ONE_ROLE);
    }
}
