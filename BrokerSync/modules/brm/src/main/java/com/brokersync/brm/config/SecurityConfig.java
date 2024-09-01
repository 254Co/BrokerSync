package com.brokersync.brm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LogManager.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring HTTP security for API Gateway.");
        http
            .authorizeHttpRequests() // Updated method to align with Spring Security 6
                .requestMatchers("/api/firms/**").hasRole("BROKER") // Only brokers can access firm endpoints
                .requestMatchers("/api/traders/**").hasRole("BROKER") // Only brokers can access trader endpoints
                .requestMatchers("/api/interactions/**").hasAnyRole("BROKER", "TRADER") // Both can access interaction endpoints
                .requestMatchers("/api/portfolios/**").hasRole("BROKER") // Only brokers can access portfolio endpoints
                .requestMatchers("/api/roles/**").hasRole("BROKER") // Only brokers can access role endpoints
                .requestMatchers("/api/messages/**").hasAnyRole("BROKER", "TRADER") // Both can access message endpoints
                .anyRequest().authenticated() // All other requests require authentication
                .and()
            .httpBasic() // Enable basic authentication
                .and()
            .csrf().disable(); // Disable CSRF for simplicity
        logger.info("HTTP security configured successfully.");
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Creating PasswordEncoder bean.");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        logger.info("Configuring in-memory authentication for demo purposes.");
        auth.inMemoryAuthentication()
            .withUser("broker").password(passwordEncoder().encode("password")).roles("BROKER")
            .and()
            .withUser("trader").password(passwordEncoder().encode("password")).roles("TRADER");
        logger.info("In-memory authentication configured successfully.");
    }
}