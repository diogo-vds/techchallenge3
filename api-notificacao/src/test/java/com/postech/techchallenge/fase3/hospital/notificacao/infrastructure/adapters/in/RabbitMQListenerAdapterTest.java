package com.postech.techchallenge.fase3.hospital.notificacao.infrastructure.adapters.in;

import com.postech.techchallenge.fase3.hospital.notificacao.application.ports.in.NotificacaoUseCase;
import com.postech.techchallenge.fase3.hospital.notificacao.domain.model.ConsultaEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RabbitMQListenerAdapterTest {

    @Mock
    private NotificacaoUseCase notificacaoUseCase;

    @InjectMocks
    private RabbitMQListenerAdapter rabbitMQListenerAdapter;

    private final String TEST_QUEUE_NAME = "test.consulta.notification.queue";

    @BeforeEach
    void setUp() {
        // Inject the @Value("${rabbitmq.queue.name}") property
        ReflectionTestUtils.setField(rabbitMQListenerAdapter, "consultaNotificationQueueName", TEST_QUEUE_NAME);
    }

    @Test
    void receiveConsultaEvento_shouldCallNotificacaoUseCase() {
        // Given
        ConsultaEvento evento = new ConsultaEvento(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), "AGENDADA");

        // When
        rabbitMQListenerAdapter.receiveConsultaNotificationMessage(evento);

        // Then
        verify(notificacaoUseCase).enviarLembrete(evento);
    }

    @Test
    void receiveConsultaNotificationMessage_shouldProcessMessage() {
        // Given
        String message = "Olá João da Silva, você agendou uma consulta...";
        ConsultaEvento evento = new ConsultaEvento(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), "AGENDADA");
        // When
        rabbitMQListenerAdapter.receiveConsultaNotificationMessage(evento);

    }
}
