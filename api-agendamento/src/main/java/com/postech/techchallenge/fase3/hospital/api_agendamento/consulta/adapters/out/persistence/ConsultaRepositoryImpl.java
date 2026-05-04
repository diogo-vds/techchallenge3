package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Consulta;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence.entity.ConsultaEntity;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence.repository.SpringConsultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConsultaRepositoryImpl implements com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.out.ConsultaRepository {

    private final SpringConsultaRepository repository;

    @Override
    public Consulta salvar(Consulta consulta) {
        return mapToDomain(repository.save(mapToEntity(consulta)));
    }

    @Override
    public Optional<Consulta> buscarPorId(UUID id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public List<Consulta> listar() {
        return repository.findAll().stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public void deletar(UUID id) {
        repository.deleteById(id);
    }

    private ConsultaEntity mapToEntity(Consulta c) {
        return ConsultaEntity.builder()
                .id(c.getId())
                .pacienteId(c.getPacienteId())
                .profissionalId(c.getProfissionalId())
                .dataHora(c.getDataHora())
                .descricao(c.getDescricao())
                .ativa(c.isAtiva())
                .originalId(c.getOriginalId())
                .dataCancelamento(c.getDataCancelamento())
                .motivoCancelamento(c.getMotivoCancelamento())
                .build();
    }

    private Consulta mapToDomain(ConsultaEntity e) {
        return Consulta.reconstituteComRastreamento(
                e.getId(),
                e.getPacienteId(),
                e.getProfissionalId(),
                e.getDataHora(),
                e.getDescricao(),
                e.isAtiva(),
                e.getOriginalId(),
                e.getDataCancelamento(),
                e.getMotivoCancelamento()
        );
    }

    @Override
    public boolean existeConflito(UUID profissionalId, LocalDateTime dataHora) {
        return repository.existsByProfissionalIdAndDataHora(profissionalId, dataHora);
    }

    @Override
    public boolean profissionalAtivo(UUID profissionalId) {
        // TODO: Implementar lógica para verificar se profissional está ativo
        return true;
    }

    @Override
    public boolean pacienteTemConsultaMesmoHorario(UUID pacienteId, LocalDateTime dataHora) {
        return repository.existsByPacienteIdAndDataHoraAndAtivaTrue(pacienteId, dataHora);
    }

    @Override
    public int contarConsultasAtivasPaciente(UUID pacienteId) {
        return repository.countByPacienteIdAndAtivaTrue(pacienteId);
    }
}