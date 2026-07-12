package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarVeiculoCommand;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.ClienteGateway;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.VeiculoGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoVeiculoException;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
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
@DisplayName("Testes de CriarVeiculoUseCase - Application Layer")
class CriarVeiculoUseCaseTest {

    @Mock
    private VeiculoGateway veiculoGateway;

    @Mock
    private ClienteGateway clienteGateway;

    @InjectMocks
    private CriarVeiculoUseCase useCase;

    private UUID clienteId;
    private Cliente cliente;
    private CriarVeiculoCommand command;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        cliente = Cliente.criar(
                Nome.of("João Silva"),
                CPF.of("12345678909"),
                Email.of("joao.silva@email.com")
        );

        command = new CriarVeiculoCommand(
                "ABC1234",
                "Toyota",
                "Corolla",
                2022,
                "Prata",
                clienteId
        );

        veiculo = new Veiculo(
                Placa.of("ABC1234"),
                "Toyota",
                "Corolla",
                2022,
                "Prata"
        );
        veiculo.setCliente(cliente);
    }

    @Test
    @DisplayName("Deve criar veículo com sucesso")
    void deveCriarVeiculoComSucesso() {
        // Arrange
        when(veiculoGateway.existsByPlacaAndClienteId(any(Placa.class), eq(clienteId))).thenReturn(false);
        when(clienteGateway.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoGateway.save(any(Veiculo.class))).thenAnswer(invocation -> {
            Veiculo savedVeiculo = invocation.getArgument(0);
            savedVeiculo.setCliente(cliente);
            return savedVeiculo;
        });

        // Act
        VeiculoResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Toyota", response.getMarca());
        assertEquals("Corolla", response.getModelo());
        assertEquals(2022, response.getAno());
        assertEquals("Prata", response.getCor());
        assertNotNull(response.getClienteId());
        assertEquals("João Silva", response.getClienteNome());

        verify(veiculoGateway, times(1)).existsByPlacaAndClienteId(any(Placa.class), eq(clienteId));
        verify(clienteGateway, times(1)).findById(clienteId);
        verify(veiculoGateway, times(1)).save(any(Veiculo.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando placa já cadastrada")
    void deveLancarExcecaoQuandoPlacaJaCadastrada() {
        // Arrange
        when(clienteGateway.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoGateway.existsByPlacaAndClienteId(any(Placa.class), eq(clienteId))).thenReturn(true);

        // Act & Assert
        ValidacaoVeiculoException exception = assertThrows(
                ValidacaoVeiculoException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Placa já cadastrada"));

        verify(veiculoGateway, times(1)).existsByPlacaAndClienteId(any(Placa.class), eq(clienteId));
        verify(clienteGateway, times(1)).findById(clienteId);
        verify(veiculoGateway, never()).save(any(Veiculo.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não encontrado")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        // Arrange
        when(clienteGateway.findById(clienteId)).thenReturn(Optional.empty());

        // Act & Assert
        ValidacaoVeiculoException exception = assertThrows(
                ValidacaoVeiculoException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Cliente não encontrado"));

        verify(veiculoGateway, never()).existsByPlacaAndClienteId(any(Placa.class), any(UUID.class));
        verify(clienteGateway, times(1)).findById(clienteId);
        verify(veiculoGateway, never()).save(any(Veiculo.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente está inativo")
    void deveLancarExcecaoQuandoClienteInativo() {
        // Arrange
        cliente.inativar();
        when(clienteGateway.findById(clienteId)).thenReturn(Optional.of(cliente));

        // Act & Assert
        ValidacaoVeiculoException exception = assertThrows(
                ValidacaoVeiculoException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Cliente inativo"));

        verify(veiculoGateway, never()).existsByPlacaAndClienteId(any(Placa.class), any(UUID.class));
        verify(clienteGateway, times(1)).findById(clienteId);
        verify(veiculoGateway, never()).save(any(Veiculo.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(veiculoGateway, never()).existsByPlacaAndClienteId(any(Placa.class), any(UUID.class));
        verify(clienteGateway, never()).findById(any(UUID.class));
        verify(veiculoGateway, never()).save(any(Veiculo.class));
    }
}
