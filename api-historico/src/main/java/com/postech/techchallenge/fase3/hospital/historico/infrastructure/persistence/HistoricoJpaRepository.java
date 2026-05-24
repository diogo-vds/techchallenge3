package com.postech.techchallenge.fase3.hospital.historico.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface HistoricoJpaRepository extends JpaRepository<HistoricoEntity, UUID> {
        List<HistoricoEntity> findByPacienteId(UUID pacienteId);
        List<HistoricoEntity> findByProfissionalId(UUID profissionalId);

        @Query("SELECT h FROM HistoricoEntity h WHERE h.dataHora BETWEEN :inicio AND :fim")
        List<HistoricoEntity> findByPeriodo(@Param("inicio") LocalDateTime inicio,
                                            @Param("fim") LocalDateTime fim);
}