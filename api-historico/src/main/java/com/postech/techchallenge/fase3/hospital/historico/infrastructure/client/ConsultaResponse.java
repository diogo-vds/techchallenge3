package com.postech.techchallenge.fase3.hospital.historico.infrastructure.client;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ConsultaResponse {
    private UUID id;
    private UUID pacienteId;
    private UUID profissionalId;
    private LocalDateTime dataHora;
    private String descricao;
}
