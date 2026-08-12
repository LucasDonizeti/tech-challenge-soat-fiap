package com.techchallenge.oficina.administrativo.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.oficina.administrativo.application.usecases.*;
import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarDadosServicoCommand;
import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarPrecoServicoCommand;
import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarServicoCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.administrativo.web.dto.AtualizarDadosServicoRequest;
import com.techchallenge.oficina.administrativo.web.dto.AtualizarPrecoServicoRequest;
import com.techchallenge.oficina.administrativo.web.dto.CriarServicoRequest;
import com.techchallenge.oficina.administrativo.web.dto.ServicoResponseDto;
import com.techchallenge.oficina.administrativo.web.mappers.ServicoWebMapper;
import com.techchallenge.oficina.administrativo.web.presenters.ServicoPresenter;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - ServicoController")
class ServicoControllerTest {

    @Mock
    private CriarServicoUseCase criarServicoUseCase;

    @Mock
    private BuscarServicoUseCase buscarServicoUseCase;

    @Mock
    private ListarServicosUseCase listarServicosUseCase;

    @Mock
    private InativarServicoUseCase inativarServicoUseCase;

    @Mock
    private AtivarServicoUseCase ativarServicoUseCase;

    @Mock
    private AtualizarPrecoServicoUseCase atualizarPrecoServicoUseCase;

    @Mock
    private AtualizarDadosServicoUseCase atualizarDadosServicoUseCase;

    @Mock
    private ServicoPresenter presenter;

    @Mock
    private PageableValidator pageableValidator;

    @InjectMocks
    private ServicoController servicoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ServicoResponse servicoResponse;
    private ServicoResponseDto servicoResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(servicoController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();

        UUID id = UUID.randomUUID();
        servicoResponse = ServicoResponse.builder()
                .id(id)
                .nome("Troca de Óleo")
                .descricao("Troca de óleo do motor")
                .preco(new BigDecimal("150.00"))
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        servicoResponseDto = ServicoResponseDto.from(servicoResponse);
    }

    @Test
    @DisplayName("Deve criar serviço com sucesso")
    void deveCriarServico() throws Exception {
        // Arrange
        CriarServicoRequest request = new CriarServicoRequest();
        request.setNome("Troca de Óleo");
        request.setCodigo("SVC001");
        request.setDescricao("Troca de óleo do motor");
        request.setPreco(new BigDecimal("150.00"));

        when(criarServicoUseCase.execute(any(CriarServicoCommand.class))).thenReturn(servicoResponse);
        when(presenter.prepararViewModel(any(ServicoResponse.class))).thenReturn(servicoResponseDto);

        // Act & Assert
        mockMvc.perform(post("/v1/admin/servicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Troca de Óleo"))
                .andExpect(jsonPath("$.descricao").value("Troca de óleo do motor"))
                .andExpect(jsonPath("$.preco").value(150.00))
                .andExpect(jsonPath("$.ativo").value(true));

        verify(criarServicoUseCase, times(1)).execute(any(CriarServicoCommand.class));
        verify(presenter, times(1)).prepararViewModel(any(ServicoResponse.class));
    }

    @Test
    @DisplayName("Deve buscar serviço por ID com sucesso")
    void deveBuscarServicoPorId() throws Exception {
        // Arrange
        UUID servicoId = servicoResponse.getId();
        when(buscarServicoUseCase.execute(servicoId)).thenReturn(servicoResponse);
        when(presenter.prepararViewModel(any(ServicoResponse.class))).thenReturn(servicoResponseDto);

        // Act & Assert
        mockMvc.perform(get("/v1/admin/servicos/{id}", servicoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(servicoId.toString()))
                .andExpect(jsonPath("$.nome").value("Troca de Óleo"));

        verify(buscarServicoUseCase, times(1)).execute(servicoId);
        verify(presenter, times(1)).prepararViewModel(any(ServicoResponse.class));
    }

    @Test
    @DisplayName("Deve listar serviços com paginação")
    void deveListarServicos() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<ServicoResponse> responsePage = new PageImpl<>(List.of(servicoResponse), pageable, 1);
        Page<ServicoResponseDto> dtoPage = new PageImpl<>(List.of(servicoResponseDto), pageable, 1);

        when(pageableValidator.validate(any(Pageable.class), any())).thenReturn(pageable);
        when(listarServicosUseCase.execute(any(Pageable.class))).thenReturn(responsePage);
        when(presenter.prepararViewModelPage(any())).thenReturn(dtoPage);

        // Act & Assert
        mockMvc.perform(get("/v1/admin/servicos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nome").value("Troca de Óleo"));

        verify(pageableValidator, times(1)).validate(any(Pageable.class), any());
        verify(listarServicosUseCase, times(1)).execute(any(Pageable.class));
        verify(presenter, times(1)).prepararViewModelPage(any());
    }

    @Test
    @DisplayName("Deve inativar serviço com sucesso")
    void deveInativarServico() throws Exception {
        // Arrange
        UUID servicoId = servicoResponse.getId();
        ServicoResponse inativadoResponse = ServicoResponse.builder()
                .id(servicoId)
                .nome("Troca de Óleo")
                .descricao("Descrição")
                .preco(new BigDecimal("150.00"))
                .ativo(false)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        when(inativarServicoUseCase.execute(servicoId)).thenReturn(inativadoResponse);
        when(presenter.prepararViewModel(any(ServicoResponse.class))).thenReturn(servicoResponseDto);

        // Act & Assert
        mockMvc.perform(patch("/v1/admin/servicos/{id}/inativar", servicoId))
                .andExpect(status().isOk());

        verify(inativarServicoUseCase, times(1)).execute(servicoId);
        verify(presenter, times(1)).prepararViewModel(any(ServicoResponse.class));
    }

