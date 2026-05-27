package com.postech.techchallenge.fase3.hospital.notificacao.application.service;

import com.postech.techchallenge.fase3.hospital.notificacao.application.dto.ConsultaNotificationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RabbitMQProducerServiceTest {

    @Mock
    private AmqpTemplate amqpTemplate;

    @InjectMocks
    private RabbitMQProducerService rabbitMQProducerService;

    private final String TEST_QUEUE_NAME = "test.consulta.notification.queue";

    @BeforeEach
    void setUp() {
        // Inject the @Value("${rabbitmq.queue.name}") property
        ReflectionTestUtils.setField(rabbitMQProducerService, "queueName", TEST_QUEUE_NAME);
    }

    @Test
    void sendConsultaNotification_shouldFormatMessageAndSendToQueue() {
        // Given
        ConsultaNotificationRequest request = new ConsultaNotificationRequest();
        request.setNomePaciente("João da Silva");
        request.setNomeMedico("Dra. Maria Oliveira");
        request.setDataConsulta(LocalDateTime.of(2026, 5, 24, 15, 30));

        String expectedMessage = "Olá João da Silva, você agendou uma consulta com o Dr(a). Dra. Maria Oliveira para o dia 24/05/2026 as 15:30.";

        // When
        rabbitMQProducerService.sendConsultaNotification(request);

        // Then
        ArgumentCaptor<String> queueNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(amqpTemplate).convertAndSend(queueNameCaptor.capture(), messageCaptor.capture());

        assertEquals(TEST_QUEUE_NAME, queueNameCaptor.getValue());
        assertEquals(expectedMessage, messageCaptor.getValue());
    }

    @Test
    void formatConsultaMessage_shouldReturnCorrectlyFormattedString() {
        // Given
        ConsultaNotificationRequest request = new ConsultaNotificationRequest();
        request.setNomePaciente("Ana Souza");
        request.setNomeMedico("Dr. Carlos Lima");
        request.setDataConsulta(LocalDateTime.of(2026, 10, 10, 9, 0));

        String expectedMessage = "Olá Ana Souza, você agendou uma consulta com o Dr(a). Dr. Carlos Lima para o dia 10/10/2026 as 09:00.";

        // Using ReflectionTestUtils to call the private method for direct testing
        String actualMessage = (String) ReflectionTestUtils.invokeMethod(rabbitMQProducerService, "formatConsultaMessage", request);

        // Then
        assertEquals(expectedMessage, actualMessage);
    }
}
