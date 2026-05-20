package com.postech.techchallenge.fase3.hospital.historico.domain.port;

import com.postech.techchallenge.fase3.hospital.historico.domain.model.Historico;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HistoricoServicePort {
    Historico salvar(Historico historico);
    Optional<Historico> buscarPorId(UUID id);
    List<Historico> buscarTodos();
    List<Historico> buscarPorPaciente(UUID pacienteId);
    List<Historico> buscarPorProfissional(UUID profissionalId);
    List<Historico> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);
    void deletar(UUID id);
}