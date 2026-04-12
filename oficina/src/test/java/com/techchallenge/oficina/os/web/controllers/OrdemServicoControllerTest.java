package com.techchallenge.oficina.os.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Testes Unitários - OrdemServicoController")
class OrdemServicoControllerTest {

    @InjectMocks
    private OrdemServicoController ordemServicoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ordemServicoController).build();
    }

    @Test
    @DisplayName("Deve retornar status 200 e mensagem de health check")
    void deveRetornarHealthCheckComSucesso() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/v1/os/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("service is running"));
    }

    @Test
    @DisplayName("Deve retornar status 200 e resposta GG no endpoint ping")
    void deveRetornarPingComSucesso() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/v1/os/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("GG"));
    }
}
