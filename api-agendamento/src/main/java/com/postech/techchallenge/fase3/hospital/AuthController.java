package com.postech.techchallenge.fase3.hospital;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto.auth.LoginRequest;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto.auth.LoginResponse;
import com.postech.techchallenge.fase3.hospital.config.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final JwtService jwtService;

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        var user =
                userDetailsService.loadUserByUsername(
                        request.email()
                );
        String token = jwtService.generateToken(user);
        return new LoginResponse(token);
    }
}