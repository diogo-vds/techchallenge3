package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Consulta;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Reagendamento;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReagendarConsultaResponse {
    private UUID consultaOriginalId;
    private UUID consultaNovaaId;
    private LocalDateTime dataOriginal;
    private LocalDateTime novaDataHora;
    private Integer totalReagendamentos;
    private String motivo;
    private LocalDateTime reagendadoEm;
    private String mensagem;

    public ReagendarConsultaResponse(Consulta consultaOriginal, Consulta consultaNova, Reagendamento reagendamento) {
        this.consultaOriginalId = consultaOriginal.getId();
        this.consultaNovaaId = consultaNova.getId();
        this.dataOriginal = consultaOriginal.getDataHora();
        this.novaDataHora = consultaNova.getDataHora();
        this.totalReagendamentos = reagendamento.getTotalReagendamentos();
        this.motivo = reagendamento.getMotivo();
        this.reagendadoEm = reagendamento.getCriadoEm();
        this.mensagem = "Consulta reagendada com sucesso";
    }

    // Getters
    public UUID getConsultaOriginalId() {
        return consultaOriginalId;
    }

    public UUID getConsultaNovaaId() {
        return consultaNovaaId;
    }

    public LocalDateTime getDataOriginal() {
        return dataOriginal;
    }

    public LocalDateTime getNovaDataHora() {
        return novaDataHora;
    }

    public Integer getTotalReagendamentos() {
        return totalReagendamentos;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getReagendadoEm() {
        return reagendadoEm;
    }

    public String getMensagem() {
        return mensagem;
    }
}

