package com.techchallenge.oficina.os.application.usecases;

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
@DisplayName("Testes de BuscarOrdemServicoUseCase - Application Layer")
class BuscarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway gateway;

    @InjectMocks
    private BuscarOrdemServicoUseCase useCase;

    private UUID ordemServicoId;
    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        ordemServicoId = UUID.randomUUID();
        ordemServico = mock(OrdemServico.class);
    }

    @Test
    @DisplayName("Deve buscar ordem de serviço com sucesso")
    void deveBuscarOrdemServicoComSucesso() {
        // Arrange
        when(gateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        // Act
        OrdemServicoResponse response = useCase.execute(ordemServicoId);

        // Assert
        assertNotNull(response);
        verify(gateway, times(1)).findById(ordemServicoId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não encontrada")
    void deveLancarExcecaoQuandoOrdemServicoNaoEncontrada() {
        // Arrange
        when(gateway.findById(ordemServicoId)).thenReturn(Optional.empty());

        // Act & Assert
        OrdemServicoNaoEncontradaException exception = assertThrows(
                OrdemServicoNaoEncontradaException.class,
                () -> useCase.execute(ordemServicoId)
        );

        assertTrue(exception.getMessage().contains(ordemServicoId.toString()));
        verify(gateway, times(1)).findById(ordemServicoId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void deveLancarExcecaoQuandoIdENulo() {
        // Act & Assert
        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.execute(null));

        verify(gateway, times(1)).findById(null);
    }

    @Test
    @DisplayName("Deve buscar ordem de serviço com UUID válido")
    void deveBuscarOrdemServicoComUUIDValido() {
        // Arrange
        UUID validId = UUID.randomUUID();
        when(gateway.findById(validId)).thenReturn(Optional.of(ordemServico));

        // Act
        OrdemServicoResponse response = useCase.execute(validId);

        // Assert
        assertNotNull(response);
        verify(gateway, times(1)).findById(validId);
    }

    @Test
    @DisplayName("Deve chamar repository apenas uma vez")
    void deveChamarRepositoryApenasUmaVez() {
        // Arrange
        when(gateway.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        // Act
        useCase.execute(ordemServicoId);

        // Assert
        verify(gateway, times(1)).findById(ordemServicoId);
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha")
    void devePropagarExcecaoQuandoRepositoryFalha() {
        // Arrange
        when(gateway.findById(ordemServicoId))
                .thenThrow(new RuntimeException("Erro ao buscar no banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(ordemServicoId)
        );

        assertEquals("Erro ao buscar no banco", exception.getMessage());
        verify(gateway, times(1)).findById(ordemServicoId);
    }
}
