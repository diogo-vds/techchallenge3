package com.postech.techchallenge.fase3.hospital.historico.infrastructure.persistence;

import com.postech.techchallenge.fase3.hospital.historico.domain.model.Historico;
import com.postech.techchallenge.fase3.hospital.historico.domain.port.HistoricoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HistoricoRepositoryAdapter implements HistoricoRepositoryPort {

    private final HistoricoJpaRepository jpaRepository;

    @Override
    public Historico save(Historico historico) {
        HistoricoEntity entity = toEntity(historico);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Historico> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Historico> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Historico> findByPacienteId(UUID pacienteId) {
        return jpaRepository.findByPacienteId(pacienteId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Historico> findByProfissionalId(UUID profissionalId) {
        return jpaRepository.findByProfissionalId(profissionalId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Historico> findByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.findByPeriodo(inicio, fim).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private HistoricoEntity toEntity(Historico historico) {
        return HistoricoEntity.builder()
                .id(historico.getId())
                .pacienteId(historico.getPacienteId())
                .profissionalId(historico.getProfissionalId())
                .dataHora(historico.getDataHora())
                .descricao(historico.getDescricao())
                .build();
    }

    private Historico toDomain(HistoricoEntity entity) {
        return Historico.builder()
                .id(entity.getId())
                .pacienteId(entity.getPacienteId())
                .profissionalId(entity.getProfissionalId())
                .dataHora(entity.getDataHora())
                .descricao(entity.getDescricao())
                .build();
    }
}