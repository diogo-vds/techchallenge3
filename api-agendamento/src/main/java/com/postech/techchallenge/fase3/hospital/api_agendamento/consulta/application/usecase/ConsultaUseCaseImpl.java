package com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.application.usecase;

import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.model.Consulta;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.in.*;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.domain.port.out.ConsultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultaUseCaseImpl implements ConsultaUseCase {

    private final ConsultaRepository repository;

    @Override
    public Consulta criar(CriarConsultaCommand command) {
        var consulta = Consulta.nova(
                command.pacienteId(),
                command.profissionalId(),
                command.dataHora(),
                command.descricao()
        );
        return repository.salvar(consulta);
    }

    @Override
    public Consulta atualizar(UUID id, AtualizarConsultaCommand command) {
        var consulta = repository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        consulta.atualizar(command.dataHora(), command.descricao());

        return repository.salvar(consulta);
    }

    @Override
    public Consulta buscarPorId(UUID id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
    }

    @Override
    public List<Consulta> listar() {
        return repository.listar();
    }

    @Override
    public void deletar(UUID id) {
        repository.deletar(id);
    }
}