package com.postech.techchallenge.fase3.hospital.api_agendamento.shared.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CONSULTA_NOTIFICATION_QUEUE = "consulta_notification_queue";

    @Bean
    public Queue consultaNotificationQueue() {
        return new Queue(CONSULTA_NOTIFICATION_QUEUE, true);
    }
}
