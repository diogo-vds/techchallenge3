package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Consulta {

    private final UUID id;
    private final UUID pacienteId;
    private final UUID profissionalId;
    private LocalDateTime dataHora;
    private String descricao;

    private Consulta(UUID id, UUID pacienteId, UUID profissionalId, LocalDateTime dataHora, String descricao) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.profissionalId = profissionalId;
        this.dataHora = dataHora;
        this.descricao = descricao;
    }

    public static Consulta nova(UUID pacienteId, UUID profissionalId, LocalDateTime dataHora, String descricao) {
        return new Consulta(UUID.randomUUID(), pacienteId, profissionalId, dataHora, descricao);
    }

    public static Consulta reconstitute(UUID id, UUID pacienteId, UUID profissionalId, LocalDateTime dataHora, String descricao) {
        return new Consulta(id, pacienteId, profissionalId, dataHora, descricao);
    }

    public void atualizar(LocalDateTime dataHora, String descricao) {
        this.dataHora = dataHora;
        this.descricao = descricao;
    }
}