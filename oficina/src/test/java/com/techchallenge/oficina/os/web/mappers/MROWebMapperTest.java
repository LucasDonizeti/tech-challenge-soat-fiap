package com.techchallenge.oficina.os.web.mappers;

import com.techchallenge.oficina.os.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.os.web.dto.MROResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - MROWebMapper")
class MROWebMapperTest {

    private MROWebMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MROWebMapper();
    }

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
        MROResponseDto dto = mapper.toDto(response);

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
    @DisplayName("Deve converter lista de MROResponse para lista de DTOs")
    void deveConverterListaDeMroResponseParaListaDeDtos() {
        // Arrange
        MROResponse response1 = MROResponse.builder()
                .id(UUID.randomUUID())
                .nome("Óleo Motor")
                .tipo("INSUMO")
                .quantidadeEstoque(100)
                .precoUnitario(new BigDecimal("45.90"))
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();
        
        MROResponse response2 = MROResponse.builder()
                .id(UUID.randomUUID())
                .nome("Filtro de Óleo")
                .tipo("PECA")
                .quantidadeEstoque(50)
                .precoUnitario(new BigDecimal("25.00"))
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();
        
        List<MROResponse> responses = List.of(response1, response2);

        // Act
        List<MROResponseDto> dtos = mapper.toDtoList(responses);

        // Assert
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals("Óleo Motor", dtos.get(0).getNome());
        assertEquals("Filtro de Óleo", dtos.get(1).getNome());
    }

    @Test
    @DisplayName("Deve converter página de MROResponse para página de DTOs")
    void deveConverterPaginaDeMroResponseParaPaginaDeDtos() {
        // Arrange
        MROResponse response1 = MROResponse.builder()
                .id(UUID.randomUUID())
                .nome("Óleo Motor")
                .tipo("INSUMO")
                .quantidadeEstoque(100)
                .precoUnitario(new BigDecimal("45.90"))
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();
        
        MROResponse response2 = MROResponse.builder()
                .id(UUID.randomUUID())
                .nome("Filtro de Óleo")
                .tipo("PECA")
                .quantidadeEstoque(50)
                .precoUnitario(new BigDecimal("25.00"))
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();
        
        List<MROResponse> responses = List.of(response1, response2);
        Page<MROResponse> responsePage = new PageImpl<>(responses, PageRequest.of(0, 10), 2);

        // Act
        Page<MROResponseDto> dtoPage = mapper.toDtoPage(responsePage);

        // Assert
        assertNotNull(dtoPage);
        assertEquals(2, dtoPage.getContent().size());
        assertEquals(2, dtoPage.getTotalElements());
        assertEquals("Óleo Motor", dtoPage.getContent().get(0).getNome());
        assertEquals("Filtro de Óleo", dtoPage.getContent().get(1).getNome());
    }

    @Test
    @DisplayName("Deve converter MROResponse com tipo PECA")
    void deveConverterMroResponseComTipoPeca() {
        // Arrange
        MROResponse response = MROResponse.builder()
                .id(UUID.randomUUID())
                .nome("Parafuso M8")
                .tipo("PECA")
                .quantidadeEstoque(500)
                .precoUnitario(new BigDecimal("0.50"))
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        // Act
        MROResponseDto dto = mapper.toDto(response);

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
        MROResponseDto dto = mapper.toDto(response);

        // Assert
        assertNotNull(dto);
        assertFalse(dto.getAtivo());
    }

    @Test
    @DisplayName("Deve converter MROResponse com descrição null")
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
        MROResponseDto dto = mapper.toDto(response);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getDescricao());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando lista de responses é vazia")
    void deveRetornarListaVaziaQuandoListaDeResponsesEVazia() {
        // Arrange
        List<MROResponse> responses = List.of();

        // Act
        List<MROResponseDto> dtos = mapper.toDtoList(responses);

        // Assert
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    @DisplayName("Deve converter página vazia de MROResponse")
    void deveConverterPaginaVaziaDeMroResponse() {
        // Arrange
        Page<MROResponse> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        // Act
        Page<MROResponseDto> dtoPage = mapper.toDtoPage(emptyPage);

        // Assert
        assertNotNull(dtoPage);
        assertTrue(dtoPage.getContent().isEmpty());
        assertEquals(0, dtoPage.getTotalElements());
    }
}
