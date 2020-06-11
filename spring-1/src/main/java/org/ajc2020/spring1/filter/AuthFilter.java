package org.ajc2020.spring1.filter;

import org.ajc2020.spring1.manager.AuthManager;
import org.ajc2020.spring1.model.Worker;
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
import java.util.Optional;

@Component
public final class AuthFilter extends OncePerRequestFilter {

    private static final String WORKER_PREFIX = "Basic";
    private static final String DEVICE_PREFIX = "DeviceToken";

    private final AuthManager authManager;

    @Autowired
    public AuthFilter(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String auth = Optional.ofNullable(request.getHeader("Authorization")).orElse("-");
        if (auth.startsWith(WORKER_PREFIX)) {
            String tokenEncoded = auth.replaceFirst(WORKER_PREFIX, "").trim();
            try {
                String decoded = new String(Base64.getDecoder().decode(tokenEncoded));
                String[] splitted = decoded.split(":");
                if (splitted.length == 2) {
                    String username = splitted[0];
                    String password = splitted[1];
                    Optional<Worker> user = authManager.findValidUser(username, password);
                    if (user.isPresent()) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.get(),
                                decoded,
                                Collections.singleton(new SimpleGrantedAuthority("WORKER")));

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (IllegalArgumentException ignored) { /*Invalid base64 format */ }
        }
        chain.doFilter(request, response);
    }
}