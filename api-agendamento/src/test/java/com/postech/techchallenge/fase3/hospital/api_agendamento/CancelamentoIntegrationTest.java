package com.postech.techchallenge.fase3.hospital.api_agendamento;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto.CancelarConsultaRequest;
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
class CancelamentoIntegrationTest {

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
        dataHora = LocalDateTime.now().plusHours(4); // 4 horas no futuro
    }

    @Test
    void testCancelarComSucesso() throws Exception {
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

        // 2. Cancelar a consulta
        CancelarConsultaRequest cancelarRequest = new CancelarConsultaRequest(
                "Paciente não pode comparecer"
        );

        MvcResult cancelarResult = mockMvc.perform(post("/v1/agendamentos/{id}/cancelar", consultaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consultaId", notNullValue()))
                .andExpect(jsonPath("$.dataHoraOriginal", notNullValue()))
                .andExpect(jsonPath("$.dataCancelamento", notNullValue()))
                .andExpect(jsonPath("$.motivo", is("Paciente não pode comparecer")))
                .andExpect(jsonPath("$.mensagem", is("Consulta cancelada com sucesso")))
                .andReturn();

        // 3. Verificar que a consulta foi cancelada
        mockMvc.perform(get("/v1/agendamentos/{id}", consultaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativa", is(false)))
                .andExpect(jsonPath("$.motivoCancelamento", is("Paciente não pode comparecer")));
    }

    @Test
    void testCancelarMenosDe2HorasAntecedencia() throws Exception {
        // 1. Criar consulta para próximas 1.5 horas
        LocalDateTime dataHoraProxima = LocalDateTime.now().plusMinutes(90); // 1h30
        var consultaRequest = criarConsultaRequest(dataHoraProxima);
        MvcResult criarResult = mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest)))
                .andExpect(status().isOk())
                .andReturn();

        consultaId = UUID.fromString(
                objectMapper.readTree(criarResult.getResponse().getContentAsString())
                        .get("id").asText()
        );

        // 2. Tentar cancelar (deve falhar)
        CancelarConsultaRequest cancelarRequest = new CancelarConsultaRequest(
                "Cancelamento tardio"
        );

        mockMvc.perform(post("/v1/agendamentos/{id}/cancelar", consultaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelarRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCancelarConsultaJaCancelada() throws Exception {
        // 1. Criar consulta
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

        // 2. Cancelar primeira vez
        CancelarConsultaRequest cancelarRequest1 = new CancelarConsultaRequest(
                "Primeiro cancelamento"
        );

        mockMvc.perform(post("/v1/agendamentos/{id}/cancelar", consultaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelarRequest1)))
                .andExpect(status().isOk());

        // 3. Tentar cancelar novamente (deve falhar)
        CancelarConsultaRequest cancelarRequest2 = new CancelarConsultaRequest(
                "Segundo cancelamento"
        );

        mockMvc.perform(post("/v1/agendamentos/{id}/cancelar", consultaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelarRequest2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCancelarConsultaNaoEncontrada() throws Exception {
        UUID consultaInexistente = UUID.randomUUID();
        CancelarConsultaRequest cancelarRequest = new CancelarConsultaRequest(
                "Consulta não existe"
        );

        mockMvc.perform(post("/v1/agendamentos/{id}/cancelar", consultaInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelarRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCancelarExatamente2HorasAntecedencia() throws Exception {
        // 1. Criar consulta para exatamente 2 horas
        LocalDateTime dataHoraExata = LocalDateTime.now().plusHours(2);
        var consultaRequest = criarConsultaRequest(dataHoraExata);
        MvcResult criarResult = mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest)))
                .andExpect(status().isOk())
                .andReturn();

        consultaId = UUID.fromString(
                objectMapper.readTree(criarResult.getResponse().getContentAsString())
                        .get("id").asText()
        );

        // 2. Cancelar (deve funcionar - limite exato)
        CancelarConsultaRequest cancelarRequest = new CancelarConsultaRequest(
                "Cancelamento no limite"
        );

        mockMvc.perform(post("/v1/agendamentos/{id}/cancelar", consultaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motivo", is("Cancelamento no limite")));
    }

    @Test
    void testCancelarComAntecedenciaMaior() throws Exception {
        // 1. Criar consulta para 6 horas no futuro
        LocalDateTime dataHoraLonge = LocalDateTime.now().plusHours(6);
        var consultaRequest = criarConsultaRequest(dataHoraLonge);
        MvcResult criarResult = mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest)))
                .andExpect(status().isOk())
                .andReturn();

        consultaId = UUID.fromString(
                objectMapper.readTree(criarResult.getResponse().getContentAsString())
                        .get("id").asText()
        );

        // 2. Cancelar (deve funcionar - antecedência suficiente)
        CancelarConsultaRequest cancelarRequest = new CancelarConsultaRequest(
                "Cancelamento antecipado"
        );

        mockMvc.perform(post("/v1/agendamentos/{id}/cancelar", consultaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motivo", is("Cancelamento antecipado")));
    }

    @Test
    void testFluxoCompletoCancelamento() throws Exception {
        // 1. Criar consulta
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

        // 2. Verificar que está ativa
        mockMvc.perform(get("/v1/agendamentos/{id}", consultaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativa", is(true)));

        // 3. Cancelar
        CancelarConsultaRequest cancelarRequest = new CancelarConsultaRequest(
                "Motivo de teste"
        );

        mockMvc.perform(post("/v1/agendamentos/{id}/cancelar", consultaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelarRequest)))
                .andExpect(status().isOk());

        // 4. Verificar que foi cancelada
        mockMvc.perform(get("/v1/agendamentos/{id}", consultaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativa", is(false)))
                .andExpect(jsonPath("$.motivoCancelamento", is("Motivo de teste")))
                .andExpect(jsonPath("$.dataCancelamento", notNullValue()));
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

