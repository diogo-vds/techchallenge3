package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.application.usecase;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Consulta;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Reagendamento;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in.ReagendarConsultaCommand;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.out.ConsultaRepository;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.out.ReagendamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReagendarConsultaUseCaseImpl {

    private final ConsultaRepository consultaRepository;
    private final ReagendamentoRepository reagendamentoRepository;
    private static final int MAX_REAGENDAMENTOS = 3;
    private static final int HORAS_MINIMAS_ANTECEDENCIA = 24;

    public Consulta reagendar(ReagendarConsultaCommand command) {
        // 1. Buscar consulta original
        Consulta consultaOriginal = consultaRepository.buscarPorId(command.consultaId())
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        // 2. Validar pré-requisitos
        validarPrecondições(consultaOriginal);

        // 3. Validar antecedência (24h)
        validarAntecedencia(consultaOriginal);

        // 4. Validar limite de reagendamentos (máx 3)
        validarLimite(command.consultaId());

        // 5. Validar nova data/hora
        validarNovaDataHora(command.novaDataHora(), consultaOriginal.getProfissionalId());

        // 6. Criar nova consulta
        Consulta novaConsulta = criarNovaConsulta(consultaOriginal, command.novaDataHora());

        // 7. Marcar original como cancelada
        marcarComoCancelada(consultaOriginal, command.novaDataHora());

        // 8. Salvar tudo em transação
        consultaRepository.salvar(consultaOriginal); // Marca como inativa
        Consulta consultaSalva = consultaRepository.salvar(novaConsulta); // Salva a nova

        // 9. Registrar histórico de reagendamento
        int totalReagendamentos = reagendamentoRepository.contagemReagendamentosPorConsulta(consultaOriginal.getId()) + 1;
        Reagendamento reagendamento = Reagendamento.criar(
                consultaOriginal.getId(),
                consultaSalva.getId(),
                command.motivo(),
                consultaOriginal.getDataHora(),
                command.novaDataHora(),
                totalReagendamentos
        );
        reagendamentoRepository.salvar(reagendamento);

        return consultaSalva;
    }

    private void validarPrecondições(Consulta consulta) {
        if (!consulta.isAtiva()) {
            throw new RuntimeException("Consulta já foi cancelada");
        }
    }

    private void validarAntecedencia(Consulta consulta) {
        LocalDateTime limite = consulta.getDataHora().minusHours(HORAS_MINIMAS_ANTECEDENCIA);
        if (LocalDateTime.now().isAfter(limite)) {
            throw new RuntimeException(
                "Reagendamento deve ser feito com mínimo " + HORAS_MINIMAS_ANTECEDENCIA + "h de antecedência"
            );
        }
    }

    private void validarLimite(UUID consultaId) {
        int totalReagendamentos = reagendamentoRepository.contagemReagendamentosPorConsulta(consultaId);
        if (totalReagendamentos >= MAX_REAGENDAMENTOS) {
            throw new RuntimeException(
                "Máximo de " + MAX_REAGENDAMENTOS + " reagendamentos por consulta foi atingido"
            );
        }
    }

    private void validarNovaDataHora(LocalDateTime novaDataHora, UUID profissionalId) {
        // Validar data no passado
        if (novaDataHora.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Não é permitido agendar consultas para datas no passado");
        }

        // Validar horário comercial (usar mesma lógica do crear)
        if (!validarHorarioComercial(novaDataHora)) {
            throw new RuntimeException("Consultas só podem ser agendadas durante o horário comercial (08:00 às 18:00)");
        }

        // Validar profissional ativo
        if (!consultaRepository.profissionalAtivo(profissionalId)) {
            throw new RuntimeException("Profissional não está ativo");
        }

        // Validar conflito de horário (mas agora a original não está mais ativa)
        // Buscar conflitos apenas em consultas ativas
        boolean conflito = consultaRepository.existeConflito(profissionalId, novaDataHora);
        if (conflito) {
            throw new RuntimeException("Já existe uma consulta para esse profissional neste horário");
        }
    }

    private boolean validarHorarioComercial(LocalDateTime dataHora) {
        if (dataHora.getDayOfWeek().getValue() >= 6) { // 6 = sábado, 7 = domingo
            return false;
        }

        int hora = dataHora.getHour();
        return hora >= 8 && hora < 18;
    }

    private Consulta criarNovaConsulta(Consulta original, LocalDateTime novaDataHora) {
        Consulta novaConsulta = Consulta.nova(
                original.getPacienteId(),
                original.getProfissionalId(),
                novaDataHora,
                original.getDescricao()
        );
        novaConsulta.marcarComoReagendamento(original.getId());
        return novaConsulta;
    }

    private void marcarComoCancelada(Consulta consulta, LocalDateTime novaDataHora) {
        consulta.cancelar("Reagendada para " + novaDataHora);
    }

    public List<Reagendamento> buscarHistorico(UUID consultaId) {
        return reagendamentoRepository.buscarPorConsultaOriginal(consultaId);
    }
}


