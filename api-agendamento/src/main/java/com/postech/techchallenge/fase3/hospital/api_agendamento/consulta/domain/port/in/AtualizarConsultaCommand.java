package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in;

import java.time.LocalDateTime;

public record AtualizarConsultaCommand(
        LocalDateTime dataHora,
        String descricao
) {}