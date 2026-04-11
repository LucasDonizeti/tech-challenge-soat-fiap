package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import com.techchallenge.oficina.administrativo.domain.services.ClienteDomainService;
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
@DisplayName("Testes de DeletarClienteUseCase - Application Layer")
class DeletarClienteUseCaseTest {

    @Mock
    private ClienteRepository repository;

    @Mock
    private ClienteDomainService domainService;

    @InjectMocks
    private DeletarClienteUseCase useCase;

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
    @DisplayName("Deve deletar cliente com sucesso")
    void deveDeletarClienteComSucesso() {
        // Arrange
        when(repository.findById(clienteId)).thenReturn(Optional.of(cliente));
        doNothing().when(domainService).validarExclusaoCliente(cliente);
        doNothing().when(repository).deleteById(clienteId);

        // Act
        assertDoesNotThrow(() -> useCase.execute(clienteId));

        // Assert
        verify(repository, times(1)).findById(clienteId);
        verify(domainService, times(1)).validarExclusaoCliente(cliente);
        verify(repository, times(1)).deleteById(clienteId);
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
        verify(domainService, never()).validarExclusaoCliente(any(Cliente.class));
        verify(repository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não pode ser excluído")
    void deveLancarExcecaoQuandoClienteNaoPodeSerExcluido() {
        // Arrange
        when(repository.findById(clienteId)).thenReturn(Optional.of(cliente));
        doThrow(new IllegalStateException("Cliente não pode ser excluído. Verifique se há ordens de serviço em andamento ou veículos cadastrados."))
                .when(domainService).validarExclusaoCliente(cliente);

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(clienteId)
        );

        assertEquals("Cliente não pode ser excluído. Verifique se há ordens de serviço em andamento ou veículos cadastrados.", exception.getMessage());

        verify(repository, times(1)).findById(clienteId);
        verify(domainService, times(1)).validarExclusaoCliente(cliente);
        verify(repository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando clienteId nulo")
    void deveLancarExcecaoQuandoClienteIdNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(null));
    }

    @Test
    @DisplayName("Deve deletar cliente pessoa jurídica com sucesso")
    void deveDeletarClientePessoaJuridicaComSucesso() {
        // Arrange
        var clientePJ = Cliente.criarPJ(
                Nome.of("Auto Peças Ltda"),
                com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ.of("12345678000195"),
                Email.of("contato@autopecas.com.br")
        );
        
        when(repository.findById(clienteId)).thenReturn(Optional.of(clientePJ));
        doNothing().when(domainService).validarExclusaoCliente(clientePJ);
        doNothing().when(repository).deleteById(clienteId);

        // Act
        assertDoesNotThrow(() -> useCase.execute(clienteId));

        // Assert
        verify(repository, times(1)).findById(clienteId);
        verify(domainService, times(1)).validarExclusaoCliente(clientePJ);
        verify(repository, times(1)).deleteById(clienteId);
    }

    @Test
    @DisplayName("Deve deletar cliente inativo com sucesso")
    void deveDeletarClienteInativoComSucesso() {
        // Arrange
        cliente.inativar();
        when(repository.findById(clienteId)).thenReturn(Optional.of(cliente));
        doNothing().when(domainService).validarExclusaoCliente(cliente);
        doNothing().when(repository).deleteById(clienteId);

        // Act
        assertDoesNotThrow(() -> useCase.execute(clienteId));

        // Assert
        verify(repository, times(1)).findById(clienteId);
        verify(domainService, times(1)).validarExclusaoCliente(cliente);
        verify(repository, times(1)).deleteById(clienteId);
    }
}
