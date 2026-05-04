package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto;

import java.time.LocalDateTime;

public class ReagendarConsultaRequest {
    private LocalDateTime novaDataHora;
    private String motivo;

    public ReagendarConsultaRequest() {}

    public ReagendarConsultaRequest(LocalDateTime novaDataHora, String motivo) {
        this.novaDataHora = novaDataHora;
        this.motivo = motivo;
    }

    public LocalDateTime getNovaDataHora() {
        return novaDataHora;
    }

    public void setNovaDataHora(LocalDateTime novaDataHora) {
        this.novaDataHora = novaDataHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}

