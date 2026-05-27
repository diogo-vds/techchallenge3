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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaUseCaseImplPacienteTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @InjectMocks
    private ConsultaUseCaseImpl consultaUseCase;

    private UUID pacienteId;
    private UUID profissionalId;
    private LocalDateTime dataHora;
    private LocalDateTime novaDataHora;

    @BeforeEach
    void setUp() {
        pacienteId = UUID.randomUUID();
        profissionalId = UUID.randomUUID();
        // Ensure dates are on a weekday during business hours
        LocalDateTime day1 = LocalDateTime.now().plusDays(1);
        while (day1.getDayOfWeek().getValue() >= 6) { // 6 = Saturday, 7 = Sunday
            day1 = day1.plusDays(1);
        }
        dataHora = day1.withHour(10).withMinute(0).withSecond(0).withNano(0);
        
        LocalDateTime day2 = day1.plusDays(1);
        while (day2.getDayOfWeek().getValue() >= 6) {
            day2 = day2.plusDays(1);
        }
        novaDataHora = day2.withHour(14).withMinute(0).withSecond(0).withNano(0);
    }

    @Test
    void testCriarConsultaPacienteComConflitoMesmoHorario() {
        // Arrange
        CriarConsultaCommand command = new CriarConsultaCommand(
                pacienteId,
                profissionalId,
                dataHora,
                "Consulta de rotina"
        );

        when(consultaRepository.profissionalAtivo(profissionalId)).thenReturn(true);
        when(consultaRepository.existeConflito(profissionalId, dataHora)).thenReturn(false);
        when(consultaRepository.pacienteTemConsultaMesmoHorario(pacienteId, dataHora)).thenReturn(true); // Conflito!

        // Act & Assert
        assertThatThrownBy(() -> consultaUseCase.criar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Paciente já tem uma consulta agendada neste horário");

        verify(consultaRepository, never()).salvar(any());
    }

    @Test
    void testCriarConsultaPacienteComMuitasConsultasSimultaneas() {
        // Arrange
        CriarConsultaCommand command = new CriarConsultaCommand(
                pacienteId,
                profissionalId,
                dataHora,
                "Consulta de rotina"
        );

        when(consultaRepository.profissionalAtivo(profissionalId)).thenReturn(true);
        when(consultaRepository.existeConflito(profissionalId, dataHora)).thenReturn(false);
        when(consultaRepository.pacienteTemConsultaMesmoHorario(pacienteId, dataHora)).thenReturn(false);
        when(consultaRepository.contarConsultasAtivasPaciente(pacienteId)).thenReturn(3); // Já tem 3!

        // Act & Assert
        assertThatThrownBy(() -> consultaUseCase.criar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Paciente não pode ter mais de 3 consultas simultâneas");

        verify(consultaRepository, never()).salvar(any());
    }

    @Test
    void testCriarConsultaPacienteLimiteExatoConsultas() {
        // Arrange
        CriarConsultaCommand command = new CriarConsultaCommand(
                pacienteId,
                profissionalId,
                dataHora,
                "Consulta de rotina"
        );

        when(consultaRepository.profissionalAtivo(profissionalId)).thenReturn(true);
        when(consultaRepository.existeConflito(profissionalId, dataHora)).thenReturn(false);
        when(consultaRepository.pacienteTemConsultaMesmoHorario(pacienteId, dataHora)).thenReturn(false);
        when(consultaRepository.contarConsultasAtivasPaciente(pacienteId)).thenReturn(2); // Tem 2, pode ter mais 1
        when(consultaRepository.salvar(any(Consulta.class))).thenReturn(mock(Consulta.class));

        // Act
        Consulta resultado = consultaUseCase.criar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(consultaRepository, times(1)).salvar(any(Consulta.class));
    }

    @Test
    void testAtualizarConsultaPacienteConflitoMesmoHorario() {
        // Arrange
        UUID consultaId = UUID.randomUUID();
        Consulta consulta = Consulta.reconstitute(consultaId, pacienteId, profissionalId, dataHora, "Consulta");

        AtualizarConsultaCommand command = new AtualizarConsultaCommand(
                novaDataHora,
                "Descrição atualizada"
        );

        when(consultaRepository.buscarPorId(consultaId)).thenReturn(Optional.of(consulta));
        when(consultaRepository.profissionalAtivo(profissionalId)).thenReturn(true);
        when(consultaRepository.existeConflito(profissionalId, novaDataHora)).thenReturn(false);
        when(consultaRepository.pacienteTemConsultaMesmoHorario(pacienteId, novaDataHora)).thenReturn(true); // Conflito!

        // Act & Assert
        assertThatThrownBy(() -> consultaUseCase.atualizar(consultaId, command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Paciente já tem uma consulta agendada neste horário");

        verify(consultaRepository, never()).salvar(any());
    }

    @Test
    void testAtualizarConsultaPacienteMesmoHorarioPermitido() {
        // Arrange - Atualizar para o mesmo horário deve ser permitido
        UUID consultaId = UUID.randomUUID();
        Consulta consulta = Consulta.reconstitute(consultaId, pacienteId, profissionalId, dataHora, "Consulta");

        AtualizarConsultaCommand command = new AtualizarConsultaCommand(
                dataHora, // Mesmo horário
                "Descrição atualizada"
        );

        when(consultaRepository.buscarPorId(consultaId)).thenReturn(Optional.of(consulta));
        when(consultaRepository.profissionalAtivo(profissionalId)).thenReturn(true);
        when(consultaRepository.existeConflito(profissionalId, dataHora)).thenReturn(false);
        when(consultaRepository.pacienteTemConsultaMesmoHorario(pacienteId, dataHora)).thenReturn(true); // Mesmo horário
        when(consultaRepository.salvar(any(Consulta.class))).thenReturn(consulta);

        // Act
        Consulta resultado = consultaUseCase.atualizar(consultaId, command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(consultaRepository, times(1)).salvar(any(Consulta.class));
    }

    @Test
    void testCriarConsultaPacienteSucesso() {
        // Arrange
        CriarConsultaCommand command = new CriarConsultaCommand(
                pacienteId,
                profissionalId,
                dataHora,
                "Consulta de rotina"
        );

        when(consultaRepository.profissionalAtivo(profissionalId)).thenReturn(true);
        when(consultaRepository.existeConflito(profissionalId, dataHora)).thenReturn(false);
        when(consultaRepository.pacienteTemConsultaMesmoHorario(pacienteId, dataHora)).thenReturn(false);
        when(consultaRepository.contarConsultasAtivasPaciente(pacienteId)).thenReturn(1); // Tem 1, pode ter mais
        when(consultaRepository.salvar(any(Consulta.class))).thenReturn(mock(Consulta.class));

        // Act
        Consulta resultado = consultaUseCase.criar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(consultaRepository, times(1)).salvar(any(Consulta.class));
    }
}
