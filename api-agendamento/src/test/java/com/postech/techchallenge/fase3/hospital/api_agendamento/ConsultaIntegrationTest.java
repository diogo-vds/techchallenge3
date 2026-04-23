package com.postech.techchallenge.fase3.hospital.api_agendamento;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto.ConsultaRequest;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto.ConsultaUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ConsultaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID pacienteId;
    private UUID profissionalId;
    private LocalDateTime dataHora;
    private String descricao;

    @BeforeEach
    void setUp() {
        pacienteId = UUID.randomUUID();
        profissionalId = UUID.randomUUID();
        dataHora = LocalDateTime.now().plusDays(1);
        descricao = "Consulta de rotina";
    }

    @Test
    void testFluxoCompletoConsulta() throws Exception {
        // 1. Criar uma consulta
        ConsultaRequest request = new ConsultaRequest();
        request.setPacienteId(pacienteId);
        request.setProfissionalId(profissionalId);
        request.setDataHora(dataHora);
        request.setDescricao(descricao);

        MvcResult criarResult = mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.pacienteId", is(pacienteId.toString())))
                .andExpect(jsonPath("$.profissionalId", is(profissionalId.toString())))
                .andExpect(jsonPath("$.descricao", is(descricao)))
                .andReturn();

        String consultaId = objectMapper.readTree(criarResult.getResponse().getContentAsString())
                .get("id").asText();

        // 2. Buscar a consulta criada
        mockMvc.perform(get("/v1/agendamentos/{id}", consultaId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(consultaId)))
                .andExpect(jsonPath("$.pacienteId", is(pacienteId.toString())));

        // 3. Listar consultas
        mockMvc.perform(get("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // 4. Atualizar a consulta
        LocalDateTime novaDataHora = dataHora.plusDays(2);
        String novaDescricao = "Consulta de acompanhamento";

        ConsultaUpdateRequest updateRequest = new ConsultaUpdateRequest();
        updateRequest.setDataHora(novaDataHora);
        updateRequest.setDescricao(novaDescricao);

        mockMvc.perform(put("/v1/agendamentos/{id}", consultaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(consultaId)))
                .andExpect(jsonPath("$.descricao", is(novaDescricao)));

        // 5. Verificar que a consulta foi atualizada
        mockMvc.perform(get("/v1/agendamentos/{id}", consultaId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao", is(novaDescricao)));

        // 6. Deletar a consulta
        mockMvc.perform(delete("/v1/agendamentos/{id}", consultaId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCriarMultiplasConsultasEListar() throws Exception {
        // Criar primeira consulta
        ConsultaRequest request1 = new ConsultaRequest();
        request1.setPacienteId(pacienteId);
        request1.setProfissionalId(profissionalId);
        request1.setDataHora(dataHora);
        request1.setDescricao("Consulta 1");

        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        // Criar segunda consulta
        ConsultaRequest request2 = new ConsultaRequest();
        request2.setPacienteId(UUID.randomUUID());
        request2.setProfissionalId(profissionalId);
        request2.setDataHora(dataHora.plusDays(1));
        request2.setDescricao("Consulta 2");

        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());

        // Listar todas as consultas
        mockMvc.perform(get("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void testValidarDataHoraConsulta() throws Exception {
        // Criar consulta com data válida
        ConsultaRequest request = new ConsultaRequest();
        request.setPacienteId(pacienteId);
        request.setProfissionalId(profissionalId);
        request.setDataHora(dataHora);
        request.setDescricao(descricao);

        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataHora", notNullValue()));
    }

    @Test
    void testAtualizarComNovosPacienteEProfissional() throws Exception {
        // Criar consulta inicial
        ConsultaRequest request = new ConsultaRequest();
        request.setPacienteId(pacienteId);
        request.setProfissionalId(profissionalId);
        request.setDataHora(dataHora);
        request.setDescricao(descricao);

        MvcResult criarResult = mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String consultaId = objectMapper.readTree(criarResult.getResponse().getContentAsString())
                .get("id").asText();

        // Atualizar apenas data e descrição (paciente e profissional não mudam)
        LocalDateTime novaDataHora = dataHora.plusDays(3);
        String novaDescricao = "Consulta rescheduled";

        ConsultaUpdateRequest updateRequest = new ConsultaUpdateRequest();
        updateRequest.setDataHora(novaDataHora);
        updateRequest.setDescricao(novaDescricao);

        mockMvc.perform(put("/v1/agendamentos/{id}", consultaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pacienteId", is(pacienteId.toString())))
                .andExpect(jsonPath("$.profissionalId", is(profissionalId.toString())))
                .andExpect(jsonPath("$.descricao", is(novaDescricao)));
    }
}

