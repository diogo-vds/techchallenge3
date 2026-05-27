package com.postech.techchallenge.fase3.hospital.historico.infrastructure.graphql;


import com.postech.techchallenge.fase3.hospital.historico.domain.model.Historico;
import com.postech.techchallenge.fase3.hospital.historico.domain.port.HistoricoServicePort;
import com.postech.techchallenge.fase3.hospital.historico.infrastructure.client.ConsultaClient;
import com.postech.techchallenge.fase3.hospital.historico.infrastructure.client.ConsultaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoricoGraphQLControllerTest {

    @Mock
    private HistoricoServicePort historicoService;

    @Mock
    private ConsultaClient consultaClient;

    @InjectMocks
    private HistoricoGraphQLController controller;

    private UUID id;
    private UUID pacienteId;
    private UUID profissionalId;
    private LocalDateTime dataHora;
    private String descricao;
    private ConsultaResponse consultaResponse;
    private Historico historico;
    private DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        pacienteId = UUID.randomUUID();
        profissionalId = UUID.randomUUID();
        dataHora = LocalDateTime.now();
        descricao = "Consulta de rotina";
        formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        consultaResponse = ConsultaResponse.builder()
                .id(id)
                .pacienteId(pacienteId)
                .profissionalId(profissionalId)
                .dataHora(dataHora)
                .descricao(descricao)
                .build();

        historico = Historico.builder()
                .id(id)
                .pacienteId(pacienteId)
                .profissionalId(profissionalId)
                .dataHora(dataHora)
                .descricao(descricao)
                .build();
    }

    // ==================== TESTS FOR CONSULTAS QUERIES ====================

    @Test
    void consultas_DeveRetornarListaDeConsultas() {
        // Arrange
        List<ConsultaResponse> consultas = List.of(consultaResponse);
        when(consultaClient.listarConsultas()).thenReturn(consultas);

        // Act
        List<ConsultaResponse> resultado = controller.consultas();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado).containsExactly(consultaResponse);
        verify(consultaClient, times(1)).listarConsultas();
    }

    @Test
    void consulta_DeveRetornarConsulta_QuandoIdExistente() {
        // Arrange
        List<ConsultaResponse> consultas = List.of(consultaResponse);
        when(consultaClient.listarConsultas()).thenReturn(consultas);

        // Act
        ConsultaResponse resultado = controller.consulta(id.toString());

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(id);
        verify(consultaClient, times(1)).listarConsultas();
    }

    @Test
    void consulta_DeveLancarExcecao_QuandoIdNaoExistente() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        List<ConsultaResponse> consultas = List.of(consultaResponse);
        when(consultaClient.listarConsultas()).thenReturn(consultas);

        // Act & Assert
        assertThatThrownBy(() -> controller.consulta(idInexistente.toString()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Consulta não encontrada");
        verify(consultaClient, times(1)).listarConsultas();
    }

    @Test
    void consultasPorPaciente_DeveRetornarConsultasDoPaciente() {
        // Arrange
        List<ConsultaResponse> consultas = List.of(consultaResponse);
        when(consultaClient.listarConsultas()).thenReturn(consultas);

        // Act
        List<ConsultaResponse> resultado = controller.consultasPorPaciente(pacienteId.toString());

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPacienteId()).isEqualTo(pacienteId);
        verify(consultaClient, times(1)).listarConsultas();
    }

    @Test
    void consultasPorPaciente_DeveRetornarListaVazia_QuandoPacienteSemConsultas() {
        // Arrange
        UUID outroPaciente = UUID.randomUUID();
        List<ConsultaResponse> consultas = List.of(consultaResponse);
        when(consultaClient.listarConsultas()).thenReturn(consultas);

        // Act
        List<ConsultaResponse> resultado = controller.consultasPorPaciente(outroPaciente.toString());

        // Assert
        assertThat(resultado).isEmpty();
        verify(consultaClient, times(1)).listarConsultas();
    }

    @Test
    void consultasPorProfissional_DeveRetornarConsultasDoProfissional() {
        // Arrange
        List<ConsultaResponse> consultas = List.of(consultaResponse);
        when(consultaClient.listarConsultas()).thenReturn(consultas);

        // Act
        List<ConsultaResponse> resultado = controller.consultasPorProfissional(profissionalId.toString());

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getProfissionalId()).isEqualTo(profissionalId);
        verify(consultaClient, times(1)).listarConsultas();
    }

    @Test
    void consultasPorPeriodo_DeveRetornarConsultasNoPeriodo() {
        // Arrange
        List<ConsultaResponse> consultas = List.of(consultaResponse);
        when(consultaClient.listarConsultas()).thenReturn(consultas);
        String inicio = dataHora.minusDays(1).format(formatter);
        String fim = dataHora.plusDays(1).format(formatter);

        // Act
        List<ConsultaResponse> resultado = controller.consultasPorPeriodo(inicio, fim);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(consultaClient, times(1)).listarConsultas();
    }

    @Test
    void consultasPorPeriodo_DeveRetornarListaVazia_QuandoNaoHaConsultasNoPeriodo() {
        // Arrange
        List<ConsultaResponse> consultas = List.of(consultaResponse);
        when(consultaClient.listarConsultas()).thenReturn(consultas);
        String inicio = dataHora.plusDays(1).format(formatter);
        String fim = dataHora.plusDays(2).format(formatter);

        // Act
        List<ConsultaResponse> resultado = controller.consultasPorPeriodo(inicio, fim);

        // Assert
        assertThat(resultado).isEmpty();
        verify(consultaClient, times(1)).listarConsultas();
    }

    // ==================== TESTS FOR HISTORICO QUERIES ====================

    @Test
    void historicos_DeveRetornarListaDeHistoricos() {
        // Arrange
        List<Historico> historicos = List.of(historico);
        when(historicoService.buscarTodos()).thenReturn(historicos);

        // Act
        List<Historico> resultado = controller.historicos();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado).containsExactly(historico);
        verify(historicoService, times(1)).buscarTodos();
    }

    @Test
    void historico_DeveRetornarHistorico_QuandoIdExistente() {
        // Arrange
        when(historicoService.buscarPorId(id)).thenReturn(Optional.of(historico));

        // Act
        Historico resultado = controller.historico(id.toString());

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(id);
        verify(historicoService, times(1)).buscarPorId(id);
    }

    @Test
    void historico_DeveLancarExcecao_QuandoIdNaoExistente() {
        // Arrange
        when(historicoService.buscarPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> controller.historico(id.toString()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Histórico não encontrado");
        verify(historicoService, times(1)).buscarPorId(id);
    }

    @Test
    void historicosPorPaciente_DeveRetornarHistoricosDoPaciente() {
        // Arrange
        List<Historico> historicos = List.of(historico);
        when(historicoService.buscarPorPaciente(pacienteId)).thenReturn(historicos);

        // Act
        List<Historico> resultado = controller.historicosPorPaciente(pacienteId.toString());

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPacienteId()).isEqualTo(pacienteId);
        verify(historicoService, times(1)).buscarPorPaciente(pacienteId);
    }

    @Test
    void historicosPorProfissional_DeveRetornarHistoricosDoProfissional() {
        // Arrange
        List<Historico> historicos = List.of(historico);
        when(historicoService.buscarPorProfissional(profissionalId)).thenReturn(historicos);

        // Act
        List<Historico> resultado = controller.historicosPorProfissional(profissionalId.toString());

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getProfissionalId()).isEqualTo(profissionalId);
        verify(historicoService, times(1)).buscarPorProfissional(profissionalId);
    }

    @Test
    void historicosPorPeriodo_DeveRetornarHistoricosNoPeriodo() {
        // Arrange
        List<Historico> historicos = List.of(historico);
        String inicio = dataHora.minusDays(1).format(formatter);
        String fim = dataHora.plusDays(1).format(formatter);
        when(historicoService.buscarPorPeriodo(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(historicos);

        // Act
        List<Historico> resultado = controller.historicosPorPeriodo(inicio, fim);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(historicoService, times(1)).buscarPorPeriodo(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    // ==================== TESTS FOR MUTATIONS ====================

    @Test
    void criarHistorico_DeveCriarHistoricoComSucesso() {
        // Arrange
        String dataHoraStr = dataHora.format(formatter);
        when(historicoService.salvar(any(Historico.class))).thenReturn(historico);

        // Act
        Historico resultado = controller.criarHistorico(
                pacienteId.toString(),
                profissionalId.toString(),
                descricao,
                dataHoraStr
        );

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getPacienteId()).isEqualTo(pacienteId);
        assertThat(resultado.getProfissionalId()).isEqualTo(profissionalId);
        assertThat(resultado.getDescricao()).isEqualTo(descricao);
        verify(historicoService, times(1)).salvar(any(Historico.class));
    }

    @Test
    void criarHistorico_DeveUsarDataHoraAtual_QuandoDataHoraNaoInformada() {
        // Arrange
        when(historicoService.salvar(any(Historico.class))).thenAnswer(invocation -> {
            Historico h = invocation.getArgument(0);
            return h;
        });

        // Act
        Historico resultado = controller.criarHistorico(
                pacienteId.toString(),
                profissionalId.toString(),
                descricao,
                null
        );

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getDataHora()).isNotNull();
        verify(historicoService, times(1)).salvar(any(Historico.class));
    }

    @Test
    void sincronizarConsulta_DeveCriarHistorico_QuandoConsultaExisteENaoTemHistorico() {
        // Arrange
        List<ConsultaResponse> consultas = List.of(consultaResponse);
        when(consultaClient.listarConsultas()).thenReturn(consultas);
        when(historicoService.buscarPorId(id)).thenReturn(Optional.empty());
        when(historicoService.salvar(any(Historico.class))).thenReturn(historico);

        // Act
        Historico resultado = controller.sincronizarConsulta(id.toString());

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(id);
        verify(consultaClient, times(1)).listarConsultas();
        verify(historicoService, times(1)).buscarPorId(id);
        verify(historicoService, times(1)).salvar(any(Historico.class));
    }

    @Test
    void sincronizarConsulta_DeveLancarExcecao_QuandoConsultaNaoExiste() {
        // Arrange
        UUID consultaInexistente = UUID.randomUUID();
        List<ConsultaResponse> consultas = List.of(consultaResponse);
        when(consultaClient.listarConsultas()).thenReturn(consultas);

        // Act & Assert
        assertThatThrownBy(() -> controller.sincronizarConsulta(consultaInexistente.toString()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Consulta não encontrada");
        verify(consultaClient, times(1)).listarConsultas();
        verify(historicoService, never()).buscarPorId(any());
        verify(historicoService, never()).salvar(any());
    }

    @Test
    void sincronizarConsulta_DeveLancarExcecao_QuandoHistoricoJaExiste() {
        // Arrange
        List<ConsultaResponse> consultas = List.of(consultaResponse);
        when(consultaClient.listarConsultas()).thenReturn(consultas);
        when(historicoService.buscarPorId(id)).thenReturn(Optional.of(historico));

        // Act & Assert
        assertThatThrownBy(() -> controller.sincronizarConsulta(id.toString()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Histórico já existe");
        verify(consultaClient, times(1)).listarConsultas();
        verify(historicoService, times(1)).buscarPorId(id);
        verify(historicoService, never()).salvar(any());
    }

    @Test
    void sincronizarTodasConsultas_DeveCriarHistoricosParaConsultasSemHistorico() {
        // Arrange
        List<ConsultaResponse> consultas = List.of(consultaResponse);
        when(consultaClient.listarConsultas()).thenReturn(consultas);
        when(historicoService.buscarPorId(id)).thenReturn(Optional.empty());
        when(historicoService.salvar(any(Historico.class))).thenReturn(historico);

        // Act
        Integer resultado = controller.sincronizarTodasConsultas();

        // Assert
        assertThat(resultado).isEqualTo(1);
        verify(consultaClient, times(1)).listarConsultas();
        verify(historicoService, times(1)).buscarPorId(id);
        verify(historicoService, times(1)).salvar(any(Historico.class));
    }

    @Test
    void sincronizarTodasConsultas_DevePularConsultasComHistoricoExistente() {
        // Arrange
        List<ConsultaResponse> consultas = List.of(consultaResponse);
        when(consultaClient.listarConsultas()).thenReturn(consultas);
        when(historicoService.buscarPorId(id)).thenReturn(Optional.of(historico));

        // Act
        Integer resultado = controller.sincronizarTodasConsultas();

        // Assert
        assertThat(resultado).isEqualTo(0);
        verify(consultaClient, times(1)).listarConsultas();
        verify(historicoService, times(1)).buscarPorId(id);
        verify(historicoService, never()).salvar(any());
    }

    @Test
    void atualizarHistorico_DeveAtualizarComSucesso_QuandoTodosCamposInformados() {
        // Arrange
        String novoPacienteId = UUID.randomUUID().toString();
        String novoProfissionalId = UUID.randomUUID().toString();
        String novaDescricao = "Descrição atualizada";
        String novaDataHora = dataHora.plusDays(1).format(formatter);

        Historico historicoAtualizado = Historico.builder()
                .id(id)
                .pacienteId(UUID.fromString(novoPacienteId))
                .profissionalId(UUID.fromString(novoProfissionalId))
                .descricao(novaDescricao)
                .dataHora(dataHora.plusDays(1))
                .build();

        when(historicoService.buscarPorId(id)).thenReturn(Optional.of(historico));
        when(historicoService.salvar(any(Historico.class))).thenReturn(historicoAtualizado);

        // Act
        Historico resultado = controller.atualizarHistorico(
                id.toString(),
                novoPacienteId,
                novoProfissionalId,
                novaDescricao,
                novaDataHora
        );

        // Assert
        assertThat(resultado).isNotNull();
        verify(historicoService, times(1)).buscarPorId(id);
        verify(historicoService, times(1)).salvar(any(Historico.class));
    }

    @Test
    void atualizarHistorico_DeveAtualizarApenasCamposFornecidos() {
        // Arrange
        String novaDescricao = "Descrição atualizada";

        when(historicoService.buscarPorId(id)).thenReturn(Optional.of(historico));
        when(historicoService.salvar(any(Historico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Historico resultado = controller.atualizarHistorico(
                id.toString(),
                null,
                null,
                novaDescricao,
                null
        );

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getDescricao()).isEqualTo(novaDescricao);
        assertThat(resultado.getPacienteId()).isEqualTo(pacienteId);
        assertThat(resultado.getProfissionalId()).isEqualTo(profissionalId);
        verify(historicoService, times(1)).buscarPorId(id);
        verify(historicoService, times(1)).salvar(any(Historico.class));
    }

    @Test
    void atualizarHistorico_DeveLancarExcecao_QuandoHistoricoNaoExiste() {
        // Arrange
        when(historicoService.buscarPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> controller.atualizarHistorico(id.toString(), null, null, null, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Histórico não encontrado");
        verify(historicoService, times(1)).buscarPorId(id);
        verify(historicoService, never()).salvar(any());
    }

    @Test
    void deletarHistorico_DeveRetornarTrue_QuandoDelecaoBemSucedida() {
        // Arrange
        doNothing().when(historicoService).deletar(id);

        // Act
        Boolean resultado = controller.deletarHistorico(id.toString());

        // Assert
        assertThat(resultado).isTrue();
        verify(historicoService, times(1)).deletar(id);
    }

    @Test
    void deletarHistorico_DeveRetornarFalse_QuandoOcorreErro() {
        // Arrange
        doThrow(new RuntimeException("Erro ao deletar")).when(historicoService).deletar(id);

        // Act
        Boolean resultado = controller.deletarHistorico(id.toString());

        // Assert
        assertThat(resultado).isFalse();
        verify(historicoService, times(1)).deletar(id);
    }

    @Test
    void deletarHistoricosPorPaciente_DeveDeletarTodosHistoricosDoPaciente() {
        // Arrange
        List<Historico> historicos = List.of(historico);
        when(historicoService.buscarPorPaciente(pacienteId)).thenReturn(historicos);
        doNothing().when(historicoService).deletar(id);

        // Act
        Integer resultado = controller.deletarHistoricosPorPaciente(pacienteId.toString());

        // Assert
        assertThat(resultado).isEqualTo(1);
        verify(historicoService, times(1)).buscarPorPaciente(pacienteId);
        verify(historicoService, times(1)).deletar(id);
    }

    @Test
    void deletarHistoricosPorPaciente_DeveRetornarZero_QuandoPacienteSemHistoricos() {
        // Arrange
        when(historicoService.buscarPorPaciente(pacienteId)).thenReturn(List.of());

        // Act
        Integer resultado = controller.deletarHistoricosPorPaciente(pacienteId.toString());

        // Assert
        assertThat(resultado).isEqualTo(0);
        verify(historicoService, times(1)).buscarPorPaciente(pacienteId);
        verify(historicoService, never()).deletar(any());
    }
}