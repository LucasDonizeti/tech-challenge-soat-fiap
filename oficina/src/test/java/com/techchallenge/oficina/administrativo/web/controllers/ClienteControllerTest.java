package com.techchallenge.oficina.administrativo.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.oficina.administrativo.application.usecases.*;
import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarClienteCommand;
import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarClienteCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import com.techchallenge.oficina.administrativo.web.dto.AtualizarClienteRequest;
import com.techchallenge.oficina.administrativo.web.dto.ClienteResponseDto;
import com.techchallenge.oficina.administrativo.web.dto.CriarClienteRequest;
import com.techchallenge.oficina.administrativo.web.mappers.ClienteWebMapper;
import com.techchallenge.oficina.administrativo.web.presenters.ClientePresenter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - ClienteController")
class ClienteControllerTest {

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
    private BuscarClientesPorFiltroUseCase buscarClientesPorFiltroUseCase;

    @Mock
    private ClientePresenter presenter;

    @InjectMocks
    private ClienteController clienteController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ClienteResponse clienteResponse;
    private ClienteResponseDto clienteResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();
        objectMapper = new ObjectMapper();

        clienteResponse = ClienteResponse.builder()
                .id(UUID.randomUUID())
                .nome("João Silva")
                .cpf("52998224725")
                .cnpj(null)
                .email("joao@example.com")
                .status(StatusCliente.ATIVO)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .isPessoaFisica(true)
                .isPessoaJuridica(false)
                .quantidadeVeiculos(0)
                .build();

