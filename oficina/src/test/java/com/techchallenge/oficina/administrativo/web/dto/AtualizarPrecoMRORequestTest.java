package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarPrecoMROCommand;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoMROException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarPrecoMRORequest")
class AtualizarPrecoMRORequestTest {

    @Test
    @DisplayName("Deve converter request para command com sucesso")
    void deveConverterRequestParaCommandComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();
        AtualizarPrecoMRORequest request = new AtualizarPrecoMRORequest();
        request.setNovoPrecoUnitario(new BigDecimal("50.00"));

        // Act
        AtualizarPrecoMROCommand command = request.toCommand(id);

        // Assert
        assertNotNull(command);
        assertEquals(id, command.getId());
        assertEquals(new BigDecimal("50.00"), command.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve converter request para command com preço decimal")
    void deveConverterRequestParaCommandComPrecoDecimal() {
        // Arrange
        UUID id = UUID.randomUUID();
        AtualizarPrecoMRORequest request = new AtualizarPrecoMRORequest();
        request.setNovoPrecoUnitario(new BigDecimal("45.90"));

        // Act
        AtualizarPrecoMROCommand command = request.toCommand(id);

        // Assert
        assertNotNull(command);
        assertEquals(new BigDecimal("45.90"), command.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve converter request para command com preço pequeno")
    void deveConverterRequestParaCommandComPrecoPequeno() {
        // Arrange
        UUID id = UUID.randomUUID();
        AtualizarPrecoMRORequest request = new AtualizarPrecoMRORequest();
        request.setNovoPrecoUnitario(new BigDecimal("0.50"));

        // Act
        AtualizarPrecoMROCommand command = request.toCommand(id);

        // Assert
        assertNotNull(command);
        assertEquals(new BigDecimal("0.50"), command.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve converter request para command com preço grande")
    void deveConverterRequestParaCommandComPrecoGrande() {
        // Arrange
        UUID id = UUID.randomUUID();
        AtualizarPrecoMRORequest request = new AtualizarPrecoMRORequest();
        request.setNovoPrecoUnitario(new BigDecimal("9999.99"));

        // Act
        AtualizarPrecoMROCommand command = request.toCommand(id);

        // Assert
        assertNotNull(command);
        assertEquals(new BigDecimal("9999.99"), command.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é null")
    void deveLancarExcecaoQuandoPrecoENull() {
        // Arrange
        UUID id = UUID.randomUUID();
        AtualizarPrecoMRORequest request = new AtualizarPrecoMRORequest();
        request.setNovoPrecoUnitario(null);

        // Act & Assert
        assertThrows(ValidacaoMROException.class, () -> request.toCommand(id));
    }
}
