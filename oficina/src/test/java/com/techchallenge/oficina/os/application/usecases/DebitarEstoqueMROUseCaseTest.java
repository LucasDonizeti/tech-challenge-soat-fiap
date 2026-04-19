package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.DebitarEstoqueMROCommand;
import com.techchallenge.oficina.os.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.os.domain.model.entities.MRO;
import com.techchallenge.oficina.os.domain.model.valueobjects.TipoMRO;
import com.techchallenge.oficina.os.domain.repositories.MRORepository;
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
    private MRORepository repository;

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
        when(repository.findById(mroId)).thenReturn(Optional.of(mro));
        when(repository.save(any(MRO.class))).thenReturn(mro);

        // Act
        MROResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals(mroId, response.getId());
        assertEquals(90, response.getQuantidadeEstoque());

        verify(repository, times(1)).findById(mroId);
        verify(repository, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando MRO não é encontrado")
    void deveLancarExcecaoQuandoMroNaoEncontrado() {
        // Arrange
        UUID idNaoExistente = UUID.randomUUID();
        DebitarEstoqueMROCommand commandNaoExistente = new DebitarEstoqueMROCommand(idNaoExistente, 10);
        when(repository.findById(idNaoExistente)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(commandNaoExistente)
        );

        assertTrue(exception.getMessage().contains("MRO não encontrado"));

        verify(repository, times(1)).findById(idNaoExistente);
        verify(repository, never()).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(repository, never()).findById(any());
        verify(repository, never()).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve debitar quantidade 1")
    void deveDebitarQuantidade1() {
        // Arrange
        DebitarEstoqueMROCommand command1 = new DebitarEstoqueMROCommand(mroId, 1);
        when(repository.findById(mroId)).thenReturn(Optional.of(mro));
        when(repository.save(any(MRO.class))).thenReturn(mro);

        // Act
        MROResponse response = useCase.execute(command1);

        // Assert
        assertNotNull(response);
        assertEquals(99, response.getQuantidadeEstoque());

        verify(repository, times(1)).findById(mroId);
        verify(repository, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve debitar quantidade grande")
    void deveDebitarQuantidadeGrande() {
        // Arrange
        MRO mroGrande = MRO.reconstruir(
                mroId,
                "Produto Grande",
                "Descrição",
                TipoMRO.PECA,
                1000,
                new BigDecimal("10.00"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        DebitarEstoqueMROCommand commandGrande = new DebitarEstoqueMROCommand(mroId, 500);
        when(repository.findById(mroId)).thenReturn(Optional.of(mroGrande));
        when(repository.save(any(MRO.class))).thenReturn(mroGrande);

        // Act
        MROResponse response = useCase.execute(commandGrande);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getQuantidadeEstoque());

        verify(repository, times(1)).findById(mroId);
        verify(repository, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha")
    void devePropagarExcecaoQuandoRepositoryFalha() {
        // Arrange
        when(repository.findById(mroId)).thenThrow(new RuntimeException("Erro de conexão"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Erro de conexão", exception.getMessage());

        verify(repository, times(1)).findById(mroId);
        verify(repository, never()).save(any(MRO.class));
    }
}
