package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.ports.output.OrdemServicoGateway;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de ListarOrdensServicoPorClienteUseCase - Application Layer")
class ListarOrdensServicoPorClienteUseCaseTest {

    @Mock
    private OrdemServicoGateway gateway;

    @InjectMocks
    private ListarOrdensServicoPorClienteUseCase useCase;

    private UUID clienteId;
    private Pageable pageable;
    private OrdemServico ordemServico1;
    private OrdemServico ordemServico2;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        pageable = PageRequest.of(0, 10);
        ordemServico1 = mock(OrdemServico.class);
        ordemServico2 = mock(OrdemServico.class);
    }

    @Test
    @DisplayName("Deve listar ordens de serviço por cliente com sucesso")
    void deveListarOrdensServicoPorClienteComSucesso() {
        // Arrange
        List<OrdemServico> ordensServico = List.of(ordemServico1, ordemServico2);
        Page<OrdemServico> page = new PageImpl<>(ordensServico);

        when(gateway.findByFilters(eq(clienteId), eq(null), eq(null),
                eq(null), eq(null), any(Pageable.class))).thenReturn(page);

        // Act
        Page<OrdemServicoResponse> response = useCase.execute(clienteId, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getTotalElements());
        verify(gateway, times(1)).findByFilters(eq(clienteId), eq(null), eq(null),
                eq(null), eq(null), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente ID é nulo")
    void deveLancarExcecaoQuandoClienteIdENulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(null, pageable)
        );

        assertEquals("Cliente ID é obrigatório", exception.getMessage());
        verify(gateway, never()).findByFilters(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não há ordens de serviço")
    void deveRetornarPaginaVaziaQuandoNaoHaOrdensServico() {
        // Arrange
        Page<OrdemServico> emptyPage = Page.empty();

        when(gateway.findByFilters(eq(clienteId), eq(null), eq(null),
                eq(null), eq(null), any(Pageable.class))).thenReturn(emptyPage);

        // Act
        Page<OrdemServicoResponse> response = useCase.execute(clienteId, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getTotalElements());
        verify(gateway, times(1)).findByFilters(eq(clienteId), eq(null), eq(null),
                eq(null), eq(null), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve chamar repository com pageable correto")
    void deveChamarRepositoryComPageableCorreto() {
        // Arrange
        Pageable customPageable = PageRequest.of(2, 20);
        Page<OrdemServico> emptyPage = Page.empty();

        when(gateway.findByFilters(eq(clienteId), eq(null), eq(null),
                eq(null), eq(null), eq(customPageable))).thenReturn(emptyPage);

        // Act
        useCase.execute(clienteId, customPageable);

        // Assert
        verify(gateway, times(1)).findByFilters(eq(clienteId), eq(null), eq(null),
                eq(null), eq(null), eq(customPageable));
    }

    @Test
    @DisplayName("Deve converter OrdemServico para OrdemServicoResponse")
    void deveConverterOrdemServicoParaOrdemServicoResponse() {
        // Arrange
        when(ordemServico1.getId()).thenReturn(UUID.randomUUID());
        when(ordemServico1.getStatus()).thenReturn(com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS.RECEBIDA);
        
        List<OrdemServico> ordensServico = List.of(ordemServico1);
        Page<OrdemServico> page = new PageImpl<>(ordensServico);

        when(gateway.findByFilters(eq(clienteId), eq(null), eq(null),
                eq(null), eq(null), any(Pageable.class))).thenReturn(page);

        // Act
        Page<OrdemServicoResponse> response = useCase.execute(clienteId, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertNotNull(response.getContent().get(0));
    }
}
