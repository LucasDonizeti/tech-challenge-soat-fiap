package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.BuscarServicoUseCase;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.os.application.usecases.commands.AdicionarServicoOrdemCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoStatusInvalidoException;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
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
    private BuscarServicoUseCase buscarServicoUseCase;

    @InjectMocks
    private AdicionarServicoOrdemUseCase useCase;

    private AdicionarServicoOrdemCommand command;
    private OrdemServico ordemServico;
    private ServicoResponse servicoResponse;

    @BeforeEach
    void setUp() {
        UUID ordemServicoId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();

        command = new AdicionarServicoOrdemCommand(ordemServicoId, servicoId);

        servicoResponse = ServicoResponse.builder()
                .id(servicoId)
                .nome("Troca de Óleo")
                .descricao("Troca completa de óleo do motor")
                .preco(new BigDecimal("150.00"))
                .ativo(true)
                .build();

        Cliente cliente = Cliente.criar(Nome.of("João Silva"), CPF.of("52998224725"), Email.of("joao@email.com"));
        Veiculo veiculo = new Veiculo(Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Branco");
        veiculo.setCliente(cliente);

        ordemServico = OrdemServico.criar(cliente, veiculo);
    }

    @Test
    @DisplayName("Deve adicionar serviço à ordem de serviço com sucesso quando status é RECEBIDA")
    void deveAdicionarServicoAOrdemServicoComSucesso() {
        // Arrange
        when(ordemServicoRepository.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(buscarServicoUseCase.execute(command.getServicoId())).thenReturn(servicoResponse);
        when(ordemServicoRepository.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals(ordemServico.getId(), response.getId());

        verify(ordemServicoRepository, times(1)).findById(command.getOrdemServicoId());
        verify(buscarServicoUseCase, times(1)).execute(command.getServicoId());
        verify(ordemServicoRepository, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não está no status RECEBIDA")
    void deveLancarExcecaoQuandoOrdemServicoNaoEstaRecebida() {
        // Arrange
        ordemServico.atualizarStatus(StatusOS.EM_DIAGNOSTICO);
        when(ordemServicoRepository.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));

        // Act & Assert
        OrdemServicoStatusInvalidoException exception = assertThrows(
                OrdemServicoStatusInvalidoException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Só é possível adicionar serviços quando a Ordem de Serviço está no status RECEBIDA"));

        verify(ordemServicoRepository, times(1)).findById(command.getOrdemServicoId());
        verify(buscarServicoUseCase, never()).execute(any());
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não encontrada")
    void deveLancarExcecaoQuandoOrdemServicoNaoEncontrada() {
        // Arrange
        when(ordemServicoRepository.findById(command.getOrdemServicoId())).thenReturn(Optional.empty());

        // Act & Assert
        OrdemServicoNaoEncontradaException exception = assertThrows(
                OrdemServicoNaoEncontradaException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Ordem de Serviço não encontrada"));

        verify(ordemServicoRepository, times(1)).findById(command.getOrdemServicoId());
        verify(buscarServicoUseCase, never()).execute(any());
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(ordemServicoRepository, never()).findById(any());
        verify(buscarServicoUseCase, never()).execute(any());
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha")
    void devePropagarExcecaoQuandoRepositoryFalha() {
        // Arrange
        when(ordemServicoRepository.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(buscarServicoUseCase.execute(command.getServicoId())).thenReturn(servicoResponse);
        when(ordemServicoRepository.save(any(OrdemServico.class))).thenThrow(new RuntimeException("Erro ao salvar no banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Erro ao salvar no banco", exception.getMessage());

        verify(ordemServicoRepository, times(1)).findById(command.getOrdemServicoId());
        verify(buscarServicoUseCase, times(1)).execute(command.getServicoId());
        verify(ordemServicoRepository, times(1)).save(any(OrdemServico.class));
    }
}
