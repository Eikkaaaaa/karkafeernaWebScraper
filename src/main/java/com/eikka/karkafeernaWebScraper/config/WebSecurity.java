package com.eikka.karkafeernaWebScraper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurity {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {

        // Deny all traffic besides traffic to the API ang Graphql
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/").permitAll()
                .requestMatchers("/api/v1/**").permitAll()
                .requestMatchers("/graphql").permitAll()
                .requestMatchers("/graphiql").permitAll()
                .requestMatchers("/**").denyAll());

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
