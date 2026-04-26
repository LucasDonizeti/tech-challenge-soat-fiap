package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - ServicoResponseDto")
class ServicoResponseDtoTest {

    @Test
    @DisplayName("Deve converter ServicoResponse para DTO com sucesso")
    void deveConverterServicoResponseParaDto() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime atualizadoEm = LocalDateTime.now();
        
        ServicoResponse response = ServicoResponse.builder()
                .id(id)
                .nome("Troca de Óleo")
                .descricao("Troca completa de óleo")
                .preco(new BigDecimal("150.00"))
                .ativo(true)
                .criadoEm(criadoEm)
                .atualizadoEm(atualizadoEm)
                .build();

        // Act
        ServicoResponseDto dto = ServicoResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Troca de Óleo", dto.getNome());
        assertEquals("Troca completa de óleo", dto.getDescricao());
        assertEquals(new BigDecimal("150.00"), dto.getPreco());
        assertTrue(dto.getAtivo());
        assertEquals(criadoEm, dto.getCriadoEm());
        assertEquals(atualizadoEm, dto.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve retornar null quando ServicoResponse é null")
    void deveRetornarNullQuandoServicoResponseENull() {
        // Act
        ServicoResponseDto dto = ServicoResponseDto.from(null);

        // Assert
        assertNull(dto);
    }

    @Test
    @DisplayName("Deve converter ServicoResponse com servico inativo")
    void deveConverterServicoResponseComServicoInativo() {
        // Arrange
        ServicoResponse response = ServicoResponse.builder()
                .id(UUID.randomUUID())
                .nome("Servico Inativo")
                .descricao("Descrição")
                .preco(new BigDecimal("100.00"))
                .ativo(false)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        // Act
        ServicoResponseDto dto = ServicoResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertFalse(dto.getAtivo());
    }

    @Test
    @DisplayName("Deve converter ServicoResponse com descricao null")
    void deveConverterServicoResponseComDescricaoNull() {
        // Arrange
        ServicoResponse response = ServicoResponse.builder()
                .id(UUID.randomUUID())
                .nome("Servico Sem Descricao")
                .descricao(null)
                .preco(new BigDecimal("80.00"))
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        // Act
        ServicoResponseDto dto = ServicoResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getDescricao());
    }

    @Test
    @DisplayName("Deve converter ServicoResponse com preco decimal")
    void deveConverterServicoResponseComPrecoDecimal() {
        // Arrange
        ServicoResponse response = ServicoResponse.builder()
                .id(UUID.randomUUID())
                .nome("Alinhamento")
                .descricao("Alinhamento de direção")
                .preco(new BigDecimal("89.90"))
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        // Act
        ServicoResponseDto dto = ServicoResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertEquals(new BigDecimal("89.90"), dto.getPreco());
    }
}
