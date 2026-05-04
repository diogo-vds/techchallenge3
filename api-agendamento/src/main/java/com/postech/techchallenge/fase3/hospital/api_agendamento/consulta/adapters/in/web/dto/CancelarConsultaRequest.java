package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto;

public class CancelarConsultaRequest {
    private String motivo;

    public CancelarConsultaRequest() {}

    public CancelarConsultaRequest(String motivo) {
        this.motivo = motivo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}

