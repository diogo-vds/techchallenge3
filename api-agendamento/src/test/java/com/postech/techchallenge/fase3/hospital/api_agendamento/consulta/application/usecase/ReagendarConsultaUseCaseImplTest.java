package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.application.usecase;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Consulta;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Reagendamento;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in.ReagendarConsultaCommand;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.out.ConsultaRepository;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.out.ReagendamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ReagendarConsultaUseCaseImplTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private ReagendamentoRepository reagendamentoRepository;

    @InjectMocks
    private ReagendarConsultaUseCaseImpl reagendarUseCase;

    private UUID consultaId;
    private UUID pacienteId;
    private UUID profissionalId;
    private LocalDateTime dataHora;
    private LocalDateTime novaDataHora;
    private Consulta consulta;

    @BeforeEach
    void setUp() {
        consultaId = UUID.randomUUID();
        pacienteId = UUID.randomUUID();
        profissionalId = UUID.randomUUID();
        
        // Ensure dates are on a weekday during business hours (14:00)
        LocalDateTime base = LocalDateTime.now().plusDays(3);
        while (base.getDayOfWeek().getValue() >= 6) { // 6 = Saturday, 7 = Sunday
            base = base.plusDays(1);
        }
        dataHora = proximoHorarioComercial(base.plusDays(1));
        novaDataHora = proximoHorarioComercial(base.plusDays(3));

        consulta = Consulta.reconstitute(
                consultaId,
                pacienteId,
                profissionalId,
                dataHora,
                "Consulta de rotina"
        );
    }

    @Test
    void testReagendarComSucesso() {
        // Arrange
        ReagendarConsultaCommand command = new ReagendarConsultaCommand(
                consultaId,
                novaDataHora,
                "Conflito de agenda"
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consulta));
        when(consultaRepository.profissionalAtivo(profissionalId))
                .thenReturn(true);
        when(consultaRepository.existeConflito(profissionalId, novaDataHora))
                .thenReturn(false);
        when(reagendamentoRepository.contagemReagendamentosPorConsulta(consultaId))
                .thenReturn(0);
        when(consultaRepository.salvar(any(Consulta.class)))
                .thenReturn(Consulta.nova(pacienteId, profissionalId, novaDataHora, "Consulta de rotina"));
        when(reagendamentoRepository.salvar(any(Reagendamento.class)))
                .thenReturn(Reagendamento.criar(consultaId, UUID.randomUUID(), "Conflito", dataHora, novaDataHora, 1));

        // Act
        Consulta resultado = reagendarUseCase.reagendar(command);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getDataHora()).isEqualTo(novaDataHora);
        verify(consultaRepository, times(2)).salvar(any(Consulta.class));
        verify(reagendamentoRepository, times(1)).salvar(any(Reagendamento.class));
    }

    @Test
    void testReagendarConsultaNaoEncontrada() {
        // Arrange
        ReagendarConsultaCommand command = new ReagendarConsultaCommand(
                consultaId,
                novaDataHora,
                "Conflito"
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reagendarUseCase.reagendar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Consulta não encontrada");

        verify(consultaRepository, never()).salvar(any());
    }

    @Test
    void testReagendarConsultaCancelada() {
        // Arrange
        Consulta consultaCancelada = Consulta.reconstituteComRastreamento(
                consultaId, pacienteId, profissionalId, dataHora, "Consulta",
                false, null, LocalDateTime.now(), "Cancelada"
        );

        ReagendarConsultaCommand command = new ReagendarConsultaCommand(
                consultaId,
                novaDataHora,
                "Conflito"
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consultaCancelada));

        // Act & Assert
        assertThatThrownBy(() -> reagendarUseCase.reagendar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Consulta já foi cancelada");
    }

    @Test
    void testReagendarMenosDe24Horas() {
        // Arrange
        LocalDateTime dataHoraProxima = LocalDateTime.now().plusHours(10);
        Consulta consultaProxima = Consulta.reconstitute(
                consultaId, pacienteId, profissionalId, dataHoraProxima, "Consulta"
        );

        ReagendarConsultaCommand command = new ReagendarConsultaCommand(
                consultaId,
                novaDataHora,
                "Conflito"
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consultaProxima));

        // Act & Assert
        assertThatThrownBy(() -> reagendarUseCase.reagendar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("mínimo");
    }

    @Test
    void testReagendarLimiteDe3Reagendamentos() {
        // Arrange
        ReagendarConsultaCommand command = new ReagendarConsultaCommand(
                consultaId,
                novaDataHora,
                "Conflito"
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consulta));
        when(reagendamentoRepository.contagemReagendamentosPorConsulta(consultaId))
                .thenReturn(3); // Já atingiu o limite

        // Act & Assert
        assertThatThrownBy(() -> reagendarUseCase.reagendar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Máximo de");
    }

    @Test
    void testReagendarDataNoPassado() {
        // Arrange
        LocalDateTime dataPassada = LocalDateTime.now().minusDays(1);
        ReagendarConsultaCommand command = new ReagendarConsultaCommand(
                consultaId,
                dataPassada,
                "Conflito"
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consulta));
        when(reagendamentoRepository.contagemReagendamentosPorConsulta(consultaId))
                .thenReturn(0);

        // Act & Assert
        assertThatThrownBy(() -> reagendarUseCase.reagendar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("datas no passado");
    }

    @Test
    void testReagendarForaDoHorarioComercial() {
        // Arrange
        LocalDateTime dataForaHorario = LocalDateTime.now().plusDays(3).withHour(22).withMinute(0);
        ReagendarConsultaCommand command = new ReagendarConsultaCommand(
                consultaId,
                dataForaHorario,
                "Conflito"
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consulta));
        when(reagendamentoRepository.contagemReagendamentosPorConsulta(consultaId))
                .thenReturn(0);

        // Act & Assert
        assertThatThrownBy(() -> reagendarUseCase.reagendar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("horário comercial");
    }

    @Test
    void testReagendarProfissionalInativo() {
        // Arrange
        ReagendarConsultaCommand command = new ReagendarConsultaCommand(
                consultaId,
                novaDataHora,
                "Conflito"
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consulta));
        when(reagendamentoRepository.contagemReagendamentosPorConsulta(consultaId))
                .thenReturn(0);
        when(consultaRepository.profissionalAtivo(profissionalId))
                .thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> reagendarUseCase.reagendar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Profissional não está ativo");
    }

    @Test
    void testReagendarComConflitoDeHorario() {
        // Arrange
        ReagendarConsultaCommand command = new ReagendarConsultaCommand(
                consultaId,
                novaDataHora,
                "Conflito"
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consulta));
        when(reagendamentoRepository.contagemReagendamentosPorConsulta(consultaId))
                .thenReturn(0);
        when(consultaRepository.profissionalAtivo(profissionalId))
                .thenReturn(true);
        when(consultaRepository.existeConflito(profissionalId, novaDataHora))
                .thenReturn(true); // Conflito encontrado

        // Act & Assert
        assertThatThrownBy(() -> reagendarUseCase.reagendar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Já existe uma consulta");
    }

    @Test
    void testBuscarHistorico() {
        // Arrange
        Reagendamento reagendamento1 = Reagendamento.criar(
                consultaId, UUID.randomUUID(), "Conflito 1",
                dataHora, dataHora.plusDays(1), 1
        );
        Reagendamento reagendamento2 = Reagendamento.criar(
                consultaId, UUID.randomUUID(), "Conflito 2",
                dataHora.plusDays(1), dataHora.plusDays(2), 2
        );

        when(reagendamentoRepository.buscarPorConsultaOriginal(consultaId))
                .thenReturn(List.of(reagendamento1, reagendamento2));

        // Act
        List<Reagendamento> resultado = reagendarUseCase.buscarHistorico(consultaId);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).contains(reagendamento1, reagendamento2);
        verify(reagendamentoRepository, times(1)).buscarPorConsultaOriginal(consultaId);
    }

    @Test
    void testBuscarHistoricoVazio() {
        // Arrange
        when(reagendamentoRepository.buscarPorConsultaOriginal(consultaId))
                .thenReturn(List.of());

        // Act
        List<Reagendamento> resultado = reagendarUseCase.buscarHistorico(consultaId);

        // Assert
        assertThat(resultado).isEmpty();
    }

    private LocalDateTime proximoHorarioComercial(LocalDateTime data) {
        LocalDateTime resultado = data
                .withHour(14)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        while (resultado.getDayOfWeek() == DayOfWeek.SATURDAY ||
                resultado.getDayOfWeek() == DayOfWeek.SUNDAY) {
            resultado = resultado.plusDays(1);
        }

        return resultado;
    }
}
