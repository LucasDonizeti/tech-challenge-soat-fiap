package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de ReativarClienteUseCase - Application Layer")
class ReativarClienteUseCaseTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ReativarClienteUseCase useCase;

    private UUID clienteId;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        cliente = Cliente.criar(
                Nome.of("João Silva"),
                CPF.of("12345678909"),
                Email.of("joao.silva@email.com")
        );
    }

    @Test
    @DisplayName("Deve reativar cliente com sucesso")
    void deveReativarClienteComSucesso() {
        // Arrange
        cliente.inativar(); // Cliente começa inativo
        when(repository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(repository.save(any(Cliente.class))).thenReturn(cliente);

        // Act
        ClienteResponse response = useCase.execute(clienteId);

        // Assert
        assertNotNull(response);
        assertEquals("João Silva", response.getNome());
        assertEquals("123.456.789-09", response.getCpf());
        assertEquals("joao.silva@email.com", response.getEmail());
        assertEquals(StatusCliente.ATIVO, response.getStatus());

        verify(repository, times(1)).findById(clienteId);
        verify(repository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não encontrado")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        // Arrange
        when(repository.findById(clienteId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(clienteId)
        );

        assertEquals("Cliente não encontrado: " + clienteId, exception.getMessage());

        verify(repository, times(1)).findById(clienteId);
        verify(repository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando clienteId nulo")
    void deveLancarExcecaoQuandoClienteIdNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(null));
    }

    @Test
    @DisplayName("Deve reativar cliente pessoa jurídica com sucesso")
    void deveReativarClientePessoaJuridicaComSucesso() {
        // Arrange
        var clientePJ = Cliente.criarPJ(
                Nome.of("Auto Peças Ltda"),
                com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ.of("12345678000195"),
                Email.of("contato@autopecas.com.br")
        );
        clientePJ.inativar(); // Cliente PJ começa inativo
        
        when(repository.findById(clienteId)).thenReturn(Optional.of(clientePJ));
        when(repository.save(any(Cliente.class))).thenReturn(clientePJ);

        // Act
        ClienteResponse response = useCase.execute(clienteId);

        // Assert
        assertNotNull(response);
        assertEquals("Auto Peças Ltda", response.getNome());
        assertEquals("12.345.678/0001-95", response.getCnpj());
        assertEquals("contato@autopecas.com.br", response.getEmail());
        assertEquals(StatusCliente.ATIVO, response.getStatus());

        verify(repository, times(1)).findById(clienteId);
        verify(repository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente já está ativo")
    void deveLancarExcecaoQuandoClienteJaEstaAtivo() {
        // Arrange
        // Cliente já está ativo por padrão após criação
        when(repository.findById(clienteId)).thenReturn(Optional.of(cliente));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(clienteId)
        );

        assertEquals("Cliente já está ativo", exception.getMessage());

        verify(repository, times(1)).findById(clienteId);
        verify(repository, never()).save(any(Cliente.class));
    }
}
