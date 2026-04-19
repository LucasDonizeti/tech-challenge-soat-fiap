package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.CriarMROCommand;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de CriarMROUseCase - Application Layer")
class CriarMROUseCaseTest {

    @Mock
    private MRORepository repository;

    @InjectMocks
    private CriarMROUseCase useCase;

    private CriarMROCommand command;
    private MRO mro;

    @BeforeEach
    void setUp() {
        command = new CriarMROCommand(
                "Óleo Motor 5W30",
                "Óleo para motor automotivo",
                TipoMRO.INSUMO,
                100,
                new BigDecimal("45.90")
        );

        mro = MRO.criar(
                "Óleo Motor 5W30",
                "Óleo para motor automotivo",
                TipoMRO.INSUMO,
                100,
                new BigDecimal("45.90")
        );
    }

    @Test
    @DisplayName("Deve criar MRO com sucesso")
    void deveCriarMROComSucesso() {
        // Arrange
        when(repository.save(any(MRO.class))).thenReturn(mro);

        // Act
        MROResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Óleo Motor 5W30", response.getNome());
        assertEquals("Óleo para motor automotivo", response.getDescricao());
        assertEquals("INSUMO", response.getTipo());
        assertEquals(100, response.getQuantidadeEstoque());
        assertEquals(new BigDecimal("45.90"), response.getPrecoUnitario());
        assertTrue(response.getAtivo());

        verify(repository, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve criar MRO com estoque zero quando quantidade não informada")
    void deveCriarMROComEstoqueZeroQuandoQuantidadeNaoInformada() {
        // Arrange
        CriarMROCommand commandZeroEstoque = new CriarMROCommand(
                "Graxa Automotiva",
                "Graxa para lubrificação",
                TipoMRO.INSUMO,
                null,
                new BigDecimal("15.50")
        );

        MRO mroZeroEstoque = MRO.criar(
                "Graxa Automotiva",
                "Graxa para lubrificação",
                TipoMRO.INSUMO,
                null,
                new BigDecimal("15.50")
        );

        when(repository.save(any(MRO.class))).thenReturn(mroZeroEstoque);

        // Act
        MROResponse response = useCase.execute(commandZeroEstoque);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getQuantidadeEstoque());

        verify(repository, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve criar MRO do tipo PECA")
    void deveCriarMRODoTipoPeca() {
        // Arrange
        CriarMROCommand commandPeca = new CriarMROCommand(
                "Filtro de Óleo",
                "Filtro para motor",
                TipoMRO.PECA,
                50,
                new BigDecimal("25.00")
        );

        MRO mroPeca = MRO.criar(
                "Filtro de Óleo",
                "Filtro para motor",
                TipoMRO.PECA,
                50,
                new BigDecimal("25.00")
        );

        when(repository.save(any(MRO.class))).thenReturn(mroPeca);

        // Act
        MROResponse response = useCase.execute(commandPeca);

        // Assert
        assertNotNull(response);
        assertEquals("PECA", response.getTipo());

        verify(repository, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve criar MRO sem descrição")
    void deveCriarMROSemDescricao() {
        // Arrange
        CriarMROCommand commandSemDescricao = new CriarMROCommand(
                "Parafuso M8",
                null,
                TipoMRO.PECA,
                500,
                new BigDecimal("0.50")
        );

        MRO mroSemDescricao = MRO.criar(
                "Parafuso M8",
                null,
                TipoMRO.PECA,
                500,
                new BigDecimal("0.50")
        );

        when(repository.save(any(MRO.class))).thenReturn(mroSemDescricao);

        // Act
        MROResponse response = useCase.execute(commandSemDescricao);

        // Assert
        assertNotNull(response);
        assertNull(response.getDescricao());

        verify(repository, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(repository, never()).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha")
    void devePropagarExcecaoQuandoRepositoryFalha() {
        // Arrange
        when(repository.save(any(MRO.class))).thenThrow(new RuntimeException("Erro ao salvar no banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Erro ao salvar no banco", exception.getMessage());

        verify(repository, times(1)).save(any(MRO.class));
    }
}
