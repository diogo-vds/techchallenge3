package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in.ConsultaUseCase;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final ConsultaUseCase useCase;

    @PostMapping
    public ResponseEntity<ConsultaResponse> criar(@RequestBody ConsultaRequest request) {
        var consulta = useCase.criar(request.toCommand());
        return ResponseEntity.ok(new ConsultaResponse(consulta));
    }

    @GetMapping
    public List<ConsultaResponse> listar() {
        return useCase.listar().stream()
                .map(ConsultaResponse::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ConsultaResponse buscar(@PathVariable UUID id) {
        return new ConsultaResponse(useCase.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ConsultaResponse atualizar(@PathVariable UUID id,
                                      @RequestBody ConsultaUpdateRequest request) {
        return new ConsultaResponse(
                useCase.atualizar(id, request.toCommand())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        useCase.deletar(id);
        return ResponseEntity.noContent().build();
    }
}