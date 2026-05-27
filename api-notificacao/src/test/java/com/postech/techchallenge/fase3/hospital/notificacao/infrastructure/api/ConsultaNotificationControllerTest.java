package com.postech.techchallenge.fase3.hospital.notificacao.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.postech.techchallenge.fase3.hospital.notificacao.application.dto.ConsultaNotificationRequest;
import com.postech.techchallenge.fase3.hospital.notificacao.application.service.RabbitMQProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultaNotificationController.class)
class ConsultaNotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RabbitMQProducerService rabbitMQProducerService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register module for LocalDateTime
    }

    @Test
    void notifyConsulta_shouldReturnOkAndSendMessage() throws Exception {
        // Given
        ConsultaNotificationRequest request = new ConsultaNotificationRequest();
        request.setNomePaciente("João da Silva");
        request.setNomeMedico("Dra. Maria Oliveira");
        request.setDataConsulta(LocalDateTime.of(2026, 5, 24, 15, 30));

        doNothing().when(rabbitMQProducerService).sendConsultaNotification(any(ConsultaNotificationRequest.class));

        // When & Then
        mockMvc.perform(post("/notificacoes/v1/consulta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Consulta notification sent to queue."));

        verify(rabbitMQProducerService).sendConsultaNotification(any(ConsultaNotificationRequest.class));
    }
}
