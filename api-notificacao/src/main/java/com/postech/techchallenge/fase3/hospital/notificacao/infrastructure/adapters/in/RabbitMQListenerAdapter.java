package com.postech.techchallenge.fase3.hospital.notificacao.infrastructure.adapters.in;

import com.postech.techchallenge.fase3.hospital.notificacao.application.ports.in.NotificacaoUseCase;
import com.postech.techchallenge.fase3.hospital.notificacao.domain.model.ConsultaEvento;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListenerAdapter {

    private final NotificacaoUseCase notificacaoUseCase;

    @Value("${rabbitmq.queue.name}")
    private String consultaNotificationQueueName;

    public RabbitMQListenerAdapter(NotificacaoUseCase notificacaoUseCase) {
        this.notificacaoUseCase = notificacaoUseCase;
    }

    // Existing listener for ConsultaEvento
//    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveConsultaEvento(ConsultaEvento evento) {
        System.out.println("Received ConsultaEvento from consulta_events_queue: " + evento.toString());
        notificacaoUseCase.enviarLembrete(evento);
    }

    // New listener for formatted consultation notification messages
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveConsultaNotificationMessage(String message) {
        System.out.println("Received consultation notification message from " + consultaNotificationQueueName + ": " + message);
        // Here you would typically process the notification message, e.g., send an SMS, email, etc.
        // For now, we'll just log it.
    }
}
