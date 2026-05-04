package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consultas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaEntity {

    @Id
    private UUID id;

    private UUID pacienteId;
    private UUID profissionalId;
    private LocalDateTime dataHora;
    private String descricao;

    @Column(name = "ativa", nullable = false)
    @Builder.Default
    private boolean ativa = true;

    @Column(name = "original_id")
    private UUID originalId;

    @Column(name = "data_cancelamento")
    private LocalDateTime dataCancelamento;

    @Column(name = "motivo_cancelamento", length = 500)
    private String motivoCancelamento;
}