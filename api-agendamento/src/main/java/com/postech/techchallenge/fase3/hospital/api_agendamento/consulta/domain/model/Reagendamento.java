package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Reagendamento {

    private final UUID id;
    private final UUID consultaOriginalId;
    private final UUID consultaNovaaId;
    private final String motivo;
    private final LocalDateTime dataAnterior;
    private final LocalDateTime dataNoaa;
    private final Integer totalReagendamentos;
    private final LocalDateTime criadoEm;

    private Reagendamento(UUID id, UUID consultaOriginalId, UUID consultaNovaaId, String motivo,
                         LocalDateTime dataAnterior, LocalDateTime dataNoaa,
                         Integer totalReagendamentos, LocalDateTime criadoEm) {
        this.id = id;
        this.consultaOriginalId = consultaOriginalId;
        this.consultaNovaaId = consultaNovaaId;
        this.motivo = motivo;
        this.dataAnterior = dataAnterior;
        this.dataNoaa = dataNoaa;
        this.totalReagendamentos = totalReagendamentos;
        this.criadoEm = criadoEm;
    }

    public static Reagendamento criar(UUID consultaOriginalId, UUID consultaNovaaId, String motivo,
                                     LocalDateTime dataAnterior, LocalDateTime dataNoaa,
                                     Integer totalReagendamentos) {
        return new Reagendamento(
            UUID.randomUUID(),
            consultaOriginalId,
            consultaNovaaId,
            motivo,
            dataAnterior,
            dataNoaa,
            totalReagendamentos,
            LocalDateTime.now()
        );
    }

    public static Reagendamento reconstitute(UUID id, UUID consultaOriginalId, UUID consultaNovaaId, String motivo,
                                            LocalDateTime dataAnterior, LocalDateTime dataNoaa,
                                            Integer totalReagendamentos, LocalDateTime criadoEm) {
        return new Reagendamento(id, consultaOriginalId, consultaNovaaId, motivo,
                               dataAnterior, dataNoaa, totalReagendamentos, criadoEm);
    }
}

