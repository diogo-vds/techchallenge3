package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto.auth;

public record LoginRequest(
        String email,
        String password
) {
}