package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.out;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Reagendamento;

import java.util.List;
import java.util.UUID;

public interface ReagendamentoRepository {

    Reagendamento salvar(Reagendamento reagendamento);

    List<Reagendamento> buscarPorConsultaOriginal(UUID consultaOriginalId);

    int contagemReagendamentosPorConsulta(UUID consultaOriginalId);
}

