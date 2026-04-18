package com.techchallenge.oficina.administrativo.web.mappers;

import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusVeiculo;
import com.techchallenge.oficina.administrativo.web.dto.VeiculoResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - VeiculoWebMapper")
class VeiculoWebMapperTest {

    private VeiculoWebMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new VeiculoWebMapper();
    }

    @Test
    @DisplayName("Deve converter VeiculoResponse para DTO com sucesso")
    void deveConverterResponseParaDto() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        
        VeiculoResponse response = VeiculoResponse.builder()
                .id(veiculoId)
                .placa("ABC-1234")
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2022)
                .cor("Prata")
                .status(StatusVeiculo.ATIVO)
                .clienteId(clienteId)
                .clienteNome("João Silva")
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        // Act
        VeiculoResponseDto dto = mapper.toDto(response);

        // Assert
        assertNotNull(dto);
        assertEquals(veiculoId, dto.getId());
        assertEquals("ABC-1234", dto.getPlaca());
        assertEquals("Toyota", dto.getMarca());
        assertEquals("Corolla", dto.getModelo());
        assertEquals(2022, dto.getAno());
        assertEquals("Prata", dto.getCor());
        assertEquals(StatusVeiculo.ATIVO, dto.getStatus());
        assertEquals(clienteId, dto.getClienteId());
        assertEquals("João Silva", dto.getClienteNome());
        assertNotNull(dto.getCriadoEm());
        assertNotNull(dto.getAtualizadoEm());
        assertNotNull(dto.getDescricaoCompleta());
    }

    @Test
    @DisplayName("Deve retornar null quando response é null")
    void deveRetornarNullQuandoResponseENull() {
        // Act
        VeiculoResponseDto dto = mapper.toDto(null);

        // Assert
        assertNull(dto);
    }

    @Test
    @DisplayName("Deve converter lista de responses para lista de DTOs")
    void deveConverterListaDeResponsesParaListaDeDtos() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        
        VeiculoResponse response1 = VeiculoResponse.builder()
                .id(UUID.randomUUID())
                .placa("ABC-1234")
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2022)
                .cor("Prata")
                .status(StatusVeiculo.ATIVO)
                .clienteId(clienteId)
                .clienteNome("João Silva")
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        VeiculoResponse response2 = VeiculoResponse.builder()
                .id(UUID.randomUUID())
                .placa("XYZ-5678")
                .marca("Honda")
                .modelo("Civic")
                .ano(2023)
                .cor("Preto")
                .status(StatusVeiculo.ATIVO)
                .clienteId(clienteId)
                .clienteNome("João Silva")
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        List<VeiculoResponse> responses = List.of(response1, response2);

        // Act
        List<VeiculoResponseDto> dtos = mapper.toDtoList(responses);

        // Assert
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals("Toyota", dtos.get(0).getMarca());
        assertEquals("Honda", dtos.get(1).getMarca());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando lista de responses é null")
    void deveRetornarListaVaziaQuandoListaDeResponsesENull() {
        // Act
        List<VeiculoResponseDto> dtos = mapper.toDtoList(null);

        // Assert
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    @DisplayName("Deve converter página de responses para página de DTOs")
    void deveConverterPaginaDeResponsesParaPaginaDeDtos() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        
        VeiculoResponse response = VeiculoResponse.builder()
                .id(UUID.randomUUID())
                .placa("ABC-1234")
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2022)
                .cor("Prata")
                .status(StatusVeiculo.ATIVO)
                .clienteId(clienteId)
                .clienteNome("João Silva")
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        Page<VeiculoResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        // Act
        Page<VeiculoResponseDto> dtoPage = mapper.toDtoPage(page);

        // Assert
        assertNotNull(dtoPage);
        assertEquals(1, dtoPage.getTotalElements());
        assertEquals(1, dtoPage.getContent().size());
        assertEquals("Toyota", dtoPage.getContent().get(0).getMarca());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando página de responses é null")
    void deveRetornarPaginaVaziaQuandoPaginaDeResponsesENull() {
        // Act
        Page<VeiculoResponseDto> dtoPage = mapper.toDtoPage(null);

        // Assert
        assertNotNull(dtoPage);
        assertTrue(dtoPage.isEmpty());
    }

    @Test
    @DisplayName("Deve manter descrição completa no DTO")
    void deveManterDescricaoCompletaNoDto() {
        // Arrange
        VeiculoResponse response = VeiculoResponse.builder()
                .id(UUID.randomUUID())
                .placa("ABC-1234")
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2022)
                .cor("Prata")
                .status(StatusVeiculo.ATIVO)
                .clienteId(UUID.randomUUID())
                .clienteNome("João Silva")
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        // Act
        VeiculoResponseDto dto = mapper.toDto(response);

        // Assert
        assertNotNull(dto.getDescricaoCompleta());
        assertTrue(dto.getDescricaoCompleta().contains("Toyota"));
        assertTrue(dto.getDescricaoCompleta().contains("Corolla"));
        assertTrue(dto.getDescricaoCompleta().contains("2022"));
        assertTrue(dto.getDescricaoCompleta().contains("ABC-1234"));
    }
}
