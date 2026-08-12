package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.output.ServicoGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoServicoException;
import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
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
@DisplayName("Testes de InativarServicoUseCase - Application Layer")
class InativarServicoUseCaseTest {

    @Mock
    private ServicoGateway gateway;

    @InjectMocks
    private InativarServicoUseCase inativarServicoUseCase;

    private UUID servicoId;
    private Servico servico;

    @BeforeEach
    void setUp() {
        servicoId = UUID.randomUUID();
        servico = Servico.criar("Troca de Óleo", "COD-TESTE", "Descrição", new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("Deve inativar serviço com sucesso")
    void deveInativarServicoComSucesso() {
        // Arrange
        when(gateway.findById(servicoId)).thenReturn(Optional.of(servico));
        when(gateway.save(any(Servico.class))).thenReturn(servico);

        // Act
        ServicoResponse response = inativarServicoUseCase.execute(servicoId);

        // Assert
        assertNotNull(response);
        assertEquals("Troca de Óleo", response.getNome());
        assertEquals("Descrição", response.getDescricao());
        assertEquals(new BigDecimal("150.00"), response.getPreco());
        assertFalse(response.getAtivo());
        verify(gateway, times(1)).findById(servicoId);
        verify(gateway, times(1)).save(any(Servico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando serviço não encontrado")
    void deveLancarExcecaoQuandoServicoNaoEncontrado() {
        // Arrange
        when(gateway.findById(servicoId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inativarServicoUseCase.execute(servicoId)
        );

        assertEquals("Serviço não encontrado: " + servicoId, exception.getMessage());
        verify(gateway, times(1)).findById(servicoId);
        verify(gateway, never()).save(any(Servico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando servicoId é nulo")
    void deveLancarExcecaoQuandoServicoIdNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> inativarServicoUseCase.execute(null));
        verify(gateway, never()).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando serviço já está inativo")
    void deveLancarExcecaoQuandoServicoJaEstaInativo() {
        // Arrange
        servico.desativar();
        when(gateway.findById(servicoId)).thenReturn(Optional.of(servico));

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> inativarServicoUseCase.execute(servicoId)
        );

        assertEquals("Serviço já está inativo", exception.getMessage());
        verify(gateway, times(1)).findById(servicoId);
        verify(gateway, never()).save(any(Servico.class));
    }
}
