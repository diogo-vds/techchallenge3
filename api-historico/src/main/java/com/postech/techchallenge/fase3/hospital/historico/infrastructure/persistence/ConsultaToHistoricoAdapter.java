package com.postech.techchallenge.fase3.hospital.historico.infrastructure.persistence;

import com.postech.techchallenge.fase3.hospital.historico.domain.model.Historico;
import com.postech.techchallenge.fase3.hospital.historico.infrastructure.client.ConsultaClient;
import com.postech.techchallenge.fase3.hospital.historico.infrastructure.client.ConsultaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConsultaToHistoricoAdapter {

    private final ConsultaClient consultaClient;

    public List<Historico> converterConsultasParaHistorico() {
        List<ConsultaResponse> consultas = consultaClient.listarConsultas();

        return consultas.stream()
                .map(this::toHistorico)
                .collect(Collectors.toList());
    }

    private Historico toHistorico(ConsultaResponse consulta) {
        return Historico.builder()
                .id(consulta.getId())
                .pacienteId(consulta.getPacienteId())
                .profissionalId(consulta.getProfissionalId())
                .dataHora(consulta.getDataHora())
                .descricao(consulta.getDescricao())
                .build();
    }
}
