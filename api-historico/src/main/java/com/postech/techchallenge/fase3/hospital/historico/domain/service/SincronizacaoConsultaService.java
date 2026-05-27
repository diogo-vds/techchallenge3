package com.postech.techchallenge.fase3.hospital.historico.domain.service;

import com.postech.techchallenge.fase3.hospital.historico.domain.model.Historico;
import com.postech.techchallenge.fase3.hospital.historico.domain.port.HistoricoServicePort;
import com.postech.techchallenge.fase3.hospital.historico.infrastructure.persistence.ConsultaToHistoricoAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SincronizacaoConsultaService {

    private final ConsultaToHistoricoAdapter consultaAdapter;
    private final HistoricoServicePort historicoService;

    @Transactional
    public void sincronizarConsultas() {
        try {
            List<Historico> historicos = consultaAdapter.converterConsultasParaHistorico();

            for (Historico historico : historicos) {
                // Verifica se já existe
                if (historicoService.buscarPorId(historico.getId()).isEmpty()) {
                    historicoService.salvar(historico);
                    log.info("Histórico salvo para consulta ID: {}", historico.getId());
                } else {
                    log.info("Histórico já existe para consulta ID: {}", historico.getId());
                }
            }

            log.info("Sincronização concluída. {} registros processados.", historicos.size());
        } catch (Exception e) {
            log.error("Erro ao sincronizar consultas: {}", e.getMessage(), e);
            throw new RuntimeException("Erro na sincronização de consultas", e);
        }
    }
}