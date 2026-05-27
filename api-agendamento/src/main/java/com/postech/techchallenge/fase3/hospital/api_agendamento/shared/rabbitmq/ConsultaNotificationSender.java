package com.postech.techchallenge.fase3.hospital.api_agendamento.shared.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.techchallenge.fase3.hospital.api_agendamento.shared.dto.ConsultaNotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultaNotificationSender {

    private final AmqpTemplate amqpTemplate;

    public void sendConsultaNotification(ConsultaNotificationMessage message) throws JsonProcessingException {
        amqpTemplate.convertAndSend("consulta_notification_queue", message);
    }
}
