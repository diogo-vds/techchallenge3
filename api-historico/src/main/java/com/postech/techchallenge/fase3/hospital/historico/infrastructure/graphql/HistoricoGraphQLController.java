package com.postech.techchallenge.fase3.hospital.historico.infrastructure.graphql;

import com.postech.techchallenge.fase3.hospital.historico.domain.model.Historico;
import com.postech.techchallenge.fase3.hospital.historico.domain.port.HistoricoServicePort;
import com.postech.techchallenge.fase3.hospital.historico.infrastructure.client.ConsultaClient;

import com.postech.techchallenge.fase3.hospital.historico.infrastructure.client.ConsultaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HistoricoGraphQLController {

    private final HistoricoServicePort historicoService;
    private final ConsultaClient consultaClient; // FeignClient para buscar consultas
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // ==================== QUERIES (GET) - Buscam do FeignClient ====================

    /**
     * Busca todas as consultas do serviço de agendamento
     */
    @QueryMapping
    public List<ConsultaResponse> consultas() {
        log.info("Buscando todas as consultas via FeignClient");
        return consultaClient.listarConsultas();
    }

    /**
     * Busca uma consulta específica por ID no serviço de agendamento
     */
    @QueryMapping
    public ConsultaResponse consulta(@Argument String id) {
        log.info("Buscando consulta por ID: {}", id);
        List<ConsultaResponse> consultas = consultaClient.listarConsultas();

        return consultas.stream()
                .filter(c -> c.getId().toString().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada com id: " + id));
    }

    /**
     * Busca consultas por paciente no serviço de agendamento
     */
    @QueryMapping
    public List<ConsultaResponse> consultasPorPaciente(@Argument String pacienteId) {
        log.info("Buscando consultas por paciente: {}", pacienteId);
        List<ConsultaResponse> consultas = consultaClient.listarConsultas();

        return consultas.stream()
                .filter(c -> c.getPacienteId().toString().equals(pacienteId))
                .collect(Collectors.toList());
    }

    /**
     * Busca consultas por profissional no serviço de agendamento
     */
    @QueryMapping
    public List<ConsultaResponse> consultasPorProfissional(@Argument String profissionalId) {
        log.info("Buscando consultas por profissional: {}", profissionalId);
        List<ConsultaResponse> consultas = consultaClient.listarConsultas();

        return consultas.stream()
                .filter(c -> c.getProfissionalId().toString().equals(profissionalId))
                .collect(Collectors.toList());
    }

    /**
     * Busca consultas por período no serviço de agendamento
     */
    @QueryMapping
    public List<ConsultaResponse> consultasPorPeriodo(@Argument String inicio, @Argument String fim) {
        log.info("Buscando consultas por período: {} a {}", inicio, fim);
        LocalDateTime dataInicio = LocalDateTime.parse(inicio, formatter);
        LocalDateTime dataFim = LocalDateTime.parse(fim, formatter);

        List<ConsultaResponse> consultas = consultaClient.listarConsultas();

        return consultas.stream()
                .filter(c -> {
                    LocalDateTime dataHora = c.getDataHora();
                    return dataHora != null &&
                            !dataHora.isBefore(dataInicio) &&
                            !dataHora.isAfter(dataFim);
                })
                .collect(Collectors.toList());
    }

    // ==================== HISTÓRICO QUERIES (GET) - Buscam do banco local ====================

    /**
     * Busca todos os históricos do banco local
     */
    @QueryMapping
    public List<Historico> historicos() {
        log.info("Buscando todos os históricos do banco local");
        return historicoService.buscarTodos();
    }

    /**
     * Busca histórico por ID no banco local
     */
    @QueryMapping
    public Historico historico(@Argument String id) {
        log.info("Buscando histórico por ID: {}", id);
        return historicoService.buscarPorId(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Histórico não encontrado com id: " + id));
    }

    /**
     * Busca históricos por paciente no banco local
     */
    @QueryMapping
    public List<Historico> historicosPorPaciente(@Argument String pacienteId) {
        log.info("Buscando históricos por paciente: {}", pacienteId);
        return historicoService.buscarPorPaciente(UUID.fromString(pacienteId));
    }

    /**
     * Busca históricos por profissional no banco local
     */
    @QueryMapping
    public List<Historico> historicosPorProfissional(@Argument String profissionalId) {
        log.info("Buscando históricos por profissional: {}", profissionalId);
        return historicoService.buscarPorProfissional(UUID.fromString(profissionalId));
    }

    /**
     * Busca históricos por período no banco local
     */
    @QueryMapping
    public List<Historico> historicosPorPeriodo(@Argument String inicio, @Argument String fim) {
        log.info("Buscando históricos por período: {} a {}", inicio, fim);
        LocalDateTime dataInicio = LocalDateTime.parse(inicio, formatter);
        LocalDateTime dataFim = LocalDateTime.parse(fim, formatter);
        return historicoService.buscarPorPeriodo(dataInicio, dataFim);
    }

    // ==================== MUTATIONS (CREATE, UPDATE, DELETE) ====================

    /**
     * Criar um novo histórico (salva no banco local)
     */
    @MutationMapping
    public Historico criarHistorico(@Argument String pacienteId,
                                    @Argument String profissionalId,
                                    @Argument String descricao,
                                    @Argument String dataHora) {
        log.info("Criando novo histórico - Paciente: {}, Profissional: {}", pacienteId, profissionalId);

        Historico novoHistorico = Historico.builder()
                .id(UUID.randomUUID())
                .pacienteId(UUID.fromString(pacienteId))
                .profissionalId(UUID.fromString(profissionalId))
                .descricao(descricao)
                .dataHora(dataHora != null ? LocalDateTime.parse(dataHora, formatter) : LocalDateTime.now())
                .build();

        return historicoService.salvar(novoHistorico);
    }

    /**
     * Criar histórico a partir de uma consulta existente (sincronização)
     */
    @MutationMapping
    public Historico sincronizarConsulta(@Argument String consultaId) {
        log.info("Sincronizando consulta ID: {} para histórico", consultaId);

        // Buscar consulta no serviço externo
        List<ConsultaResponse> consultas = consultaClient.listarConsultas();
        ConsultaResponse consulta = consultas.stream()
                .filter(c -> c.getId().toString().equals(consultaId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada: " + consultaId));

        // Verificar se já existe histórico para esta consulta
        UUID consultaUUID = consulta.getId();
        if (historicoService.buscarPorId(consultaUUID).isPresent()) {
            throw new RuntimeException("Histórico já existe para esta consulta: " + consultaId);
        }

        // Converter consulta para histórico
        Historico historico = Historico.builder()
                .id(consulta.getId())
                .pacienteId(consulta.getPacienteId())
                .profissionalId(consulta.getProfissionalId())
                .dataHora(consulta.getDataHora())
                .descricao(consulta.getDescricao())
                .build();

        return historicoService.salvar(historico);
    }

    /**
     * Sincronizar todas as consultas (cria históricos para todas as consultas do Feign)
     */
    @MutationMapping
    public Integer sincronizarTodasConsultas() {
        log.info("Sincronizando todas as consultas para histórico");

        List<ConsultaResponse> consultas = consultaClient.listarConsultas();
        int contador = 0;

        for (ConsultaResponse consulta : consultas) {
            UUID consultaId = consulta.getId();

            // Verificar se já existe histórico
            if (historicoService.buscarPorId(consultaId).isEmpty()) {
                Historico historico = Historico.builder()
                        .id(consulta.getId())
                        .pacienteId(consulta.getPacienteId())
                        .profissionalId(consulta.getProfissionalId())
                        .dataHora(consulta.getDataHora())
                        .descricao(consulta.getDescricao())
                        .build();

                historicoService.salvar(historico);
                contador++;
            }
        }

        log.info("Sincronização concluída. {} novos históricos criados.", contador);
        return contador;
    }

    /**
     * Atualizar um histórico existente
     */
    @MutationMapping
    public Historico atualizarHistorico(@Argument String id,
                                        @Argument String pacienteId,
                                        @Argument String profissionalId,
                                        @Argument String descricao,
                                        @Argument String dataHora) {
        log.info("Atualizando histórico ID: {}", id);

        UUID historicoId = UUID.fromString(id);
        Historico historicoExistente = historicoService.buscarPorId(historicoId)
                .orElseThrow(() -> new RuntimeException("Histórico não encontrado com id: " + id));

        // Atualizar campos
        if (pacienteId != null) {
            historicoExistente.setPacienteId(UUID.fromString(pacienteId));
        }
        if (profissionalId != null) {
            historicoExistente.setProfissionalId(UUID.fromString(profissionalId));
        }
        if (descricao != null) {
            historicoExistente.setDescricao(descricao);
        }
        if (dataHora != null) {
            historicoExistente.setDataHora(LocalDateTime.parse(dataHora, formatter));
        }

        return historicoService.salvar(historicoExistente);
    }

    /**
     * Deletar um histórico por ID
     */
    @MutationMapping
    public Boolean deletarHistorico(@Argument String id) {
        log.info("Deletando histórico ID: {}", id);
        try {
            historicoService.deletar(UUID.fromString(id));
            return true;
        } catch (Exception e) {
            log.error("Erro ao deletar histórico: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Deletar todos os históricos de um paciente
     */
    @MutationMapping
    public Integer deletarHistoricosPorPaciente(@Argument String pacienteId) {
        log.info("Deletando históricos do paciente: {}", pacienteId);

        List<Historico> historicos = historicoService.buscarPorPaciente(UUID.fromString(pacienteId));
        int contador = 0;

        for (Historico historico : historicos) {
            historicoService.deletar(historico.getId());
            contador++;
        }

        log.info("{} históricos deletados para o paciente {}", contador, pacienteId);
        return contador;
    }
}