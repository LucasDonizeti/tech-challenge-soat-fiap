package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.AtualizarObservacoesServicoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoStatusInvalidoException;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
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
@DisplayName("Testes de AtualizarObservacoesServicoUseCase - Application Layer")
class AtualizarObservacoesServicoUseCaseTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @InjectMocks
    private AtualizarObservacoesServicoUseCase useCase;

    private UUID ordemServicoId;
    private UUID itemServicoId;
    private AtualizarObservacoesServicoCommand command;
    private OrdemServico ordemServico;
    private ItemServico itemServico;

    @BeforeEach
    void setUp() {
        ordemServicoId = UUID.randomUUID();
        itemServicoId = UUID.randomUUID();
        command = new AtualizarObservacoesServicoCommand(ordemServicoId, itemServicoId, "Nova observação");
        
        ordemServico = mock(OrdemServico.class);
        itemServico = mock(ItemServico.class);
        
        when(itemServico.getId()).thenReturn(itemServicoId);
    }

    @Test
    @DisplayName("Deve atualizar observações com sucesso quando status é RECEBIDA")
    void deveAtualizarObservacoesComSucessoQuandoStatusERecebida() {
        // Arrange
        when(ordemServico.getStatus()).thenReturn(StatusOS.RECEBIDA);
        when(ordemServico.getItensServico()).thenReturn(List.of(itemServico));
        when(ordemServicoRepository.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoRepository.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        verify(ordemServicoRepository, times(1)).findById(ordemServicoId);
        verify(itemServico, times(1)).atualizarObservacoes("Nova observação");
        verify(ordemServicoRepository, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve atualizar observações com sucesso quando status é EM_DIAGNOSTICO")
    void deveAtualizarObservacoesComSucessoQuandoStatusEEmDiagnostico() {
        // Arrange
        when(ordemServico.getStatus()).thenReturn(StatusOS.EM_DIAGNOSTICO);
        when(ordemServico.getItensServico()).thenReturn(List.of(itemServico));
        when(ordemServicoRepository.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoRepository.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        verify(ordemServicoRepository, times(1)).findById(ordemServicoId);
        verify(itemServico, times(1)).atualizarObservacoes("Nova observação");
        verify(ordemServicoRepository, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não encontrada")
    void deveLancarExcecaoQuandoOrdemServicoNaoEncontrada() {
        // Arrange
        when(ordemServicoRepository.findById(ordemServicoId)).thenReturn(Optional.empty());

        // Act & Assert
        OrdemServicoNaoEncontradaException exception = assertThrows(
                OrdemServicoNaoEncontradaException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains(ordemServicoId.toString()));
        verify(ordemServicoRepository, times(1)).findById(ordemServicoId);
        verify(ordemServico, never()).getStatus();
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando status é inválido")
    void deveLancarExcecaoQuandoStatusEInvalido() {
        // Arrange
        when(ordemServico.getStatus()).thenReturn(StatusOS.FINALIZADA);
        when(ordemServicoRepository.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        // Act & Assert
        OrdemServicoStatusInvalidoException exception = assertThrows(
                OrdemServicoStatusInvalidoException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Só é possível atualizar observações quando a Ordem de Serviço está nos status RECEBIDA ou EM_DIAGNOSTICO"));
        verify(ordemServicoRepository, times(1)).findById(ordemServicoId);
        verify(ordemServico, atLeastOnce()).getStatus();
        verify(ordemServico, never()).getItensServico();
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando item de serviço não encontrado")
    void deveLancarExcecaoQuandoItemServicoNaoEncontrado() {
        // Arrange
        when(ordemServico.getStatus()).thenReturn(StatusOS.RECEBIDA);
        when(ordemServico.getItensServico()).thenReturn(new ArrayList<>());
        when(ordemServicoRepository.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        // Act & Assert
        ItemServicoNaoEncontradoException exception = assertThrows(
                ItemServicoNaoEncontradoException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains(itemServicoId.toString()));
        verify(ordemServicoRepository, times(1)).findById(ordemServicoId);
        verify(ordemServico, times(1)).getStatus();
        verify(ordemServico, times(1)).getItensServico();
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar observações com valor nulo")
    void deveAtualizarObservacoesComValorNulo() {
        // Arrange
        AtualizarObservacoesServicoCommand commandNull = new AtualizarObservacoesServicoCommand(ordemServicoId, itemServicoId, null);
        when(ordemServico.getStatus()).thenReturn(StatusOS.RECEBIDA);
        when(ordemServico.getItensServico()).thenReturn(List.of(itemServico));
        when(ordemServicoRepository.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoRepository.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(commandNull);

        // Assert
        assertNotNull(response);
        verify(itemServico, times(1)).atualizarObservacoes(null);
        verify(ordemServicoRepository, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve atualizar observações com valor vazio")
    void deveAtualizarObservacoesComValorVazio() {
        // Arrange
        AtualizarObservacoesServicoCommand commandEmpty = new AtualizarObservacoesServicoCommand(ordemServicoId, itemServicoId, "");
        when(ordemServico.getStatus()).thenReturn(StatusOS.EM_DIAGNOSTICO);
        when(ordemServico.getItensServico()).thenReturn(List.of(itemServico));
        when(ordemServicoRepository.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoRepository.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(commandEmpty);

        // Assert
        assertNotNull(response);
        verify(itemServico, times(1)).atualizarObservacoes("");
        verify(ordemServicoRepository, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoENulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(ordemServicoRepository, never()).findById(any());
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha ao salvar")
    void devePropagarExcecaoQuandoRepositoryFalhaAoSalvar() {
        // Arrange
        when(ordemServico.getStatus()).thenReturn(StatusOS.RECEBIDA);
        when(ordemServico.getItensServico()).thenReturn(List.of(itemServico));
        when(ordemServicoRepository.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoRepository.save(any(OrdemServico.class)))
                .thenThrow(new RuntimeException("Erro ao salvar no banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Erro ao salvar no banco", exception.getMessage());
        verify(ordemServicoRepository, times(1)).findById(ordemServicoId);
        verify(itemServico, times(1)).atualizarObservacoes("Nova observação");
        verify(ordemServicoRepository, times(1)).save(any(OrdemServico.class));
    }
}
