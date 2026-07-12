package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.AtualizarQuantidadeMROCommand;
import com.techchallenge.oficina.os.application.usecases.ports.output.OrdemServicoGateway;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemMRO;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Testes de AtualizarQuantidadeMROUseCase - Application Layer")
class AtualizarQuantidadeMROUseCaseTest {

    @Mock
    private OrdemServicoGateway gateway;

    @InjectMocks
    private AtualizarQuantidadeMROUseCase useCase;

    private UUID ordemServicoId;
    private UUID itemServicoId;
    private UUID itemMroId;
    private AtualizarQuantidadeMROCommand command;
    private OrdemServico ordemServico;
    private ItemServico itemServico;
    private ItemMRO itemMRO;

    @BeforeEach
    void setUp() {
        ordemServicoId = UUID.randomUUID();
        itemServicoId = UUID.randomUUID();
        itemMroId = UUID.randomUUID();
        command = new AtualizarQuantidadeMROCommand(ordemServicoId, itemServicoId, itemMroId, 5);
        
        ordemServico = mock(OrdemServico.class);
        itemServico = mock(ItemServico.class);
        itemMRO = mock(ItemMRO.class);
        
        when(itemServico.getId()).thenReturn(itemServicoId);
        when(itemMRO.getId()).thenReturn(itemMroId);
    }

    @Test
    @DisplayName("Deve atualizar quantidade de MRO com sucesso")
    void deveAtualizarQuantidadeMROComSucesso() {
        // Arrange
        when(ordemServico.getItensServico()).thenReturn(List.of(itemServico));
        when(itemServico.getMrosServicos()).thenReturn(List.of(itemMRO));
        when(gateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(gateway.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        verify(gateway, times(1)).findById(ordemServicoId);
        verify(itemMRO, times(1)).atualizarQuantidade(5);
        verify(itemServico, times(1)).atualizarValorMRO();
        verify(gateway, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não encontrada")
    void deveLancarExcecaoQuandoOrdemServicoNaoEncontrada() {
        // Arrange
        when(gateway.findById(ordemServicoId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Ordem de Serviço não encontrada"));
        verify(gateway, times(1)).findById(ordemServicoId);
        verify(ordemServico, never()).getItensServico();
        verify(gateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando item de serviço não encontrado")
    void deveLancarExcecaoQuandoItemServicoNaoEncontrado() {
        // Arrange
        when(ordemServico.getItensServico()).thenReturn(new ArrayList<>());
        when(gateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Item de Serviço não encontrado"));
        verify(gateway, times(1)).findById(ordemServicoId);
        verify(ordemServico, times(1)).getItensServico();
        verify(gateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando item de MRO não encontrado")
    void deveLancarExcecaoQuandoItemMRONaoEncontrado() {
        // Arrange
        when(ordemServico.getItensServico()).thenReturn(List.of(itemServico));
        when(itemServico.getMrosServicos()).thenReturn(new ArrayList<>());
        when(gateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Item de MRO não encontrado com ID"));
        verify(gateway, times(1)).findById(ordemServicoId);
        verify(ordemServico, times(1)).getItensServico();
        verify(itemServico, times(1)).getMrosServicos();
        verify(gateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar quantidade com valor 1")
    void deveAtualizarQuantidadeComValor1() {
        // Arrange
        AtualizarQuantidadeMROCommand command1 = new AtualizarQuantidadeMROCommand(ordemServicoId, itemServicoId, itemMroId, 1);
        when(ordemServico.getItensServico()).thenReturn(List.of(itemServico));
        when(itemServico.getMrosServicos()).thenReturn(List.of(itemMRO));
        when(gateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(gateway.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(command1);

        // Assert
        assertNotNull(response);
        verify(itemMRO, times(1)).atualizarQuantidade(1);
        verify(itemServico, times(1)).atualizarValorMRO();
        verify(gateway, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve atualizar quantidade com valor grande")
    void deveAtualizarQuantidadeComValorGrande() {
        // Arrange
        AtualizarQuantidadeMROCommand commandLarge = new AtualizarQuantidadeMROCommand(ordemServicoId, itemServicoId, itemMroId, 100);
        when(ordemServico.getItensServico()).thenReturn(List.of(itemServico));
        when(itemServico.getMrosServicos()).thenReturn(List.of(itemMRO));
        when(gateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(gateway.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(commandLarge);

        // Assert
        assertNotNull(response);
        verify(itemMRO, times(1)).atualizarQuantidade(100);
        verify(itemServico, times(1)).atualizarValorMRO();
        verify(gateway, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoENulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(gateway, never()).findById(any());
        verify(gateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha ao salvar")
    void devePropagarExcecaoQuandoRepositoryFalhaAoSalvar() {
        // Arrange
        when(ordemServico.getItensServico()).thenReturn(List.of(itemServico));
        when(itemServico.getMrosServicos()).thenReturn(List.of(itemMRO));
        when(gateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(gateway.save(any(OrdemServico.class)))
                .thenThrow(new RuntimeException("Erro ao salvar no banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Erro ao salvar no banco", exception.getMessage());
        verify(gateway, times(1)).findById(ordemServicoId);
        verify(itemMRO, times(1)).atualizarQuantidade(5);
        verify(itemServico, times(1)).atualizarValorMRO();
        verify(gateway, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve chamar atualizarValorMRO após atualizarQuantidade")
    void deveChamarAtualizarValorMROAposAtualizarQuantidade() {
        // Arrange
        when(ordemServico.getItensServico()).thenReturn(List.of(itemServico));
        when(itemServico.getMrosServicos()).thenReturn(List.of(itemMRO));
        when(gateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(gateway.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        useCase.execute(command);

        // Assert
        verify(itemMRO, times(1)).atualizarQuantidade(5);
        verify(itemServico, times(1)).atualizarValorMRO();
    }
}
