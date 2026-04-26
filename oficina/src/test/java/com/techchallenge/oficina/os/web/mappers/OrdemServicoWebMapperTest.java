package com.techchallenge.oficina.os.web.mappers;

import com.techchallenge.oficina.os.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.os.web.dto.OrdemServicoResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - OrdemServicoWebMapper")
class OrdemServicoWebMapperTest {

    private OrdemServicoWebMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrdemServicoWebMapper();
    }

    @Test
    @DisplayName("Deve converter Response para DTO com sucesso")
    void deveConverterResponseParaDTO() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        LocalDateTime dataCriacao = LocalDateTime.now();
        
        ClienteResponse clienteResponse = ClienteResponse.builder()
                .id(clienteId)
                .nome("João Silva")
                .build();
        
        VeiculoResponse veiculoResponse = VeiculoResponse.builder()
                .id(veiculoId)
                .placa("ABC1234")
                .modelo("Toyota Corolla")
                .build();
        
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(id)
                .cliente(clienteResponse)
                .veiculo(veiculoResponse)
                .status("ABERTA")
                .dataCriacao(dataCriacao)
                .valorTotal(BigDecimal.valueOf(150.00))
                .itensServico(List.of())
                .build();

        // Act
        OrdemServicoResponseDto dto = mapper.toDto(response);

        // Assert
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertNotNull(dto.getCliente());
        assertEquals(clienteId, dto.getCliente().getId());
        assertEquals("João Silva", dto.getCliente().getNome());
        assertNotNull(dto.getVeiculo());
        assertEquals(veiculoId, dto.getVeiculo().getId());
        assertEquals("ABC1234", dto.getVeiculo().getPlaca());
        assertEquals("Toyota Corolla", dto.getVeiculo().getModelo());
        assertEquals("ABERTA", dto.getStatus());
        assertEquals(dataCriacao, dto.getDataCriacao());
        assertEquals(BigDecimal.valueOf(150.00), dto.getValorTotal());
    }

    @Test
    @DisplayName("Deve converter lista de Responses para lista de DTOs")
    void deveConverterListaDeResponsesParaListaDeDTOs() {
        // Arrange
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        
        ClienteResponse cliente1 = ClienteResponse.builder().id(UUID.randomUUID()).nome("Cliente 1").build();
        VeiculoResponse veiculo1 = VeiculoResponse.builder().id(UUID.randomUUID()).placa("ABC1234").modelo("Toyota Corolla").build();
        
        ClienteResponse cliente2 = ClienteResponse.builder().id(UUID.randomUUID()).nome("Cliente 2").build();
        VeiculoResponse veiculo2 = VeiculoResponse.builder().id(UUID.randomUUID()).placa("XYZ9876").modelo("Honda Civic").build();
        
        OrdemServicoResponse response1 = OrdemServicoResponse.builder()
                .id(id1)
                .cliente(cliente1)
                .veiculo(veiculo1)
                .status("ABERTA")
                .dataCriacao(LocalDateTime.now())
                .valorTotal(BigDecimal.valueOf(100.00))
                .itensServico(List.of())
                .build();
        
        OrdemServicoResponse response2 = OrdemServicoResponse.builder()
                .id(id2)
                .cliente(cliente2)
                .veiculo(veiculo2)
                .status("CONCLUIDA")
                .dataCriacao(LocalDateTime.now())
                .valorTotal(BigDecimal.valueOf(200.00))
                .itensServico(List.of())
                .build();
        
        List<OrdemServicoResponse> responses = List.of(response1, response2);

        // Act
        List<OrdemServicoResponseDto> dtos = mapper.toDtoList(responses);

        // Assert
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(id1, dtos.get(0).getId());
        assertEquals(id2, dtos.get(1).getId());
    }

    @Test
    @DisplayName("Deve converter Response null para DTO null")
    void deveConverterResponseNullParaDTONull() {
        // Act
        OrdemServicoResponseDto dto = mapper.toDto(null);

        // Assert
        assertNull(dto);
    }

    @Test
    @DisplayName("Deve converter lista vazia de Responses")
    void deveConverterListaVaziaDeResponses() {
        // Arrange
        List<OrdemServicoResponse> responses = List.of();

        // Act
        List<OrdemServicoResponseDto> dtos = mapper.toDtoList(responses);

        // Assert
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    @DisplayName("Deve converter lista null de Responses")
    void deveConverterListaNullDeResponses() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> mapper.toDtoList(null));
    }

    @Test
    @DisplayName("Deve converter Response com valor zero")
    void deveConverterResponseComValorZero() {
        // Arrange
        ClienteResponse cliente = ClienteResponse.builder().id(UUID.randomUUID()).nome("Cliente").build();
        VeiculoResponse veiculo = VeiculoResponse.builder().id(UUID.randomUUID()).placa("ABC1234").modelo("Modelo").build();
        
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .veiculo(veiculo)
                .status("ABERTA")
                .dataCriacao(LocalDateTime.now())
                .valorTotal(BigDecimal.ZERO)
                .itensServico(List.of())
                .build();

        // Act
        OrdemServicoResponseDto dto = mapper.toDto(response);

        // Assert
        assertNotNull(dto);
        assertEquals(BigDecimal.ZERO, dto.getValorTotal());
    }

    @Test
    @DisplayName("Deve converter Response com valor alto")
    void deveConverterResponseComValorAlto() {
        // Arrange
        ClienteResponse cliente = ClienteResponse.builder().id(UUID.randomUUID()).nome("Cliente").build();
        VeiculoResponse veiculo = VeiculoResponse.builder().id(UUID.randomUUID()).placa("ABC1234").modelo("Modelo").build();
        
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .veiculo(veiculo)
                .status("ABERTA")
                .dataCriacao(LocalDateTime.now())
                .valorTotal(BigDecimal.valueOf(5000.00))
                .itensServico(List.of())
                .build();

        // Act
        OrdemServicoResponseDto dto = mapper.toDto(response);

        // Assert
        assertNotNull(dto);
        assertEquals(BigDecimal.valueOf(5000.00), dto.getValorTotal());
    }

    @Test
    @DisplayName("Deve converter Response com valor decimal")
    void deveConverterResponseComValorDecimal() {
        // Arrange
        ClienteResponse cliente = ClienteResponse.builder().id(UUID.randomUUID()).nome("Cliente").build();
        VeiculoResponse veiculo = VeiculoResponse.builder().id(UUID.randomUUID()).placa("ABC1234").modelo("Modelo").build();
        
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .veiculo(veiculo)
                .status("ABERTA")
                .dataCriacao(LocalDateTime.now())
                .valorTotal(BigDecimal.valueOf(125.50))
                .itensServico(List.of())
                .build();

        // Act
        OrdemServicoResponseDto dto = mapper.toDto(response);

        // Assert
        assertNotNull(dto);
        assertEquals(BigDecimal.valueOf(125.50), dto.getValorTotal());
    }

    @Test
    @DisplayName("Deve converter Response com status EM_ANDAMENTO")
    void deveConverterResponseComStatusEmAndamento() {
        // Arrange
        ClienteResponse cliente = ClienteResponse.builder().id(UUID.randomUUID()).nome("Cliente").build();
        VeiculoResponse veiculo = VeiculoResponse.builder().id(UUID.randomUUID()).placa("ABC1234").modelo("Modelo").build();
        
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .veiculo(veiculo)
                .status("EM_ANDAMENTO")
                .dataCriacao(LocalDateTime.now())
                .valorTotal(BigDecimal.valueOf(100.00))
                .itensServico(List.of())
                .build();

        // Act
        OrdemServicoResponseDto dto = mapper.toDto(response);

        // Assert
        assertNotNull(dto);
        assertEquals("EM_ANDAMENTO", dto.getStatus());
    }

    @Test
    @DisplayName("Deve converter Response com status CONCLUIDA")
    void deveConverterResponseComStatusConcluida() {
        // Arrange
        ClienteResponse cliente = ClienteResponse.builder().id(UUID.randomUUID()).nome("Cliente").build();
        VeiculoResponse veiculo = VeiculoResponse.builder().id(UUID.randomUUID()).placa("ABC1234").modelo("Modelo").build();
        
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .veiculo(veiculo)
                .status("CONCLUIDA")
                .dataCriacao(LocalDateTime.now())
                .valorTotal(BigDecimal.valueOf(100.00))
                .itensServico(List.of())
                .build();

        // Act
        OrdemServicoResponseDto dto = mapper.toDto(response);

        // Assert
        assertNotNull(dto);
        assertEquals("CONCLUIDA", dto.getStatus());
    }

    @Test
    @DisplayName("Deve converter Response com status CANCELADA")
    void deveConverterResponseComStatusCancelada() {
        // Arrange
        ClienteResponse cliente = ClienteResponse.builder().id(UUID.randomUUID()).nome("Cliente").build();
        VeiculoResponse veiculo = VeiculoResponse.builder().id(UUID.randomUUID()).placa("ABC1234").modelo("Modelo").build();
        
        OrdemServicoResponse response = OrdemServicoResponse.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .veiculo(veiculo)
                .status("CANCELADA")
                .dataCriacao(LocalDateTime.now())
                .valorTotal(BigDecimal.valueOf(100.00))
                .itensServico(List.of())
                .build();

        // Act
        OrdemServicoResponseDto dto = mapper.toDto(response);

        // Assert
        assertNotNull(dto);
        assertEquals("CANCELADA", dto.getStatus());
    }
}
