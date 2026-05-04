package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.application.usecase;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Consulta;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in.CancelarConsultaCommand;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.out.ConsultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CancelarConsultaUseCaseImpl {

    private final ConsultaRepository consultaRepository;

    // Antecedência mínima para cancelamento: 2 horas
    private static final int HORAS_MINIMAS_ANTECEDENCIA = 2;

    public Consulta cancelar(CancelarConsultaCommand command) {
        // 1. Buscar consulta
        Consulta consulta = consultaRepository.buscarPorId(command.consultaId())
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        // 2. Validar se já foi cancelada
        if (!consulta.isAtiva()) {
            throw new RuntimeException("Consulta já foi cancelada");
        }

        // 3. Validar antecedência mínima
        validarAntecedencia(consulta);

        // 4. Cancelar consulta
        consulta.cancelar(command.motivo());

        // 5. Salvar
        return consultaRepository.salvar(consulta);
    }

    private void validarAntecedencia(Consulta consulta) {
        LocalDateTime limite = consulta.getDataHora().minusHours(HORAS_MINIMAS_ANTECEDENCIA);
        if (LocalDateTime.now().isAfter(limite)) {
            throw new RuntimeException(
                "Cancelamento deve ser feito com mínimo " + HORAS_MINIMAS_ANTECEDENCIA + "h de antecedência"
            );
        }
    }

    public Consulta buscarPorId(UUID id) {
        return consultaRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
    }
}

