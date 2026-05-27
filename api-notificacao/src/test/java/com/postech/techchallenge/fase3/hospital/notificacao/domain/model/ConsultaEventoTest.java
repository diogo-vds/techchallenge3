package com.postech.techchallenge.fase3.hospital.notificacao.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConsultaEventoTest {

    @Test
    void consultaEvento_shouldBeCreatedAndAccessedCorrectly() {
        // Given
        UUID pacienteId = UUID.randomUUID();
        UUID profissionalId = UUID.randomUUID();
        LocalDateTime dataConsulta = LocalDateTime.of(2026, 6, 1, 10, 0);
        String status = "AGENDADA";

        // When
        ConsultaEvento evento = new ConsultaEvento(pacienteId, profissionalId, dataConsulta, status);

        // Then
        assertNotNull(evento);
        assertEquals(pacienteId, evento.pacienteId());
        assertEquals(profissionalId, evento.profissionalId());
        assertEquals(dataConsulta, evento.dataConsulta());
        assertEquals(status, evento.status());
    }

    @Test
    void consultaEvento_equalsAndHashCode_shouldWorkCorrectly() {
        // Given
        UUID pacienteId = UUID.randomUUID();
        UUID profissionalId = UUID.randomUUID();
        LocalDateTime dataConsulta = LocalDateTime.of(2026, 6, 1, 10, 0);
        String status = "AGENDADA";

        ConsultaEvento evento1 = new ConsultaEvento(pacienteId, profissionalId, dataConsulta, status);
        ConsultaEvento evento2 = new ConsultaEvento(pacienteId, profissionalId, dataConsulta, status);
        ConsultaEvento evento3 = new ConsultaEvento(UUID.randomUUID(), profissionalId, dataConsulta, status); // Different pacienteId

        // Then
        assertEquals(evento1, evento2);
        assertNotEquals(evento1, evento3);
        assertEquals(evento1.hashCode(), evento2.hashCode());
        assertNotEquals(evento1.hashCode(), evento3.hashCode());
    }

    @Test
    void consultaEvento_toString_shouldContainAllFields() {
        // Given
        UUID pacienteId = UUID.randomUUID();
        UUID profissionalId = UUID.randomUUID();
        LocalDateTime dataConsulta = LocalDateTime.of(2026, 6, 1, 10, 0);
        String status = "AGENDADA";

        ConsultaEvento evento = new ConsultaEvento(pacienteId, profissionalId, dataConsulta, status);

        // When
        String toStringResult = evento.toString();

        // Then
        assertTrue(toStringResult.contains(pacienteId.toString()));
        assertTrue(toStringResult.contains(profissionalId.toString()));
        assertTrue(toStringResult.contains(dataConsulta.toString()));
        assertTrue(toStringResult.contains(status));
        assertTrue(toStringResult.contains("ConsultaEvento"));
    }
}
