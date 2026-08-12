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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de ListarMROsUseCase - Application Layer")
class ListarMROsUseCaseTest {

    @Mock
    private MROGateway gateway;

    @InjectMocks
    private ListarMROsUseCase useCase;

    private Pageable pageable;
    private List<MRO> mros;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
        
        MRO mro1 = MRO.reconstruir(
                UUID.randomUUID(),
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

        MRO mro2 = MRO.reconstruir(
                UUID.randomUUID(),
                "Filtro de Óleo",
                "MRO-TESTE",
                "Filtro para motor",
                TipoMRO.PECA,
                50,
                new BigDecimal("25.00"),
                true,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now()
        );

        mros = List.of(mro1, mro2);
    }

    @Test
    @DisplayName("Deve listar MROs com paginação com sucesso")
    void deveListarMROsComPaginacaoComSucesso() {
        // Arrange
        Page<MRO> mroPage = new PageImpl<>(mros, pageable, mros.size());
        when(gateway.findAll(pageable)).thenReturn(mroPage);

        // Act
        Page<MROResponse> response = useCase.execute(pageable);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(2, response.getTotalElements());
        assertEquals(1, response.getTotalPages());

        verify(gateway, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não há MROs")
    void deveRetornarPaginaVaziaQuandoNaoHaMros() {
        // Arrange
        Page<MRO> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(gateway.findAll(pageable)).thenReturn(emptyPage);

        // Act
        Page<MROResponse> response = useCase.execute(pageable);

        // Assert
        assertNotNull(response);
        assertTrue(response.getContent().isEmpty());
        assertEquals(0, response.getTotalElements());

        verify(gateway, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve listar MROs com paginação de tamanho diferente")
    void deveListarMROsComPaginacaoDeTamanhoDiferente() {
        // Arrange
        Pageable pageable20 = PageRequest.of(0, 20);
        Page<MRO> mroPage = new PageImpl<>(mros, pageable20, mros.size());
        when(gateway.findAll(pageable20)).thenReturn(mroPage);

        // Act
        Page<MROResponse> response = useCase.execute(pageable20);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getContent().size());

        verify(gateway, times(1)).findAll(pageable20);
    }

    @Test
    @DisplayName("Deve listar MROs com segunda página")
    void deveListarMROsComSegundaPagina() {
        // Arrange
        Pageable pageablePage1 = PageRequest.of(0, 1);
        Pageable pageablePage2 = PageRequest.of(1, 1);
        
        Page<MRO> page1 = new PageImpl<>(List.of(mros.get(0)), pageablePage1, 2);
        Page<MRO> page2 = new PageImpl<>(List.of(mros.get(1)), pageablePage2, 2);
        
        when(gateway.findAll(pageablePage2)).thenReturn(page2);

        // Act
        Page<MROResponse> response = useCase.execute(pageablePage2);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Filtro de Óleo", response.getContent().get(0).getNome());

        verify(gateway, times(1)).findAll(pageablePage2);
    }

    @Test
    @DisplayName("Deve lançar exceção quando pageable é nulo")
    void deveLancarExcecaoQuandoPageableENulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(gateway, never()).findAll(any());
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha")
    void devePropagarExcecaoQuandoRepositoryFalha() {
        // Arrange
        when(gateway.findAll(pageable)).thenThrow(new RuntimeException("Erro de conexão"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(pageable)
        );

        assertEquals("Erro de conexão", exception.getMessage());

        verify(gateway, times(1)).findAll(pageable);
    }
}
