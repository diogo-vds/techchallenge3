package com.postech.techchallenge.fase3.hospital.api_agendamento.shared.feign;

import com.postech.techchallenge.fase3.hospital.api_agendamento.shared.dto.NotificacaoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notificacao", url = "${api.notificacao.url}")
public interface NotificacaoClient {

    @PostMapping("/v1/consulta")
    void enviarNotificacaoConsulta(@RequestBody NotificacaoRequest request);
}
