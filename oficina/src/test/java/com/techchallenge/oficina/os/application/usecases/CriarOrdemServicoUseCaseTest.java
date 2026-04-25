package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import com.techchallenge.oficina.administrativo.domain.repositories.VeiculoRepository;
import com.techchallenge.oficina.os.application.usecases.commands.CriarOrdemServicoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
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
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

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
        when(clienteRepository.findById(command.getClienteId())).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(command.getVeiculoId())).thenReturn(Optional.of(veiculo));
        when(ordemServicoRepository.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals(ordemServico.getId(), response.getId());
        assertNotNull(response.getCliente());
        assertNotNull(response.getVeiculo());
        assertNotNull(response.getStatus());

        verify(clienteRepository, times(1)).findById(command.getClienteId());
        verify(veiculoRepository, times(1)).findById(command.getVeiculoId());
        verify(ordemServicoRepository, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não encontrado")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        // Arrange
        when(clienteRepository.findById(command.getClienteId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Cliente não encontrado"));

        verify(clienteRepository, times(1)).findById(command.getClienteId());
        verify(veiculoRepository, never()).findById(any());
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando veículo não encontrado")
    void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        // Arrange
        when(clienteRepository.findById(command.getClienteId())).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(command.getVeiculoId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Veículo não encontrado"));

        verify(clienteRepository, times(1)).findById(command.getClienteId());
        verify(veiculoRepository, times(1)).findById(command.getVeiculoId());
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(clienteRepository, never()).findById(any());
        verify(veiculoRepository, never()).findById(any());
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha")
    void devePropagarExcecaoQuandoRepositoryFalha() {
        // Arrange
        when(clienteRepository.findById(command.getClienteId())).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(command.getVeiculoId())).thenReturn(Optional.of(veiculo));
        when(ordemServicoRepository.save(any(OrdemServico.class))).thenThrow(new RuntimeException("Erro ao salvar no banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Erro ao salvar no banco", exception.getMessage());

        verify(clienteRepository, times(1)).findById(command.getClienteId());
        verify(veiculoRepository, times(1)).findById(command.getVeiculoId());
        verify(ordemServicoRepository, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve criar ordem de serviço com status inicial correto")
    void deveCriarOrdemServicoComStatusInicialCorreto() {
        // Arrange
        when(clienteRepository.findById(command.getClienteId())).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(command.getVeiculoId())).thenReturn(Optional.of(veiculo));
        when(ordemServicoRepository.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response.getStatus());
        assertEquals(ordemServico.getStatus().toString(), response.getStatus());

        verify(ordemServicoRepository, times(1)).save(any(OrdemServico.class));
    }
}
