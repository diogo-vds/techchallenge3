package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.application.usecase;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Consulta;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in.AtualizarConsultaCommand;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in.CriarConsultaCommand;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.out.ConsultaRepository;
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
class ConsultaUseCaseImplTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @InjectMocks
    private ConsultaUseCaseImpl consultaUseCase;

    private UUID consultaId;
    private UUID pacienteId;
    private UUID profissionalId;
    private LocalDateTime dataHora;
    private String descricao;
    private Consulta consulta;

    @BeforeEach
    void setUp() {
        consultaId = UUID.randomUUID();
        pacienteId = UUID.randomUUID();
        profissionalId = UUID.randomUUID();
        
        // Ensure datetime is on a weekday during business hours (14:00)
        LocalDateTime nextDay = LocalDateTime.now().plusDays(1);
        while (nextDay.getDayOfWeek().getValue() >= 6) { // 6 = Saturday, 7 = Sunday
            nextDay = nextDay.plusDays(1);
        }
        dataHora = nextDay.withHour(14).withMinute(0).withSecond(0).withNano(0);
        descricao = "Consulta de rotina";

        consulta = Consulta.reconstitute(
                consultaId,
                pacienteId,
                profissionalId,
                dataHora,
                descricao
        );
    }

    @Test
    void testCriarConsultaComSucesso() {
        // Arrange
        CriarConsultaCommand command = new CriarConsultaCommand(
                pacienteId,
                profissionalId,
                dataHora,
                descricao
        );

        when(consultaRepository.profissionalAtivo(profissionalId))
                .thenReturn(true);
        when(consultaRepository.existeConflito(profissionalId, dataHora))
                .thenReturn(false);
        when(consultaRepository.salvar(any(Consulta.class)))
                .thenReturn(consulta);

        // Act
        Consulta resultado = consultaUseCase.criar(command);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(consultaId);
        assertThat(resultado.getPacienteId()).isEqualTo(pacienteId);
        assertThat(resultado.getProfissionalId()).isEqualTo(profissionalId);
        assertThat(resultado.getDataHora()).isEqualTo(dataHora);
        assertThat(resultado.getDescricao()).isEqualTo(descricao);

        verify(consultaRepository, times(1)).existeConflito(profissionalId, dataHora);
        verify(consultaRepository, times(1)).salvar(any(Consulta.class));
    }

    @Test
    void testCriarConsultaComConflito() {
        // Arrange
        CriarConsultaCommand command = new CriarConsultaCommand(
                pacienteId,
                profissionalId,
                dataHora,
                descricao
        );

        when(consultaRepository.profissionalAtivo(profissionalId))
                .thenReturn(true);
        when(consultaRepository.existeConflito(profissionalId, dataHora))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> consultaUseCase.criar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Já existe uma consulta para esse profissional neste horário");

        verify(consultaRepository, times(1)).existeConflito(profissionalId, dataHora);
        verify(consultaRepository, never()).salvar(any());
    }

    @Test
    void testBuscarConsultaPorIdComSucesso() {
        // Arrange
        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consulta));

        // Act
        Consulta resultado = consultaUseCase.buscarPorId(consultaId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(consultaId);
        assertThat(resultado.getPacienteId()).isEqualTo(pacienteId);

        verify(consultaRepository, times(1)).buscarPorId(consultaId);
    }

    @Test
    void testBuscarConsultaPorIdNaoEncontrada() {
        // Arrange
        when(consultaRepository.buscarPorId(any()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> consultaUseCase.buscarPorId(consultaId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Consulta não encontrada");

        verify(consultaRepository, times(1)).buscarPorId(consultaId);
    }

    @Test
    void testAtualizarConsultaComSucesso() {
        // Arrange
        LocalDateTime novaDataHora = dataHora.plusDays(2);
        String novaDescricao = "Consulta de acompanhamento";

        AtualizarConsultaCommand command = new AtualizarConsultaCommand(
                novaDataHora,
                novaDescricao
        );

        Consulta consultaAtualizada = Consulta.reconstitute(
                consultaId,
                pacienteId,
                profissionalId,
                novaDataHora,
                novaDescricao
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consulta));
        when(consultaRepository.profissionalAtivo(profissionalId))
                .thenReturn(true);
        when(consultaRepository.existeConflito(profissionalId, novaDataHora))
                .thenReturn(false);
        when(consultaRepository.salvar(any(Consulta.class)))
                .thenReturn(consultaAtualizada);

        // Act
        Consulta resultado = consultaUseCase.atualizar(consultaId, command);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(consultaId);
        assertThat(resultado.getDataHora()).isEqualTo(novaDataHora);
        assertThat(resultado.getDescricao()).isEqualTo(novaDescricao);

        verify(consultaRepository, times(1)).buscarPorId(consultaId);
        verify(consultaRepository, times(1)).existeConflito(profissionalId, novaDataHora);
        verify(consultaRepository, times(1)).salvar(any(Consulta.class));
    }

    @Test
    void testAtualizarConsultaComConflito() {
        // Arrange
        LocalDateTime novaDataHora = dataHora.plusDays(2);
        String novaDescricao = "Consulta de acompanhamento";

        AtualizarConsultaCommand command = new AtualizarConsultaCommand(
                novaDataHora,
                novaDescricao
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consulta));
        when(consultaRepository.profissionalAtivo(profissionalId))
                .thenReturn(true);
        when(consultaRepository.existeConflito(profissionalId, novaDataHora))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> consultaUseCase.atualizar(consultaId, command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Horário já ocupado");

        verify(consultaRepository, times(1)).buscarPorId(consultaId);
        verify(consultaRepository, times(1)).existeConflito(profissionalId, novaDataHora);
        verify(consultaRepository, never()).salvar(any());
    }

    @Test
    void testAtualizarConsultaMesmaDtHoraSSemConflito() {
        // Arrange - Atualizar para a mesma data/hora não deve gerar conflito
        String novaDescricao = "Descrição atualizada";

        AtualizarConsultaCommand command = new AtualizarConsultaCommand(
                dataHora,  // mesma data/hora
                novaDescricao
        );

        Consulta consultaAtualizada = Consulta.reconstitute(
                consultaId,
                pacienteId,
                profissionalId,
                dataHora,
                novaDescricao
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consulta));
        when(consultaRepository.profissionalAtivo(profissionalId))
                .thenReturn(true);
        when(consultaRepository.existeConflito(profissionalId, dataHora))
                .thenReturn(true); // tem conflito, mas com a mesma data não deve falhar
        when(consultaRepository.salvar(any(Consulta.class)))
                .thenReturn(consultaAtualizada);

        // Act
        Consulta resultado = consultaUseCase.atualizar(consultaId, command);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(consultaId);
        assertThat(resultado.getDataHora()).isEqualTo(dataHora);
        assertThat(resultado.getDescricao()).isEqualTo(novaDescricao);

        verify(consultaRepository, times(1)).buscarPorId(consultaId);
        verify(consultaRepository, times(1)).existeConflito(profissionalId, dataHora);
        verify(consultaRepository, times(1)).salvar(any(Consulta.class));
    }

    @Test
    void testAtualizarConsultaNaoEncontrada() {
        // Arrange
        AtualizarConsultaCommand command = new AtualizarConsultaCommand(
                dataHora.plusDays(2),
                "Nova descrição"
        );

        when(consultaRepository.buscarPorId(any()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> consultaUseCase.atualizar(consultaId, command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Consulta não encontrada");

        verify(consultaRepository, times(1)).buscarPorId(consultaId);
        verify(consultaRepository, never()).salvar(any());
    }

    @Test
    void testCriarConsultaComDataNoPAssado() {
        // Arrange
        LocalDateTime dataPassada = LocalDateTime.now().minusDays(1);
        CriarConsultaCommand command = new CriarConsultaCommand(
                pacienteId,
                profissionalId,
                dataPassada,
                descricao
        );

        // Act & Assert
        assertThatThrownBy(() -> consultaUseCase.criar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Não é permitido agendar consultas para datas no passado");

        verify(consultaRepository, never()).profissionalAtivo(any());
        verify(consultaRepository, never()).existeConflito(any(), any());
        verify(consultaRepository, never()).salvar(any());
    }

    @Test
    void testAtualizarConsultaParaDataNoPAssado() {
        // Arrange
        LocalDateTime dataPassada = LocalDateTime.now().minusHours(1);
        AtualizarConsultaCommand command = new AtualizarConsultaCommand(
                dataPassada,
                "Descrição atualizada"
        );

        // Act & Assert
        assertThatThrownBy(() -> consultaUseCase.atualizar(consultaId, command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Não é permitido agendar consultas para datas no passado");

        // Date validation should occur before repository call
        verify(consultaRepository, never()).buscarPorId(any());
        verify(consultaRepository, never()).existeConflito(any(), any());
        verify(consultaRepository, never()).salvar(any());
    }

    @Test
    void testListarConsultas() {
        // Arrange
        UUID consultaId2 = UUID.randomUUID();
        Consulta consulta2 = Consulta.reconstitute(
                consultaId2,
                pacienteId,
                profissionalId,
                dataHora.plusDays(1),
                "Consulta 2"
        );

        when(consultaRepository.listar())
                .thenReturn(List.of(consulta, consulta2));

        // Act
        List<Consulta> resultado = consultaUseCase.listar();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        assertThat(resultado).contains(consulta, consulta2);

        verify(consultaRepository, times(1)).listar();
    }

    @Test
    void testListarConsultasVazia() {
        // Arrange
        when(consultaRepository.listar())
                .thenReturn(List.of());

        // Act
        List<Consulta> resultado = consultaUseCase.listar();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();

        verify(consultaRepository, times(1)).listar();
    }

    @Test
    void testDeletarConsulta() {
        // Arrange
        doNothing().when(consultaRepository).deletar(consultaId);

        // Act
        consultaUseCase.deletar(consultaId);

        // Assert
        verify(consultaRepository, times(1)).deletar(consultaId);
    }
}

