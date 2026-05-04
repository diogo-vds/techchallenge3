package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Consulta;

import java.time.LocalDateTime;
import java.util.UUID;

public class CancelarConsultaResponse {
    private UUID consultaId;
    private LocalDateTime dataHoraOriginal;
    private LocalDateTime dataCancelamento;
    private String motivo;
    private String mensagem;

    public CancelarConsultaResponse(Consulta consulta) {
        this.consultaId = consulta.getId();
        this.dataHoraOriginal = consulta.getDataHora();
        this.dataCancelamento = consulta.getDataCancelamento();
        this.motivo = consulta.getMotivoCancelamento();
        this.mensagem = "Consulta cancelada com sucesso";
    }

    // Getters
    public UUID getConsultaId() {
        return consultaId;
    }

    public LocalDateTime getDataHoraOriginal() {
        return dataHoraOriginal;
    }

    public LocalDateTime getDataCancelamento() {
        return dataCancelamento;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getMensagem() {
        return mensagem;
    }
}

