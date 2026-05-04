package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reagendamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReagendamentoEntity {

    @Id
    private UUID id;

    @Column(name = "consulta_original_id", nullable = false)
    private UUID consultaOriginalId;

    @Column(name = "consulta_nova_id", nullable = false)
    private UUID consultaNovaaId;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "data_anterior", nullable = false)
    private LocalDateTime dataAnterior;

    @Column(name = "data_nova", nullable = false)
    private LocalDateTime dataNoaa;

    @Column(name = "total_reagendamentos", nullable = false)
    private Integer totalReagendamentos;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
}

