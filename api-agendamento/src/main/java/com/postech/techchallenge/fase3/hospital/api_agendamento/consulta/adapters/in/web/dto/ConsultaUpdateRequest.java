package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in.AtualizarConsultaCommand;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConsultaUpdateRequest {
    private LocalDateTime dataHora;
    private String descricao;

    public AtualizarConsultaCommand toCommand() {
        return new AtualizarConsultaCommand(dataHora, descricao);
    }
}
