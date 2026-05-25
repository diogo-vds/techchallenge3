package com.postech.techchallenge.fase3.hospital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserConfig {

    @Bean
    public UserDetailsService userDetailsService() {

        return new InMemoryUserDetailsManager(

                User.builder()
                        .username("admin@hospital.com")
                        .password("$2a$10$DowJonesIndex12345678901234567890")
                        .roles("ADMIN")
                        .build()
        );
    }
}