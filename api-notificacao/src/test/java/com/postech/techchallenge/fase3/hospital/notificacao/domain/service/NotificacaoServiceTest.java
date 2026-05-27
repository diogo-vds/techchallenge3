package com.postech.techchallenge.fase3.hospital.notificacao.domain.service;

import com.postech.techchallenge.fase3.hospital.notificacao.application.ports.out.EmailServicePort;
import com.postech.techchallenge.fase3.hospital.notificacao.domain.model.ConsultaEvento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock
    private EmailServicePort emailServicePort;

    @InjectMocks
    private NotificacaoService notificacaoService;

    @Test
    void enviarLembrete_shouldFormatEmailAndSend() {
        // Given
        UUID pacienteId = UUID.randomUUID();
        UUID profissionalId = UUID.randomUUID();
        LocalDateTime dataConsulta = LocalDateTime.of(2026, 6, 1, 10, 0);
        String status = "AGENDADA";

        ConsultaEvento evento = new ConsultaEvento(pacienteId, profissionalId, dataConsulta, status);

        String expectedSubject = "Lembrete de Consulta: " + pacienteId;
        String expectedBody = String.format(
                "\n\nOlá %s,\n\nEste é um lembrete da sua consulta com o(a) Dr(a). %s agendada para 01/06/2026 10:00.\n\nStatus: %s\n\n",
                pacienteId,
                profissionalId,
                status
        );

        // When
        notificacaoService.enviarLembrete(evento);

        // Then
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailServicePort).enviarEmail(subjectCaptor.capture(), bodyCaptor.capture());

        assertEquals(expectedSubject, subjectCaptor.getValue());
        assertEquals(expectedBody, bodyCaptor.getValue());
    }
}
