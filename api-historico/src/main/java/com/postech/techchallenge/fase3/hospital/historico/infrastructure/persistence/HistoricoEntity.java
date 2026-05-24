package com.postech.techchallenge.fase3.hospital.historico.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historico")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID pacienteId;

    @Column(nullable = false)
    private UUID profissionalId;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(columnDefinition = "TEXT")
    private String descricao;
}