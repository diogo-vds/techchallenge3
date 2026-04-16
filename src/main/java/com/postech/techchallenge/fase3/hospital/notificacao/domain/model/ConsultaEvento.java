package com.postech.techchallenge.fase3.hospital.notificacao.domain.model;

import java.time.LocalDateTime;

public record ConsultaEvento(
    String consultaId,
    String pacienteNome,
    String pacienteEmail,
    String medicoNome,
    LocalDateTime dataHorario,
    String status
) {}
