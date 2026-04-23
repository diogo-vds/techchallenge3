package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence.entity.ConsultaEntity;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.out.persistence.repository.SpringConsultaRepository;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Consulta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaRepositoryImplTest {

    @Mock
    private SpringConsultaRepository springRepository;

    @InjectMocks
    private ConsultaRepositoryImpl consultaRepository;

    private UUID consultaId;
    private UUID pacienteId;
    private UUID profissionalId;
    private LocalDateTime dataHora;
    private String descricao;
    private Consulta consulta;
    private ConsultaEntity entity;

    @BeforeEach
    void setUp() {
        consultaId = UUID.randomUUID();
        pacienteId = UUID.randomUUID();
        profissionalId = UUID.randomUUID();
        dataHora = LocalDateTime.now().plusDays(1);
        descricao = "Consulta de rotina";

        consulta = Consulta.reconstitute(
                consultaId,
                pacienteId,
                profissionalId,
                dataHora,
                descricao
        );

        entity = ConsultaEntity.builder()
                .id(consultaId)
                .pacienteId(pacienteId)
                .profissionalId(profissionalId)
                .dataHora(dataHora)
                .descricao(descricao)
                .build();
    }

    @Test
    void testSalvarConsulta() {
        // Arrange
        when(springRepository.save(any(ConsultaEntity.class)))
                .thenReturn(entity);

        // Act
        Consulta resultado = consultaRepository.salvar(consulta);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(consultaId);
        assertThat(resultado.getPacienteId()).isEqualTo(pacienteId);
        assertThat(resultado.getProfissionalId()).isEqualTo(profissionalId);
        assertThat(resultado.getDataHora()).isEqualTo(dataHora);
        assertThat(resultado.getDescricao()).isEqualTo(descricao);

        verify(springRepository, times(1)).save(any(ConsultaEntity.class));
    }

    @Test
    void testBuscarConsultaPorIdComSucesso() {
        // Arrange
        when(springRepository.findById(consultaId))
                .thenReturn(Optional.of(entity));

        // Act
        Optional<Consulta> resultado = consultaRepository.buscarPorId(consultaId);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(consultaId);
        assertThat(resultado.get().getPacienteId()).isEqualTo(pacienteId);

        verify(springRepository, times(1)).findById(consultaId);
    }

    @Test
    void testBuscarConsultaPorIdNaoEncontrada() {
        // Arrange
        when(springRepository.findById(any()))
                .thenReturn(Optional.empty());

        // Act
        Optional<Consulta> resultado = consultaRepository.buscarPorId(consultaId);

        // Assert
        assertThat(resultado).isEmpty();

        verify(springRepository, times(1)).findById(consultaId);
    }

    @Test
    void testListarConsultas() {
        // Arrange
        UUID consultaId2 = UUID.randomUUID();
        ConsultaEntity entity2 = ConsultaEntity.builder()
                .id(consultaId2)
                .pacienteId(pacienteId)
                .profissionalId(profissionalId)
                .dataHora(dataHora.plusDays(1))
                .descricao("Consulta 2")
                .build();

        when(springRepository.findAll())
                .thenReturn(List.of(entity, entity2));

        // Act
        List<Consulta> resultado = consultaRepository.listar();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getId()).isEqualTo(consultaId);
        assertThat(resultado.get(1).getId()).isEqualTo(consultaId2);

        verify(springRepository, times(1)).findAll();
    }

    @Test
    void testListarConsultasVazia() {
        // Arrange
        when(springRepository.findAll())
                .thenReturn(List.of());

        // Act
        List<Consulta> resultado = consultaRepository.listar();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();

        verify(springRepository, times(1)).findAll();
    }

    @Test
    void testDeletarConsulta() {
        // Arrange
        doNothing().when(springRepository).deleteById(consultaId);

        // Act
        consultaRepository.deletar(consultaId);

        // Assert
        verify(springRepository, times(1)).deleteById(consultaId);
    }

    @Test
    void testMapToDomain() {
        // Arrange
        ConsultaEntity entity = ConsultaEntity.builder()
                .id(consultaId)
                .pacienteId(pacienteId)
                .profissionalId(profissionalId)
                .dataHora(dataHora)
                .descricao(descricao)
                .build();

        when(springRepository.save(any(ConsultaEntity.class)))
                .thenReturn(entity);

        // Act
        Consulta resultado = consultaRepository.salvar(consulta);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(entity.getId());
        assertThat(resultado.getPacienteId()).isEqualTo(entity.getPacienteId());
        assertThat(resultado.getProfissionalId()).isEqualTo(entity.getProfissionalId());
        assertThat(resultado.getDataHora()).isEqualTo(entity.getDataHora());
        assertThat(resultado.getDescricao()).isEqualTo(entity.getDescricao());
    }
}

