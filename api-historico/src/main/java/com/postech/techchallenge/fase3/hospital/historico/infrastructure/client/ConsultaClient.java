package com.postech.techchallenge.fase3.hospital.historico.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "consulta-client", url = "${consulta.api.url}")
public interface ConsultaClient {

    @GetMapping("/consultas")
    List<ConsultaResponse> listarConsultas();
}
