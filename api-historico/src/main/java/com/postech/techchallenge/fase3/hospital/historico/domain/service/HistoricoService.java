package com.postech.techchallenge.fase3.hospital.historico.domain.service;

import com.postech.techchallenge.fase3.hospital.historico.domain.model.Historico;
import com.postech.techchallenge.fase3.hospital.historico.domain.port.HistoricoRepositoryPort;
import com.postech.techchallenge.fase3.hospital.historico.domain.port.HistoricoServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HistoricoService implements HistoricoServicePort {

    private final HistoricoRepositoryPort repository;
    @Override
    public Historico salvar(Historico historico) {
        if (historico.getDataHora() == null) {
            historico.setDataHora(LocalDateTime.now());
        }
        return repository.save(historico);
    }

    @Override
    public Optional<Historico> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Historico> buscarTodos() {
        return repository.findAll();
    }

    @Override
    public List<Historico> buscarPorUsuario(String usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<Historico> buscarPorEntidade(String entidadeId) {
        return repository.findByEntidadeId(entidadeId);
    }

    @Override
    public List<Historico> buscarPorAcao(String acao) {
        return repository.findByAcao(acao);
    }

    @Override
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
