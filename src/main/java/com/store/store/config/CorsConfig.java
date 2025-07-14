package com.store.store.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class CorsConfig {
    /*
     * @Bean
     * public CorsFilter corsFilter() {
     * UrlBasedCorsConfigurationSource source = new
     * UrlBasedCorsConfigurationSource();
     * CorsConfiguration config = new CorsConfiguration();
     * config.setAllowCredentials(true);
     * config.setAllowedOrigins(Arrays.asList("http://localhost:3000",
     * "http://192.168.1.121:3000", "*"));
     * config.setAllowedHeaders(Arrays.asList("*"));
     * config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE",
     * "OPTIONS"));
     * source.registerCorsConfiguration("/**", config);
     * return new CorsFilter(source);
     * }
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Allow CORS on all endpoints
                        .allowedOrigins(
                                "http://localhost:3000",
                                "http://192.168.1.121:3000",
                                "https://iteci-c8bd4.web.app/") // ✅ Allow specific origins
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // ✅ Allow all HTTP methods
                        .allowedHeaders("*") // ✅ Allow all headers
                        .allowCredentials(true); // ✅ Allow credentials (cookies, auth headers)
            }
        };
    }

}