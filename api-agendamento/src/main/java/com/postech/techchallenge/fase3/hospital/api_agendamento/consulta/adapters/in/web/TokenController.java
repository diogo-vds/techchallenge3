package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web;

import com.postech.techchallenge.fase3.hospital.config.JwtService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    private final JwtService jwtService;

    public TokenController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/token")
    public String token() {

        return jwtService.generateToken();
    }
}