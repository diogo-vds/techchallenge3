package com.postech.techchallenge.fase3.hospital.notificacao.config;

import org.mockito.Mockito;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestRabbitMQConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        return Mockito.mock(ConnectionFactory.class);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}