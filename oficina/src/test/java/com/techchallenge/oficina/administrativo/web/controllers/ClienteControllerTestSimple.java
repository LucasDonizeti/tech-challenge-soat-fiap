package com.techchallenge.oficina.administrativo.web.controllers;

import com.techchallenge.oficina.administrativo.application.usecases.*;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import com.techchallenge.oficina.administrativo.web.dto.ClienteResponseDto;
import com.techchallenge.oficina.administrativo.web.dto.CriarClienteRequest;
import com.techchallenge.oficina.administrativo.web.mappers.ClienteWebMapper;
import com.techchallenge.oficina.administrativo.web.presenters.ClientePresenter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ClienteControllerSimpleTest {

    @Mock
    private CriarClienteUseCase criarClienteUseCase;

    @Mock
    private BuscarClienteUseCase buscarClienteUseCase;

    @Mock
    private AtualizarClienteUseCase atualizarClienteUseCase;

    @Mock
    private InativarClienteUseCase inativarClienteUseCase;

    @Mock
    private ReativarClienteUseCase reativarClienteUseCase;

    @Mock
    private DeletarClienteUseCase deletarClienteUseCase;

    @Mock
    private ListarClientesUseCase listarClientesUseCase;

    @Mock
    private ClientePresenter presenter;

    @InjectMocks
    private ClienteController clienteController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();
    }

    @Test
    void testCriarCliente() throws Exception {
        // Arrange
        CriarClienteRequest request = new CriarClienteRequest();
        request.setNome("João Silva");
        request.setCpf("12345678909");
        request.setEmail("joao.silva@email.com");

        ClienteResponse response = ClienteResponse.builder()
                .id(UUID.randomUUID())
                .nome("João Silva")
                .cpf("123.456.789-09")
                .email("joao.silva@email.com")
                .status(StatusCliente.ATIVO)
                .isPessoaFisica(true)
                .isPessoaJuridica(false)
                .quantidadeVeiculos(0)
                .build();

        ClienteResponseDto dto = ClienteResponseDto.from(response);

        when(criarClienteUseCase.execute(any())).thenReturn(response);
        when(presenter.prepararViewModel(response)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(post("/v1/admin/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"João Silva\",\"cpf\":\"12345678909\",\"email\":\"joao.silva@email.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value("123.456.789-09"))
                .andExpect(jsonPath("$.email").value("joao.silva@email.com"))
                .andExpect(jsonPath("$.status").value("ATIVO"));

        verify(criarClienteUseCase).execute(any());
    }

    @Test
    void testBuscarCliente() throws Exception {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        ClienteResponse response = ClienteResponse.builder()
                .id(clienteId)
                .nome("João Silva")
                .cpf("123.456.789-09")
                .email("joao.silva@email.com")
                .status(StatusCliente.ATIVO)
                .build();

        ClienteResponseDto dto = ClienteResponseDto.from(response);

        when(buscarClienteUseCase.execute(any())).thenReturn(response);
        when(presenter.prepararViewModel(response)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/v1/admin/clientes/{id}", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clienteId.toString()))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value("123.456.789-09"));

        verify(buscarClienteUseCase).execute(any());
    }
}
