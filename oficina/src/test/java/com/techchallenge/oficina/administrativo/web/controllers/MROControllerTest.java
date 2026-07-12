package com.techchallenge.oficina.administrativo.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.oficina.administrativo.application.usecases.*;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
import com.techchallenge.oficina.administrativo.web.dto.*;
import com.techchallenge.oficina.administrativo.web.mappers.MROWebMapper;
import com.techchallenge.oficina.administrativo.web.presenters.MROPresenter;
import com.techchallenge.oficina.sharedkernel.common.PageableValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de MROController - Web Layer")
class MROControllerTest {

    @Mock
    private CriarMROUseCase criarMROUseCase;

    @Mock
    private BuscarMROUseCase buscarMROUseCase;

    @Mock
    private ListarMROsUseCase listarMROsUseCase;

    @Mock
    private InativarMROUseCase inativarMROUseCase;

    @Mock
    private AtivarMROUseCase ativarMROUseCase;

    @Mock
    private AtualizarPrecoMROUseCase atualizarPrecoMROUseCase;

    @Mock
    private AtualizarDadosMROUseCase atualizarDadosMROUseCase;

    @Mock
    private ReporEstoqueMROUseCase reporEstoqueMROUseCase;

    @Mock
    private DebitarEstoqueMROUseCase debitarEstoqueMROUseCase;

    @Mock
    private MROPresenter presenter;

    @Mock
    private PageableValidator pageableValidator;

    @InjectMocks
    private MROController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UUID mroId;
    private MROResponse mroResponse;
    private MROResponseDto mroResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        mroId = UUID.randomUUID();
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime atualizadoEm = LocalDateTime.now();

        mroResponse = MROResponse.builder()
                .id(mroId)
                .nome("Óleo Motor 5W30")
                .descricao("Óleo para motor automotivo")
                .tipo("INSUMO")
                .quantidadeEstoque(100)
                .precoUnitario(new BigDecimal("45.90"))
                .ativo(true)
                .criadoEm(criadoEm)
                .atualizadoEm(atualizadoEm)
                .build();

