package com.postech.techchallenge.fase3.hospital.historico.infrastructure.graphql;

import lombok.Data;

@Data
public class HistoricoInput {

    private String usuarioId;
    private String acao;
    private String detalhes;
    private String entidadeId;
    private String tipoOperacao;
}
