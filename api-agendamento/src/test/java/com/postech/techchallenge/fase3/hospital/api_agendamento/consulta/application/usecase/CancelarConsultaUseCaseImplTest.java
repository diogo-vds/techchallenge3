package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.application.usecase;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Consulta;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in.CancelarConsultaCommand;
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
class CancelarConsultaUseCaseImplTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @InjectMocks
    private CancelarConsultaUseCaseImpl cancelarUseCase;

    private UUID consultaId;
    private UUID pacienteId;
    private UUID profissionalId;
    private LocalDateTime dataHora;
    private Consulta consulta;

    @BeforeEach
    void setUp() {
        consultaId = UUID.randomUUID();
        pacienteId = UUID.randomUUID();
        profissionalId = UUID.randomUUID();
        dataHora = LocalDateTime.now().plusHours(4); // 4 horas no futuro

        consulta = Consulta.reconstitute(
                consultaId,
                pacienteId,
                profissionalId,
                dataHora,
                "Consulta de rotina"
        );
    }

    @Test
    void testCancelarComSucesso() {
        // Arrange
        CancelarConsultaCommand command = new CancelarConsultaCommand(
                consultaId,
                "Paciente não pode comparecer"
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consulta));
        when(consultaRepository.salvar(any(Consulta.class)))
                .thenReturn(consulta);

        // Act
        Consulta resultado = cancelarUseCase.cancelar(command);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.isAtiva()).isFalse();
        assertThat(resultado.getMotivoCancelamento()).isEqualTo("Paciente não pode comparecer");
        assertThat(resultado.getDataCancelamento()).isNotNull();
        verify(consultaRepository, times(1)).salvar(any(Consulta.class));
    }

    @Test
    void testCancelarConsultaNaoEncontrada() {
        // Arrange
        CancelarConsultaCommand command = new CancelarConsultaCommand(
                consultaId,
                "Motivo qualquer"
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cancelarUseCase.cancelar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Consulta não encontrada");

        verify(consultaRepository, never()).salvar(any());
    }

    @Test
    void testCancelarConsultaJaCancelada() {
        // Arrange
        Consulta consultaCancelada = Consulta.reconstituteComRastreamento(
                consultaId, pacienteId, profissionalId, dataHora, "Consulta",
                false, null, LocalDateTime.now().minusHours(1), "Já cancelada"
        );

        CancelarConsultaCommand command = new CancelarConsultaCommand(
                consultaId,
                "Tentativa de cancelar novamente"
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consultaCancelada));

        // Act & Assert
        assertThatThrownBy(() -> cancelarUseCase.cancelar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Consulta já foi cancelada");

        verify(consultaRepository, never()).salvar(any());
    }

    @Test
    void testCancelarMenosDe2HorasAntecedencia() {
        // Arrange
        LocalDateTime dataHoraProxima = LocalDateTime.now().plusHours(1); // Apenas 1 hora no futuro
        Consulta consultaProxima = Consulta.reconstitute(
                consultaId, pacienteId, profissionalId, dataHoraProxima, "Consulta próxima"
        );

        CancelarConsultaCommand command = new CancelarConsultaCommand(
                consultaId,
                "Cancelamento em cima da hora"
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consultaProxima));

        // Act & Assert
        assertThatThrownBy(() -> cancelarUseCase.cancelar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("mínimo 2h de antecedência");

        verify(consultaRepository, never()).salvar(any());
    }

    @Test
    void testCancelarExatamente2HorasAntecedencia() {
        // Arrange
        LocalDateTime dataHoraExata = LocalDateTime.now().plusHours(2); // Exatamente 2 horas
        Consulta consultaExata = Consulta.reconstitute(
                consultaId, pacienteId, profissionalId, dataHoraExata, "Consulta em 2h"
        );

        CancelarConsultaCommand command = new CancelarConsultaCommand(
                consultaId,
                "Cancelamento no limite"
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consultaExata));
        when(consultaRepository.salvar(any(Consulta.class)))
                .thenReturn(consultaExata);

        // Act
        Consulta resultado = cancelarUseCase.cancelar(command);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.isAtiva()).isFalse();
        verify(consultaRepository, times(1)).salvar(any(Consulta.class));
    }

    @Test
    void testCancelarMaisDe2HorasAntecedencia() {
        // Arrange
        LocalDateTime dataHoraLonge = LocalDateTime.now().plusHours(6); // 6 horas no futuro
        Consulta consultaLonge = Consulta.reconstitute(
                consultaId, pacienteId, profissionalId, dataHoraLonge, "Consulta distante"
        );

        CancelarConsultaCommand command = new CancelarConsultaCommand(
                consultaId,
                "Cancelamento com antecedência"
        );

        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consultaLonge));
        when(consultaRepository.salvar(any(Consulta.class)))
                .thenReturn(consultaLonge);

        // Act
        Consulta resultado = cancelarUseCase.cancelar(command);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.isAtiva()).isFalse();
        verify(consultaRepository, times(1)).salvar(any(Consulta.class));
    }

    @Test
    void testBuscarPorId() {
        // Arrange
        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.of(consulta));

        // Act
        Consulta resultado = cancelarUseCase.buscarPorId(consultaId);

        // Assert
        assertThat(resultado).isEqualTo(consulta);
        verify(consultaRepository, times(1)).buscarPorId(consultaId);
    }

    @Test
    void testBuscarPorIdNaoEncontrada() {
        // Arrange
        when(consultaRepository.buscarPorId(consultaId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cancelarUseCase.buscarPorId(consultaId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Consulta não encontrada");
    }
}

