package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.output.ServicoGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - BuscarServicoUseCase")
class BuscarServicoUseCaseTest {

    @Mock
    private ServicoGateway gateway;

    @InjectMocks
    private BuscarServicoUseCase useCase;

    private Servico servico;
    private UUID servicoId;

    @BeforeEach
    void setUp() {
        servicoId = UUID.randomUUID();
        servico = Servico.criar(
                "Troca de Óleo",
                "COD-TESTE",
                "Troca de óleo sintético",
                new BigDecimal("150.00")
        );
    }

    @Test
    @DisplayName("Deve buscar serviço por ID com sucesso")
    void deveBuscarServicoPorIdComSucesso() {
        // Arrange
        when(gateway.findById(servicoId)).thenReturn(Optional.of(servico));

        // Act
        ServicoResponse result = useCase.execute(servicoId);

        // Assert
        assertNotNull(result);
        assertEquals(servico.getId(), result.getId());
        assertEquals("Troca de Óleo", result.getNome());
        assertEquals("Troca de óleo sintético", result.getDescricao());
        assertEquals(new BigDecimal("150.00"), result.getPreco());
        assertTrue(result.getAtivo());
        verify(gateway, times(1)).findById(servicoId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando serviço não encontrado")
    void deveLancarExcecaoQuandoServicoNaoEncontrado() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(gateway.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(idInexistente)
        );

        assertTrue(exception.getMessage().contains("Serviço não encontrado"));
        verify(gateway, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("Deve retornar response com todos os dados do serviço")
    void deveRetornarResponseComTodosOsDadosDoServico() {
        // Arrange
        when(gateway.findById(servicoId)).thenReturn(Optional.of(servico));

        // Act
        ServicoResponse result = useCase.execute(servicoId);

        // Assert
        assertNotNull(result);
        assertEquals(servico.getId(), result.getId());
        assertEquals(servico.getNome(), result.getNome());
        assertEquals(servico.getDescricao(), result.getDescricao());
        assertEquals(servico.getPreco(), result.getPreco());
        assertEquals(servico.getAtivo(), result.getAtivo());
        assertEquals(servico.getCriadoEm(), result.getCriadoEm());
        assertEquals(servico.getAtualizadoEm(), result.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve buscar serviço inativo")
    void deveBuscarServicoInativo() {
        // Arrange
        servico.desativar();
        when(gateway.findById(servicoId)).thenReturn(Optional.of(servico));

        // Act
        ServicoResponse result = useCase.execute(servicoId);

        // Assert
        assertNotNull(result);
        assertFalse(result.getAtivo());
        verify(gateway, times(1)).findById(servicoId);
    }

    @Test
    @DisplayName("Deve buscar serviço com preço atualizado")
    void deveBuscarServicoComPrecoAtualizado() {
        // Arrange
        servico.atualizarPreco(new BigDecimal("180.00"));
        when(gateway.findById(servicoId)).thenReturn(Optional.of(servico));

        // Act
        ServicoResponse result = useCase.execute(servicoId);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("180.00"), result.getPreco());
        verify(gateway, times(1)).findById(servicoId);
    }

    @Test
    @DisplayName("Deve buscar serviço com dados atualizados")
    void deveBuscarServicoComDadosAtualizados() {
        // Arrange
        servico.atualizarDados("Troca de Óleo Premium", "Troca de óleo sintético premium");
        when(gateway.findById(servicoId)).thenReturn(Optional.of(servico));

        // Act
        ServicoResponse result = useCase.execute(servicoId);

        // Assert
        assertNotNull(result);
        assertEquals("Troca de Óleo Premium", result.getNome());
        assertEquals("Troca de óleo sintético premium", result.getDescricao());
        verify(gateway, times(1)).findById(servicoId);
    }

    @Test
    @DisplayName("Deve manter timestamps no response")
    void deveManterTimestampsNoResponse() {
        // Arrange
        when(gateway.findById(servicoId)).thenReturn(Optional.of(servico));

        // Act
        ServicoResponse result = useCase.execute(servicoId);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getCriadoEm());
        assertNotNull(result.getAtualizadoEm());
        assertEquals(servico.getCriadoEm(), result.getCriadoEm());
        assertEquals(servico.getAtualizadoEm(), result.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve chamar repository apenas uma vez")
    void deveChamarRepositoryApenasUmaVez() {
        // Arrange
        when(gateway.findById(servicoId)).thenReturn(Optional.of(servico));

        // Act
        useCase.execute(servicoId);

        // Assert
        verify(gateway, times(1)).findById(servicoId);
        verifyNoMoreInteractions(gateway);
    }
}
