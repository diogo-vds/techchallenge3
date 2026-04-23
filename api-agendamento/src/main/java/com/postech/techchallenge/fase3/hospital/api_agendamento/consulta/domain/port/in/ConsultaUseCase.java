package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Consulta;

import java.util.List;
import java.util.UUID;

public interface ConsultaUseCase {

    Consulta criar(CriarConsultaCommand command);
    Consulta atualizar(UUID id, AtualizarConsultaCommand command);
    Consulta buscarPorId(UUID id);
    List<Consulta> listar();
    void deletar(UUID id);
}