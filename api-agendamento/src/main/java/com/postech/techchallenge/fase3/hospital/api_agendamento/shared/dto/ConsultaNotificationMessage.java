package com.postech.techchallenge.fase3.hospital.api_agendamento.shared.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaNotificationMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID pacienteId;
    private UUID profissionalId;
    private LocalDateTime dataConsulta;
    private String status;
}
