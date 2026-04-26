package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoMROException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarPrecoMROCommand")
class AtualizarPrecoMROCommandTest {

    @Test
    @DisplayName("Deve criar comando com preço válido")
    void deveCriarComandoComPrecoValido() {
        // Arrange
        UUID id = UUID.randomUUID();
        BigDecimal preco = new BigDecimal("150.00");

        // Act
        AtualizarPrecoMROCommand command = new AtualizarPrecoMROCommand(id, preco);

        // Assert
        assertNotNull(command);
        assertEquals(id, command.getId());
        assertEquals(preco, command.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve criar comando com preço decimal")
    void deveCriarComandoComPrecoDecimal() {
        // Arrange
        UUID id = UUID.randomUUID();
        BigDecimal preco = new BigDecimal("125.50");

        // Act
        AtualizarPrecoMROCommand command = new AtualizarPrecoMROCommand(id, preco);

        // Assert
        assertNotNull(command);
        assertEquals(preco, command.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve criar comando com preço alto")
    void deveCriarComandoComPrecoAlto() {
        // Arrange
        UUID id = UUID.randomUUID();
        BigDecimal preco = new BigDecimal("5000.00");

        // Act
        AtualizarPrecoMROCommand command = new AtualizarPrecoMROCommand(id, preco);

        // Assert
        assertNotNull(command);
        assertEquals(preco, command.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve criar comando com preço baixo")
    void deveCriarComandoComPrecoBaixo() {
        // Arrange
        UUID id = UUID.randomUUID();
        BigDecimal preco = new BigDecimal("0.01");

        // Act
        AtualizarPrecoMROCommand command = new AtualizarPrecoMROCommand(id, preco);

        // Assert
        assertNotNull(command);
        assertEquals(preco, command.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void deveLancarExcecaoQuandoIdENulo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new AtualizarPrecoMROCommand(null, new BigDecimal("100.00"))
        );

        assertEquals("ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é nulo")
    void deveLancarExcecaoQuandoPrecoENulo() {
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
    @DisplayName("Deve lançar exceção quando preço é zero")
    void deveLancarExcecaoQuandoPrecoEZero() {
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
    @DisplayName("Deve lançar exceção quando preço é negativo")
    void deveLancarExcecaoQuandoPrecoENegativo() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new AtualizarPrecoMROCommand(id, new BigDecimal("-50.00"))
        );

        assertEquals("Preço unitário deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar comando com ID específico")
    void deveCriarComandoComIdEspecifico() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        // Act
        AtualizarPrecoMROCommand command = new AtualizarPrecoMROCommand(id, new BigDecimal("200.00"));

        // Assert
        assertNotNull(command);
        assertEquals(id, command.getId());
    }

    @Test
    @DisplayName("Deve criar comando com preço com muitas casas decimais")
    void deveCriarComandoComPrecoComMuitasCasasDecimais() {
        // Arrange
        UUID id = UUID.randomUUID();
        BigDecimal preco = new BigDecimal("123.456789");

        // Act
        AtualizarPrecoMROCommand command = new AtualizarPrecoMROCommand(id, preco);

        // Assert
        assertNotNull(command);
        assertEquals(preco, command.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve criar comando com preço muito pequeno")
    void deveCriarComandoComPrecoMuitoPequeno() {
        // Arrange
        UUID id = UUID.randomUUID();
        BigDecimal preco = new BigDecimal("0.001");

        // Act
        AtualizarPrecoMROCommand command = new AtualizarPrecoMROCommand(id, preco);

        // Assert
        assertNotNull(command);
        assertEquals(preco, command.getPrecoUnitario());
    }
}
