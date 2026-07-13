package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.BuscarMROUseCase;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.os.application.usecases.commands.AdicionarMROServicoCommand;
import com.techchallenge.oficina.os.application.usecases.ports.output.OrdemServicoGateway;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoStatusInvalidoException;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
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
@DisplayName("Testes de AdicionarMROServicoUseCase - Application Layer")
class AdicionarMROServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway gateway;

    @Mock
    private BuscarMROUseCase buscarMROUseCase;

    @InjectMocks
    private AdicionarMROServicoUseCase useCase;

    private AdicionarMROServicoCommand command;
    private OrdemServico ordemServico;
    private ItemServico itemServico;
    private MROResponse mroResponse;

    @BeforeEach
    void setUp() {
        UUID servicoId = UUID.randomUUID();
        itemServico = ItemServico.criarComDados(servicoId, "Troca de Óleo", "Troca completa de óleo", new BigDecimal("150.00"));

        Cliente cliente = Cliente.criar(Nome.of("João Silva"), CPF.of("52998224725"), Email.of("joao@email.com"));
        Veiculo veiculo = new Veiculo(Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Branco");
        veiculo.setCliente(cliente);

        ordemServico = OrdemServico.criar(cliente, veiculo);
        ordemServico.adicionarItemServico(itemServico);

        UUID ordemServicoId = ordemServico.getId();
        UUID itemServicoId = itemServico.getId();
        UUID mroId = UUID.randomUUID();

        command = new AdicionarMROServicoCommand(ordemServicoId, itemServicoId, mroId, 5);

        mroResponse = MROResponse.builder()
                .id(mroId)
                .nome("Óleo Motor")
                .descricao("Óleo para motor")
                .tipo("INSUMO")
                .quantidadeEstoque(100)
                .precoUnitario(new BigDecimal("45.90"))
                .ativo(true)
                .build();
    }

    @Test
    @DisplayName("Deve adicionar MRO ao serviço com sucesso quando status é RECEBIDA")
    void deveAdicionarMROAoServicoComSucesso() {
        // Arrange
        when(gateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(buscarMROUseCase.execute(command.getMroId())).thenReturn(mroResponse);
        when(gateway.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals(ordemServico.getId(), response.getId());

        verify(gateway, times(1)).findById(command.getOrdemServicoId());
        verify(buscarMROUseCase, times(1)).execute(command.getMroId());
        verify(gateway, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve adicionar MRO ao serviço com sucesso quando status é EM_DIAGNOSTICO")
    void deveAdicionarMROAoServicoComSucessoEmDiagnostico() {
        // Arrange
        ordemServico.enviarParaDiagnostico();
        when(gateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(buscarMROUseCase.execute(command.getMroId())).thenReturn(mroResponse);
        when(gateway.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals(ordemServico.getId(), response.getId());

        verify(gateway, times(1)).findById(command.getOrdemServicoId());
        verify(buscarMROUseCase, times(1)).execute(command.getMroId());
        verify(gateway, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não está no status RECEBIDA ou EM_DIAGNOSTICO")
    void deveLancarExcecaoQuandoOrdemServicoNaoEstaRecebida() {
        // Arrange
        ordemServico.atualizarStatus(StatusOS.EM_EXECUCAO);
        when(gateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));

        // Act & Assert
        OrdemServicoStatusInvalidoException exception = assertThrows(
                OrdemServicoStatusInvalidoException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Só é possível adicionar MROs quando a Ordem de Serviço está nos status RECEBIDA ou EM_DIAGNOSTICO"));

        verify(gateway, times(1)).findById(command.getOrdemServicoId());
        verify(buscarMROUseCase, never()).execute(any());
        verify(gateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não encontrada")
    void deveLancarExcecaoQuandoOrdemServicoNaoEncontrada() {
        // Arrange
        when(gateway.findById(command.getOrdemServicoId())).thenReturn(Optional.empty());

        // Act & Assert
        OrdemServicoNaoEncontradaException exception = assertThrows(
                OrdemServicoNaoEncontradaException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Ordem de Serviço não encontrada"));

        verify(gateway, times(1)).findById(command.getOrdemServicoId());
        verify(buscarMROUseCase, never()).execute(any());
        verify(gateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando item de serviço não encontrado")
    void deveLancarExcecaoQuandoItemServicoNaoEncontrado() {
        // Arrange
        Cliente cliente = Cliente.criar(Nome.of("João Silva"), CPF.of("52998224725"), Email.of("joao@email.com"));
        Veiculo veiculo = new Veiculo(Placa.of("XYZ5678"), "Chevrolet", "Onix", 2021, "Prata");
        veiculo.setCliente(cliente);
        OrdemServico ordemServicoSemItem = OrdemServico.criar(cliente, veiculo);

        when(gateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServicoSemItem));

        // Act & Assert
        ItemServicoNaoEncontradoException exception = assertThrows(
                ItemServicoNaoEncontradoException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Item de Serviço não encontrado"));

        verify(gateway, times(1)).findById(command.getOrdemServicoId());
        verify(buscarMROUseCase, never()).execute(any());
        verify(gateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(gateway, never()).findById(any());
        verify(buscarMROUseCase, never()).execute(any());
        verify(gateway, never()).save(any());
    }

}
