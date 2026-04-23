package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in;

import java.time.LocalDateTime;
import java.util.UUID;

public record CriarConsultaCommand(
        UUID pacienteId,
        UUID profissionalId,
        LocalDateTime dataHora,
        String descricao
) {}