package com.postech.techchallenge.fase3.hospital.notificacao.application.service;

import com.postech.techchallenge.fase3.hospital.notificacao.application.dto.ConsultaNotificationRequest;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class RabbitMQProducerService {

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    private final AmqpTemplate amqpTemplate;

    public RabbitMQProducerService(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void sendConsultaNotification(ConsultaNotificationRequest request) {
        String message = formatConsultaMessage(request);
        amqpTemplate.convertAndSend(queueName, message);
        System.out.println("Sent message to RabbitMQ: " + message);
    }

    private String formatConsultaMessage(ConsultaNotificationRequest request) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String dataConsultaFormatted = request.getDataConsulta().format(dateFormatter);
        String horaConsultaFormatted = request.getDataConsulta().format(timeFormatter);

        return String.format("Olá %s, você agendou uma consulta com o Dr(a). %s para o dia %s as %s.",
                request.getNomePaciente(),
                request.getNomeMedico(),
                dataConsultaFormatted,
                horaConsultaFormatted);
    }
}
