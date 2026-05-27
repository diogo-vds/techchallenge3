package com.postech.techchallenge.fase3.hospital.historico.domain.service;

import com.postech.techchallenge.fase3.hospital.historico.domain.model.Historico;
import com.postech.techchallenge.fase3.hospital.historico.domain.port.HistoricoRepositoryPort;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoricoServiceTest {

    @Mock
    private HistoricoRepositoryPort repository;

    @InjectMocks
    private HistoricoService historicoService;

    private Historico historico;
    private UUID id;
    private UUID pacienteId;
    private UUID profissionalId;
    private LocalDateTime dataHora;
    private String descricao;
    private LocalDateTime inicio;
    private LocalDateTime fim;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        pacienteId = UUID.randomUUID();
        profissionalId = UUID.randomUUID();
        dataHora = LocalDateTime.now();
        descricao = "Consulta de rotina";
        inicio = LocalDateTime.now().minusDays(7);
        fim = LocalDateTime.now();

        historico = Historico.builder()
                .id(id)
                .pacienteId(pacienteId)
                .profissionalId(profissionalId)
                .dataHora(dataHora)
                .descricao(descricao)
                .build();
    }

    @Test
    void salvar_ComSucesso_QuandoHistoricoValido() {
        // Arrange
        when(repository.save(any(Historico.class))).thenReturn(historico);

        // Act
        Historico resultado = historicoService.salvar(historico);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(id);
        verify(repository, times(1)).save(historico);
    }

    @Test
    void salvar_DeveGerarId_QuandoIdNaoInformado() {
        // Arrange
        Historico novoHistorico = Historico.builder()
                .pacienteId(pacienteId)
                .profissionalId(profissionalId)
                .descricao("Nova consulta")
                .build();

        when(repository.save(any(Historico.class))).thenAnswer(invocation -> {
            Historico h = invocation.getArgument(0);
            return Historico.builder()
                    .id(UUID.randomUUID())
                    .pacienteId(h.getPacienteId())
                    .profissionalId(h.getProfissionalId())
                    .dataHora(h.getDataHora())
                    .descricao(h.getDescricao())
                    .build();
        });

        // Act
        Historico resultado = historicoService.salvar(novoHistorico);

        // Assert
        assertThat(resultado.getId()).isNotNull();
        verify(repository, times(1)).save(novoHistorico);
    }

    @Test
    void salvar_DeveDefinirDataHora_QuandoDataHoraNaoInformada() {
        // Arrange
        Historico novoHistorico = Historico.builder()
                .id(id)
                .pacienteId(pacienteId)
                .profissionalId(profissionalId)
                .descricao("Nova consulta")
                .build();

        when(repository.save(any(Historico.class))).thenAnswer(invocation -> {
            Historico h = invocation.getArgument(0);
            return Historico.builder()
                    .id(h.getId())
                    .pacienteId(h.getPacienteId())
                    .profissionalId(h.getProfissionalId())
                    .dataHora(h.getDataHora())
                    .descricao(h.getDescricao())
                    .build();
        });

        // Act
        Historico resultado = historicoService.salvar(novoHistorico);

        // Assert
        assertThat(resultado.getDataHora()).isNotNull();
        verify(repository, times(1)).save(novoHistorico);
    }

    @Test
    void salvar_NaoDeveSobrescreverDataHora_QuandoDataHoraJaInformada() {
        // Arrange
        LocalDateTime dataExistente = LocalDateTime.of(2024, 1, 1, 10, 0);
        Historico historicoComData = Historico.builder()
                .id(id)
                .pacienteId(pacienteId)
                .profissionalId(profissionalId)
                .dataHora(dataExistente)
                .descricao(descricao)
                .build();

        when(repository.save(any(Historico.class))).thenReturn(historicoComData);

        // Act
        Historico resultado = historicoService.salvar(historicoComData);

        // Assert
        assertThat(resultado.getDataHora()).isEqualTo(dataExistente);
        verify(repository, times(1)).save(historicoComData);
    }

    @Test
    void buscarPorId_DeveRetornarHistorico_QuandoIdExistente() {
        // Arrange
        when(repository.findById(id)).thenReturn(Optional.of(historico));

        // Act
        Optional<Historico> resultado = historicoService.buscarPorId(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(id);
        verify(repository, times(1)).findById(id);
    }

    @Test
    void buscarPorId_DeveRetornarOptionalVazio_QuandoIdNaoExistente() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act
        Optional<Historico> resultado = historicoService.buscarPorId(idInexistente);

        // Assert
        assertThat(resultado).isEmpty();
        verify(repository, times(1)).findById(idInexistente);
    }

    @Test
    void buscarTodos_DeveRetornarListaDeHistoricos() {
        // Arrange
        Historico historico2 = Historico.builder()
                .id(UUID.randomUUID())
                .pacienteId(UUID.randomUUID())
                .profissionalId(UUID.randomUUID())
                .dataHora(LocalDateTime.now())
                .descricao("Outro histórico")
                .build();

        List<Historico> historicos = List.of(historico, historico2);
        when(repository.findAll()).thenReturn(historicos);

        // Act
        List<Historico> resultado = historicoService.buscarTodos();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).containsExactlyElementsOf(historicos);
        verify(repository, times(1)).findAll();
    }

    @Test
    void buscarTodos_DeveRetornarListaVazia_QuandoNaoHaHistoricos() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of());

        // Act
        List<Historico> resultado = historicoService.buscarTodos();

        // Assert
        assertThat(resultado).isEmpty();
        verify(repository, times(1)).findAll();
    }

    @Test
    void buscarPorPaciente_DeveRetornarHistoricosDoPaciente() {
        // Arrange
        List<Historico> historicosPaciente = List.of(historico);
        when(repository.findByPacienteId(pacienteId)).thenReturn(historicosPaciente);

        // Act
        List<Historico> resultado = historicoService.buscarPorPaciente(pacienteId);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPacienteId()).isEqualTo(pacienteId);
        verify(repository, times(1)).findByPacienteId(pacienteId);
    }

    @Test
    void buscarPorPaciente_DeveRetornarListaVazia_QuandoPacienteSemHistorico() {
        // Arrange
        UUID pacienteSemHistorico = UUID.randomUUID();
        when(repository.findByPacienteId(pacienteSemHistorico)).thenReturn(List.of());

        // Act
        List<Historico> resultado = historicoService.buscarPorPaciente(pacienteSemHistorico);

        // Assert
        assertThat(resultado).isEmpty();
        verify(repository, times(1)).findByPacienteId(pacienteSemHistorico);
    }

    @Test
    void buscarPorProfissional_DeveRetornarHistoricosDoProfissional() {
        // Arrange
        List<Historico> historicosProfissional = List.of(historico);
        when(repository.findByProfissionalId(profissionalId)).thenReturn(historicosProfissional);

        // Act
        List<Historico> resultado = historicoService.buscarPorProfissional(profissionalId);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getProfissionalId()).isEqualTo(profissionalId);
        verify(repository, times(1)).findByProfissionalId(profissionalId);
    }

    @Test
    void buscarPorProfissional_DeveRetornarListaVazia_QuandoProfissionalSemAtendimentos() {
        // Arrange
        UUID profissionalSemAtendimentos = UUID.randomUUID();
        when(repository.findByProfissionalId(profissionalSemAtendimentos)).thenReturn(List.of());

        // Act
        List<Historico> resultado = historicoService.buscarPorProfissional(profissionalSemAtendimentos);

        // Assert
        assertThat(resultado).isEmpty();
        verify(repository, times(1)).findByProfissionalId(profissionalSemAtendimentos);
    }

    @Test
    void buscarPorPeriodo_DeveRetornarHistoricosNoPeriodo() {
        // Arrange
        List<Historico> historicosPeriodo = List.of(historico);
        when(repository.findByPeriodo(inicio, fim)).thenReturn(historicosPeriodo);

        // Act
        List<Historico> resultado = historicoService.buscarPorPeriodo(inicio, fim);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(repository, times(1)).findByPeriodo(inicio, fim);
    }

    @Test
    void buscarPorPeriodo_DeveRetornarListaVazia_QuandoNaoHaHistoricosNoPeriodo() {
        // Arrange
        when(repository.findByPeriodo(inicio, fim)).thenReturn(List.of());

        // Act
        List<Historico> resultado = historicoService.buscarPorPeriodo(inicio, fim);

        // Assert
        assertThat(resultado).isEmpty();
        verify(repository, times(1)).findByPeriodo(inicio, fim);
    }

    @Test
    void deletar_DeveChamarRepositoryDeleteById() {
        // Arrange
        doNothing().when(repository).deleteById(id);

        // Act
        historicoService.deletar(id);

        // Assert
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void deletar_DeveLancarExcecao_QuandoIdNaoExistente() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        doThrow(new RuntimeException("Histórico não encontrado")).when(repository).deleteById(idInexistente);

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            historicoService.deletar(idInexistente);
        });
        verify(repository, times(1)).deleteById(idInexistente);
    }
}