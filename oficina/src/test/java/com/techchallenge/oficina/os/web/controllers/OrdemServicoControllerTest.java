package com.techchallenge.oficina.os.web.controllers;

import com.techchallenge.oficina.os.application.usecases.EnviarOrcamentoAoClienteUseCase;
import com.techchallenge.oficina.os.application.usecases.EntregarOrdemServicoUseCase;
import com.techchallenge.oficina.os.application.usecases.commands.EnviarOrcamentoAoClienteCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.web.dto.OrdemServicoResponseDto;
import com.techchallenge.oficina.os.web.mappers.OrdemServicoWebMapper;
import com.techchallenge.oficina.os.web.presenters.OrdemServicoPresenter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Testes Unitários - OrdemServicoController")
class OrdemServicoControllerTest {

    @Mock
    private EnviarOrcamentoAoClienteUseCase enviarOrcamentoAoClienteUseCase;

    @Mock
    private EntregarOrdemServicoUseCase entregarOrdemServicoUseCase;

    @Mock
    private OrdemServicoPresenter presenter;

    @InjectMocks
    private OrdemServicoController ordemServicoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ordemServicoController).build();
    }



    @Test
    @DisplayName("Deve enviar orçamento ao cliente com sucesso")
    void deveEnviarOrcamentoAoClienteComSucesso() throws Exception {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .status(StatusOS.AGUARDANDO_APROVACAO.toString())
                .valorTotal(new BigDecimal("150.00"))
                .build();

        OrdemServicoResponseDto dto = new OrdemServicoResponseDto();
        dto.setId(ordemServicoId);
        dto.setStatus(StatusOS.AGUARDANDO_APROVACAO.toString());

        when(enviarOrcamentoAoClienteUseCase.execute(any(EnviarOrcamentoAoClienteCommand.class))).thenReturn(response);
        when(presenter.prepararViewModel(response)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(post("/v1/os/{id}/enviar-orcamento-ao-cliente", ordemServicoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ordemServicoId.toString()))
                .andExpect(jsonPath("$.status").value(StatusOS.AGUARDANDO_APROVACAO.toString()));
    }

    @Test
    @DisplayName("Deve entregar ordem de serviço com sucesso")
    void deveEntregarOrdemServicoComSucesso() throws Exception {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .status(StatusOS.ENTREGUE.toString())
                .valorTotal(new BigDecimal("150.00"))
                .build();

        OrdemServicoResponseDto dto = new OrdemServicoResponseDto();
        dto.setId(ordemServicoId);
        dto.setStatus(StatusOS.ENTREGUE.toString());

        when(entregarOrdemServicoUseCase.execute(any())).thenReturn(response);
        when(presenter.prepararViewModel(response)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(post("/v1/os/{id}/entregar", ordemServicoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ordemServicoId.toString()))
                .andExpect(jsonPath("$.status").value(StatusOS.ENTREGUE.toString()));
    }
}
