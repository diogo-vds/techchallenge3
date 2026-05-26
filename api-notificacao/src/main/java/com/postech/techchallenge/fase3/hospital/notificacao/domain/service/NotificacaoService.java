package com.postech.techchallenge.fase3.hospital.notificacao.domain.service;

import com.postech.techchallenge.fase3.hospital.notificacao.application.ports.in.NotificacaoUseCase;
import com.postech.techchallenge.fase3.hospital.notificacao.application.ports.out.EmailServicePort;
import com.postech.techchallenge.fase3.hospital.notificacao.domain.model.ConsultaEvento;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class NotificacaoService implements NotificacaoUseCase {

    private final EmailServicePort emailServicePort;

    public NotificacaoService(EmailServicePort emailServicePort) {
        this.emailServicePort = emailServicePort;
    }

    @Override
    public void enviarLembrete(ConsultaEvento evento) {
        String assunto = "Lembrete de Consulta: " + evento.pacienteId();
        String dataFormatada = evento.dataConsulta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        
        String corpo = String.format(
            "\n\nOlá %s,\n\nEste é um lembrete da sua consulta com o(a) Dr(a). %s agendada para %s.\n\nStatus: %s\n\n",
            evento.pacienteId(),
            evento.profissionalId(),
            dataFormatada,
            evento.status()
        );

        emailServicePort.enviarEmail(assunto, corpo);
    }
}
