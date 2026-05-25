package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in.ConsultaUseCase;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in.ReagendarConsultaCommand;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in.CancelarConsultaCommand;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.application.usecase.ReagendarConsultaUseCaseImpl;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.application.usecase.CancelarConsultaUseCaseImpl;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final ConsultaUseCase useCase;
    private final ReagendarConsultaUseCaseImpl reagendarUseCase;
    private final CancelarConsultaUseCaseImpl cancelarUseCase;

    @PostMapping
    public ResponseEntity<ConsultaResponse> criar(@RequestBody ConsultaRequest request) {
        var consulta = useCase.criar(request.toCommand());
        return ResponseEntity.ok(new ConsultaResponse(consulta));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
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

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        useCase.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reagendar")
    public ResponseEntity<ReagendarConsultaResponse> reagendar(@PathVariable UUID id,
                                                               @RequestBody ReagendarConsultaRequest request) {
        ReagendarConsultaCommand command = new ReagendarConsultaCommand(
                id,
                request.getNovaDataHora(),
                request.getMotivo()
        );
        var consultaOriginal = useCase.buscarPorId(id);
        var consultaSalva = reagendarUseCase.reagendar(command);
        
        // Buscar histórico de reagendamentos da consulta original
        var reagendamentos = reagendarUseCase.buscarHistorico(id);
        var ultimoReagendamento = reagendamentos.isEmpty() ? null : reagendamentos.get(reagendamentos.size() - 1);
        
        if (ultimoReagendamento != null) {
            return ResponseEntity.ok(new ReagendarConsultaResponse(
                    consultaOriginal,
                    consultaSalva,
                    ultimoReagendamento
            ));
        }
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/reagendamentos")
    public List<ReagendamentoResponse> listarReagendamentos(@PathVariable UUID id) {
        return reagendarUseCase.buscarHistorico(id)
                .stream()
                .map(ReagendamentoResponse::new)
                .toList();
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<CancelarConsultaResponse> cancelar(@PathVariable UUID id,
                                                             @RequestBody CancelarConsultaRequest request) {
        CancelarConsultaCommand command = new CancelarConsultaCommand(
                id,
                request.getMotivo()
        );
        var consultaCancelada = cancelarUseCase.cancelar(command);
        return ResponseEntity.ok(new CancelarConsultaResponse(consultaCancelada));
    }
}