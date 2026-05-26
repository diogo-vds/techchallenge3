package com.postech.techchallenge.fase3.hospital.notificacao.infrastructure.adapters.in;

import com.postech.techchallenge.fase3.hospital.notificacao.application.ports.in.NotificacaoUseCase;
import com.postech.techchallenge.fase3.hospital.notificacao.domain.model.ConsultaEvento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQListenerAdapter.class);
    private final NotificacaoUseCase notificacaoUseCase;

    @Value("${rabbitmq.queue.name}")
    private String consultaNotificationQueueName;

    public RabbitMQListenerAdapter(NotificacaoUseCase notificacaoUseCase) {
        this.notificacaoUseCase = notificacaoUseCase;
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveConsultaNotificationMessage(ConsultaEvento message) {
        log.info("Mensagem de notificação recebida da fila {} e status: {} ", consultaNotificationQueueName, message.status());
        notificacaoUseCase.enviarLembrete(message);
    }
}
