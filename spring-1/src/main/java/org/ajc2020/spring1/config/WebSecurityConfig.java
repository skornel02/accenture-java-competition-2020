package org.ajc2020.spring1.config;

import org.ajc2020.spring1.filter.AuthFilter;
import org.ajc2020.spring1.filter.CustomCorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthFilter authFilter;
    private final CustomCorsFilter corsFilter;

    @Autowired
    public WebSecurityConfig(AuthFilter authFilter, CustomCorsFilter corsFilter) {
        this.authFilter = authFilter;
        this.corsFilter = corsFilter;
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .cors()

                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**",
                        "/rfid/**").permitAll()
                .anyRequest().authenticated()

                .and()
                .addFilterBefore(authFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(corsFilter, AuthFilter.class);
    }

}