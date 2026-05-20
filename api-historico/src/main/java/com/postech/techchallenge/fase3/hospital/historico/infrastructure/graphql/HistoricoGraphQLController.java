package com.postech.techchallenge.fase3.hospital.historico.infrastructure.graphql;

import com.postech.techchallenge.fase3.hospital.historico.domain.model.Historico;
import com.postech.techchallenge.fase3.hospital.historico.domain.port.HistoricoServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class HistoricoGraphQLController {

    private final HistoricoServicePort historicoService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @QueryMapping
    public Historico historico(@Argument String id) {
        return historicoService.buscarPorId(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Histórico não encontrado com id: " + id));
    }

    @QueryMapping
    public List<Historico> historicos() {
        return historicoService.buscarTodos();
    }

    @QueryMapping
    public List<Historico> historicosPorPaciente(@Argument String pacienteId) {
        return historicoService.buscarPorPaciente(UUID.fromString(pacienteId));
    }

    @QueryMapping
    public List<Historico> historicosPorProfissional(@Argument String profissionalId) {
        return historicoService.buscarPorProfissional(UUID.fromString(profissionalId));
    }

    @QueryMapping
    public List<Historico> historicosPorPeriodo(@Argument String inicio, @Argument String fim) {
        LocalDateTime dataInicio = LocalDateTime.parse(inicio, formatter);
        LocalDateTime dataFim = LocalDateTime.parse(fim, formatter);
        return historicoService.buscarPorPeriodo(dataInicio, dataFim);
    }

    @MutationMapping
    public Historico criarHistorico(@Argument String pacienteId,
                                    @Argument String profissionalId,
                                    @Argument String descricao,
                                    @Argument String dataHora) {
        Historico novoHistorico = Historico.builder()
                .id(UUID.randomUUID())
                .pacienteId(UUID.fromString(pacienteId))
                .profissionalId(UUID.fromString(profissionalId))
                .descricao(descricao)
                .dataHora(dataHora != null ? LocalDateTime.parse(dataHora, formatter) : LocalDateTime.now())
                .build();

        return historicoService.salvar(novoHistorico);
    }

    @MutationMapping
    public Boolean deletarHistorico(@Argument String id) {
        try {
            historicoService.deletar(UUID.fromString(id));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}