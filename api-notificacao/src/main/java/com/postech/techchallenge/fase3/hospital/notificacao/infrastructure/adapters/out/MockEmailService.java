package com.postech.techchallenge.fase3.hospital.notificacao.infrastructure.adapters.out;

import com.postech.techchallenge.fase3.hospital.notificacao.application.ports.out.EmailServicePort;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class MockEmailService implements EmailServicePort {
    private static final Logger logger = Logger.getLogger(MockEmailService.class.getName());

    @Override
    public void enviarEmail(String assunto, String corpo) {
        logger.info("**************************************************");
        logger.info("ENVIANDO EMAIL DE CONFIRMAÇÃO ");
        logger.info("ASSUNTO: " + assunto);
        logger.info("CORPO: " + corpo);
        logger.info("**************************************************");
    }
}
