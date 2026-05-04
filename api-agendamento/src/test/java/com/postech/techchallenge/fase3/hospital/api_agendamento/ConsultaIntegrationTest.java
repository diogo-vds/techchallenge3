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
    void testConflitHorariosAoCriarConsulta() throws Exception {
        // 1. Criar primeira consulta
        ConsultaRequest request1 = new ConsultaRequest();
        request1.setPacienteId(pacienteId);
        request1.setProfissionalId(profissionalId);
        request1.setDataHora(dataHora);
        request1.setDescricao(descricao);

        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        // 2. Tentar criar segunda consulta com mesmo profissional e mesmo horário
        ConsultaRequest request2 = new ConsultaRequest();
        request2.setPacienteId(UUID.randomUUID()); // paciente diferente
        request2.setProfissionalId(profissionalId); // mesmo profissional
        request2.setDataHora(dataHora); // mesmo horário
        request2.setDescricao("Consulta conflitante");

        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMesmoHorarioDiferentesProfissionais() throws Exception {
        // 1. Criar primeira consulta
        ConsultaRequest request1 = new ConsultaRequest();
        request1.setPacienteId(pacienteId);
        request1.setProfissionalId(profissionalId);
        request1.setDataHora(dataHora);
        request1.setDescricao(descricao);

        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        // 2. Criar segunda consulta com horário igual mas profissional diferente - DEVE FUNCIONAR
        ConsultaRequest request2 = new ConsultaRequest();
        request2.setPacienteId(UUID.randomUUID());
        request2.setProfissionalId(UUID.randomUUID()); // profissional DIFERENTE
        request2.setDataHora(dataHora); // mesmo horário
        request2.setDescricao("Consulta OK");

        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk()); // deve permitir
    }

    @Test
    void testAtualizarConsultaParaHorarioOcupado() throws Exception {
        // 1. Criar primeira consulta
        ConsultaRequest request1 = new ConsultaRequest();
        request1.setPacienteId(pacienteId);
        request1.setProfissionalId(profissionalId);
        request1.setDataHora(dataHora);
        request1.setDescricao(descricao);

        MvcResult result1 = mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk())
                .andReturn();

        String consultaId1 = objectMapper.readTree(result1.getResponse().getContentAsString())
                .get("id").asText();

        // 2. Criar segunda consulta com horário diferente
        LocalDateTime dataHora2 = dataHora.plusDays(1);
        ConsultaRequest request2 = new ConsultaRequest();
        request2.setPacienteId(UUID.randomUUID());
        request2.setProfissionalId(profissionalId);
        request2.setDataHora(dataHora2);
        request2.setDescricao("Consulta 2");

        MvcResult result2 = mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andReturn();

        String consultaId2 = objectMapper.readTree(result2.getResponse().getContentAsString())
                .get("id").asText();

        // 3. Tentar atualizar segunda consulta para o horário da primeira (deve falhar)
        ConsultaUpdateRequest updateRequest = new ConsultaUpdateRequest();
        updateRequest.setDataHora(dataHora); // horário da primeira consulta
        updateRequest.setDescricao("Tentativa de conflito");

        mockMvc.perform(put("/v1/agendamentos/{id}", consultaId2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAtualizarConsultaParaMesmoDtHora() throws Exception {
        // Criar consulta
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

        // Atualizar só a descrição (mesma data/hora) - DEVE FUNCIONAR mesmo que tenha "conflito"
        ConsultaUpdateRequest updateRequest = new ConsultaUpdateRequest();
        updateRequest.setDataHora(dataHora); // mesma data/hora
        updateRequest.setDescricao("Descrição atualizada");

        mockMvc.perform(put("/v1/agendamentos/{id}", consultaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao", is("Descrição atualizada")));
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

    @Test
    void testCriarConsultaComDataNoPAssado() throws Exception {
        // Tentar criar consulta com data no passado
        ConsultaRequest request = new ConsultaRequest();
        request.setPacienteId(pacienteId);
        request.setProfissionalId(profissionalId);
        request.setDataHora(LocalDateTime.now().minusDays(1)); // Data no passado
        request.setDescricao(descricao);

        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCriarConsultaComDataComSegundoNoPassado() throws Exception {
        // Tentar criar consulta com data/hora alguns segundos no passado
        ConsultaRequest request = new ConsultaRequest();
        request.setPacienteId(pacienteId);
        request.setProfissionalId(profissionalId);
        request.setDataHora(LocalDateTime.now().minusSeconds(30)); // Alguns segundos atrás
        request.setDescricao(descricao);

        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAtualizarConsultaParaDataNoPAssado() throws Exception {
        // 1. Criar consulta com data futura
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

        // 2. Tentar atualizar para data no passado
        ConsultaUpdateRequest updateRequest = new ConsultaUpdateRequest();
        updateRequest.setDataHora(LocalDateTime.now().minusDays(1)); // Data no passado
        updateRequest.setDescricao("Descrição atualizada");

        mockMvc.perform(put("/v1/agendamentos/{id}", consultaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCriarConsultaComDataFutura() throws Exception {
        // Criar consulta com data no futuro - deve funcionar
        ConsultaRequest request = new ConsultaRequest();
        request.setPacienteId(pacienteId);
        request.setProfissionalId(profissionalId);
        request.setDataHora(LocalDateTime.now().plusDays(7)); // Data bem no futuro
        request.setDescricao(descricao);

        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataHora", notNullValue()));
    }

    @Test
    void testAtualizarConsultaParaDataFuturaValida() throws Exception {
        // 1. Criar consulta
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

        // 2. Atualizar para outra data no futuro - deve funcionar
        LocalDateTime novaData = dataHora.plusDays(5);
        ConsultaUpdateRequest updateRequest = new ConsultaUpdateRequest();
        updateRequest.setDataHora(novaData);
        updateRequest.setDescricao("Data atualizada");

        mockMvc.perform(put("/v1/agendamentos/{id}", consultaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataHora", notNullValue()));
    }

    @Test
    void testCriarConsultaForaDoHorarioComercial() throws Exception {
        // Tentar criar consulta fora do horário comercial (22:00)
        LocalDateTime dataHoraForaHorario = LocalDateTime.now()
                .plusDays(1)
                .withHour(22)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        ConsultaRequest request = new ConsultaRequest();
        request.setPacienteId(pacienteId);
        request.setProfissionalId(profissionalId);
        request.setDataHora(dataHoraForaHorario);
        request.setDescricao(descricao);

        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCriarConsultaMuitoCedoForaHorarioComercial() throws Exception {
        // Tentar criar consulta muito cedo (07:00)
        LocalDateTime dataHoraForaHorario = LocalDateTime.now()
                .plusDays(1)
                .withHour(7)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        ConsultaRequest request = new ConsultaRequest();
        request.setPacienteId(pacienteId);
        request.setProfissionalId(profissionalId);
        request.setDataHora(dataHoraForaHorario);
        request.setDescricao(descricao);

        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCriarConsultaEmSabado() throws Exception {
        // Tentar criar consulta em sábado
        LocalDateTime proximoSabado = LocalDateTime.now().plusDays(1);
        while (proximoSabado.getDayOfWeek().getValue() != 6) { // 6 = sábado
            proximoSabado = proximoSabado.plusDays(1);
        }
        proximoSabado = proximoSabado.withHour(14).withMinute(0);

        ConsultaRequest request = new ConsultaRequest();
        request.setPacienteId(pacienteId);
        request.setProfissionalId(profissionalId);
        request.setDataHora(proximoSabado);
        request.setDescricao(descricao);

        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCriarConsultaEmDomingo() throws Exception {
        // Tentar criar consulta em domingo
        LocalDateTime proximoDomingo = LocalDateTime.now().plusDays(1);
        while (proximoDomingo.getDayOfWeek().getValue() != 7) { // 7 = domingo
            proximoDomingo = proximoDomingo.plusDays(1);
        }
        proximoDomingo = proximoDomingo.withHour(14).withMinute(0);

        ConsultaRequest request = new ConsultaRequest();
        request.setPacienteId(pacienteId);
        request.setProfissionalId(profissionalId);
        request.setDataHora(proximoDomingo);
        request.setDescricao(descricao);

        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCriarConsultaDuranteHorarioComercial() throws Exception {
        // Criar consulta dentro do horário comercial (14:00) em um dia útil
        LocalDateTime dataHorasComercial = LocalDateTime.now()
                .plusDays(1)
                .withHour(14)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        // Garantir que não seja fim de semana
        while (dataHorasComercial.getDayOfWeek().getValue() >= 6) {
            dataHorasComercial = dataHorasComercial.plusDays(1);
        }

        ConsultaRequest request = new ConsultaRequest();
        request.setPacienteId(pacienteId);
        request.setProfissionalId(profissionalId);
        request.setDataHora(dataHorasComercial);
        request.setDescricao(descricao);

        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void testCriarConsultaPacienteMesmoHorario() throws Exception {
        // 1. Criar primeira consulta para o paciente
        var consultaRequest1 = criarConsultaRequest();
        MvcResult criarResult1 = mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest1)))
                .andExpect(status().isOk())
                .andReturn();

        // 2. Tentar criar segunda consulta no mesmo horário para o mesmo paciente
        var consultaRequest2 = criarConsultaRequest(dataHora); // Mesmo horário
        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCriarConsultaPacienteLimiteConsultasSimultaneas() throws Exception {
        // 1. Criar 3 consultas para o mesmo paciente
        for (int i = 0; i < 3; i++) {
            LocalDateTime dataHoraConsulta = dataHora.plusDays(i + 1);
            var consultaRequest = criarConsultaRequest(dataHoraConsulta);
            mockMvc.perform(post("/v1/agendamentos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(consultaRequest)))
                    .andExpect(status().isOk());
        }

        // 2. Tentar criar 4ª consulta (deve falhar)
        LocalDateTime dataHoraQuarta = dataHora.plusDays(4);
        var consultaRequest4 = criarConsultaRequest(dataHoraQuarta);
        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest4)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCriarConsultaPacienteDiferentesHorarios() throws Exception {
        // 1. Criar primeira consulta
        var consultaRequest1 = criarConsultaRequest();
        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest1)))
                .andExpect(status().isOk());

        // 2. Criar segunda consulta em horário diferente (deve funcionar)
        LocalDateTime dataHora2 = dataHora.plusHours(2);
        var consultaRequest2 = criarConsultaRequest(dataHora2);
        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest2)))
                .andExpect(status().isOk());
    }

    @Test
    void testAtualizarConsultaPacienteConflitoHorario() throws Exception {
        // 1. Criar primeira consulta
        var consultaRequest1 = criarConsultaRequest();
        MvcResult criarResult1 = mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest1)))
                .andExpect(status().isOk())
                .andReturn();

        String consultaId1 = objectMapper.readTree(criarResult1.getResponse().getContentAsString())
                .get("id").asText();

        // 2. Criar segunda consulta em horário diferente
        LocalDateTime dataHora2 = dataHora.plusDays(1);
        var consultaRequest2 = criarConsultaRequest(dataHora2);
        MvcResult criarResult2 = mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest2)))
                .andExpect(status().isOk())
                .andReturn();

        String consultaId2 = objectMapper.readTree(criarResult2.getResponse().getContentAsString())
                .get("id").asText();

        // 3. Tentar atualizar segunda consulta para o horário da primeira (deve falhar)
        ConsultaUpdateRequest updateRequest = new ConsultaUpdateRequest();
        updateRequest.setDataHora(dataHora); // Horário da primeira consulta
        updateRequest.setDescricao("Tentativa de conflito");

        mockMvc.perform(put("/v1/agendamentos/{id}", consultaId2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPacientePodeTerConsultasComProfissionaisDiferentes() throws Exception {
        // 1. Criar consulta com primeiro profissional
        var consultaRequest1 = criarConsultaRequest();
        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest1)))
                .andExpect(status().isOk());

        // 2. Criar consulta no mesmo horário com profissional diferente (deve funcionar)
        UUID profissionalId2 = UUID.randomUUID();
        var consultaRequest2 = new com.postech.techchallenge.fase3.hospital.api_agendamento.consulta.adapters.in.web.dto.ConsultaRequest();
        consultaRequest2.setPacienteId(pacienteId); // Mesmo paciente
        consultaRequest2.setProfissionalId(profissionalId2); // Profissional diferente
        consultaRequest2.setDataHora(dataHora); // Mesmo horário
        consultaRequest2.setDescricao("Consulta com outro profissional");

        mockMvc.perform(post("/v1/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequest2)))
                .andExpect(status().isBadRequest()); // Deve falhar por conflito de paciente
    }

    private ConsultaRequest criarConsultaRequest() {
        return criarConsultaRequest(LocalDateTime.now().plusDays(1));
    }

    private ConsultaRequest criarConsultaRequest(LocalDateTime dataHora) {
        var req = new ConsultaRequest();
        req.setPacienteId(UUID.randomUUID());
        req.setProfissionalId(UUID.randomUUID());
        req.setDataHora(dataHora);
        req.setDescricao("Teste");
        return req;
    }
}
