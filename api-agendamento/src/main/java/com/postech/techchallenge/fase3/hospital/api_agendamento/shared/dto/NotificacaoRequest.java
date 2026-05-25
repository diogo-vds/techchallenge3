package com.postech.techchallenge.fase3.hospital.api_agendamento.shared.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificacaoRequest {
    private UUID pacienteId;
    private UUID profissionalId;
    private LocalDateTime dataConsulta;

    public NotificacaoRequest() {
    }

    public NotificacaoRequest(UUID pacienteId, UUID profissionalId, LocalDateTime dataConsulta) {
        this.pacienteId = pacienteId;
        this.profissionalId = profissionalId;
        this.dataConsulta = dataConsulta;
    }

    public UUID getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(UUID pacienteId) {
        this.pacienteId = pacienteId;
    }

    public UUID getProfissionalId() {
        return profissionalId;
    }

    public void setProfissionalId(UUID profissionalId) {
        this.profissionalId = profissionalId;
    }

    public LocalDateTime getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(LocalDateTime dataConsulta) {
        this.dataConsulta = dataConsulta;
    }
}
