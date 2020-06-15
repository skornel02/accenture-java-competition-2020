package org.ajc2020.spring1.filter;

import org.ajc2020.spring1.config.KIBeConfig;
import org.ajc2020.spring1.manager.AuthManager;
import org.ajc2020.utility.resource.PermissionLevel;
import org.ajc2020.spring1.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Component
public final class AuthFilter extends OncePerRequestFilter {

    private static final String USER_PREFIX = "Basic";

    private final AuthManager authManager;
    private final KIBeConfig config;

    @Autowired
    public AuthFilter(AuthManager authManager, KIBeConfig config) {
        this.authManager = authManager;
        this.config = config;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String auth = Optional.ofNullable(request.getHeader("Authorization")).orElse("-");
        if (auth.startsWith(USER_PREFIX)) {
            String tokenEncoded = auth.replaceFirst(USER_PREFIX, "").trim();
            try {
                String decoded = new String(Base64.getDecoder().decode(tokenEncoded));
                String[] split = decoded.split(":");
                if (split.length == 2) {
                    String username = split[0];
                    String password = split[1];
                    Optional<User> userOptional = authManager.findValidUser(username, password);
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user,
                                decoded,
                                Collections.singleton(new SimpleGrantedAuthority(user.getPermissionLevel().getAuthority())));

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (IllegalArgumentException ignored) { /*Invalid base64 format */ }
        }
        if (auth.startsWith(config.getDevice().getAuthorizationType())) {
            String token = auth.replaceFirst(config.getDevice().getAuthorizationType(), "").trim();
            if (Objects.equals(config.getDevice().getToken(), token)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(null,
                        token,
                        Collections.singleton(new SimpleGrantedAuthority(PermissionLevel.DEVICE.getAuthority())));

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}