package com.postech.techchallenge.fase3.hospital.historico.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ConsultaScheduler {

    private final SincronizacaoConsultaService sincronizacaoService;

    // Executar a cada 5 minutos
    @Scheduled(fixedDelay = 300000)
    public void sincronizarConsultasAutomatico() {
        log.info("Iniciando sincronização automática de consultas...");
        sincronizacaoService.sincronizarConsultas();
    }
}
