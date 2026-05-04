package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence.repository;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence.entity.ConsultaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SpringConsultaRepository extends JpaRepository<ConsultaEntity, UUID> {
    boolean existsByProfissionalIdAndDataHora(UUID profissionalId, LocalDateTime dataHora);
    boolean existsByPacienteIdAndDataHoraAndAtivaTrue(UUID pacienteId, LocalDateTime dataHora);
    int countByPacienteIdAndAtivaTrue(UUID pacienteId);
}