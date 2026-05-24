package com.postech.techchallenge.fase3.hospital.historico.infrastructure.graphql;

import com.postech.techchallenge.fase3.hospital.historico.domain.service.SincronizacaoConsultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sincronizacao")
@RequiredArgsConstructor
public class SincronizacaoController {

    private final SincronizacaoConsultaService sincronizacaoService;

    @PostMapping("/consultas")
    public ResponseEntity<Map<String, String>> sincronizarConsultas() {
        sincronizacaoService.sincronizarConsultas();

        Map<String, String> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Sincronização de consultas iniciada com sucesso");

        return ResponseEntity.ok(response);
    }
}