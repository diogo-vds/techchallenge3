package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence.repository;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence.entity.ReagendamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringReagendamentoRepository extends JpaRepository<ReagendamentoEntity, UUID> {

    List<ReagendamentoEntity> findByConsultaOriginalId(UUID consultaOriginalId);

    @Query("SELECT COUNT(r) FROM ReagendamentoEntity r WHERE r.consultaOriginalId = :consultaOriginalId")
    int countByConsultaOriginalId(@Param("consultaOriginalId") UUID consultaOriginalId);
}

