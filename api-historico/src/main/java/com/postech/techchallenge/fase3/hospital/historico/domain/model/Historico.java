package com.postech.techchallenge.fase3.hospital.historico.domain.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class Historico {
    private Long id;
    private String usuarioId;
    private String acao;
    private String detalhes;
    private LocalDateTime dataHora;
    private String entidadeId;
    private String tipoOperacao;
}
