package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusVeiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - VeiculoResponseDto")
class VeiculoResponseDtoTest {

    private VeiculoResponseDto dto;
    private VeiculoResponse response;

    @BeforeEach
    void setUp() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        response = VeiculoResponse.builder()
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

        dto = new VeiculoResponseDto();
        dto.setId(response.getId());
        dto.setPlaca(response.getPlaca());
        dto.setMarca(response.getMarca());
        dto.setModelo(response.getModelo());
        dto.setAno(response.getAno());
        dto.setCor(response.getCor());
        dto.setStatus(response.getStatus());
        dto.setClienteId(response.getClienteId());
        dto.setClienteNome(response.getClienteNome());
        dto.setCriadoEm(response.getCriadoEm());
        dto.setAtualizadoEm(response.getAtualizadoEm());
        dto.setDescricaoCompleta(response.getDescricaoCompleta());
    }

    @Test
    @DisplayName("Deve criar DTO a partir de VeiculoResponse com sucesso")
    void deveCriarDtoAPartirDeResponseComSucesso() {
        // Act
        VeiculoResponseDto result = VeiculoResponseDto.from(response);

        // Assert
        assertNotNull(result);
        assertEquals(response.getId(), result.getId());
        assertEquals("ABC-1234", result.getPlaca());
        assertEquals("Toyota", result.getMarca());
        assertEquals("Corolla", result.getModelo());
        assertEquals(2022, result.getAno());
        assertEquals("Prata", result.getCor());
        assertEquals(StatusVeiculo.ATIVO, result.getStatus());
        assertEquals(response.getClienteId(), result.getClienteId());
        assertEquals("João Silva", result.getClienteNome());
        assertNotNull(result.getCriadoEm());
        assertNotNull(result.getAtualizadoEm());
        assertNotNull(result.getDescricaoCompleta());
    }

    @Test
    @DisplayName("Deve retornar null quando response é null")
    void deveRetornarNullQuandoResponseENull() {
        // Act
        VeiculoResponseDto result = VeiculoResponseDto.from(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Deve manter getters e setters funcionando corretamente")
    void deveManterGettersESettersFuncionandoCorretamente() {
        // Arrange
        VeiculoResponseDto newDto = new VeiculoResponseDto();
        UUID newId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Act
        newDto.setId(newId);
        newDto.setPlaca("XYZ-5678");
        newDto.setMarca("Honda");
        newDto.setModelo("Civic");
        newDto.setAno(2023);
        newDto.setCor("Preto");
        newDto.setStatus(StatusVeiculo.INATIVO);
        newDto.setClienteId(UUID.randomUUID());
        newDto.setClienteNome("Maria Santos");
        newDto.setCriadoEm(now);
        newDto.setAtualizadoEm(now);
        newDto.setDescricaoCompleta("Honda Civic 2023 (XYZ-5678)");

        // Assert
        assertEquals(newId, newDto.getId());
        assertEquals("XYZ-5678", newDto.getPlaca());
        assertEquals("Honda", newDto.getMarca());
        assertEquals("Civic", newDto.getModelo());
        assertEquals(2023, newDto.getAno());
        assertEquals("Preto", newDto.getCor());
        assertEquals(StatusVeiculo.INATIVO, newDto.getStatus());
        assertEquals("Maria Santos", newDto.getClienteNome());
        assertEquals(now, newDto.getCriadoEm());
        assertEquals(now, newDto.getAtualizadoEm());
        assertEquals("Honda Civic 2023 (XYZ-5678)", newDto.getDescricaoCompleta());
    }

    @Test
    @DisplayName("Deve lidar com response sem cliente vinculado")
    void deveLidarComResponseSemClienteVinculado() {
        // Arrange
        VeiculoResponse responseWithoutClient = VeiculoResponse.builder()
                .id(UUID.randomUUID())
                .placa("DEF-9012")
                .marca("Chevrolet")
                .modelo("Onix")
                .ano(2021)
                .cor("Branco")
                .status(StatusVeiculo.ATIVO)
                .clienteId(null)
                .clienteNome(null)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        // Act
        VeiculoResponseDto result = VeiculoResponseDto.from(responseWithoutClient);

        // Assert
        assertNotNull(result);
        assertNull(result.getClienteId());
        assertNull(result.getClienteNome());
    }

    @Test
    @DisplayName("Deve manter descrição completa formatada corretamente")
    void deveManterDescricaoCompletaFormatadaCorretamente() {
        // Act
        String descricao = dto.getDescricaoCompleta();

        // Assert
        assertNotNull(descricao);
        assertTrue(descricao.contains("Toyota"));
        assertTrue(descricao.contains("Corolla"));
        assertTrue(descricao.contains("2022"));
        assertTrue(descricao.contains("ABC-1234"));
    }
}
