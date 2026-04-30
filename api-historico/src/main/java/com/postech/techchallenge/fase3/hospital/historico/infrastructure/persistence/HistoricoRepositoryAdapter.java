package com.postech.techchallenge.fase3.hospital.historico.infrastructure.persistence;

import com.postech.techchallenge.fase3.hospital.historico.domain.model.Historico;
import com.postech.techchallenge.fase3.hospital.historico.domain.port.HistoricoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HistoricoRepositoryAdapter  implements HistoricoRepositoryPort {

    private final HistoricoJpaRepository jpaRepository;

    @Override
    public Historico save(Historico historico) {
        HistoricoEntity entity = toEntity(historico);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Historico> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Historico> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Historico> findByUsuarioId(String usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Historico> findByEntidadeId(String entidadeId) {
        return jpaRepository.findByEntidadeId(entidadeId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Historico> findByAcao(String acao) {
        return jpaRepository.findByAcao(acao).stream()
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
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private HistoricoEntity toEntity(Historico historico) {
        return HistoricoEntity.builder()
                .id(historico.getId())
                .usuarioId(historico.getUsuarioId())
                .acao(historico.getAcao())
                .detalhes(historico.getDetalhes())
                .dataHora(historico.getDataHora())
                .entidadeId(historico.getEntidadeId())
                .tipoOperacao(historico.getTipoOperacao())
                .build();
    }

    private Historico toDomain(HistoricoEntity entity) {
        return Historico.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuarioId())
                .acao(entity.getAcao())
                .detalhes(entity.getDetalhes())
                .dataHora(entity.getDataHora())
                .entidadeId(entity.getEntidadeId())
                .tipoOperacao(entity.getTipoOperacao())
                .build();
    }
}
