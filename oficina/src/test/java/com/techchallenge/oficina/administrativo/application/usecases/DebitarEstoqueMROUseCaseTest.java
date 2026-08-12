package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.DebitarEstoqueMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.MROGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.MRO;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de DebitarEstoqueMROUseCase - Application Layer")
class DebitarEstoqueMROUseCaseTest {

    @Mock
    private MROGateway gateway;

    @InjectMocks
    private DebitarEstoqueMROUseCase useCase;

    private MRO mro;
    private UUID mroId;
    private DebitarEstoqueMROCommand command;

    @BeforeEach
    void setUp() {
        mroId = UUID.randomUUID();
        mro = MRO.reconstruir(
                mroId,
                "Óleo Motor 5W30",
                "MRO-TESTE",
                "Óleo para motor automotivo",
                TipoMRO.INSUMO,
                100,
                new BigDecimal("45.90"),
                true,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        command = new DebitarEstoqueMROCommand(mroId, 10);
    }

    @Test
    @DisplayName("Deve debitar estoque do MRO com sucesso")
    void deveDebitarEstoqueMROComSucesso() {
        // Arrange
        when(gateway.findById(mroId)).thenReturn(Optional.of(mro));
        when(gateway.save(any(MRO.class))).thenReturn(mro);

        // Act
        MROResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals(mroId, response.getId());
        assertEquals(90, response.getQuantidadeEstoque());

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando MRO não é encontrado")
    void deveLancarExcecaoQuandoMroNaoEncontrado() {
        // Arrange
        UUID idNaoExistente = UUID.randomUUID();
        DebitarEstoqueMROCommand commandNaoExistente = new DebitarEstoqueMROCommand(idNaoExistente, 10);
        when(gateway.findById(idNaoExistente)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(commandNaoExistente)
        );

        assertTrue(exception.getMessage().contains("MRO não encontrado"));

        verify(gateway, times(1)).findById(idNaoExistente);
        verify(gateway, never()).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(gateway, never()).findById(any());
        verify(gateway, never()).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve debitar quantidade 1")
    void deveDebitarQuantidade1() {
        // Arrange
        DebitarEstoqueMROCommand command1 = new DebitarEstoqueMROCommand(mroId, 1);
        when(gateway.findById(mroId)).thenReturn(Optional.of(mro));
        when(gateway.save(any(MRO.class))).thenReturn(mro);

        // Act
        MROResponse response = useCase.execute(command1);

        // Assert
        assertNotNull(response);
        assertEquals(99, response.getQuantidadeEstoque());

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve debitar quantidade grande")
    void deveDebitarQuantidadeGrande() {
        // Arrange
        MRO mroGrande = MRO.reconstruir(
                mroId,
                "Produto Grande",
                "MRO-TESTE",
                "Descrição",
                TipoMRO.PECA,
                1000,
                new BigDecimal("10.00"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        DebitarEstoqueMROCommand commandGrande = new DebitarEstoqueMROCommand(mroId, 500);
        when(gateway.findById(mroId)).thenReturn(Optional.of(mroGrande));
        when(gateway.save(any(MRO.class))).thenReturn(mroGrande);

        // Act
        MROResponse response = useCase.execute(commandGrande);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getQuantidadeEstoque());

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha")
    void devePropagarExcecaoQuandoRepositoryFalha() {
        // Arrange
        when(gateway.findById(mroId)).thenThrow(new RuntimeException("Erro de conexão"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Erro de conexão", exception.getMessage());

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, never()).save(any(MRO.class));
    }
}
