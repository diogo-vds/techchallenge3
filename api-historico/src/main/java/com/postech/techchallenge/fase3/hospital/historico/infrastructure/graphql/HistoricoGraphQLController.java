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

@Controller
@RequiredArgsConstructor
public class HistoricoGraphQLController {

    private final HistoricoServicePort historicoService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @QueryMapping
    public Historico historico(@Argument Long id) {
        return historicoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Histórico não encontrado com id: " + id));
    }

    @QueryMapping
    public List<Historico> historicos() {
        return historicoService.buscarTodos();
    }

    @QueryMapping
    public List<Historico> historicosPorUsuario(@Argument String usuarioId) {
        return historicoService.buscarPorUsuario(usuarioId);
    }

    @QueryMapping
    public List<Historico> historicosPorEntidade(@Argument String entidadeId) {
        return historicoService.buscarPorEntidade(entidadeId);
    }

    @QueryMapping
    public List<Historico> historicosPorAcao(@Argument String acao) {
        return historicoService.buscarPorAcao(acao);
    }

    @QueryMapping
    public List<Historico> historicosPorPeriodo(@Argument PeriodoInput periodo) {
        LocalDateTime inicio = LocalDateTime.parse(periodo.getInicio(), formatter);
        LocalDateTime fim = LocalDateTime.parse(periodo.getFim(), formatter);
        // Implementar no service se necessário
        return List.of();
    }

    @MutationMapping
    public Historico criarHistorico(@Argument HistoricoInput historico) {
        Historico novoHistorico = Historico.builder()
                .usuarioId(historico.getUsuarioId())
                .acao(historico.getAcao())
                .detalhes(historico.getDetalhes())
                .entidadeId(historico.getEntidadeId())
                .tipoOperacao(historico.getTipoOperacao())
                .dataHora(LocalDateTime.now())
                .build();

        return historicoService.salvar(novoHistorico);
    }

    @MutationMapping
    public Boolean deletarHistorico(@Argument Long id) {
        try {
            historicoService.deletar(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}