        clienteResponseDto = new ClienteResponseDto();
        clienteResponseDto.setId(clienteResponse.getId());
        clienteResponseDto.setNome(clienteResponse.getNome());
        clienteResponseDto.setCpf(clienteResponse.getCpf());
        clienteResponseDto.setEmail(clienteResponse.getEmail());
        clienteResponseDto.setStatus(clienteResponse.getStatus());
        clienteResponseDto.setCriadoEm(clienteResponse.getCriadoEm());
        clienteResponseDto.setAtualizadoEm(clienteResponse.getAtualizadoEm());
        clienteResponseDto.setPessoaFisica(clienteResponse.isPessoaFisica());
        clienteResponseDto.setPessoaJuridica(clienteResponse.isPessoaJuridica());
        clienteResponseDto.setQuantidadeVeiculos(clienteResponse.getQuantidadeVeiculos());
        clienteResponseDto.setTipoPessoa("Pessoa Física");
        clienteResponseDto.setDocumento("12345678901");
    }

    @Test
    @DisplayName("Deve criar cliente com sucesso")
    void deveCriarClienteComSucesso() throws Exception {
        // Arrange
        CriarClienteRequest request = new CriarClienteRequest();
        request.setNome("João Silva");
        request.setCpf("52998224725");
        request.setEmail("joao@example.com");

        when(criarClienteUseCase.execute(any(CriarClienteCommand.class))).thenReturn(clienteResponse);
        when(presenter.prepararViewModel(any(ClienteResponse.class))).thenReturn(clienteResponseDto);

        // Act & Assert
        mockMvc.perform(post("/v1/admin/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@example.com"));

        verify(criarClienteUseCase, times(1)).execute(any(CriarClienteCommand.class));
        verify(presenter, times(1)).prepararViewModel(any(ClienteResponse.class));
    }

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    void deveBuscarClientePorIdComSucesso() throws Exception {
        // Arrange
        UUID id = clienteResponse.getId();
        when(buscarClienteUseCase.execute(id)).thenReturn(clienteResponse);
        when(presenter.prepararViewModel(any(ClienteResponse.class))).thenReturn(clienteResponseDto);

        // Act & Assert
        mockMvc.perform(get("/v1/admin/clientes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(buscarClienteUseCase, times(1)).execute(id);
        verify(presenter, times(1)).prepararViewModel(any(ClienteResponse.class));
    }


    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void deveAtualizarClienteComSucesso() throws Exception {
        // Arrange
        UUID id = clienteResponse.getId();
        AtualizarClienteRequest request = new AtualizarClienteRequest();
        request.setNome("João Silva Atualizado");
        request.setEmail("joao.atualizado@example.com");

        ClienteResponse updatedResponse = ClienteResponse.builder()
                .id(id)
                .nome("João Silva Atualizado")
                .cpf("12345678901")
                .email("joao.atualizado@example.com")
                .status(StatusCliente.ATIVO)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .isPessoaFisica(true)
                .isPessoaJuridica(false)
                .quantidadeVeiculos(0)
                .build();

        when(atualizarClienteUseCase.execute(eq(id), any(AtualizarClienteCommand.class)))
                .thenReturn(updatedResponse);
        when(presenter.prepararViewModel(any(ClienteResponse.class))).thenReturn(clienteResponseDto);

        // Act & Assert
        mockMvc.perform(put("/v1/admin/clientes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(atualizarClienteUseCase, times(1)).execute(eq(id), any(AtualizarClienteCommand.class));
        verify(presenter, times(1)).prepararViewModel(any(ClienteResponse.class));
    }

    @Test
    @DisplayName("Deve inativar cliente com sucesso")
    void deveInativarClienteComSucesso() throws Exception {
        // Arrange
        UUID id = clienteResponse.getId();

        ClienteResponse inativadoResponse = ClienteResponse.builder()
                .id(id)
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@example.com")
                .status(StatusCliente.INATIVO)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .isPessoaFisica(true)
                .isPessoaJuridica(false)
                .quantidadeVeiculos(0)
                .build();

        when(inativarClienteUseCase.execute(id)).thenReturn(inativadoResponse);
        when(presenter.prepararViewModel(any(ClienteResponse.class))).thenReturn(clienteResponseDto);

        // Act & Assert
        mockMvc.perform(patch("/v1/admin/clientes/{id}/inativar", id))
                .andExpect(status().isOk());

        verify(inativarClienteUseCase, times(1)).execute(id);
        verify(presenter, times(1)).prepararViewModel(any(ClienteResponse.class));
    }

    @Test
    @DisplayName("Deve reativar cliente com sucesso")
    void deveReativarClienteComSucesso() throws Exception {
        // Arrange
        UUID id = clienteResponse.getId();

        when(reativarClienteUseCase.execute(id)).thenReturn(clienteResponse);
        when(presenter.prepararViewModel(any(ClienteResponse.class))).thenReturn(clienteResponseDto);

        // Act & Assert
        mockMvc.perform(patch("/v1/admin/clientes/{id}/reativar", id))
                .andExpect(status().isOk());

        verify(reativarClienteUseCase, times(1)).execute(id);
        verify(presenter, times(1)).prepararViewModel(any(ClienteResponse.class));
    }

    @Test
    @DisplayName("Deve deletar cliente com sucesso")
    void deveDeletarClienteComSucesso() throws Exception {
        // Arrange
        UUID id = clienteResponse.getId();
        doNothing().when(deletarClienteUseCase).execute(id);

        // Act & Assert
        mockMvc.perform(delete("/v1/admin/clientes/{id}", id))
                .andExpect(status().isNoContent());

        verify(deletarClienteUseCase, times(1)).execute(id);
    }

    @Test
    @DisplayName("Deve retornar erro de validação ao criar cliente sem nome")
    void deveRetornarErroValidacaoCriarClienteSemNome() throws Exception {
        // Arrange
        CriarClienteRequest request = new CriarClienteRequest();
        request.setCpf("12345678901");
        request.setEmail("joao@example.com");

        // Act & Assert
        mockMvc.perform(post("/v1/admin/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(criarClienteUseCase, never()).execute(any(CriarClienteCommand.class));
    }

    @Test
    @DisplayName("Deve retornar erro de validação ao criar cliente sem email")
    void deveRetornarErroValidacaoCriarClienteSemEmail() throws Exception {
        // Arrange
        CriarClienteRequest request = new CriarClienteRequest();
        request.setNome("João Silva");
        request.setCpf("12345678901");

        // Act & Assert
        mockMvc.perform(post("/v1/admin/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(criarClienteUseCase, never()).execute(any(CriarClienteCommand.class));
    }

    @Test
    @DisplayName("Deve retornar erro de validação ao atualizar cliente sem nome")
    void deveRetornarErroValidacaoAtualizarClienteSemNome() throws Exception {
        // Arrange
        UUID id = clienteResponse.getId();
        AtualizarClienteRequest request = new AtualizarClienteRequest();
        request.setEmail("joao@example.com");

        // Act & Assert
        mockMvc.perform(put("/v1/admin/clientes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(atualizarClienteUseCase, never()).execute(eq(id), any(AtualizarClienteCommand.class));
    }
}
