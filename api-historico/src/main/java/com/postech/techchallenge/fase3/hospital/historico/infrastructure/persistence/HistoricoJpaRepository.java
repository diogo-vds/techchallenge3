package com.postech.techchallenge.fase3.hospital.historico.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

import java.util.List;

public interface HistoricoJpaRepository extends JpaRepository<HistoricoEntity, Long> {
        List<HistoricoEntity> findByUsuarioId(String usuarioId);
        List<HistoricoEntity> findByEntidadeId(String entidadeId);
        List<HistoricoEntity> findByAcao(String acao);

        @Query("SELECT h FROM HistoricoEntity h WHERE h.dataHora BETWEEN :inicio AND :fim")
        List<HistoricoEntity> findByPeriodo(@Param("inicio") LocalDateTime inicio,
                @Param("fim") LocalDateTime fim);
}
