package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.os.application.usecases.commands.AprovarOrcamentoCommand;
import com.techchallenge.oficina.os.application.usecases.ports.output.OrdemServicoGateway;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
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
@DisplayName("Testes de AprovarOrcamentoUseCase - Application Layer")
class AprovarOrcamentoUseCaseTest {

    @Mock
    private OrdemServicoGateway gateway;

    @InjectMocks
    private AprovarOrcamentoUseCase useCase;

    private AprovarOrcamentoCommand command;
    private OrdemServico ordemServico;
    private ItemServico itemServico;

    @BeforeEach
    void setUp() {
        Cliente cliente = Cliente.criar(Nome.of("João Silva"), CPF.of("52998224725"), Email.of("joao@email.com"));
        Veiculo veiculo = new Veiculo(Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Branco");
        veiculo.setCliente(cliente);

        ordemServico = OrdemServico.criar(cliente, veiculo);

        UUID servicoId = UUID.randomUUID();
        itemServico = ItemServico.criarComDados(servicoId, "Troca de Óleo", "Troca completa de óleo", new BigDecimal("150.00"));
        ordemServico.adicionarItemServico(itemServico);

        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();

        UUID ordemServicoId = ordemServico.getId();
        command = new AprovarOrcamentoCommand(ordemServicoId);
    }

    @Test
    @DisplayName("Deve aprovar orçamento com sucesso")
    void deveAprovarOrcamentoComSucesso() {
        // Arrange
        when(gateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(gateway.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals(ordemServico.getId(), response.getId());
        assertEquals(StatusOS.EM_EXECUCAO.toString(), response.getStatus());

        verify(gateway, times(1)).findById(command.getOrdemServicoId());
        verify(gateway, times(1)).save(any(OrdemServico.class));
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
        verify(gateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não está em AGUARDANDO_APROVACAO")
    void deveLancarExcecaoQuandoOrdemServicoNaoEstaAguardandoAprovacao() {
        // Arrange
        OrdemServico ordemServicoRecebida = OrdemServico.criar(
                Cliente.criar(Nome.of("Maria Silva"), CPF.of("98765432100"), Email.of("maria@email.com")),
                new Veiculo(Placa.of("XYZ5678"), "Chevrolet", "Onix", 2021, "Prata")
        );
        when(gateway.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServicoRecebida));

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Só é possível aprovar orçamento quando a OS está no status AGUARDANDO_APROVACAO"));

        verify(gateway, times(1)).findById(command.getOrdemServicoId());
        verify(gateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(gateway, never()).findById(any());
        verify(gateway, never()).save(any());
    }
}
