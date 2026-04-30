package com.postech.techchallenge.fase3.hospital.historico.domain.port;

import com.postech.techchallenge.fase3.hospital.historico.domain.model.Historico;

import java.util.List;
import java.util.Optional;

public interface HistoricoServicePort {

    Historico salvar(Historico historico);
    Optional<Historico> buscarPorId(Long id);
    List<Historico> buscarTodos();
    List<Historico> buscarPorUsuario(String usuarioId);
    List<Historico> buscarPorEntidade(String entidadeId);
    List<Historico> buscarPorAcao(String acao);
    void deletar(Long id);
}
