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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de BuscarMROUseCase - Application Layer")
class BuscarMROUseCaseTest {

    @Mock
    private MROGateway gateway;

    @InjectMocks
    private BuscarMROUseCase useCase;

    private UUID mroId;
    private MRO mro;

    @BeforeEach
    void setUp() {
        mroId = UUID.randomUUID();

        mro = MRO.criar(
                "Óleo Motor 5W30",
                "MRO-TESTE",
                "Óleo para motor automotivo",
                TipoMRO.INSUMO,
                100,
                new BigDecimal("45.90")
        );
    }

    @Test
    @DisplayName("Deve buscar MRO com sucesso")
    void deveBuscarMROComSucesso() {
        // Arrange
        when(gateway.findById(mroId)).thenReturn(Optional.of(mro));

        // Act
        MROResponse response = useCase.execute(mroId);

        // Assert
        assertNotNull(response);
        assertEquals("Óleo Motor 5W30", response.getNome());
        assertEquals("Óleo para motor automotivo", response.getDescricao());
        assertEquals("INSUMO", response.getTipo());
        assertEquals(100, response.getQuantidadeEstoque());
        assertEquals(new BigDecimal("45.90"), response.getPrecoUnitario());
        assertTrue(response.getAtivo());

        verify(gateway, times(1)).findById(mroId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando MRO não encontrado")
    void deveLancarExcecaoQuandoMRONaoEncontrado() {
        // Arrange
        when(gateway.findById(mroId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(mroId)
        );

        assertTrue(exception.getMessage().contains("MRO não encontrado"));

        verify(gateway, times(1)).findById(mroId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void deveLancarExcecaoQuandoIdENulo() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> useCase.execute(null));

        verify(gateway, never()).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha")
    void devePropagarExcecaoQuandoRepositoryFalha() {
        // Arrange
        when(gateway.findById(mroId)).thenThrow(new RuntimeException("Erro ao buscar no banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(mroId)
        );

        assertEquals("Erro ao buscar no banco", exception.getMessage());

        verify(gateway, times(1)).findById(mroId);
    }

    @Test
    @DisplayName("Deve buscar MRO do tipo PECA")
    void deveBuscarMRODoTipoPeca() {
        // Arrange
        MRO mroPeca = MRO.criar(
                "Filtro de Óleo",
                "MRO-TESTE",
                "Filtro para motor",
                TipoMRO.PECA,
                50,
                new BigDecimal("25.00")
        );

        when(gateway.findById(mroId)).thenReturn(Optional.of(mroPeca));

        // Act
        MROResponse response = useCase.execute(mroId);

        // Assert
        assertNotNull(response);
        assertEquals("PECA", response.getTipo());

        verify(gateway, times(1)).findById(mroId);
    }

    @Test
    @DisplayName("Deve buscar MRO inativo")
    void deveBuscarMROInativo() {
        // Arrange
        MRO mroInativo = MRO.criar(
                "Óleo Motor 5W30",
                "MRO-TESTE",
                "Óleo para motor automotivo",
                TipoMRO.INSUMO,
                100,
                new BigDecimal("45.90")
        );
        mroInativo.desativar();

        when(gateway.findById(mroId)).thenReturn(Optional.of(mroInativo));

        // Act
        MROResponse response = useCase.execute(mroId);

        // Assert
        assertNotNull(response);
        assertFalse(response.getAtivo());

        verify(gateway, times(1)).findById(mroId);
    }

    @Test
    @DisplayName("Deve buscar MRO com estoque zero")
    void deveBuscarMROComEstoqueZero() {
        // Arrange
        MRO mroEstoqueZero = MRO.criar(
                "Graxa Automotiva",
                "MRO-TESTE",
                "Graxa para lubrificação",
                TipoMRO.INSUMO,
                0,
                new BigDecimal("15.50")
        );

        when(gateway.findById(mroId)).thenReturn(Optional.of(mroEstoqueZero));

        // Act
        MROResponse response = useCase.execute(mroId);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getQuantidadeEstoque());

        verify(gateway, times(1)).findById(mroId);
    }
}
