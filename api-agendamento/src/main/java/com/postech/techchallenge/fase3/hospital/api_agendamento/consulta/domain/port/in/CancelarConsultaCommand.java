package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in;

import java.util.UUID;

public record CancelarConsultaCommand(
    UUID consultaId,
    String motivo
) {}

