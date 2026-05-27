package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.application.usecase;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Consulta;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in.*;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.out.ConsultaRepository;
import com.postech.techchallenge.fase3.hospital.api_agendamento.shared.dto.ConsultaNotificationMessage;
import com.postech.techchallenge.fase3.hospital.api_agendamento.shared.rabbitmq.ConsultaNotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultaUseCaseImpl implements ConsultaUseCase {

    private final ConsultaRepository repository;
    private final ConsultaNotificationSender consultaNotificationSender;

    // Horário comercial: 08:00 às 18:00
    private static final int HORA_INICIO_COMERCIAL = 8;
    private static final int HORA_FIM_COMERCIAL = 18;

    @Override
    public Consulta criar(CriarConsultaCommand command) {

        if (command.dataHora().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Não é permitido agendar consultas para datas no passado");
        }

        if (!validarHorarioComercial(command.dataHora())) {
            throw new RuntimeException("Consultas só podem ser agendadas durante o horário comercial (08:00 às 18:00)");
        }

        if (!repository.profissionalAtivo(command.profissionalId())) {
            throw new RuntimeException("Profissional não está ativo");
        }

        boolean conflitoProfissional = repository.existeConflito(
                command.profissionalId(),
                command.dataHora()
        );

        if (conflitoProfissional) {
            throw new RuntimeException("Já existe uma consulta para esse profissional neste horário");
        }

        // NOVAS VALIDAÇÕES POR PACIENTE
        boolean conflitoPaciente = repository.pacienteTemConsultaMesmoHorario(
                command.pacienteId(),
                command.dataHora()
        );

        if (conflitoPaciente) {
            throw new RuntimeException("Paciente já tem uma consulta agendada neste horário");
        }

        int consultasAtivasPaciente = repository.contarConsultasAtivasPaciente(command.pacienteId());
        if (consultasAtivasPaciente >= 3) {
            throw new RuntimeException("Paciente não pode ter mais de 3 consultas simultâneas");
        }

        var consulta = Consulta.nova(
                command.pacienteId(),
                command.profissionalId(),
                command.dataHora(),
                command.descricao()
        );
        Consulta savedConsulta = repository.salvar(consulta);

        this.enviaNotificacao(savedConsulta);

        return savedConsulta;
    }

    private void enviaNotificacao(Consulta savedConsulta) {
        // Enviar notificação via RabbitMQ
        ConsultaNotificationMessage notificationMessage = new ConsultaNotificationMessage(
                savedConsulta.getPacienteId(),
                savedConsulta.getProfissionalId(),
                savedConsulta.getDataHora(),
                "agendado"
        );
        try {
            consultaNotificationSender.sendConsultaNotification(notificationMessage);
        } catch (Exception e) {
            // Logar o erro, mas não impedir o agendamento da consulta
            System.err.println("Erro ao enviar notificação: " + e.getMessage());
        }
    }

    @Override
    public Consulta atualizar(UUID id, AtualizarConsultaCommand command) {

        if (command.dataHora().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Não é permitido agendar consultas para datas no passado");
        }

        if (!validarHorarioComercial(command.dataHora())) {
            throw new RuntimeException("Consultas só podem ser agendadas durante o horário comercial (08:00 às 18:00)");
        }

        var consulta = repository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        if (!repository.profissionalAtivo(consulta.getProfissionalId())) {
            throw new RuntimeException("Profissional não está ativo");
        }

        boolean conflitoProfissional = repository.existeConflito(
                consulta.getProfissionalId(),
                command.dataHora()
        );

        if (conflitoProfissional && !consulta.getDataHora().equals(command.dataHora())) {
            throw new RuntimeException("Horário já ocupado");
        }

        // NOVAS VALIDAÇÕES POR PACIENTE
        boolean conflitoPaciente = repository.pacienteTemConsultaMesmoHorario(
                consulta.getPacienteId(),
                command.dataHora()
        );

        if (conflitoPaciente && !consulta.getDataHora().equals(command.dataHora())) {
            throw new RuntimeException("Paciente já tem uma consulta agendada neste horário");
        }

        consulta.atualizar(command.dataHora(), command.descricao());

        return repository.salvar(consulta);
    }

    @Override
    public Consulta buscarPorId(UUID id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
    }

    @Override
    public List<Consulta> listar() {
        return repository.listar();
    }

    @Override
    public void deletar(UUID id) {
        repository.deletar(id);
    }

    /**
     * Valida se a data/hora da consulta está dentro do horário comercial
     * Horário comercial: Segunda a Sexta, 08:00 às 18:00
     */
    private boolean validarHorarioComercial(LocalDateTime dataHora) {
        // Verifica se é fim de semana (sábado ou domingo)
        if (dataHora.getDayOfWeek() == DayOfWeek.SATURDAY || 
            dataHora.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return false;
        }

        // Verifica se está dentro do horário comercial
        LocalTime hora = dataHora.toLocalTime();
        LocalTime horaInicio = LocalTime.of(HORA_INICIO_COMERCIAL, 0);
        LocalTime horaFim = LocalTime.of(HORA_FIM_COMERCIAL, 0);

        return !hora.isBefore(horaInicio) && hora.isBefore(horaFim);
    }
}