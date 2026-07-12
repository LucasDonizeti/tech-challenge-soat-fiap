package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.os.application.usecases.commands.CancelarServicoCommand;
import com.techchallenge.oficina.os.application.usecases.ports.output.OrdemServicoGateway;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.exceptions.ValidacaoEstoqueException;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusItemServico;
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
@DisplayName("Testes do Use Case de Cancelar Serviço")
class CancelarServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway gateway;

    @InjectMocks
    private CancelarServicoUseCase useCase;

    private OrdemServico ordemServico;
    private ItemServico itemServico;
    private UUID ordemServicoId;
    private UUID itemServicoId;
    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        ordemServicoId = UUID.randomUUID();

        cliente = Cliente.criar(Nome.of("João Silva"), CPF.of("12345678909"), Email.of("joao@email.com"));
        veiculo = new Veiculo(Placa.of("ABC1234"), "Toyota", "Corolla", 2020, "Prata");
        veiculo.setCliente(cliente);

        ordemServico = OrdemServico.criar(cliente, veiculo);

        itemServico = ItemServico.criarComDados(UUID.randomUUID(), "Troca de Óleo", "Troca completa", new BigDecimal("150.00"));
        ordemServico.adicionarItemServico(itemServico);
        itemServicoId = itemServico.getId();

        // Add a second item to test removal
        ItemServico itemServico2 = ItemServico.criarComDados(UUID.randomUUID(), "Troca de Filtro", "Troca de filtro de ar", new BigDecimal("50.00"));
        ordemServico.adicionarItemServico(itemServico2);

        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();
    }

    @Test
    @DisplayName("Deve cancelar serviço com sucesso")
    void deveCancelarServicoComSucesso() {
        // Arrange
        CancelarServicoCommand command = CancelarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(itemServicoId)
                .build();

        when(gateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(gateway.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertEquals(StatusItemServico.CANCELADO, itemServico.getStatus());
        verify(gateway, times(1)).save(ordemServico);
    }

    @Test
    @DisplayName("Deve lançar exceção quando OS não encontrada")
    void deveLancarExcecaoQuandoOSNaoEncontrada() {
        // Arrange
        CancelarServicoCommand command = CancelarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(itemServicoId)
                .build();

        when(gateway.findById(ordemServicoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.execute(command));
        verify(gateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ItemServico não encontrado")
    void deveLancarExcecaoQuandoItemServicoNaoEncontrado() {
        // Arrange
        UUID nonExistentItemId = UUID.randomUUID();
        
        CancelarServicoCommand command = CancelarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(nonExistentItemId)
                .build();

        when(gateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        // Act & Assert
        assertThrows(ItemServicoNaoEncontradoException.class, () -> useCase.execute(command));
        verify(gateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando item já está CONCLUIDO")
    void deveLancarExcecaoQuandoItemJaEstaConcluido() {
        // Arrange
        itemServico.atualizarStatus(StatusItemServico.EM_ANDAMENTO);
        itemServico.atualizarStatus(StatusItemServico.CONCLUIDO);
        
        CancelarServicoCommand command = CancelarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(itemServicoId)
                .build();

        when(gateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        // Act & Assert
        assertThrows(ValidacaoEstoqueException.class, () -> useCase.execute(command));
        verify(gateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando item já está CANCELADO")
    void deveLancarExcecaoQuandoItemJaEstaCancelado() {
        // Arrange
        itemServico.atualizarStatus(StatusItemServico.CANCELADO);
        
        CancelarServicoCommand command = CancelarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(itemServicoId)
                .build();

        when(gateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        // Act & Assert
        assertThrows(ValidacaoEstoqueException.class, () -> useCase.execute(command));
        verify(gateway, never()).save(any());
    }
}
