package com.postech.techchallenge.fase3.hospital.historico.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String usuarioId;

    @Column(nullable = false)
    private String acao;

    @Column(columnDefinition = "TEXT")
    private String detalhes;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    private String entidadeId;

    private String tipoOperacao;

}
