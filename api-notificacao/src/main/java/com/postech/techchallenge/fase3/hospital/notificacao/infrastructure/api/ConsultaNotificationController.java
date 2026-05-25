package com.postech.techchallenge.fase3.hospital.notificacao.infrastructure.api;

import com.postech.techchallenge.fase3.hospital.notificacao.application.dto.ConsultaNotificationRequest;
import com.postech.techchallenge.fase3.hospital.notificacao.application.service.RabbitMQProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notificacoes/v1")
public class ConsultaNotificationController {

    private final RabbitMQProducerService rabbitMQProducerService;

    public ConsultaNotificationController(RabbitMQProducerService rabbitMQProducerService) {
        this.rabbitMQProducerService = rabbitMQProducerService;
    }

    @PostMapping("/consulta")
    public ResponseEntity<String> notifyConsulta(@RequestBody ConsultaNotificationRequest request) {
        rabbitMQProducerService.sendConsultaNotification(request);
        return ResponseEntity.ok("Consulta notification sent to queue.");
    }
}
