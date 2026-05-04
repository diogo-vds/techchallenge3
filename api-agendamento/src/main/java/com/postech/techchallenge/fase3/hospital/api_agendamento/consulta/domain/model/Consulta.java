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
    
    // Campos para rastreamento de reagendamento
    private boolean ativa;
    private UUID originalId;
    private LocalDateTime dataCancelamento;
    private String motivoCancelamento;

    private Consulta(UUID id, UUID pacienteId, UUID profissionalId, LocalDateTime dataHora, String descricao) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.profissionalId = profissionalId;
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.ativa = true;
        this.originalId = null;
        this.dataCancelamento = null;
        this.motivoCancelamento = null;
    }

    private Consulta(UUID id, UUID pacienteId, UUID profissionalId, LocalDateTime dataHora, String descricao,
                     boolean ativa, UUID originalId, LocalDateTime dataCancelamento, String motivoCancelamento) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.profissionalId = profissionalId;
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.ativa = ativa;
        this.originalId = originalId;
        this.dataCancelamento = dataCancelamento;
        this.motivoCancelamento = motivoCancelamento;
    }

    public static Consulta nova(UUID pacienteId, UUID profissionalId, LocalDateTime dataHora, String descricao) {
        return new Consulta(UUID.randomUUID(), pacienteId, profissionalId, dataHora, descricao);
    }

    public static Consulta reconstitute(UUID id, UUID pacienteId, UUID profissionalId, LocalDateTime dataHora, String descricao) {
        return new Consulta(id, pacienteId, profissionalId, dataHora, descricao);
    }

    public static Consulta reconstituteComRastreamento(UUID id, UUID pacienteId, UUID profissionalId, 
                                                        LocalDateTime dataHora, String descricao,
                                                        boolean ativa, UUID originalId, 
                                                        LocalDateTime dataCancelamento, String motivoCancelamento) {
        return new Consulta(id, pacienteId, profissionalId, dataHora, descricao, 
                          ativa, originalId, dataCancelamento, motivoCancelamento);
    }

    public void atualizar(LocalDateTime dataHora, String descricao) {
        this.dataHora = dataHora;
        this.descricao = descricao;
    }

    public void cancelar(String motivo) {
        this.ativa = false;
        this.dataCancelamento = LocalDateTime.now();
        this.motivoCancelamento = motivo;
    }

    public void marcarComoReagendamento(UUID originalId) {
        this.originalId = originalId;
    }
}