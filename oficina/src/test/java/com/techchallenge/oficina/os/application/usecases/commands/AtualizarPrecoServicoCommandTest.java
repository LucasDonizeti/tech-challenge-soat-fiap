package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoServicoException;
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
        BigDecimal novoPreco = new BigDecimal("150.00");

        // Act
        AtualizarPrecoServicoCommand command = new AtualizarPrecoServicoCommand(novoPreco);

        // Assert
        assertNotNull(command);
        assertEquals(novoPreco, command.getNovoPreco());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é null")
    void deveLancarExcecaoQuandoPrecoENull() {
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
        // Arrange
        BigDecimal precoZero = BigDecimal.ZERO;

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new AtualizarPrecoServicoCommand(precoZero)
        );

        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é negativo")
    void deveLancarExcecaoQuandoPrecoENegativo() {
        // Arrange
        BigDecimal precoNegativo = new BigDecimal("-50.00");

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new AtualizarPrecoServicoCommand(precoNegativo)
        );

        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar preço decimal")
    void deveAceitarPrecoDecimal() {
        // Arrange
        BigDecimal precoDecimal = new BigDecimal("175.50");

        // Act
        AtualizarPrecoServicoCommand command = new AtualizarPrecoServicoCommand(precoDecimal);

        // Assert
        assertNotNull(command);
        assertEquals(precoDecimal, command.getNovoPreco());
    }

    @Test
    @DisplayName("Deve aceitar preço muito pequeno")
    void deveAceitarPrecoMuitoPequeno() {
        // Arrange
        BigDecimal precoPequeno = new BigDecimal("0.01");

        // Act
        AtualizarPrecoServicoCommand command = new AtualizarPrecoServicoCommand(precoPequeno);

        // Assert
        assertNotNull(command);
        assertEquals(precoPequeno, command.getNovoPreco());
    }

    @Test
    @DisplayName("Deve aceitar preço muito grande")
    void deveAceitarPrecoMuitoGrande() {
        // Arrange
        BigDecimal precoGrande = new BigDecimal("999999.99");

        // Act
        AtualizarPrecoServicoCommand command = new AtualizarPrecoServicoCommand(precoGrande);

        // Assert
        assertNotNull(command);
        assertEquals(precoGrande, command.getNovoPreco());
    }

    @Test
    @DisplayName("Deve manter valor do preço imutável")
    void deveManterValorDoPrecoImutavel() {
        // Arrange
        BigDecimal novoPreco = new BigDecimal("200.00");
        AtualizarPrecoServicoCommand command = new AtualizarPrecoServicoCommand(novoPreco);

        // Act
        BigDecimal precoRetornado = command.getNovoPreco();

        // Assert
        assertEquals(novoPreco, precoRetornado);
        assertSame(novoPreco, precoRetornado);
    }

    @Test
    @DisplayName("Deve aceitar preço com múltiplas casas decimais")
    void deveAceitarPrecoComMultiplasCasasDecimais() {
        // Arrange
        BigDecimal precoMultiplasDecimais = new BigDecimal("150.123");

        // Act
        AtualizarPrecoServicoCommand command = new AtualizarPrecoServicoCommand(precoMultiplasDecimais);

        // Assert
        assertNotNull(command);
        assertEquals(precoMultiplasDecimais, command.getNovoPreco());
    }

    @Test
    @DisplayName("Deve lançar exceção para preço 0.00")
    void deveLancarExcecaoParaPreco000() {
        // Arrange
        BigDecimal preco00 = new BigDecimal("0.00");

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new AtualizarPrecoServicoCommand(preco00)
        );

        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar comando com preço inteiro")
    void deveCriarComandoComPrecoInteiro() {
        // Arrange
        BigDecimal precoInteiro = new BigDecimal("100");

        // Act
        AtualizarPrecoServicoCommand command = new AtualizarPrecoServicoCommand(precoInteiro);

        // Assert
        assertNotNull(command);
        assertEquals(precoInteiro, command.getNovoPreco());
    }

    @Test
    @DisplayName("Deve aceitar preço com escala zero")
    void deveAceitarPrecoComEscalaZero() {
        // Arrange
        BigDecimal precoEscalaZero = new BigDecimal("150.00");

        // Act
        AtualizarPrecoServicoCommand command = new AtualizarPrecoServicoCommand(precoEscalaZero);

        // Assert
        assertNotNull(command);
        assertEquals(precoEscalaZero, command.getNovoPreco());
    }
}
