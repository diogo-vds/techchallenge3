package com.postech.techchallenge.fase3.hospital.notificacao.application.ports.in;

import com.postech.techchallenge.fase3.hospital.notificacao.domain.model.ConsultaEvento;

public interface NotificacaoUseCase {
    void enviarLembrete(ConsultaEvento evento);
}
