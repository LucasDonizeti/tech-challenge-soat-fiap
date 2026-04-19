package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoMROException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarPrecoMROCommand")
class AtualizarPrecoMROCommandTest {

    @Test
    @DisplayName("Deve criar comando com sucesso")
    void deveCriarComandoComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();
        BigDecimal precoUnitario = new BigDecimal("55.90");

        // Act
        AtualizarPrecoMROCommand command = new AtualizarPrecoMROCommand(id, precoUnitario);

        // Assert
        assertNotNull(command);
        assertEquals(id, command.getId());
        assertEquals(precoUnitario, command.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void deveLancarExcecaoQuandoIdENulo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new AtualizarPrecoMROCommand(null, new BigDecimal("55.90"))
        );

        assertEquals("ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço unitário é nulo")
    void deveLancarExcecaoQuandoPrecoUnitarioENulo() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new AtualizarPrecoMROCommand(id, null)
        );

        assertEquals("Preço unitário deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço unitário é zero")
    void deveLancarExcecaoQuandoPrecoUnitarioEZero() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new AtualizarPrecoMROCommand(id, BigDecimal.ZERO)
        );

        assertEquals("Preço unitário deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço unitário é negativo")
    void deveLancarExcecaoQuandoPrecoUnitarioENegativo() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new AtualizarPrecoMROCommand(id, new BigDecimal("-10.00"))
        );

        assertEquals("Preço unitário deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar preço unitário com casas decimais")
    void deveAceitarPrecoUnitarioComCasasDecimais() {
        // Arrange
        UUID id = UUID.randomUUID();
        BigDecimal precoUnitario = new BigDecimal("99.99");

        // Act
        AtualizarPrecoMROCommand command = new AtualizarPrecoMROCommand(id, precoUnitario);

        // Assert
        assertNotNull(command);
        assertEquals(precoUnitario, command.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve aceitar preço unitário muito pequeno")
    void deveAceitarPrecoUnitarioMuitoPequeno() {
        // Arrange
        UUID id = UUID.randomUUID();
        BigDecimal precoUnitario = new BigDecimal("0.01");

        // Act
        AtualizarPrecoMROCommand command = new AtualizarPrecoMROCommand(id, precoUnitario);

        // Assert
        assertNotNull(command);
        assertEquals(precoUnitario, command.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve aceitar preço unitário muito grande")
    void deveAceitarPrecoUnitarioMuitoGrande() {
        // Arrange
        UUID id = UUID.randomUUID();
        BigDecimal precoUnitario = new BigDecimal("99999.99");

        // Act
        AtualizarPrecoMROCommand command = new AtualizarPrecoMROCommand(id, precoUnitario);

        // Assert
        assertNotNull(command);
        assertEquals(precoUnitario, command.getPrecoUnitario());
    }
}
