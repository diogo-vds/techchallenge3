package com.postech.techchallenge.fase3.hospital.historico.domain.port;

import com.postech.techchallenge.fase3.hospital.historico.domain.model.Historico;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HistoricoRepositoryPort {

    Historico save(Historico historico);
    Optional<Historico> findById(Long id);
    List<Historico> findAll();
    List<Historico> findByUsuarioId(String usuarioId);
    List<Historico> findByEntidadeId(String entidadeId);
    List<Historico> findByAcao(String acao);
    List<Historico> findByPeriodo(LocalDateTime inicio, LocalDateTime fim);
    void deleteById(Long id);
}
