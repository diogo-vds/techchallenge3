package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.out;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Consulta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultaRepository {

    Consulta salvar(Consulta consulta);
    Optional<Consulta> buscarPorId(UUID id);
    List<Consulta> listar();
    void deletar(UUID id);
    boolean existeConflito(UUID profissionalId, LocalDateTime dataHora);
    boolean profissionalAtivo(UUID profissionalId);
    boolean pacienteTemConsultaMesmoHorario(UUID pacienteId, LocalDateTime dataHora);
    int contarConsultasAtivasPaciente(UUID pacienteId);
}