        mroResponseDto = MROResponseDto.builder()
                .id(mroId)
                .nome("Óleo Motor 5W30")
                .descricao("Óleo para motor automotivo")
                .tipo("INSUMO")
                .quantidadeEstoque(100)
                .precoUnitario(new BigDecimal("45.90"))
                .ativo(true)
                .criadoEm(criadoEm)
                .atualizadoEm(atualizadoEm)
                .build();
    }

    @Test
    @DisplayName("Deve criar MRO com sucesso")
    void deveCriarMROComSucesso() throws Exception {
        // Arrange
        CriarMRORequest request = new CriarMRORequest();
        request.setNome("Óleo Motor 5W30");
        request.setDescricao("Óleo para motor automotivo");
        request.setTipo("INSUMO");
        request.setQuantidadeEstoque(100);
        request.setPrecoUnitario(new BigDecimal("45.90"));

        when(criarMROUseCase.execute(any())).thenReturn(mroResponse);
        when(presenter.prepararViewModel(any(MROResponse.class))).thenReturn(mroResponseDto);

        // Act & Assert
        mockMvc.perform(post("/v1/admin/mros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(mroId.toString()))
                .andExpect(jsonPath("$.nome").value("Óleo Motor 5W30"))
                .andExpect(jsonPath("$.tipo").value("INSUMO"));

        verify(criarMROUseCase, times(1)).execute(any());
        verify(presenter, times(1)).prepararViewModel(any(MROResponse.class));
    }

    @Test
    @DisplayName("Deve buscar MRO por ID com sucesso")
    void deveBuscarMroPorIdComSucesso() throws Exception {
        // Arrange
        when(buscarMROUseCase.execute(mroId)).thenReturn(mroResponse);
        when(presenter.prepararViewModel(any(MROResponse.class))).thenReturn(mroResponseDto);

        // Act & Assert
        mockMvc.perform(get("/v1/admin/mros/{id}", mroId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mroId.toString()))
                .andExpect(jsonPath("$.nome").value("Óleo Motor 5W30"));

        verify(buscarMROUseCase, times(1)).execute(mroId);
        verify(presenter, times(1)).prepararViewModel(any(MROResponse.class));
    }

    @Test
    @DisplayName("Deve inativar MRO com sucesso")
    void deveInativarMroComSucesso() throws Exception {
        // Arrange
        MROResponse inativoResponse = MROResponse.builder()
                .id(mroId)
                .nome("Óleo Motor 5W30")
                .ativo(false)
                .build();
        MROResponseDto inativoDto = MROResponseDto.builder()
                .id(mroId)
                .nome("Óleo Motor 5W30")
                .ativo(false)
                .build();

        when(inativarMROUseCase.execute(mroId)).thenReturn(inativoResponse);
        when(presenter.prepararViewModel(any(MROResponse.class))).thenReturn(inativoDto);

        // Act & Assert
        mockMvc.perform(patch("/v1/admin/mros/{id}/inativar", mroId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));

        verify(inativarMROUseCase, times(1)).execute(mroId);
        verify(presenter, times(1)).prepararViewModel(any(MROResponse.class));
    }

    @Test
    @DisplayName("Deve ativar MRO com sucesso")
    void deveAtivarMroComSucesso() throws Exception {
        // Arrange
        when(ativarMROUseCase.execute(mroId)).thenReturn(mroResponse);
        when(presenter.prepararViewModel(any(MROResponse.class))).thenReturn(mroResponseDto);

        // Act & Assert
        mockMvc.perform(patch("/v1/admin/mros/{id}/ativar", mroId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(true));

        verify(ativarMROUseCase, times(1)).execute(mroId);
        verify(presenter, times(1)).prepararViewModel(any(MROResponse.class));
    }

    @Test
    @DisplayName("Deve atualizar preço do MRO com sucesso")
    void deveAtualizarPrecoDoMroComSucesso() throws Exception {
        // Arrange
        AtualizarPrecoMRORequest request = new AtualizarPrecoMRORequest();
        request.setNovoPrecoUnitario(new BigDecimal("50.00"));

        MROResponse responseComNovoPreco = MROResponse.builder()
                .id(mroId)
                .nome("Óleo Motor 5W30")
                .precoUnitario(new BigDecimal("50.00"))
                .build();
        MROResponseDto dtoComNovoPreco = MROResponseDto.builder()
                .id(mroId)
                .nome("Óleo Motor 5W30")
                .precoUnitario(new BigDecimal("50.00"))
                .build();

        when(atualizarPrecoMROUseCase.execute(any())).thenReturn(responseComNovoPreco);
        when(presenter.prepararViewModel(any(MROResponse.class))).thenReturn(dtoComNovoPreco);

        // Act & Assert
        mockMvc.perform(patch("/v1/admin/mros/{id}/preco", mroId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precoUnitario").value(50.00));

        verify(atualizarPrecoMROUseCase, times(1)).execute(any());
        verify(presenter, times(1)).prepararViewModel(any(MROResponse.class));
    }

    @Test
    @DisplayName("Deve atualizar dados do MRO com sucesso")
    void deveAtualizarDadosDoMroComSucesso() throws Exception {
        // Arrange
        AtualizarDadosMRORequest request = new AtualizarDadosMRORequest();
        request.setNome("Óleo Motor 10W40");
        request.setDescricao("Óleo para motor diesel");
        request.setTipo("INSUMO");

        MROResponse responseAtualizado = MROResponse.builder()
                .id(mroId)
                .nome("Óleo Motor 10W40")
                .descricao("Óleo para motor diesel")
                .tipo("INSUMO")
                .build();
        MROResponseDto dtoAtualizado = MROResponseDto.builder()
                .id(mroId)
                .nome("Óleo Motor 10W40")
                .descricao("Óleo para motor diesel")
                .tipo("INSUMO")
                .build();

        when(atualizarDadosMROUseCase.execute(any())).thenReturn(responseAtualizado);
        when(presenter.prepararViewModel(any(MROResponse.class))).thenReturn(dtoAtualizado);

        // Act & Assert
        mockMvc.perform(patch("/v1/admin/mros/{id}/dados", mroId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Óleo Motor 10W40"))
                .andExpect(jsonPath("$.descricao").value("Óleo para motor diesel"));

        verify(atualizarDadosMROUseCase, times(1)).execute(any());
        verify(presenter, times(1)).prepararViewModel(any(MROResponse.class));
    }

    @Test
    @DisplayName("Deve incrementar estoque do MRO com sucesso")
    void deveIncrementarEstoqueDoMroComSucesso() throws Exception {
        // Arrange
        IncrementarEstoqueMRORequest request = new IncrementarEstoqueMRORequest();
        request.setQuantidade(10);

        MROResponse responseComEstoqueIncrementado = MROResponse.builder()
                .id(mroId)
                .nome("Óleo Motor 5W30")
                .quantidadeEstoque(110)
                .build();
        MROResponseDto dtoComEstoqueIncrementado = MROResponseDto.builder()
                .id(mroId)
                .nome("Óleo Motor 5W30")
                .quantidadeEstoque(110)
                .build();

        when(reporEstoqueMROUseCase.execute(any())).thenReturn(responseComEstoqueIncrementado);
        when(presenter.prepararViewModel(any(MROResponse.class))).thenReturn(dtoComEstoqueIncrementado);

        // Act & Assert
        mockMvc.perform(post("/v1/admin/mros/{id}/estoque/incrementar", mroId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeEstoque").value(110));

        verify(reporEstoqueMROUseCase, times(1)).execute(any());
        verify(presenter, times(1)).prepararViewModel(any(MROResponse.class));
    }

    @Test
    @DisplayName("Deve retirar estoque do MRO com sucesso")
    void deveRetirarEstoqueDoMroComSucesso() throws Exception {
        // Arrange
        RetirarEstoqueMRORequest request = new RetirarEstoqueMRORequest();
        request.setQuantidade(5);

        MROResponse responseComEstoqueRetirado = MROResponse.builder()
                .id(mroId)
                .nome("Óleo Motor 5W30")
                .quantidadeEstoque(95)
                .build();
        MROResponseDto dtoComEstoqueRetirado = MROResponseDto.builder()
                .id(mroId)
                .nome("Óleo Motor 5W30")
                .quantidadeEstoque(95)
                .build();

        when(debitarEstoqueMROUseCase.execute(any())).thenReturn(responseComEstoqueRetirado);
        when(presenter.prepararViewModel(any(MROResponse.class))).thenReturn(dtoComEstoqueRetirado);

        // Act & Assert
        mockMvc.perform(post("/v1/admin/mros/{id}/estoque/retirar", mroId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeEstoque").value(95));

        verify(debitarEstoqueMROUseCase, times(1)).execute(any());
        verify(presenter, times(1)).prepararViewModel(any(MROResponse.class));
    }
}
