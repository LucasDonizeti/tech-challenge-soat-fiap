package com.techchallenge.oficina.os.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.oficina.os.application.usecases.*;
import com.techchallenge.oficina.os.application.usecases.commands.*;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.web.dto.CriarOrdemServicoRequest;
import com.techchallenge.oficina.os.web.dto.DecisaoOrcamentoRequest;
import com.techchallenge.oficina.os.web.dto.OrdemServicoResponseDto;
import com.techchallenge.oficina.os.web.mappers.OrdemServicoWebMapper;
import com.techchallenge.oficina.os.web.presenters.OrdemServicoPresenter;
import com.techchallenge.oficina.sharedkernel.common.PageableValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Testes Unitários - OrdemServicoController")
class OrdemServicoControllerTest {

    @Mock
    private CriarOrdemServicoUseCase criarOrdemServicoUseCase;

    @Mock
    private BuscarOrdemServicoUseCase buscarOrdemServicoUseCase;

    @Mock
    private ListarOrdensServicoUseCase listarOrdensServicoUseCase;

    @Mock
    private AprovarOrcamentoUseCase aprovarOrcamentoUseCase;

    @Mock
    private RecusarOrcamentoUseCase recusarOrcamentoUseCase;

    @Mock
    private EnviarOrcamentoAoClienteUseCase enviarOrcamentoAoClienteUseCase;

    @Mock
    private EntregarOrdemServicoUseCase entregarOrdemServicoUseCase;

    @Mock
    private OrdemServicoPresenter presenter;

    @Mock
    private PageableValidator pageableValidator;

    @InjectMocks
    private OrdemServicoController ordemServicoController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();
        mockMvc = MockMvcBuilders.standaloneSetup(ordemServicoController)
                .setCustomArgumentResolvers(pageableResolver)
                .build();
        objectMapper = new ObjectMapper();
        
        // Mock pageableValidator to return the same pageable
        when(pageableValidator.validate(any(Pageable.class), anySet())).thenAnswer(invocation -> invocation.getArgument(0));
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

    @Test
    @DisplayName("Deve criar ordem de serviço com sucesso")
    void deveCriarOrdemServicoComSucesso() throws Exception {
        // Arrange
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(UUID.randomUUID())
                .status(StatusOS.RECEBIDA.toString())
                .valorTotal(new BigDecimal("150.00"))
                .build();

        OrdemServicoResponseDto dto = new OrdemServicoResponseDto();
        dto.setId(response.getId());
        dto.setStatus(StatusOS.RECEBIDA.toString());
        dto.setValorTotal(new BigDecimal("150.00"));

        CriarOrdemServicoRequest request = new CriarOrdemServicoRequest();
        request.setCpfOuCnpj("52998224725");
        request.setPlaca("ABC1234");
        request.setCodigosServico(List.of("SVC001"));
        request.setItensMRO(List.of());

        when(criarOrdemServicoUseCase.execute(any(CriarOrdemServicoCommand.class))).thenReturn(response);
        when(presenter.prepararViewModel(response)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(post("/v1/os")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value(StatusOS.RECEBIDA.toString()));
    }

    @Test
    @DisplayName("Deve buscar ordem de serviço por ID com sucesso")
    void deveBuscarOrdemServicoPorIdComSucesso() throws Exception {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .status(StatusOS.EM_DIAGNOSTICO.toString())
                .valorTotal(new BigDecimal("200.00"))
                .build();

        OrdemServicoResponseDto dto = new OrdemServicoResponseDto();
        dto.setId(ordemServicoId);
        dto.setStatus(StatusOS.EM_DIAGNOSTICO.toString());

        when(buscarOrdemServicoUseCase.execute(ordemServicoId)).thenReturn(response);
        when(presenter.prepararViewModel(response)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/v1/os/{id}", ordemServicoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ordemServicoId.toString()))
                .andExpect(jsonPath("$.status").value(StatusOS.EM_DIAGNOSTICO.toString()));
    }

    @Test
    @DisplayName("Deve listar ordens de serviço com sucesso")
    void deveListarOrdensServicoComSucesso() throws Exception {
        // Arrange
        UUID osId = UUID.randomUUID();
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(osId)
                .status(StatusOS.RECEBIDA.toString())
                .valorTotal(new BigDecimal("150.00"))
                .build();

        OrdemServicoResponseDto dto = new OrdemServicoResponseDto();
        dto.setId(osId);
        dto.setStatus(StatusOS.RECEBIDA.toString());

        Page<OrdemServicoResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);
        Page<OrdemServicoResponseDto> dtoPage = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        when(listarOrdensServicoUseCase.execute(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);
        when(presenter.prepararViewModelPage(page)).thenReturn(dtoPage);

        // Act & Assert
        mockMvc.perform(get("/v1/os")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Deve processar decisão de orçamento aprovado com sucesso")
    void deveProcessarDecisaoOrcamentoAprovadoComSucesso() throws Exception {
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

        DecisaoOrcamentoRequest request = new DecisaoOrcamentoRequest();
        request.setDecisao(DecisaoOrcamentoRequest.Decisao.APROVADO);
        request.setMotivo(null);

        when(aprovarOrcamentoUseCase.execute(any(AprovarOrcamentoCommand.class))).thenReturn(response);
        when(presenter.prepararViewModel(response)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(post("/v1/os/{id}/decisao-orcamento", ordemServicoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ordemServicoId.toString()))
                .andExpect(jsonPath("$.status").value(StatusOS.EM_EXECUCAO.toString()));
    }

    @Test
    @DisplayName("Deve processar decisão de orçamento recusado com sucesso")
    void deveProcessarDecisaoOrcamentoRecusadoComSucesso() throws Exception {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(ordemServicoId)
                .status(StatusOS.CANCELADA.toString())
                .valorTotal(new BigDecimal("150.00"))
                .build();

        OrdemServicoResponseDto dto = new OrdemServicoResponseDto();
        dto.setId(ordemServicoId);
        dto.setStatus(StatusOS.CANCELADA.toString());

        DecisaoOrcamentoRequest request = new DecisaoOrcamentoRequest();
        request.setDecisao(DecisaoOrcamentoRequest.Decisao.RECUSADO);
        request.setMotivo("Preço muito alto");

        when(recusarOrcamentoUseCase.execute(any(RecusarOrcamentoCommand.class))).thenReturn(response);
        when(presenter.prepararViewModel(response)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(post("/v1/os/{id}/decisao-orcamento", ordemServicoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ordemServicoId.toString()))
                .andExpect(jsonPath("$.status").value(StatusOS.CANCELADA.toString()));
    }

    @Test
    @DisplayName("Deve listar ordens de serviço com filtros")
    void deveListarOrdensServicoComFiltros() throws Exception {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        UUID osId = UUID.randomUUID();
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(osId)
                .status(StatusOS.FINALIZADA.toString())
                .valorTotal(new BigDecimal("300.00"))
                .build();

        OrdemServicoResponseDto dto = new OrdemServicoResponseDto();
        dto.setId(osId);
        dto.setStatus(StatusOS.FINALIZADA.toString());

        Page<OrdemServicoResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);
        Page<OrdemServicoResponseDto> dtoPage = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        when(listarOrdensServicoUseCase.execute(
                eq(clienteId), any(), eq(StatusOS.FINALIZADA), any(), any(), any(Pageable.class)))
                .thenReturn(page);
        when(presenter.prepararViewModelPage(page)).thenReturn(dtoPage);

        // Act & Assert
        mockMvc.perform(get("/v1/os")
                        .param("clienteId", clienteId.toString())
                        .param("status", "FINALIZADA")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
