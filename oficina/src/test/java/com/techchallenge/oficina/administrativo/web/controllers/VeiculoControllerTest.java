package com.techchallenge.oficina.administrativo.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.oficina.administrativo.application.usecases.*;
import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusVeiculo;
import com.techchallenge.oficina.administrativo.web.dto.CriarVeiculoRequest;
import com.techchallenge.oficina.administrativo.web.dto.VeiculoResponseDto;
import com.techchallenge.oficina.administrativo.web.mappers.VeiculoWebMapper;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - VeiculoController")
class VeiculoControllerTest {

    @Mock
    private CriarVeiculoUseCase criarVeiculoUseCase;

    @Mock
    private BuscarVeiculoUseCase buscarVeiculoUseCase;

    @Mock
    private BuscarVeiculosPorClienteUseCase buscarVeiculosPorClienteUseCase;

    @Mock
    private InativarVeiculoUseCase inativarVeiculoUseCase;

    @Mock
    private ReativarVeiculoUseCase reativarVeiculoUseCase;

    @Mock
    private ListarVeiculosUseCase listarVeiculosUseCase;

    @Mock
    private VeiculoWebMapper mapper;

    @Mock
    private PageableValidator pageableValidator;

    @InjectMocks
    private VeiculoController veiculoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private VeiculoResponse veiculoResponse;
    private VeiculoResponseDto veiculoResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(veiculoController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();

        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        veiculoResponse = VeiculoResponse.builder()
                .id(veiculoId)
                .placa("ABC-1234")
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2022)
                .cor("Prata")
                .status(StatusVeiculo.ATIVO)
                .clienteId(clienteId)
                .clienteNome("João Silva")
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        veiculoResponseDto = new VeiculoResponseDto();
        veiculoResponseDto.setId(veiculoResponse.getId());
        veiculoResponseDto.setPlaca(veiculoResponse.getPlaca());
        veiculoResponseDto.setMarca(veiculoResponse.getMarca());
        veiculoResponseDto.setModelo(veiculoResponse.getModelo());
        veiculoResponseDto.setAno(veiculoResponse.getAno());
        veiculoResponseDto.setCor(veiculoResponse.getCor());
        veiculoResponseDto.setStatus(veiculoResponse.getStatus());
        veiculoResponseDto.setClienteId(veiculoResponse.getClienteId());
        veiculoResponseDto.setClienteNome(veiculoResponse.getClienteNome());
        veiculoResponseDto.setCriadoEm(veiculoResponse.getCriadoEm());
        veiculoResponseDto.setAtualizadoEm(veiculoResponse.getAtualizadoEm());
        veiculoResponseDto.setDescricaoCompleta(veiculoResponse.getDescricaoCompleta());
    }

    @Test
    @DisplayName("Deve criar veículo com sucesso")
    void deveCriarVeiculoComSucesso() throws Exception {
        // Arrange
        CriarVeiculoRequest request = new CriarVeiculoRequest();
        request.setPlaca("ABC1234");
        request.setMarca("Toyota");
        request.setModelo("Corolla");
        request.setAno(2022);
        request.setCor("Prata");
        request.setClienteId(UUID.randomUUID());

        when(criarVeiculoUseCase.execute(any())).thenReturn(veiculoResponse);
        when(mapper.toDto(any(VeiculoResponse.class))).thenReturn(veiculoResponseDto);

        // Act & Assert
        mockMvc.perform(post("/v1/admin/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.marca").value("Toyota"))
                .andExpect(jsonPath("$.modelo").value("Corolla"));

        verify(criarVeiculoUseCase, times(1)).execute(any());
        verify(mapper, times(1)).toDto(any(VeiculoResponse.class));
    }

    @Test
    @DisplayName("Deve buscar veículo por ID com sucesso")
    void deveBuscarVeiculoPorIdComSucesso() throws Exception {
        // Arrange
        UUID id = veiculoResponse.getId();
        when(buscarVeiculoUseCase.execute(id)).thenReturn(veiculoResponse);
        when(mapper.toDto(any(VeiculoResponse.class))).thenReturn(veiculoResponseDto);

        // Act & Assert
        mockMvc.perform(get("/v1/admin/veiculos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.marca").value("Toyota"));

        verify(buscarVeiculoUseCase, times(1)).execute(id);
        verify(mapper, times(1)).toDto(any(VeiculoResponse.class));
    }

    @Test
    @DisplayName("Deve buscar veículos por cliente com sucesso")
    void deveBuscarVeiculosPorClienteComSucesso() throws Exception {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        List<VeiculoResponse> responses = List.of(veiculoResponse);
        when(buscarVeiculosPorClienteUseCase.execute(clienteId)).thenReturn(responses);
        when(mapper.toDtoList(any())).thenReturn(List.of(veiculoResponseDto));

        // Act & Assert
        mockMvc.perform(get("/v1/admin/veiculos/cliente/{clienteId}", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(buscarVeiculosPorClienteUseCase, times(1)).execute(clienteId);
        verify(mapper, times(1)).toDtoList(any());
    }

    @Test
    @DisplayName("Deve listar veículos com paginação")
    void deveListarVeiculosComPaginacao() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<VeiculoResponse> page = new PageImpl<>(List.of(veiculoResponse), pageable, 1);
        when(pageableValidator.validate(any(Pageable.class), any())).thenReturn(pageable);
        when(listarVeiculosUseCase.execute(any(Pageable.class))).thenReturn(page);
        when(mapper.toDtoPage(any())).thenReturn(new PageImpl<>(List.of(veiculoResponseDto), pageable, 1));

        // Act & Assert
        mockMvc.perform(get("/v1/admin/veiculos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(pageableValidator, times(1)).validate(any(Pageable.class), any());
        verify(listarVeiculosUseCase, times(1)).execute(any(Pageable.class));
        verify(mapper, times(1)).toDtoPage(any());
    }

    @Test
    @DisplayName("Deve inativar veículo com sucesso")
    void deveInativarVeiculoComSucesso() throws Exception {
        // Arrange
        UUID id = veiculoResponse.getId();
        VeiculoResponse inativadoResponse = VeiculoResponse.builder()
                .id(id)
                .placa("ABC-1234")
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2022)
                .cor("Prata")
                .status(StatusVeiculo.INATIVO)
                .clienteId(UUID.randomUUID())
                .clienteNome("João Silva")
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        when(inativarVeiculoUseCase.execute(id)).thenReturn(inativadoResponse);
        when(mapper.toDto(any(VeiculoResponse.class))).thenReturn(veiculoResponseDto);

        // Act & Assert
        mockMvc.perform(patch("/v1/admin/veiculos/{id}/inativar", id))
                .andExpect(status().isOk());

        verify(inativarVeiculoUseCase, times(1)).execute(id);
        verify(mapper, times(1)).toDto(any(VeiculoResponse.class));
    }

    @Test
    @DisplayName("Deve reativar veículo com sucesso")
    void deveReativarVeiculoComSucesso() throws Exception {
        // Arrange
        UUID id = veiculoResponse.getId();
        when(reativarVeiculoUseCase.execute(id)).thenReturn(veiculoResponse);
        when(mapper.toDto(any(VeiculoResponse.class))).thenReturn(veiculoResponseDto);

        // Act & Assert
        mockMvc.perform(patch("/v1/admin/veiculos/{id}/reativar", id))
                .andExpect(status().isOk());

        verify(reativarVeiculoUseCase, times(1)).execute(id);
        verify(mapper, times(1)).toDto(any(VeiculoResponse.class));
    }

    @Test
    @DisplayName("Deve retornar erro de validação ao criar veículo sem placa")
    void deveRetornarErroValidacaoCriarVeiculoSemPlaca() throws Exception {
        // Arrange
        CriarVeiculoRequest request = new CriarVeiculoRequest();
        request.setMarca("Toyota");
        request.setModelo("Corolla");
        request.setAno(2022);
        request.setClienteId(UUID.randomUUID());

        // Act & Assert
        mockMvc.perform(post("/v1/admin/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(criarVeiculoUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Deve retornar erro de validação ao criar veículo sem marca")
    void deveRetornarErroValidacaoCriarVeiculoSemMarca() throws Exception {
        // Arrange
        CriarVeiculoRequest request = new CriarVeiculoRequest();
        request.setPlaca("ABC1234");
        request.setModelo("Corolla");
        request.setAno(2022);
        request.setClienteId(UUID.randomUUID());

        // Act & Assert
        mockMvc.perform(post("/v1/admin/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(criarVeiculoUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Deve retornar erro de validação ao criar veículo sem clienteId")
    void deveRetornarErroValidacaoCriarVeiculoSemClienteId() throws Exception {
        // Arrange
        CriarVeiculoRequest request = new CriarVeiculoRequest();
        request.setPlaca("ABC1234");
        request.setMarca("Toyota");
        request.setModelo("Corolla");
        request.setAno(2022);

        // Act & Assert
        mockMvc.perform(post("/v1/admin/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(criarVeiculoUseCase, never()).execute(any());
    }
}
