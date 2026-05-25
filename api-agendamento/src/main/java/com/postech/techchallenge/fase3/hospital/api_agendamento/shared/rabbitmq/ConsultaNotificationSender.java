package com.postech.techchallenge.fase3.hospital.api_agendamento.shared.rabbitmq;

import com.postech.techchallenge.fase3.hospital.api_agendamento.shared.dto.ConsultaNotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultaNotificationSender {

    private final RabbitTemplate rabbitTemplate;

    public void sendConsultaNotification(ConsultaNotificationMessage message) {
        rabbitTemplate.convertAndSend("consulta_notification_queue", message);
    }
}
