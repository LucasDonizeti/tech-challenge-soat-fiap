package com.techchallenge.oficina.os.application.usecases.responses;

import com.techchallenge.oficina.os.domain.model.entities.MRO;
import com.techchallenge.oficina.os.domain.model.valueobjects.TipoMRO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - MROResponse")
class MROResponseTest {

    @Test
    @DisplayName("Deve criar response a partir de MRO com sucesso")
    void deveCriarResponseApartirDeMRO() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime atualizadoEm = LocalDateTime.now();
        
        MRO mro = MRO.reconstruir(
                id,
                "Óleo Motor 5W30",
                "Óleo para motor automotivo",
                TipoMRO.INSUMO,
                100,
                new BigDecimal("45.90"),
                true,
                criadoEm,
                atualizadoEm
        );

        // Act
        MROResponse response = MROResponse.from(mro);

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Óleo Motor 5W30", response.getNome());
        assertEquals("Óleo para motor automotivo", response.getDescricao());
        assertEquals("INSUMO", response.getTipo());
        assertEquals(100, response.getQuantidadeEstoque());
        assertEquals(new BigDecimal("45.90"), response.getPrecoUnitario());
        assertTrue(response.getAtivo());
        assertEquals(criadoEm, response.getCriadoEm());
        assertEquals(atualizadoEm, response.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve retornar null quando MRO é null")
    void deveRetornarNullQuandoMroENull() {
        // Act
        MROResponse response = MROResponse.from(null);

        // Assert
        assertNull(response);
    }

    @Test
    @DisplayName("Deve criar response com tipo null")
    void deveCriarResponseComTipoNull() {
        // Arrange
        UUID id = UUID.randomUUID();
        MRO mro = MRO.reconstruir(
                id,
                "Produto Genérico",
                "Descrição",
                null,
                50,
                new BigDecimal("10.00"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Act
        MROResponse response = MROResponse.from(mro);

        // Assert
        assertNotNull(response);
        assertNull(response.getTipo());
    }

    @Test
    @DisplayName("Deve criar response com descrição null")
    void deveCriarResponseComDescricaoNull() {
        // Arrange
        UUID id = UUID.randomUUID();
        MRO mro = MRO.reconstruir(
                id,
                "Produto Sem Descrição",
                null,
                TipoMRO.PECA,
                50,
                new BigDecimal("10.00"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Act
        MROResponse response = MROResponse.from(mro);

        // Assert
        assertNotNull(response);
        assertNull(response.getDescricao());
    }

    @Test
    @DisplayName("Deve criar response com MRO inativo")
    void deveCriarResponseComMroInativo() {
        // Arrange
        UUID id = UUID.randomUUID();
        MRO mro = MRO.reconstruir(
                id,
                "Produto Inativo",
                "Descrição",
                TipoMRO.PECA,
                0,
                new BigDecimal("10.00"),
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Act
        MROResponse response = MROResponse.from(mro);

        // Assert
        assertNotNull(response);
        assertFalse(response.getAtivo());
    }

    @Test
    @DisplayName("Deve criar response com estoque zero")
    void deveCriarResponseComEstoqueZero() {
        // Arrange
        UUID id = UUID.randomUUID();
        MRO mro = MRO.reconstruir(
                id,
                "Produto Sem Estoque",
                "Descrição",
                TipoMRO.INSUMO,
                0,
                new BigDecimal("10.00"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Act
        MROResponse response = MROResponse.from(mro);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve criar response com tipo PECA")
    void deveCriarResponseComTipoPeca() {
        // Arrange
        UUID id = UUID.randomUUID();
        MRO mro = MRO.reconstruir(
                id,
                "Filtro de Óleo",
                "Filtro para motor",
                TipoMRO.PECA,
                50,
                new BigDecimal("25.00"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Act
        MROResponse response = MROResponse.from(mro);

        // Assert
        assertNotNull(response);
        assertEquals("PECA", response.getTipo());
    }
}
