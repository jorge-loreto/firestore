package com.store.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.withUsername("admin")
                .password("{noop}admin123") // {noop} means no password encoding
                .roles("ADMIN")
                .build();
        UserDetails user = User.withUsername("user")
                .password("{noop}user123")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ping/**").permitAll() // <-- Make ping public
                        .requestMatchers("/locations/**").hasRole("ADMIN")
                        .anyRequest().authenticated());
        http.httpBasic()
                .and()
                .csrf().disable(); // Disable CSRF for simplicity, not recommended for production

        return http.build();
    }

}
