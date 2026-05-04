package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence.entity.ReagendamentoEntity;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence.repository.SpringReagendamentoRepository;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Reagendamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReagendamentoRepositoryImpl implements com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.out.ReagendamentoRepository {

    private final SpringReagendamentoRepository repository;

    @Override
    public Reagendamento salvar(Reagendamento reagendamento) {
        ReagendamentoEntity entity = mapToEntity(reagendamento);
        ReagendamentoEntity salvo = repository.save(entity);
        return mapToDomain(salvo);
    }

    @Override
    public List<Reagendamento> buscarPorConsultaOriginal(UUID consultaOriginalId) {
        return repository.findByConsultaOriginalId(consultaOriginalId).stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public int contagemReagendamentosPorConsulta(UUID consultaOriginalId) {
        return repository.countByConsultaOriginalId(consultaOriginalId);
    }

    private ReagendamentoEntity mapToEntity(Reagendamento r) {
        return ReagendamentoEntity.builder()
                .id(r.getId())
                .consultaOriginalId(r.getConsultaOriginalId())
                .consultaNovaaId(r.getConsultaNovaaId())
                .motivo(r.getMotivo())
                .dataAnterior(r.getDataAnterior())
                .dataNoaa(r.getDataNoaa())
                .totalReagendamentos(r.getTotalReagendamentos())
                .criadoEm(r.getCriadoEm())
                .build();
    }

    private Reagendamento mapToDomain(ReagendamentoEntity e) {
        return Reagendamento.reconstitute(
                e.getId(),
                e.getConsultaOriginalId(),
                e.getConsultaNovaaId(),
                e.getMotivo(),
                e.getDataAnterior(),
                e.getDataNoaa(),
                e.getTotalReagendamentos(),
                e.getCriadoEm()
        );
    }
}

