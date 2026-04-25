package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.os.application.usecases.commands.AdicionarServicoOrdemCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.entities.Servico;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import com.techchallenge.oficina.os.domain.repositories.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de AdicionarServicoOrdemUseCase - Application Layer")
class AdicionarServicoOrdemUseCaseTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private ServicoRepository servicoRepository;

    @InjectMocks
    private AdicionarServicoOrdemUseCase useCase;

    private AdicionarServicoOrdemCommand command;
    private OrdemServico ordemServico;
    private Servico servico;

    @BeforeEach
    void setUp() {
        UUID ordemServicoId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();

        command = new AdicionarServicoOrdemCommand(ordemServicoId, servicoId);

        servico = Servico.criar("Troca de Óleo", "Troca completa de óleo do motor", 
                new BigDecimal("150.00"));

        Cliente cliente = Cliente.criar(Nome.of("João Silva"), CPF.of("52998224725"), Email.of("joao@email.com"));
        Veiculo veiculo = new Veiculo(Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Branco");
        veiculo.setCliente(cliente);

        ordemServico = OrdemServico.criar(cliente, veiculo);
    }

    @Test
    @DisplayName("Deve adicionar serviço à ordem de serviço com sucesso")
    void deveAdicionarServicoAOrdemServicoComSucesso() {
        // Arrange
        when(ordemServicoRepository.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(servicoRepository.findById(command.getServicoId())).thenReturn(Optional.of(servico));
        when(ordemServicoRepository.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals(ordemServico.getId(), response.getId());

        verify(ordemServicoRepository, times(1)).findById(command.getOrdemServicoId());
        verify(servicoRepository, times(1)).findById(command.getServicoId());
        verify(ordemServicoRepository, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não encontrada")
    void deveLancarExcecaoQuandoOrdemServicoNaoEncontrada() {
        // Arrange
        when(ordemServicoRepository.findById(command.getOrdemServicoId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Ordem de Serviço não encontrada"));

        verify(ordemServicoRepository, times(1)).findById(command.getOrdemServicoId());
        verify(servicoRepository, never()).findById(any());
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando serviço não encontrado")
    void deveLancarExcecaoQuandoServicoNaoEncontrado() {
        // Arrange
        when(ordemServicoRepository.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(servicoRepository.findById(command.getServicoId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Serviço não encontrado"));

        verify(ordemServicoRepository, times(1)).findById(command.getOrdemServicoId());
        verify(servicoRepository, times(1)).findById(command.getServicoId());
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(ordemServicoRepository, never()).findById(any());
        verify(servicoRepository, never()).findById(any());
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha")
    void devePropagarExcecaoQuandoRepositoryFalha() {
        // Arrange
        when(ordemServicoRepository.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(servicoRepository.findById(command.getServicoId())).thenReturn(Optional.of(servico));
        when(ordemServicoRepository.save(any(OrdemServico.class))).thenThrow(new RuntimeException("Erro ao salvar no banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Erro ao salvar no banco", exception.getMessage());

        verify(ordemServicoRepository, times(1)).findById(command.getOrdemServicoId());
        verify(servicoRepository, times(1)).findById(command.getServicoId());
        verify(ordemServicoRepository, times(1)).save(any(OrdemServico.class));
    }
}
