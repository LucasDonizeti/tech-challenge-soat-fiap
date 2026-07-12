package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.ports.output.ClienteGateway;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.os.application.usecases.commands.CriarOrdemServicoCommand;
import com.techchallenge.oficina.os.application.usecases.ports.output.OrdemServicoGateway;
import com.techchallenge.oficina.os.application.usecases.ports.output.VeiculoGateway;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
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
@DisplayName("Testes de CriarOrdemServicoUseCase - Application Layer")
class CriarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private ClienteGateway clienteGateway;

    @Mock
    private VeiculoGateway veiculoGateway;

    @InjectMocks
    private CriarOrdemServicoUseCase useCase;

    private CriarOrdemServicoCommand command;
    private Cliente cliente;
    private Veiculo veiculo;
    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        command = new CriarOrdemServicoCommand(clienteId, veiculoId);

        cliente = Cliente.criar(Nome.of("João Silva"), CPF.of("52998224725"), Email.of("joao@email.com"));
        veiculo = new Veiculo(Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Branco");
        veiculo.setCliente(cliente);

        ordemServico = OrdemServico.criar(cliente, veiculo);
    }

    @Test
    @DisplayName("Deve criar ordem de serviço com sucesso")
    void deveCriarOrdemServicoComSucesso() {
        // Arrange
        when(clienteGateway.findById(command.getClienteId())).thenReturn(Optional.of(cliente));
        when(veiculoGateway.findById(command.getVeiculoId())).thenReturn(Optional.of(veiculo));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals(ordemServico.getId(), response.getId());
        assertNotNull(response.getCliente());
        assertNotNull(response.getVeiculo());
        assertNotNull(response.getStatus());

        verify(clienteGateway, times(1)).findById(command.getClienteId());
        verify(veiculoGateway, times(1)).findById(command.getVeiculoId());
        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não encontrado")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        // Arrange
        when(clienteGateway.findById(command.getClienteId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Cliente não encontrado"));

        verify(clienteGateway, times(1)).findById(command.getClienteId());
        verify(veiculoGateway, never()).findById(any());
        verify(ordemServicoGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando veículo não encontrado")
    void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        // Arrange
        when(clienteGateway.findById(command.getClienteId())).thenReturn(Optional.of(cliente));
        when(veiculoGateway.findById(command.getVeiculoId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Veículo não encontrado"));

        verify(clienteGateway, times(1)).findById(command.getClienteId());
        verify(veiculoGateway, times(1)).findById(command.getVeiculoId());
        verify(ordemServicoGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(clienteGateway, never()).findById(any());
        verify(veiculoGateway, never()).findById(any());
        verify(ordemServicoGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha")
    void devePropagarExcecaoQuandoRepositoryFalha() {
        // Arrange
        when(clienteGateway.findById(command.getClienteId())).thenReturn(Optional.of(cliente));
        when(veiculoGateway.findById(command.getVeiculoId())).thenReturn(Optional.of(veiculo));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenThrow(new RuntimeException("Erro ao salvar no banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Erro ao salvar no banco", exception.getMessage());

        verify(clienteGateway, times(1)).findById(command.getClienteId());
        verify(veiculoGateway, times(1)).findById(command.getVeiculoId());
        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve criar ordem de serviço com status inicial correto")
    void deveCriarOrdemServicoComStatusInicialCorreto() {
        // Arrange
        when(clienteGateway.findById(command.getClienteId())).thenReturn(Optional.of(cliente));
        when(veiculoGateway.findById(command.getVeiculoId())).thenReturn(Optional.of(veiculo));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response.getStatus());
        assertEquals(ordemServico.getStatus().toString(), response.getStatus());

        verify(ordemServicoGateway, times(1)).save(any(OrdemServico.class));
    }
}
