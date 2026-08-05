package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarPrecoMROCommand;
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
@DisplayName("Testes de AtualizarPrecoMROUseCase - Application Layer")
class AtualizarPrecoMROUseCaseTest {

    @Mock
    private MROGateway gateway;

    @InjectMocks
    private AtualizarPrecoMROUseCase useCase;

    private UUID mroId;
    private MRO mro;
    private AtualizarPrecoMROCommand command;
    private BigDecimal novoPreco;

    @BeforeEach
    void setUp() {
        mroId = UUID.randomUUID();
        novoPreco = new BigDecimal("55.90");
        command = new AtualizarPrecoMROCommand(mroId, novoPreco);

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
    @DisplayName("Deve atualizar preço do MRO com sucesso")
    void deveAtualizarPrecoDoMROComSucesso() {
        // Arrange
        when(gateway.findById(mroId)).thenReturn(Optional.of(mro));
        when(gateway.save(any(MRO.class))).thenReturn(mro);

        // Act
        MROResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Óleo Motor 5W30", response.getNome());
        assertEquals(novoPreco, response.getPrecoUnitario());

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
    @DisplayName("Deve atualizar preço para valor maior")
    void deveAtualizarPrecoParaValorMaior() {
        // Arrange
        BigDecimal precoMaior = new BigDecimal("99.99");
        AtualizarPrecoMROCommand commandPrecoMaior = new AtualizarPrecoMROCommand(mroId, precoMaior);

        when(gateway.findById(mroId)).thenReturn(Optional.of(mro));
        when(gateway.save(any(MRO.class))).thenReturn(mro);

        // Act
        MROResponse response = useCase.execute(commandPrecoMaior);

        // Assert
        assertNotNull(response);
        assertEquals(precoMaior, response.getPrecoUnitario());

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve atualizar preço para valor menor")
    void deveAtualizarPrecoParaValorMenor() {
        // Arrange
        BigDecimal precoMenor = new BigDecimal("25.00");
        AtualizarPrecoMROCommand commandPrecoMenor = new AtualizarPrecoMROCommand(mroId, precoMenor);

        when(gateway.findById(mroId)).thenReturn(Optional.of(mro));
        when(gateway.save(any(MRO.class))).thenReturn(mro);

        // Act
        MROResponse response = useCase.execute(commandPrecoMenor);

        // Assert
        assertNotNull(response);
        assertEquals(precoMenor, response.getPrecoUnitario());

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, times(1)).save(any(MRO.class));
    }
}
