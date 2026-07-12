package com.techchallenge.oficina.administrativo.application.usecases;

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
@DisplayName("Testes de AtivarMROUseCase - Application Layer")
class AtivarMROUseCaseTest {

    @Mock
    private MROGateway gateway;

    @InjectMocks
    private AtivarMROUseCase useCase;

    private MRO mro;
    private UUID mroId;

    @BeforeEach
    void setUp() {
        mroId = UUID.randomUUID();
        mro = MRO.reconstruir(
                mroId,
                "Óleo Motor 5W30",
                "Óleo para motor automotivo",
                TipoMRO.INSUMO,
                100,
                new BigDecimal("45.90"),
                false,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve ativar MRO com sucesso")
    void deveAtivarMROComSucesso() {
        // Arrange
        when(gateway.findById(mroId)).thenReturn(Optional.of(mro));
        when(gateway.save(any(MRO.class))).thenReturn(mro);

        // Act
        MROResponse response = useCase.execute(mroId);

        // Assert
        assertNotNull(response);
        assertEquals(mroId, response.getId());
        assertTrue(response.getAtivo());

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando MRO não é encontrado")
    void deveLancarExcecaoQuandoMroNaoEncontrado() {
        // Arrange
        UUID idNaoExistente = UUID.randomUUID();
        when(gateway.findById(idNaoExistente)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(idNaoExistente)
        );

        assertTrue(exception.getMessage().contains("MRO não encontrado"));

        verify(gateway, times(1)).findById(idNaoExistente);
        verify(gateway, never()).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void deveLancarExcecaoQuandoIdENulo() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> useCase.execute(null));

        verify(gateway, times(1)).findById(null);
        verify(gateway, never()).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha")
    void devePropagarExcecaoQuandoRepositoryFalha() {
        // Arrange
        when(gateway.findById(mroId)).thenThrow(new RuntimeException("Erro de conexão"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(mroId)
        );

        assertEquals("Erro de conexão", exception.getMessage());

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, never()).save(any(MRO.class));
    }
}
