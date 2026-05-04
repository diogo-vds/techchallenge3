package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReagendarConsultaCommand(
    UUID consultaId,
    LocalDateTime novaDataHora,
    String motivo
) {}

