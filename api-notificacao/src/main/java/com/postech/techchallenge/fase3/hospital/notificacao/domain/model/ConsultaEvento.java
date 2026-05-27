package com.postech.techchallenge.fase3.hospital.notificacao.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultaEvento(
        UUID pacienteId,
        UUID profissionalId,
        LocalDateTime dataConsulta,
        String status
) {}
