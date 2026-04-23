package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence.repository;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence.entity.ConsultaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringConsultaRepository extends JpaRepository<ConsultaEntity, UUID> {
}