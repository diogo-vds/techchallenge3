package com.postech.techchallenge.fase3.hospital.notificacao.infrastructure.adapters.in;

import com.postech.techchallenge.fase3.hospital.notificacao.application.ports.in.NotificacaoUseCase;
import com.postech.techchallenge.fase3.hospital.notificacao.domain.model.ConsultaEvento;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListenerAdapter {

    private final NotificacaoUseCase notificacaoUseCase;

    public RabbitMQListenerAdapter(NotificacaoUseCase notificacaoUseCase) {
        this.notificacaoUseCase = notificacaoUseCase;
    }

    @RabbitListener(queues = "consulta_events_queue")
    public void receiveMessage(ConsultaEvento evento) {
        notificacaoUseCase.enviarLembrete(evento);
    }
}
