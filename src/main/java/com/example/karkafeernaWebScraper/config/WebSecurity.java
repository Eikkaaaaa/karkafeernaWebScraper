package com.example.karkafeernaWebScraper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurity {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Deny all traffic besides traffic to the API
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/api/v1/meals").permitAll()
                .anyRequest().denyAll());
        return http.build();
    }
}
