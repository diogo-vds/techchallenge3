package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web;

import com.postech.techchallenge.fase3.hospital.config.JwtService;
import com.postech.techchallenge.fase3.hospital.config.Role;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
public class TokenController {

    private final JwtService jwtService;

    public TokenController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping
    public String token(
            @RequestParam String usuario,
            @RequestParam Role role
    ) {

        return jwtService.generateToken(usuario, role);
    }
}