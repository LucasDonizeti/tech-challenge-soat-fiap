package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
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
@DisplayName("Testes de BuscarClienteUseCase - Application Layer")
class BuscarClienteUseCaseTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private BuscarClienteUseCase useCase;

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
    @DisplayName("Deve buscar cliente com sucesso quando encontrado")
    void deveBuscarClienteComSucesso() {
        // Arrange
        when(repository.findById(clienteId)).thenReturn(Optional.of(cliente));

        // Act
        ClienteResponse response = useCase.execute(clienteId);

        // Assert
        assertNotNull(response);
        assertEquals("João Silva", response.getNome());
        assertEquals("123.456.789-09", response.getCpf());
        assertEquals("joao.silva@email.com", response.getEmail());

        verify(repository, times(1)).findById(clienteId);
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
    }

    @Test
    @DisplayName("Deve lançar exceção quando clienteId nulo")
    void deveLancarExcecaoQuandoClienteIdNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(null));
    }

    @Test
    @DisplayName("Deve buscar cliente pessoa jurídica com sucesso")
    void deveBuscarClientePessoaJuridicaComSucesso() {
        // Arrange
        var clientePJ = Cliente.criarPJ(
                Nome.of("Auto Peças Ltda"),
                com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ.of("12345678000195"),
                Email.of("contato@autopecas.com.br")
        );
        
        when(repository.findById(clienteId)).thenReturn(Optional.of(clientePJ));

        // Act
        ClienteResponse response = useCase.execute(clienteId);

        // Assert
        assertNotNull(response);
        assertEquals("Auto Peças Ltda", response.getNome());
        assertEquals("12.345.678/0001-95", response.getCnpj());
        assertEquals("contato@autopecas.com.br", response.getEmail());
        assertTrue(response.isPessoaJuridica());
        assertFalse(response.isPessoaFisica());

        verify(repository, times(1)).findById(clienteId);
    }

    @Test
    @DisplayName("Deve buscar cliente inativo com sucesso")
    void deveBuscarClienteInativoComSucesso() {
        // Arrange
        cliente.inativar();
        when(repository.findById(clienteId)).thenReturn(Optional.of(cliente));

        // Act
        ClienteResponse response = useCase.execute(clienteId);

        // Assert
        assertNotNull(response);
        assertEquals("João Silva", response.getNome());
        assertEquals("INATIVO", response.getStatus().name());

        verify(repository, times(1)).findById(clienteId);
    }
}
