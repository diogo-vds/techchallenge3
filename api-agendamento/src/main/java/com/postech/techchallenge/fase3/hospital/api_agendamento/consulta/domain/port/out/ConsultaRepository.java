package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.out;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Consulta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultaRepository {

    Consulta salvar(Consulta consulta);
    Optional<Consulta> buscarPorId(UUID id);
    List<Consulta> listar();
    void deletar(UUID id);
}