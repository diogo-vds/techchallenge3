package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Reagendamento;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReagendamentoResponse {
    private UUID id;
    private LocalDateTime dataAnterior;
    private LocalDateTime dataNoaa;
    private String motivo;
    private LocalDateTime criadoEm;

    public ReagendamentoResponse(Reagendamento reagendamento) {
        this.id = reagendamento.getId();
        this.dataAnterior = reagendamento.getDataAnterior();
        this.dataNoaa = reagendamento.getDataNoaa();
        this.motivo = reagendamento.getMotivo();
        this.criadoEm = reagendamento.getCriadoEm();
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public LocalDateTime getDataAnterior() {
        return dataAnterior;
    }

    public LocalDateTime getDataNoaa() {
        return dataNoaa;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}

