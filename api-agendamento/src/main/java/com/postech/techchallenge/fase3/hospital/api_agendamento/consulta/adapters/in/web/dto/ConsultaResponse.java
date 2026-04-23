package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Consulta;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ConsultaResponse {

    private final UUID id;
    private final UUID pacienteId;
    private final UUID profissionalId;
    private final LocalDateTime dataHora;
    private final String descricao;

    public ConsultaResponse(Consulta c) {
        this.id = c.getId();
        this.pacienteId = c.getPacienteId();
        this.profissionalId = c.getProfissionalId();
        this.dataHora = c.getDataHora();
        this.descricao = c.getDescricao();
    }
}