package com.postech.techchallenge.fase3.hospital.historico.domain.port;

import com.postech.techchallenge.fase3.hospital.historico.domain.model.Historico;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HistoricoRepositoryPort {
    Historico save(Historico historico);
    Optional<Historico> findById(UUID id);
    List<Historico> findAll();
    List<Historico> findByPacienteId(UUID pacienteId);
    List<Historico> findByProfissionalId(UUID profissionalId);
    List<Historico> findByPeriodo(LocalDateTime inicio, LocalDateTime fim);
    void deleteById(UUID id);
}
