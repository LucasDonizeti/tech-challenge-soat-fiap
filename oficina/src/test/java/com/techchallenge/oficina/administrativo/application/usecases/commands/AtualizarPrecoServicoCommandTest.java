package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoServicoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarPrecoServicoCommand")
class AtualizarPrecoServicoCommandTest {

    @Test
    @DisplayName("Deve criar comando com preço válido")
    void deveCriarComandoComPrecoValido() {
        // Arrange
        BigDecimal preco = new BigDecimal("150.00");

        // Act
        AtualizarPrecoServicoCommand command = new AtualizarPrecoServicoCommand(preco);

        // Assert
        assertNotNull(command);
        assertEquals(preco, command.getNovoPreco());
    }

    @Test
    @DisplayName("Deve criar comando com preço decimal")
    void deveCriarComandoComPrecoDecimal() {
        // Arrange
        BigDecimal preco = new BigDecimal("125.50");

        // Act
        AtualizarPrecoServicoCommand command = new AtualizarPrecoServicoCommand(preco);

        // Assert
        assertNotNull(command);
        assertEquals(preco, command.getNovoPreco());
    }

    @Test
    @DisplayName("Deve criar comando com preço alto")
    void deveCriarComandoComPrecoAlto() {
        // Arrange
        BigDecimal preco = new BigDecimal("5000.00");

        // Act
        AtualizarPrecoServicoCommand command = new AtualizarPrecoServicoCommand(preco);

        // Assert
        assertNotNull(command);
        assertEquals(preco, command.getNovoPreco());
    }

    @Test
    @DisplayName("Deve criar comando com preço baixo")
    void deveCriarComandoComPrecoBaixo() {
        // Arrange
        BigDecimal preco = new BigDecimal("0.01");

        // Act
        AtualizarPrecoServicoCommand command = new AtualizarPrecoServicoCommand(preco);

        // Assert
        assertNotNull(command);
        assertEquals(preco, command.getNovoPreco());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é nulo")
    void deveLancarExcecaoQuandoPrecoENulo() {
        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new AtualizarPrecoServicoCommand(null)
        );

        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é zero")
    void deveLancarExcecaoQuandoPrecoEZero() {
        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new AtualizarPrecoServicoCommand(BigDecimal.ZERO)
        );

        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é negativo")
    void deveLancarExcecaoQuandoPrecoENegativo() {
        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new AtualizarPrecoServicoCommand(new BigDecimal("-50.00"))
        );

        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar comando com preço com muitas casas decimais")
    void deveCriarComandoComPrecoComMuitasCasasDecimais() {
        // Arrange
        BigDecimal preco = new BigDecimal("123.456789");

        // Act
        AtualizarPrecoServicoCommand command = new AtualizarPrecoServicoCommand(preco);

        // Assert
        assertNotNull(command);
        assertEquals(preco, command.getNovoPreco());
    }

    @Test
    @DisplayName("Deve criar comando com preço muito pequeno")
    void deveCriarComandoComPrecoMuitoPequeno() {
        // Arrange
        BigDecimal preco = new BigDecimal("0.001");

        // Act
        AtualizarPrecoServicoCommand command = new AtualizarPrecoServicoCommand(preco);

        // Assert
        assertNotNull(command);
        assertEquals(preco, command.getNovoPreco());
    }
}
