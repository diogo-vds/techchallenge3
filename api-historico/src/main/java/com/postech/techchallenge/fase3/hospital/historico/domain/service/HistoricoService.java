package com.postech.techchallenge.fase3.hospital.historico.domain.service;

import com.postech.techchallenge.fase3.hospital.historico.domain.model.Historico;
import com.postech.techchallenge.fase3.hospital.historico.domain.port.HistoricoRepositoryPort;
import com.postech.techchallenge.fase3.hospital.historico.domain.port.HistoricoServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoricoService implements HistoricoServicePort {

    private final HistoricoRepositoryPort repository;

    @Override
    public Historico salvar(Historico historico) {
        if (historico.getDataHora() == null) {
            historico.setDataHora(LocalDateTime.now());
        }
        if (historico.getId() == null) {
            historico.setId(UUID.randomUUID());
        }
        return repository.save(historico);
    }

    @Override
    public Optional<Historico> buscarPorId(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Historico> buscarTodos() {
        return repository.findAll();
    }

    @Override
    public List<Historico> buscarPorPaciente(UUID pacienteId) {
        return repository.findByPacienteId(pacienteId);
    }

    @Override
    public List<Historico> buscarPorProfissional(UUID profissionalId) {
        return repository.findByProfissionalId(profissionalId);
    }

    @Override
    public List<Historico> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return repository.findByPeriodo(inicio, fim);
    }

    @Override
    public void deletar(UUID id) {
        repository.deleteById(id);
    }
}