    @Test
    @DisplayName("Deve ativar serviço com sucesso")
    void deveAtivarServico() throws Exception {
        // Arrange
        UUID servicoId = servicoResponse.getId();
        when(ativarServicoUseCase.execute(servicoId)).thenReturn(servicoResponse);
        when(presenter.prepararViewModel(any(ServicoResponse.class))).thenReturn(servicoResponseDto);

        // Act & Assert
        mockMvc.perform(patch("/v1/admin/servicos/{id}/ativar", servicoId))
                .andExpect(status().isOk());

        verify(ativarServicoUseCase, times(1)).execute(servicoId);
        verify(presenter, times(1)).prepararViewModel(any(ServicoResponse.class));
    }

    @Test
    @DisplayName("Deve atualizar preço com sucesso")
    void deveAtualizarPreco() throws Exception {
        // Arrange
        UUID servicoId = servicoResponse.getId();
        AtualizarPrecoServicoRequest request = new AtualizarPrecoServicoRequest();
        request.setNovoPreco(new BigDecimal("200.00"));

        ServicoResponse atualizadoResponse = ServicoResponse.builder()
                .id(servicoId)
                .nome("Troca de Óleo")
                .descricao("Descrição")
                .preco(new BigDecimal("200.00"))
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        when(atualizarPrecoServicoUseCase.execute(eq(servicoId), any(AtualizarPrecoServicoCommand.class))).thenReturn(atualizadoResponse);
        when(presenter.prepararViewModel(any(ServicoResponse.class))).thenReturn(servicoResponseDto);

        // Act & Assert
        mockMvc.perform(patch("/v1/admin/servicos/{id}/preco", servicoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(atualizarPrecoServicoUseCase, times(1)).execute(eq(servicoId), any(AtualizarPrecoServicoCommand.class));
        verify(presenter, times(1)).prepararViewModel(any(ServicoResponse.class));
    }

    @Test
    @DisplayName("Deve atualizar dados com sucesso")
    void deveAtualizarDados() throws Exception {
        // Arrange
        UUID servicoId = servicoResponse.getId();
        AtualizarDadosServicoRequest request = new AtualizarDadosServicoRequest();
        request.setNome("Troca de Óleo Premium");
        request.setDescricao("Troca de óleo sintético");

        when(atualizarDadosServicoUseCase.execute(eq(servicoId), any(AtualizarDadosServicoCommand.class))).thenReturn(servicoResponse);
        when(presenter.prepararViewModel(any(ServicoResponse.class))).thenReturn(servicoResponseDto);

        // Act & Assert
        mockMvc.perform(patch("/v1/admin/servicos/{id}/dados", servicoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(atualizarDadosServicoUseCase, times(1)).execute(eq(servicoId), any(AtualizarDadosServicoCommand.class));
        verify(presenter, times(1)).prepararViewModel(any(ServicoResponse.class));
    }

    @Test
    @DisplayName("Deve retornar erro de validação ao criar serviço sem nome")
    void deveRetornarErroValidacaoCriarServicoSemNome() throws Exception {
        // Arrange
        CriarServicoRequest request = new CriarServicoRequest();
        request.setDescricao("Descrição");
        request.setPreco(new BigDecimal("150.00"));

        // Act & Assert
        mockMvc.perform(post("/v1/admin/servicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(criarServicoUseCase, never()).execute(any(CriarServicoCommand.class));
    }

    @Test
    @DisplayName("Deve retornar erro de validação ao criar serviço sem preço")
    void deveRetornarErroValidacaoCriarServicoSemPreco() throws Exception {
        // Arrange
        CriarServicoRequest request = new CriarServicoRequest();
        request.setNome("Troca de Óleo");
        request.setDescricao("Descrição");

        // Act & Assert
        mockMvc.perform(post("/v1/admin/servicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(criarServicoUseCase, never()).execute(any(CriarServicoCommand.class));
    }

    @Test
    @DisplayName("Deve retornar erro de validação ao atualizar preço com valor inválido")
    void deveRetornarErroValidacaoAtualizarPrecoInvalido() throws Exception {
        // Arrange
        UUID servicoId = servicoResponse.getId();
        AtualizarPrecoServicoRequest request = new AtualizarPrecoServicoRequest();
        request.setNovoPreco(BigDecimal.ZERO);

        // Act & Assert
        mockMvc.perform(patch("/v1/admin/servicos/{id}/preco", servicoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(atualizarPrecoServicoUseCase, never()).execute(eq(servicoId), any(AtualizarPrecoServicoCommand.class));
    }

    @Test
    @DisplayName("Deve retornar erro de validação ao atualizar dados sem nome")
    void deveRetornarErroValidacaoAtualizarDadosSemNome() throws Exception {
        // Arrange
        UUID servicoId = servicoResponse.getId();
        AtualizarDadosServicoRequest request = new AtualizarDadosServicoRequest();
        request.setDescricao("Nova Descrição");

        // Act & Assert
        mockMvc.perform(patch("/v1/admin/servicos/{id}/dados", servicoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(atualizarDadosServicoUseCase, never()).execute(eq(servicoId), any(AtualizarDadosServicoCommand.class));
    }
}
