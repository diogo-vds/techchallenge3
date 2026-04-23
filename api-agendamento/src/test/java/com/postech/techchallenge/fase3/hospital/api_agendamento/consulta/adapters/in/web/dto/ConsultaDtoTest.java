package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Consulta;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in.AtualizarConsultaCommand;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in.CriarConsultaCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class ConsultaDtoTest {

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
    void testConsultaRequestToCommand() {
        // Arrange
        ConsultaRequest request = new ConsultaRequest();
        request.setPacienteId(pacienteId);
        request.setProfissionalId(profissionalId);
        request.setDataHora(dataHora);
        request.setDescricao(descricao);

        // Act
        CriarConsultaCommand command = request.toCommand();

        // Assert
        assertThat(command).isNotNull();
        assertThat(command.pacienteId()).isEqualTo(pacienteId);
        assertThat(command.profissionalId()).isEqualTo(profissionalId);
        assertThat(command.dataHora()).isEqualTo(dataHora);
        assertThat(command.descricao()).isEqualTo(descricao);
    }

    @Test
    void testConsultaUpdateRequestToCommand() {
        // Arrange
        LocalDateTime novaDataHora = dataHora.plusDays(2);
        String novaDescricao = "Consulta atualizada";

        ConsultaUpdateRequest request = new ConsultaUpdateRequest();
        request.setDataHora(novaDataHora);
        request.setDescricao(novaDescricao);

        // Act
        AtualizarConsultaCommand command = request.toCommand();

        // Assert
        assertThat(command).isNotNull();
        assertThat(command.dataHora()).isEqualTo(novaDataHora);
        assertThat(command.descricao()).isEqualTo(novaDescricao);
    }

    @Test
    void testConsultaResponseFromDomain() {
        // Arrange
        UUID consultaId = UUID.randomUUID();
        Consulta consulta = Consulta.reconstitute(
                consultaId,
                pacienteId,
                profissionalId,
                dataHora,
                descricao
        );

        // Act
        ConsultaResponse response = new ConsultaResponse(consulta);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(consultaId);
        assertThat(response.getPacienteId()).isEqualTo(pacienteId);
        assertThat(response.getProfissionalId()).isEqualTo(profissionalId);
        assertThat(response.getDataHora()).isEqualTo(dataHora);
        assertThat(response.getDescricao()).isEqualTo(descricao);
    }

    @Test
    void testConsultaRequestComNulos() {
        // Arrange
        ConsultaRequest request = new ConsultaRequest();
        request.setPacienteId(null);
        request.setProfissionalId(null);
        request.setDataHora(null);
        request.setDescricao(null);

        // Act
        CriarConsultaCommand command = request.toCommand();

        // Assert
        assertThat(command).isNotNull();
        assertThat(command.pacienteId()).isNull();
        assertThat(command.profissionalId()).isNull();
        assertThat(command.dataHora()).isNull();
        assertThat(command.descricao()).isNull();
    }

    @Test
    void testConsultaResponseSettersGetters() {
        // Arrange
        UUID consultaId = UUID.randomUUID();
        Consulta consulta = Consulta.reconstitute(
                consultaId,
                pacienteId,
                profissionalId,
                dataHora,
                descricao
        );

        // Act
        ConsultaResponse response = new ConsultaResponse(consulta);

        // Assert
        assertThat(response.getId()).isEqualTo(consultaId);
        assertThat(response.getPacienteId()).isEqualTo(pacienteId);
        assertThat(response.getProfissionalId()).isEqualTo(profissionalId);
        assertThat(response.getDataHora()).isEqualTo(dataHora);
        assertThat(response.getDescricao()).isEqualTo(descricao);
    }

    @Test
    void testConsultaUpdateRequestSettersGetters() {
        // Arrange
        LocalDateTime novaDataHora = dataHora.plusDays(3);
        String novaDescricao = "Nova descrição";

        // Act
        ConsultaUpdateRequest request = new ConsultaUpdateRequest();
        request.setDataHora(novaDataHora);
        request.setDescricao(novaDescricao);

        // Assert
        assertThat(request.getDataHora()).isEqualTo(novaDataHora);
        assertThat(request.getDescricao()).isEqualTo(novaDescricao);
    }

    @Test
    void testConsultaRequestSettersGetters() {
        // Arrange & Act
        ConsultaRequest request = new ConsultaRequest();
        request.setPacienteId(pacienteId);
        request.setProfissionalId(profissionalId);
        request.setDataHora(dataHora);
        request.setDescricao(descricao);

        // Assert
        assertThat(request.getPacienteId()).isEqualTo(pacienteId);
        assertThat(request.getProfissionalId()).isEqualTo(profissionalId);
        assertThat(request.getDataHora()).isEqualTo(dataHora);
        assertThat(request.getDescricao()).isEqualTo(descricao);
    }
}

