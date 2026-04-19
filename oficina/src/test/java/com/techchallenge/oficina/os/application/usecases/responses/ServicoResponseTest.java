package com.techchallenge.oficina.os.application.usecases.responses;

import com.techchallenge.oficina.os.domain.model.entities.Servico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - ServicoResponse")
class ServicoResponseTest {

    @Test
    @DisplayName("Deve criar response a partir de Servico com sucesso")
    void deveCriarResponseApartirDeServico() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime atualizadoEm = LocalDateTime.now();
        
        Servico servico = Servico.reconstruir(
                id,
                "Troca de Óleo",
                "Troca completa de óleo do motor",
                new BigDecimal("150.00"),
                true,
                criadoEm,
                atualizadoEm
        );

        // Act
        ServicoResponse response = ServicoResponse.from(servico);

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Troca de Óleo", response.getNome());
        assertEquals("Troca completa de óleo do motor", response.getDescricao());
        assertEquals(new BigDecimal("150.00"), response.getPreco());
        assertTrue(response.getAtivo());
        assertEquals(criadoEm, response.getCriadoEm());
        assertEquals(atualizadoEm, response.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve retornar null quando Servico é null")
    void deveRetornarNullQuandoServicoENull() {
        // Act
        ServicoResponse response = ServicoResponse.from(null);

        // Assert
        assertNull(response);
    }

    @Test
    @DisplayName("Deve criar response com servico inativo")
    void deveCriarResponseComServicoInativo() {
        // Arrange
        UUID id = UUID.randomUUID();
        Servico servico = Servico.reconstruir(
                id,
                "Servico Descontinuado",
                "Descrição",
                new BigDecimal("100.00"),
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Act
        ServicoResponse response = ServicoResponse.from(servico);

        // Assert
        assertNotNull(response);
        assertFalse(response.getAtivo());
    }

    @Test
    @DisplayName("Deve criar response com descricao null")
    void deveCriarResponseComDescricaoNull() {
        // Arrange
        UUID id = UUID.randomUUID();
        Servico servico = Servico.reconstruir(
                id,
                "Servico Sem Descricao",
                null,
                new BigDecimal("80.00"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Act
        ServicoResponse response = ServicoResponse.from(servico);

        // Assert
        assertNotNull(response);
        assertNull(response.getDescricao());
    }

    @Test
    @DisplayName("Deve criar response com preco decimal")
    void deveCriarResponseComPrecoDecimal() {
        // Arrange
        UUID id = UUID.randomUUID();
        Servico servico = Servico.reconstruir(
                id,
                "Alinhamento",
                "Alinhamento de direção",
                new BigDecimal("89.90"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Act
        ServicoResponse response = ServicoResponse.from(servico);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("89.90"), response.getPreco());
    }
}
