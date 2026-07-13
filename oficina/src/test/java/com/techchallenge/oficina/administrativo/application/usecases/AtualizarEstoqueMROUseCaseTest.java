package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarEstoqueMROCommand;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de AtualizarEstoqueMROUseCase - Application Layer")
class AtualizarEstoqueMROUseCaseTest {

    @Mock
    private MROGateway gateway;

    @InjectMocks
    private AtualizarEstoqueMROUseCase useCase;

    private UUID mroId;
    private MRO mro;
    private AtualizarEstoqueMROCommand command;
    private Integer novaQuantidade;

    @BeforeEach
    void setUp() {
        mroId = UUID.randomUUID();
        novaQuantidade = 150;
        command = new AtualizarEstoqueMROCommand(mroId, novaQuantidade);

        mro = MRO.criar(
                "Óleo Motor 5W30",
                "Óleo para motor automotivo",
                TipoMRO.INSUMO,
                100,
                new BigDecimal("45.90")
        );
    }

    @Test
    @DisplayName("Deve atualizar estoque do MRO com sucesso")
    void deveAtualizarEstoqueDoMROComSucesso() {
        // Arrange
        when(gateway.findById(mroId)).thenReturn(Optional.of(mro));
        when(gateway.save(any(MRO.class))).thenReturn(mro);

        // Act
        MROResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Óleo Motor 5W30", response.getNome());
        assertEquals(novaQuantidade, response.getQuantidadeEstoque());

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando MRO não encontrado")
    void deveLancarExcecaoQuandoMRONaoEncontrado() {
        // Arrange
        when(gateway.findById(mroId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("MRO não encontrado"));

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, never()).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(gateway, never()).findById(any(UUID.class));
        verify(gateway, never()).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha ao buscar")
    void devePropagarExcecaoQuandoRepositoryFalhaAoBuscar() {
        // Arrange
        when(gateway.findById(mroId)).thenThrow(new RuntimeException("Erro ao buscar no banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Erro ao buscar no banco", exception.getMessage());

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, never()).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha ao salvar")
    void devePropagarExcecaoQuandoRepositoryFalhaAoSalvar() {
        // Arrange
        when(gateway.findById(mroId)).thenReturn(Optional.of(mro));
        when(gateway.save(any(MRO.class))).thenThrow(new RuntimeException("Erro ao salvar no banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Erro ao salvar no banco", exception.getMessage());

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve atualizar estoque para zero")
    void deveAtualizarEstoqueParaZero() {
        // Arrange
        Integer quantidadeZero = 0;
        AtualizarEstoqueMROCommand commandZero = new AtualizarEstoqueMROCommand(mroId, quantidadeZero);

        when(gateway.findById(mroId)).thenReturn(Optional.of(mro));
        when(gateway.save(any(MRO.class))).thenReturn(mro);

        // Act
        MROResponse response = useCase.execute(commandZero);

        // Assert
        assertNotNull(response);
        assertEquals(quantidadeZero, response.getQuantidadeEstoque());

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve atualizar estoque para valor maior")
    void deveAtualizarEstoqueParaValorMaior() {
        // Arrange
        Integer quantidadeMaior = 500;
        AtualizarEstoqueMROCommand commandMaior = new AtualizarEstoqueMROCommand(mroId, quantidadeMaior);

        when(gateway.findById(mroId)).thenReturn(Optional.of(mro));
        when(gateway.save(any(MRO.class))).thenReturn(mro);

        // Act
        MROResponse response = useCase.execute(commandMaior);

        // Assert
        assertNotNull(response);
        assertEquals(quantidadeMaior, response.getQuantidadeEstoque());

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve atualizar estoque para valor menor")
    void deveAtualizarEstoqueParaValorMenor() {
        // Arrange
        Integer quantidadeMenor = 50;
        AtualizarEstoqueMROCommand commandMenor = new AtualizarEstoqueMROCommand(mroId, quantidadeMenor);

        when(gateway.findById(mroId)).thenReturn(Optional.of(mro));
        when(gateway.save(any(MRO.class))).thenReturn(mro);

        // Act
        MROResponse response = useCase.execute(commandMenor);

        // Assert
        assertNotNull(response);
        assertEquals(quantidadeMenor, response.getQuantidadeEstoque());

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, times(1)).save(any(MRO.class));
    }
}
