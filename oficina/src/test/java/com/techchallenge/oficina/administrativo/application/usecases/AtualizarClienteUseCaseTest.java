package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarClienteCommand;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.ClienteGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.exceptions.EmailJaCadastradoException;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoClienteException;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de AtualizarClienteUseCase - Application Layer")
class AtualizarClienteUseCaseTest {

    @Mock
    private ClienteGateway gateway;

    @Mock
    private ClienteDomainService domainService;

    @InjectMocks
    private AtualizarClienteUseCase useCase;

    private UUID clienteId;
    private Cliente clienteExistente;
    private AtualizarClienteCommand command;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        clienteExistente = Cliente.criar(
                Nome.of("João Silva"),
                CPF.of("12345678909"),
                Email.of("joao.silva@email.com")
        );
        
        command = new AtualizarClienteCommand(
                "João Silva Santos",
                "joao.silva.santos@email.com"
        );
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso quando email não foi alterado")
    void deveAtualizarClienteComSucessoMesmoEmail() {
        // Arrange
        AtualizarClienteCommand commandMesmoEmail = new AtualizarClienteCommand(
                "João Silva Santos",
                "joao.silva@email.com"
        );
        
        when(gateway.findById(clienteId)).thenReturn(Optional.of(clienteExistente));
        when(gateway.save(any(Cliente.class))).thenReturn(clienteExistente);

        // Act
        ClienteResponse response = useCase.execute(clienteId, commandMesmoEmail);

        // Assert
        assertNotNull(response);
        assertEquals("João Silva Santos", response.getNome());
        assertEquals("joao.silva@email.com", response.getEmail());

        verify(gateway, times(1)).findById(clienteId);
        verify(gateway, times(1)).save(any(Cliente.class));
        verify(domainService, never()).validarEmailUnico(any(Email.class));
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso quando email foi alterado")
    void deveAtualizarClienteComSucessoEmailAlterado() {
        // Arrange
        when(gateway.findById(clienteId)).thenReturn(Optional.of(clienteExistente));
        when(gateway.save(any(Cliente.class))).thenReturn(clienteExistente);

        // Act
        ClienteResponse response = useCase.execute(clienteId, command);

        // Assert
        assertNotNull(response);
        assertEquals("João Silva Santos", response.getNome());
        assertEquals("joao.silva.santos@email.com", response.getEmail());

        verify(gateway, times(1)).findById(clienteId);
        verify(gateway, times(1)).save(any(Cliente.class));
        verify(domainService, times(1)).validarEmailUnico(any(Email.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não encontrado")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        // Arrange
        when(gateway.findById(clienteId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(clienteId, command)
        );

        assertEquals("Cliente não encontrado: " + clienteId, exception.getMessage());

        verify(gateway, times(1)).findById(clienteId);
        verify(gateway, never()).save(any(Cliente.class));
        verify(domainService, never()).validarEmailUnico(any(Email.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já está em uso por outro cliente")
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        // Arrange
        when(gateway.findById(clienteId)).thenReturn(Optional.of(clienteExistente));
        doThrow(new EmailJaCadastradoException(Email.of("novo.email@teste.com")))
                .when(domainService).validarEmailUnico(any(Email.class));

        // Act & Assert
        EmailJaCadastradoException exception = assertThrows(
                EmailJaCadastradoException.class,
                () -> useCase.execute(clienteId, command)
        );

        assertEquals("Email já cadastrado: novo.email@teste.com", exception.getMessage());

        verify(gateway, times(1)).findById(clienteId);
        verify(gateway, never()).save(any(Cliente.class));
        verify(domainService, times(1)).validarEmailUnico(any(Email.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando command com nome nulo")
    void deveLancarExcecaoQuandoCommandNomeNulo() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new AtualizarClienteCommand(null, "email@teste.com")
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando command com nome em branco")
    void deveLancarExcecaoQuandoCommandNomeEmBranco() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new AtualizarClienteCommand("", "email@teste.com")
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando command com email nulo")
    void deveLancarExcecaoQuandoCommandEmailNulo() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new AtualizarClienteCommand("Nome Teste", null)
        );

        assertEquals("Email é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando command com email em branco")
    void deveLancarExcecaoQuandoCommandEmailEmBranco() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new AtualizarClienteCommand("Nome Teste", "")
        );

        assertEquals("Email é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando clienteId nulo")
    void deveLancarExcecaoQuandoClienteIdNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(null, command));
    }

    @Test
    @DisplayName("Deve lançar exceção quando command nulo")
    void deveLancarExcecaoQuandoCommandNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(clienteId, null));
    }
}
