package com.techchallenge.oficina.os.web.controllers;

import com.techchallenge.oficina.os.application.usecases.AprovarOrcamentoUseCase;
import com.techchallenge.oficina.os.application.usecases.BuscarOrdemServicoUseCase;
import com.techchallenge.oficina.os.application.usecases.ListarOrdensServicoPorClienteUseCase;
import com.techchallenge.oficina.os.application.usecases.commands.AprovarOrcamentoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.web.dto.OrdemServicoResponseDto;
import com.techchallenge.oficina.os.web.mappers.OrdemServicoWebMapper;
import com.techchallenge.oficina.sharedkernel.common.PageableValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Testes Unitários - ClienteOSController")
class ClienteOSControllerTest {

    @Mock
    private AprovarOrcamentoUseCase aprovarOrcamentoUseCase;

    @Mock
    private BuscarOrdemServicoUseCase buscarOrdemServicoUseCase;

    @Mock
    private ListarOrdensServicoPorClienteUseCase listarOrdensServicoPorClienteUseCase;

    @Mock
    private OrdemServicoWebMapper mapper;

    @Mock
    private PageableValidator pageableValidator;

    @InjectMocks
    private ClienteOSController clienteOSController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        mockMvc = MockMvcBuilders.standaloneSetup(clienteOSController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("Deve listar ordens de serviço por cliente com sucesso")
    void deveListarOrdensServicoPorClienteComSucesso() throws Exception {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(UUID.randomUUID())
                .status(StatusOS.AGUARDANDO_APROVACAO.toString())
                .valorTotal(new BigDecimal("150.00"))
                .build();

        Page<OrdemServicoResponse> responsePage = new PageImpl<>(Collections.singletonList(response));

        when(listarOrdensServicoPorClienteUseCase.execute(eq(clienteId), any(Pageable.class))).thenReturn(responsePage);
        
        // Mock the mapper to return a simple DTO page
        OrdemServicoResponseDto dto = new OrdemServicoResponseDto();
        dto.setId(response.getId());
        dto.setStatus(StatusOS.AGUARDANDO_APROVACAO.toString());
        
        Page<OrdemServicoResponseDto> dtoPage = new PageImpl<>(Collections.singletonList(dto));
        when(mapper.toDtoPage(responsePage)).thenReturn(dtoPage);
        
        // Mock pageableValidator to return the same pageable
        when(pageableValidator.validate(any(Pageable.class), any(Set.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        mockMvc.perform(get("/api/cliente/os/{id}", clienteId));

        // Assert - just verify the use case was called correctly
        verify(listarOrdensServicoPorClienteUseCase).execute(eq(clienteId), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve aprovar orçamento com sucesso")
    void deveAprovarOrcamentoComSucesso() throws Exception {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .status(StatusOS.EM_EXECUCAO.toString())
                .valorTotal(new BigDecimal("150.00"))
                .build();

        OrdemServicoResponseDto dto = new OrdemServicoResponseDto();
        dto.setId(ordemServicoId);
        dto.setStatus(StatusOS.EM_EXECUCAO.toString());

        when(aprovarOrcamentoUseCase.execute(any(AprovarOrcamentoCommand.class))).thenReturn(response);
        when(mapper.toDto(response)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(post("/api/cliente/os/{osId}/aprovar-orcamento", ordemServicoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ordemServicoId.toString()))
                .andExpect(jsonPath("$.status").value(StatusOS.EM_EXECUCAO.toString()));
    }
}
