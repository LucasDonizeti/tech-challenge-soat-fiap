package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarPrecoServicoCommand;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoServicoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarPrecoServicoRequest")
class AtualizarPrecoServicoRequestTest {

    private AtualizarPrecoServicoRequest request;

    @BeforeEach
    void setUp() {
        request = new AtualizarPrecoServicoRequest();
    }

    @Test
    @DisplayName("Deve criar AtualizarPrecoServicoCommand com preço válido")
    void deveCriarAtualizarPrecoServicoCommandComPrecoValido() {
        // Arrange
        BigDecimal preco = new BigDecimal("150.00");
        request.setNovoPreco(preco);

        // Act
        AtualizarPrecoServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(preco, command.getNovoPreco());
    }

    @Test
    @DisplayName("Deve criar AtualizarPrecoServicoCommand com preço decimal")
    void deveCriarAtualizarPrecoServicoCommandComPrecoDecimal() {
        // Arrange
        BigDecimal preco = new BigDecimal("125.50");
        request.setNovoPreco(preco);

        // Act
        AtualizarPrecoServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(preco, command.getNovoPreco());
    }

    @Test
    @DisplayName("Deve criar AtualizarPrecoServicoCommand com preço alto")
    void deveCriarAtualizarPrecoServicoCommandComPrecoAlto() {
        // Arrange
        BigDecimal preco = new BigDecimal("5000.00");
        request.setNovoPreco(preco);

        // Act
        AtualizarPrecoServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(preco, command.getNovoPreco());
    }

    @Test
    @DisplayName("Deve criar AtualizarPrecoServicoCommand com preço baixo")
    void deveCriarAtualizarPrecoServicoCommandComPrecoBaixo() {
        // Arrange
        BigDecimal preco = new BigDecimal("10.00");
        request.setNovoPreco(preco);

        // Act
        AtualizarPrecoServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(preco, command.getNovoPreco());
    }

    @Test
    @DisplayName("Deve permitir definir e obter preço")
    void devePermitirDefinirEObterPreco() {
        // Arrange
        BigDecimal preco = new BigDecimal("200.00");

        // Act
        request.setNovoPreco(preco);

        // Assert
        assertEquals(preco, request.getNovoPreco());
    }

    @Test
    @DisplayName("Deve permitir atualizar preço múltiplas vezes")
    void devePermitirAtualizarPrecoMultiplasVezes() {
        // Act & Assert
        request.setNovoPreco(new BigDecimal("100.00"));
        assertEquals(new BigDecimal("100.00"), request.getNovoPreco());

        request.setNovoPreco(new BigDecimal("150.00"));
        assertEquals(new BigDecimal("150.00"), request.getNovoPreco());

        request.setNovoPreco(new BigDecimal("200.00"));
        assertEquals(new BigDecimal("200.00"), request.getNovoPreco());
    }

    @Test
    @DisplayName("Deve criar command com preço com muitas casas decimais")
    void deveCriarCommandComPrecoComMuitasCasasDecimais() {
        // Arrange
        BigDecimal preco = new BigDecimal("123.456789");
        request.setNovoPreco(preco);

        // Act
        AtualizarPrecoServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(preco, command.getNovoPreco());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é zero via toCommand")
    void deveLancarExcecaoQuandoPrecoEZeroViaToCommand() {
        // Arrange
        request.setNovoPreco(BigDecimal.ZERO);

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> request.toCommand()
        );

        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é negativo via toCommand")
    void deveLancarExcecaoQuandoPrecoENegativoViaToCommand() {
        // Arrange
        request.setNovoPreco(new BigDecimal("-50.00"));

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> request.toCommand()
        );

        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }
}
