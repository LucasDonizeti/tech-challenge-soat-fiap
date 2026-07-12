package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.EnviarParaDiagnosticoCommand;
import com.techchallenge.oficina.os.application.usecases.ports.output.OrdemServicoGateway;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
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
@DisplayName("Testes de EnviarParaDiagnosticoUseCase - Application Layer")
class EnviarParaDiagnosticoUseCaseTest {

    @Mock
    private OrdemServicoGateway gateway;

    @InjectMocks
    private EnviarParaDiagnosticoUseCase useCase;

    private UUID ordemServicoId;
    private EnviarParaDiagnosticoCommand command;
    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        ordemServicoId = UUID.randomUUID();
        command = new EnviarParaDiagnosticoCommand(ordemServicoId);
        ordemServico = mock(OrdemServico.class);
    }

    @Test
    @DisplayName("Deve enviar ordem de serviço para diagnóstico com sucesso")
    void deveEnviarOrdemServicoParaDiagnosticoComSucesso() {
        // Arrange
        when(gateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(gateway.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        verify(gateway, times(1)).findById(ordemServicoId);
        verify(ordemServico, times(1)).enviarParaDiagnostico();
        verify(gateway, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não encontrada")
    void deveLancarExcecaoQuandoOrdemServicoNaoEncontrada() {
        // Arrange
        when(gateway.findById(ordemServicoId)).thenReturn(Optional.empty());

        // Act & Assert
        OrdemServicoNaoEncontradaException exception = assertThrows(
                OrdemServicoNaoEncontradaException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains(ordemServicoId.toString()));
        verify(gateway, times(1)).findById(ordemServicoId);
        verify(ordemServico, never()).enviarParaDiagnostico();
        verify(gateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoENulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(gateway, never()).findById(any());
        verify(ordemServico, never()).enviarParaDiagnostico();
        verify(gateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar exceção quando domínio falha")
    void devePropagarExcecaoQuandoDominioFalha() {
        // Arrange
        when(gateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        doThrow(new RuntimeException("Status inválido para envio")).when(ordemServico).enviarParaDiagnostico();

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Status inválido para envio", exception.getMessage());
        verify(gateway, times(1)).findById(ordemServicoId);
        verify(ordemServico, times(1)).enviarParaDiagnostico();
        verify(gateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha ao salvar")
    void devePropagarExcecaoQuandoRepositoryFalhaAoSalvar() {
        // Arrange
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
        verify(ordemServico, times(1)).enviarParaDiagnostico();
        verify(gateway, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve chamar enviarParaDiagnostico antes de salvar")
    void deveChamarEnviarParaDiagnosticoAntesDeSalvar() {
        // Arrange
        when(gateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(gateway.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        useCase.execute(command);

        // Assert
        verify(gateway, times(1)).findById(ordemServicoId);
        verify(ordemServico, times(1)).enviarParaDiagnostico();
        verify(gateway, times(1)).save(any(OrdemServico.class));
    }
}
