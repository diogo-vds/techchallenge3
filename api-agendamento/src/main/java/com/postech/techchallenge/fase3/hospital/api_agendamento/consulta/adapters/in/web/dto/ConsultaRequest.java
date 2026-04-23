package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in.CriarConsultaCommand;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ConsultaRequest {
    private UUID pacienteId;
    private UUID profissionalId;
    private LocalDateTime dataHora;
    private String descricao;

    public CriarConsultaCommand toCommand() {
        return new CriarConsultaCommand(pacienteId, profissionalId, dataHora, descricao);
    }
}