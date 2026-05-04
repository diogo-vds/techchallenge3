package com.postech.techchallenge.fase3.hospital.api_agendamento;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto.ReagendarConsultaRequest;
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
class ReagendamentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID consultaId;
    private UUID pacienteId;
    private UUID profissionalId;
    private LocalDateTime dataHora;

    @BeforeEach
    void setUp() {
        pacienteId = UUID.randomUUID();
        profissionalId = UUID.randomUUID();
        dataHora = LocalDateTime.now().plusDays(3);
    }

    @Test
    void testReagendarComSucesso() throws Exception {
        // 1. Criar uma consulta inicial
        var consultaRequest = criarConsultaRequest();
        MvcResult criarResult = mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest)))
                .andExpect(status().isOk())
                .andReturn();

        consultaId = UUID.fromString(
                objectMapper.readTree(criarResult.getResponse().getContentAsString())
                        .get("id").asText()
        );

        // 2. Reagendar a consulta
        LocalDateTime novaDataHora = dataHora.plusDays(2);
        ReagendarConsultaRequest reagendarRequest = new ReagendarConsultaRequest(
                novaDataHora,
                "Conflito com outro compromisso"
        );

        MvcResult reagendarResult = mockMvc.perform(post("/v1/agendamentos/{id}/reagendar", consultaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reagendarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consultaOriginalId", notNullValue()))
                .andExpect(jsonPath("$.consultaNovaaId", notNullValue()))
                .andExpect(jsonPath("$.totalReagendamentos", is(1)))
                .andExpect(jsonPath("$.motivo", is("Conflito com outro compromisso")))
                .andReturn();

        // 3. Validar resposta
        var responseContent = reagendarResult.getResponse().getContentAsString();
        org.junit.jupiter.api.Assertions.assertTrue(responseContent.contains("reagendada com sucesso"));
    }

    @Test
    void testReagendarMenosDe24Horas() throws Exception {
        // 1. Criar consulta para próximas 10 horas
        LocalDateTime proximasHoras = LocalDateTime.now().plusHours(10);
        var consultaRequest = new com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto.ConsultaRequest();
        consultaRequest.setPacienteId(pacienteId);
        consultaRequest.setProfissionalId(profissionalId);
        consultaRequest.setDataHora(proximasHoras);
        consultaRequest.setDescricao("Consulta próxima");

        MvcResult criarResult = mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest)))
                .andExpect(status().isOk())
                .andReturn();

        consultaId = UUID.fromString(
                objectMapper.readTree(criarResult.getResponse().getContentAsString())
                        .get("id").asText()
        );

        // 2. Tentar reagendar (deve falhar)
        LocalDateTime novaDataHora = proximasHoras.plusHours(2);
        ReagendarConsultaRequest reagendarRequest = new ReagendarConsultaRequest(
                novaDataHora,
                "Conflito"
        );

        mockMvc.perform(post("/v1/agendamentos/{id}/reagendar", consultaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reagendarRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testReagendarDataNoPassado() throws Exception {
        // 1. Criar consulta válida
        var consultaRequest = criarConsultaRequest();
        MvcResult criarResult = mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest)))
                .andExpect(status().isOk())
                .andReturn();

        consultaId = UUID.fromString(
                objectMapper.readTree(criarResult.getResponse().getContentAsString())
                        .get("id").asText()
        );

        // 2. Tentar reagendar para data no passado
        LocalDateTime dataPassada = LocalDateTime.now().minusDays(1);
        ReagendarConsultaRequest reagendarRequest = new ReagendarConsultaRequest(
                dataPassada,
                "Conflito"
        );

        mockMvc.perform(post("/v1/agendamentos/{id}/reagendar", consultaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reagendarRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testReagendarForaDoHorarioComercial() throws Exception {
        // 1. Criar consulta válida
        var consultaRequest = criarConsultaRequest();
        MvcResult criarResult = mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest)))
                .andExpect(status().isOk())
                .andReturn();

        consultaId = UUID.fromString(
                objectMapper.readTree(criarResult.getResponse().getContentAsString())
                        .get("id").asText()
        );

        // 2. Tentar reagendar para fora do horário comercial (22:00)
        LocalDateTime dataForaHorario = dataHora.withHour(22).withMinute(0);
        ReagendarConsultaRequest reagendarRequest = new ReagendarConsultaRequest(
                dataForaHorario,
                "Conflito"
        );

        mockMvc.perform(post("/v1/agendamentos/{id}/reagendar", consultaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reagendarRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testVerHistoricoReagendamentos() throws Exception {
        // 1. Criar uma consulta
        var consultaRequest = criarConsultaRequest();
        MvcResult criarResult = mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest)))
                .andExpect(status().isOk())
                .andReturn();

        consultaId = UUID.fromString(
                objectMapper.readTree(criarResult.getResponse().getContentAsString())
                        .get("id").asText()
        );

        // 2. Buscar histórico (vazio no início)
        mockMvc.perform(get("/v1/agendamentos/{id}/reagendamentos", consultaId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testReagendarConsultaNaoEncontrada() throws Exception {
        UUID consultaInexistente = UUID.randomUUID();
        LocalDateTime novaDataHora = dataHora.plusDays(2);

        ReagendarConsultaRequest reagendarRequest = new ReagendarConsultaRequest(
                novaDataHora,
                "Conflito"
        );

        mockMvc.perform(post("/v1/agendamentos/{id}/reagendar", consultaInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reagendarRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFluxoCompletoReagendamento() throws Exception {
        // 1. Criar primeira consulta
        var consultaRequest1 = criarConsultaRequest();
        MvcResult criarResult1 = mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest1)))
                .andExpect(status().isOk())
                .andReturn();

        UUID consultaId1 = UUID.fromString(
                objectMapper.readTree(criarResult1.getResponse().getContentAsString())
                        .get("id").asText()
        );

        // 2. Criar segunda consulta
        LocalDateTime dataHora2 = dataHora.plusDays(1);
        var consultaRequest2 = criarConsultaRequest(dataHora2);
        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest2)))
                .andExpect(status().isOk());

        // 3. Buscar primeira consulta
        mockMvc.perform(get("/v1/agendamentos/{id}", consultaId1))
                .andExpect(status().isOk());

        // 4. Listar todas
        mockMvc.perform(get("/v1/agendamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));

        // 5. Reagendar primeira
        LocalDateTime novaData = dataHora.plusDays(3);
        ReagendarConsultaRequest reagendarRequest = new ReagendarConsultaRequest(
                novaData,
                "Ajuste de agenda"
        );

        mockMvc.perform(post("/v1/agendamentos/{id}/reagendar", consultaId1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reagendarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReagendamentos", is(1)));
    }

    // Helper methods
    private com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto.ConsultaRequest criarConsultaRequest() {
        return criarConsultaRequest(dataHora);
    }

    private com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto.ConsultaRequest criarConsultaRequest(LocalDateTime dataHora) {
        var request = new com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto.ConsultaRequest();
        request.setPacienteId(pacienteId);
        request.setProfissionalId(profissionalId);
        request.setDataHora(dataHora);
        request.setDescricao("Consulta de teste");
        return request;
    }
}
