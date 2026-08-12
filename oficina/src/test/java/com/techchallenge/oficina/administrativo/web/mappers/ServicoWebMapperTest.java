package com.techchallenge.oficina.administrativo.web.mappers;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
import com.techchallenge.oficina.administrativo.web.dto.ServicoResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - ServicoWebMapper")
class ServicoWebMapperTest {

    private ServicoWebMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ServicoWebMapper();
    }

    @Test
    @DisplayName("Deve converter ServicoResponse para DTO com sucesso")
    void deveConverterServicoResponseParaDTO() {
        // Arrange
        Servico servico = Servico.criar("Troca de Óleo","COD001", "Troca de óleo sintético", new BigDecimal("150.00"));
        ServicoResponse response = ServicoResponse.from(servico);

        // Act
        ServicoResponseDto dto = mapper.toDto(response);

        // Assert
        assertNotNull(dto);
        assertEquals(response.getId(), dto.getId());
        assertEquals("Troca de Óleo", dto.getNome());
        assertEquals("Troca de óleo sintético", dto.getDescricao());
        assertEquals(new BigDecimal("150.00"), dto.getPreco());
        assertTrue(dto.getAtivo());
    }

    @Test
    @DisplayName("Deve converter lista de ServicoResponse para lista de DTOs")
    void deveConverterListaDeServicoResponseParaListaDeDTOs() {
        // Arrange
        Servico servico1 = Servico.criar("Troca de Óleo","COD001", "Troca de óleo sintético", new BigDecimal("150.00"));
        Servico servico2 = Servico.criar("Troca de Pneu","COD002", "Troca de pneu", new BigDecimal("200.00"));

        ServicoResponse response1 = ServicoResponse.from(servico1);
        ServicoResponse response2 = ServicoResponse.from(servico2);

        List<ServicoResponse> responses = List.of(response1, response2);

        // Act
        List<ServicoResponseDto> dtos = mapper.toDtoList(responses);

        // Assert
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals("Troca de Óleo", dtos.get(0).getNome());
        assertEquals("Troca de Pneu", dtos.get(1).getNome());
    }

    @Test
    @DisplayName("Deve converter página de ServicoResponse para página de DTOs")
    void deveConverterPaginaDeServicoResponseParaPaginaDeDTOs() {
        // Arrange
        Servico servico1 = Servico.criar("Troca de Óleo","COD001", "Troca de óleo sintético", new BigDecimal("150.00"));
        Servico servico2 = Servico.criar("Troca de Pneu","COD002", "Troca de pneu", new BigDecimal("200.00"));

        ServicoResponse response1 = ServicoResponse.from(servico1);
        ServicoResponse response2 = ServicoResponse.from(servico2);

        Page<ServicoResponse> responsePage = new PageImpl<>(List.of(response1, response2));

        // Act
        Page<ServicoResponseDto> dtoPage = mapper.toDtoPage(responsePage);

        // Assert
        assertNotNull(dtoPage);
        assertEquals(2, dtoPage.getContent().size());
        assertEquals(2, dtoPage.getTotalElements());
        assertEquals("Troca de Óleo", dtoPage.getContent().get(0).getNome());
        assertEquals("Troca de Pneu", dtoPage.getContent().get(1).getNome());
    }

    @Test
    @DisplayName("Deve converter lista vazia de ServicoResponse para lista vazia de DTOs")
    void deveConverterListaVaziaDeServicoResponseParaListaVaziaDeDTOs() {
        // Arrange
        List<ServicoResponse> responses = List.of();

        // Act
        List<ServicoResponseDto> dtos = mapper.toDtoList(responses);

        // Assert
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    @DisplayName("Deve converter página vazia de ServicoResponse para página vazia de DTOs")
    void deveConverterPaginaVaziaDeServicoResponseParaPaginaVaziaDeDTOs() {
        // Arrange
        Page<ServicoResponse> responsePage = new PageImpl<>(List.of());

        // Act
        Page<ServicoResponseDto> dtoPage = mapper.toDtoPage(responsePage);

        // Assert
        assertNotNull(dtoPage);
        assertTrue(dtoPage.getContent().isEmpty());
        assertEquals(0, dtoPage.getTotalElements());
    }

    @Test
    @DisplayName("Deve manter informações de paginação ao converter página")
    void deveManterInformacoesDePaginacaoAoConverterPagina() {
        // Arrange
        Servico servico = Servico.criar("Troca de Óleo","COD001", "Troca de óleo sintético", new BigDecimal("150.00"));
        ServicoResponse response = ServicoResponse.from(servico);

        Page<ServicoResponse> responsePage = new PageImpl<>(List.of(response));

        // Act
        Page<ServicoResponseDto> dtoPage = mapper.toDtoPage(responsePage);

        // Assert
        assertNotNull(dtoPage);
        assertEquals(0, dtoPage.getNumber());
        assertEquals(1, dtoPage.getSize());
        assertEquals(1, dtoPage.getTotalElements());
        assertEquals(1, dtoPage.getTotalPages());
    }

    @Test
    @DisplayName("Deve converter serviço inativo corretamente")
    void deveConverterServicoInativoCorretamente() {
        // Arrange
        Servico servico = Servico.criar("Troca de Óleo","COD001", "Troca de óleo sintético", new BigDecimal("150.00"));
        servico.desativar();
        ServicoResponse response = ServicoResponse.from(servico);

        // Act
        ServicoResponseDto dto = mapper.toDto(response);

        // Assert
        assertNotNull(dto);
        assertFalse(dto.getAtivo());
    }

    @Test
    @DisplayName("Deve converter preço decimal corretamente")
    void deveConverterPrecoDecimalCorretamente() {
        // Arrange
        Servico servico = Servico.criar("Troca de Óleo","COD001", "Troca de óleo sintético", new BigDecimal("175.50"));
        ServicoResponse response = ServicoResponse.from(servico);

        // Act
        ServicoResponseDto dto = mapper.toDto(response);

        // Assert
        assertNotNull(dto);
        assertEquals(new BigDecimal("175.50"), dto.getPreco());
    }

    @Test
    @DisplayName("Deve converter serviço com descrição null")
    void deveConverterServicoComDescricaoNull() {
        // Arrange
        Servico servico = Servico.criar("Troca de Óleo","COD001", null, new BigDecimal("150.00"));
        ServicoResponse response = ServicoResponse.from(servico);

        // Act
        ServicoResponseDto dto = mapper.toDto(response);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getDescricao());
    }

    @Test
    @DisplayName("Deve manter ID ao converter")
    void deveManterIdAoConverter() {
        // Arrange
        Servico servico = Servico.criar("Troca de Óleo","COD001", "Troca de óleo sintético", new BigDecimal("150.00"));
        UUID id = servico.getId();
        ServicoResponse response = ServicoResponse.from(servico);

        // Act
        ServicoResponseDto dto = mapper.toDto(response);

        // Assert
        assertNotNull(dto);
        assertEquals(id, dto.getId());
    }
}
