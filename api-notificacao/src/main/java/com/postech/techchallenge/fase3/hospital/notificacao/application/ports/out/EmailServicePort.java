package com.postech.techchallenge.fase3.hospital.notificacao.application.ports.out;

public interface EmailServicePort {
    void enviarEmail(String para, String assunto, String corpo);
}
