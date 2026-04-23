package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class ConsultaTest {

    private UUID pacienteId;
    private UUID profissionalId;
    private LocalDateTime dataHora;
    private String descricao;

    @BeforeEach
    void setUp() {
        pacienteId = UUID.randomUUID();
        profissionalId = UUID.randomUUID();
        dataHora = LocalDateTime.now().plusDays(1);
        descricao = "Consulta de rotina";
    }

    @Test
    void testCriarNovaConsulta() {
        // Act
        Consulta consulta = Consulta.nova(pacienteId, profissionalId, dataHora, descricao);

        // Assert
        assertThat(consulta).isNotNull();
        assertThat(consulta.getId()).isNotNull();
        assertThat(consulta.getPacienteId()).isEqualTo(pacienteId);
        assertThat(consulta.getProfissionalId()).isEqualTo(profissionalId);
        assertThat(consulta.getDataHora()).isEqualTo(dataHora);
        assertThat(consulta.getDescricao()).isEqualTo(descricao);
    }

    @Test
    void testCriarNovaConsultaGeraIdUnico() {
        // Act
        Consulta consulta1 = Consulta.nova(pacienteId, profissionalId, dataHora, descricao);
        Consulta consulta2 = Consulta.nova(pacienteId, profissionalId, dataHora, descricao);

        // Assert
        assertThat(consulta1.getId()).isNotEqualTo(consulta2.getId());
    }

    @Test
    void testReconstituirConsulta() {
        // Arrange
        UUID consultaId = UUID.randomUUID();

        // Act
        Consulta consulta = Consulta.reconstitute(consultaId, pacienteId, profissionalId, dataHora, descricao);

        // Assert
        assertThat(consulta).isNotNull();
        assertThat(consulta.getId()).isEqualTo(consultaId);
        assertThat(consulta.getPacienteId()).isEqualTo(pacienteId);
        assertThat(consulta.getProfissionalId()).isEqualTo(profissionalId);
        assertThat(consulta.getDataHora()).isEqualTo(dataHora);
        assertThat(consulta.getDescricao()).isEqualTo(descricao);
    }

    @Test
    void testAtualizarConsulta() {
        // Arrange
        Consulta consulta = Consulta.nova(pacienteId, profissionalId, dataHora, descricao);
        LocalDateTime novaDataHora = dataHora.plusDays(2);
        String novaDescricao = "Consulta de acompanhamento";

        // Act
        consulta.atualizar(novaDataHora, novaDescricao);

        // Assert
        assertThat(consulta.getDataHora()).isEqualTo(novaDataHora);
        assertThat(consulta.getDescricao()).isEqualTo(novaDescricao);
        // IDs devem permanecer os mesmos
        assertThat(consulta.getPacienteId()).isEqualTo(pacienteId);
        assertThat(consulta.getProfissionalId()).isEqualTo(profissionalId);
    }

    @Test
    void testAtualizarConsultaComNuloDataHora() {
        // Arrange
        Consulta consulta = Consulta.nova(pacienteId, profissionalId, dataHora, descricao);

        // Act
        consulta.atualizar(null, "Nova descrição");

        // Assert
        assertThat(consulta.getDataHora()).isNull();
        assertThat(consulta.getDescricao()).isEqualTo("Nova descrição");
    }

    @Test
    void testGettersConsulta() {
        // Arrange
        UUID consultaId = UUID.randomUUID();
        Consulta consulta = Consulta.reconstitute(consultaId, pacienteId, profissionalId, dataHora, descricao);

        // Act & Assert
        assertThat(consulta.getId()).isEqualTo(consultaId);
        assertThat(consulta.getPacienteId()).isEqualTo(pacienteId);
        assertThat(consulta.getProfissionalId()).isEqualTo(profissionalId);
        assertThat(consulta.getDataHora()).isEqualTo(dataHora);
        assertThat(consulta.getDescricao()).isEqualTo(descricao);
    }

    @Test
    void testConsultaComDescricaoNull() {
        // Act
        Consulta consulta = Consulta.nova(pacienteId, profissionalId, dataHora, null);

        // Assert
        assertThat(consulta).isNotNull();
        assertThat(consulta.getId()).isNotNull();
        assertThat(consulta.getDescricao()).isNull();
    }

    @Test
    void testConsultaImutavelPacienteId() {
        // Arrange
        Consulta consulta = Consulta.nova(pacienteId, profissionalId, dataHora, descricao);
        UUID idOriginal = consulta.getPacienteId();

        // Act & Assert
        assertThat(consulta.getPacienteId()).isEqualTo(idOriginal);
        // O pacienteId não deve ser alterável
        assertThat(consulta.getPacienteId()).isEqualTo(pacienteId);
    }

    @Test
    void testConsultaImutavelProfissionalId() {
        // Arrange
        Consulta consulta = Consulta.nova(pacienteId, profissionalId, dataHora, descricao);
        UUID idOriginal = consulta.getProfissionalId();

        // Act & Assert
        assertThat(consulta.getProfissionalId()).isEqualTo(idOriginal);
        // O profissionalId não deve ser alterável
        assertThat(consulta.getProfissionalId()).isEqualTo(profissionalId);
    }
}

