package com.store.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        // ===== In-memory users =====
        @Bean
        public InMemoryUserDetailsManager userDetailsService() {
                UserDetails admin = User.withUsername("admin")
                                .password("{noop}admin123")
                                .roles("ADMIN")
                                .build();
                UserDetails user = User.withUsername("jorge")
                                .password("{noop}user123")
                                .roles("USER")
                                .build();
                return new InMemoryUserDetailsManager(admin, user);
        }

        // ===== Security filter chain =====
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> {
                                }) // Enable CORS using WebMvcConfigurer
                                .authorizeHttpRequests(auth -> auth
                                                // Allow all OPTIONS preflight requests
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                // Public ping endpoint
                                                .requestMatchers("/ping/**").permitAll()
                                                .anyRequest().authenticated())
                                .httpBasic() // Basic Auth
                                .and()
                                .csrf().disable(); // Disable CSRF for APIs

                return http.build();
        }

        // ===== Global CORS settings =====
        @Bean
        public WebMvcConfigurer corsConfigurer() {
                return new WebMvcConfigurer() {
                        @Override
                        public void addCorsMappings(CorsRegistry registry) {
                                registry.addMapping("/**")
                                                .allowedOriginPatterns(
                                                                "https://my-react-1095159323845.us-central1.run.app",
                                                                "http://localhost:3000",
                                                                "http://192.168.1.121:3000",
                                                                "http://192.168.0.227:3000",
                                                                "http://192.168.1.121:8080",
                                                                "http://192.168.0.227:8080",
                                                                "https://iteci-c8bd4.web.app")
                                                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                                                .allowedHeaders("*")
                                                .allowCredentials(true); // Required for Basic Auth or cookies
                        }
                };
        }
}
