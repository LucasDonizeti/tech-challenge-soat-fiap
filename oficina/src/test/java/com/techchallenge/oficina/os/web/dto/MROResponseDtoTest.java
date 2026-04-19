package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.responses.MROResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - MROResponseDto")
class MROResponseDtoTest {

    @Test
    @DisplayName("Deve converter MROResponse para DTO com sucesso")
    void deveConverterMroResponseParaDto() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime atualizadoEm = LocalDateTime.now();
        
        MROResponse response = MROResponse.builder()
                .id(id)
                .nome("Óleo Motor 5W30")
                .descricao("Óleo para motor automotivo")
                .tipo("INSUMO")
                .quantidadeEstoque(100)
                .precoUnitario(new BigDecimal("45.90"))
                .ativo(true)
                .criadoEm(criadoEm)
                .atualizadoEm(atualizadoEm)
                .build();

        // Act
        MROResponseDto dto = MROResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Óleo Motor 5W30", dto.getNome());
        assertEquals("Óleo para motor automotivo", dto.getDescricao());
        assertEquals("INSUMO", dto.getTipo());
        assertEquals(100, dto.getQuantidadeEstoque());
        assertEquals(new BigDecimal("45.90"), dto.getPrecoUnitario());
        assertTrue(dto.getAtivo());
        assertEquals(criadoEm, dto.getCriadoEm());
        assertEquals(atualizadoEm, dto.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve retornar null quando MROResponse é null")
    void deveRetornarNullQuandoMroResponseENull() {
        // Act
        MROResponseDto dto = MROResponseDto.from(null);

        // Assert
        assertNull(dto);
    }

    @Test
    @DisplayName("Deve converter MROResponse com tipo PECA")
    void deveConverterMroResponseComTipoPeca() {
        // Arrange
        MROResponse response = MROResponse.builder()
                .id(UUID.randomUUID())
                .nome("Filtro de Óleo")
                .tipo("PECA")
                .quantidadeEstoque(50)
                .precoUnitario(new BigDecimal("25.00"))
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        // Act
        MROResponseDto dto = MROResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertEquals("PECA", dto.getTipo());
    }

    @Test
    @DisplayName("Deve converter MROResponse inativo")
    void deveConverterMroResponseInativo() {
        // Arrange
        MROResponse response = MROResponse.builder()
                .id(UUID.randomUUID())
                .nome("Produto Inativo")
                .tipo("INSUMO")
                .quantidadeEstoque(0)
                .precoUnitario(new BigDecimal("10.00"))
                .ativo(false)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        // Act
        MROResponseDto dto = MROResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertFalse(dto.getAtivo());
    }

    @Test
    @DisplayName("Deve converter MROResponse com descricao null")
    void deveConverterMroResponseComDescricaoNull() {
        // Arrange
        MROResponse response = MROResponse.builder()
                .id(UUID.randomUUID())
                .nome("Produto Sem Descrição")
                .descricao(null)
                .tipo("PECA")
                .quantidadeEstoque(100)
                .precoUnitario(new BigDecimal("15.00"))
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        // Act
        MROResponseDto dto = MROResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getDescricao());
    }

    @Test
    @DisplayName("Deve converter MROResponse com estoque zero")
    void deveConverterMroResponseComEstoqueZero() {
        // Arrange
        MROResponse response = MROResponse.builder()
                .id(UUID.randomUUID())
                .nome("Produto Sem Estoque")
                .tipo("INSUMO")
                .quantidadeEstoque(0)
                .precoUnitario(new BigDecimal("10.00"))
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        // Act
        MROResponseDto dto = MROResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertEquals(0, dto.getQuantidadeEstoque());
    }
}
