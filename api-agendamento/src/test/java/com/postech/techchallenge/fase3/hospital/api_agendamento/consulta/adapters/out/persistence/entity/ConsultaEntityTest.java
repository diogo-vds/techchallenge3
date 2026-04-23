package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class ConsultaEntityTest {

    private UUID consultaId;
    private UUID pacienteId;
    private UUID profissionalId;
    private LocalDateTime dataHora;
    private String descricao;

    @BeforeEach
    void setUp() {
        consultaId = UUID.randomUUID();
        pacienteId = UUID.randomUUID();
        profissionalId = UUID.randomUUID();
        dataHora = LocalDateTime.now().plusDays(1);
        descricao = "Consulta de rotina";
    }

    @Test
    void testCriarConsultaEntityComBuilder() {
        // Act
        ConsultaEntity entity = ConsultaEntity.builder()
                .id(consultaId)
                .pacienteId(pacienteId)
                .profissionalId(profissionalId)
                .dataHora(dataHora)
                .descricao(descricao)
                .build();

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(consultaId);
        assertThat(entity.getPacienteId()).isEqualTo(pacienteId);
        assertThat(entity.getProfissionalId()).isEqualTo(profissionalId);
        assertThat(entity.getDataHora()).isEqualTo(dataHora);
        assertThat(entity.getDescricao()).isEqualTo(descricao);
    }

    @Test
    void testCriarConsultaEntityComConstructor() {
        // Act
        ConsultaEntity entity = new ConsultaEntity(
                consultaId,
                pacienteId,
                profissionalId,
                dataHora,
                descricao
        );

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(consultaId);
        assertThat(entity.getPacienteId()).isEqualTo(pacienteId);
        assertThat(entity.getProfissionalId()).isEqualTo(profissionalId);
        assertThat(entity.getDataHora()).isEqualTo(dataHora);
        assertThat(entity.getDescricao()).isEqualTo(descricao);
    }

    @Test
    void testCriarConsultaEntityComConstructorVazio() {
        // Act
        ConsultaEntity entity = new ConsultaEntity();

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getPacienteId()).isNull();
        assertThat(entity.getProfissionalId()).isNull();
        assertThat(entity.getDataHora()).isNull();
        assertThat(entity.getDescricao()).isNull();
    }

    @Test
    void testConsultaEntitySettersGetters() {
        // Arrange
        ConsultaEntity entity = new ConsultaEntity();

        // Act
        entity.setId(consultaId);
        entity.setPacienteId(pacienteId);
        entity.setProfissionalId(profissionalId);
        entity.setDataHora(dataHora);
        entity.setDescricao(descricao);

        // Assert
        assertThat(entity.getId()).isEqualTo(consultaId);
        assertThat(entity.getPacienteId()).isEqualTo(pacienteId);
        assertThat(entity.getProfissionalId()).isEqualTo(profissionalId);
        assertThat(entity.getDataHora()).isEqualTo(dataHora);
        assertThat(entity.getDescricao()).isEqualTo(descricao);
    }

    @Test
    void testAtualizarConsultaEntity() {
        // Arrange
        ConsultaEntity entity = ConsultaEntity.builder()
                .id(consultaId)
                .pacienteId(pacienteId)
                .profissionalId(profissionalId)
                .dataHora(dataHora)
                .descricao(descricao)
                .build();

        LocalDateTime novaDataHora = dataHora.plusDays(2);
        String novaDescricao = "Consulta atualizada";

        // Act
        entity.setDataHora(novaDataHora);
        entity.setDescricao(novaDescricao);

        // Assert
        assertThat(entity.getId()).isEqualTo(consultaId);
        assertThat(entity.getPacienteId()).isEqualTo(pacienteId);
        assertThat(entity.getProfissionalId()).isEqualTo(profissionalId);
        assertThat(entity.getDataHora()).isEqualTo(novaDataHora);
        assertThat(entity.getDescricao()).isEqualTo(novaDescricao);
    }

    @Test
    void testConsultaEntityComValoresNulos() {
        // Act
        ConsultaEntity entity = ConsultaEntity.builder()
                .id(consultaId)
                .pacienteId(null)
                .profissionalId(null)
                .dataHora(null)
                .descricao(null)
                .build();

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(consultaId);
        assertThat(entity.getPacienteId()).isNull();
        assertThat(entity.getProfissionalId()).isNull();
        assertThat(entity.getDataHora()).isNull();
        assertThat(entity.getDescricao()).isNull();
    }

    @Test
    void testCopiarConsultaEntity() {
        // Arrange
        ConsultaEntity entity1 = ConsultaEntity.builder()
                .id(consultaId)
                .pacienteId(pacienteId)
                .profissionalId(profissionalId)
                .dataHora(dataHora)
                .descricao(descricao)
                .build();

        // Act
        ConsultaEntity entity2 = ConsultaEntity.builder()
                .id(entity1.getId())
                .pacienteId(entity1.getPacienteId())
                .profissionalId(entity1.getProfissionalId())
                .dataHora(entity1.getDataHora())
                .descricao(entity1.getDescricao())
                .build();

        // Assert
        assertThat(entity2.getId()).isEqualTo(entity1.getId());
        assertThat(entity2.getPacienteId()).isEqualTo(entity1.getPacienteId());
        assertThat(entity2.getProfissionalId()).isEqualTo(entity1.getProfissionalId());
        assertThat(entity2.getDataHora()).isEqualTo(entity1.getDataHora());
        assertThat(entity2.getDescricao()).isEqualTo(entity1.getDescricao());
    }
}

