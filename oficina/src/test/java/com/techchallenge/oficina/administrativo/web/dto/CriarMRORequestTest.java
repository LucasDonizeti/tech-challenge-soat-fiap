package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarMROCommand;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoMROException;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - CriarMRORequest")
class CriarMRORequestTest {

    @Test
    @DisplayName("Deve converter request para command com sucesso")
    void deveConverterRequestParaCommandComSucesso() {
        // Arrange
        CriarMRORequest request = new CriarMRORequest();
        request.setNome("Óleo Motor 5W30");
        request.setDescricao("Óleo para motor automotivo");
        request.setTipo("INSUMO");
        request.setQuantidadeEstoque(100);
        request.setPrecoUnitario(new BigDecimal("45.90"));

        // Act
        CriarMROCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("Óleo Motor 5W30", command.getNome());
        assertEquals("Óleo para motor automotivo", command.getDescricao());
        assertEquals(TipoMRO.INSUMO, command.getTipo());
        assertEquals(100, command.getQuantidadeEstoque());
        assertEquals(new BigDecimal("45.90"), command.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve converter request para command com tipo PECA")
    void deveConverterRequestParaCommandComTipoPeca() {
        // Arrange
        CriarMRORequest request = new CriarMRORequest();
        request.setNome("Filtro de Óleo");
        request.setTipo("PECA");
        request.setQuantidadeEstoque(50);
        request.setPrecoUnitario(new BigDecimal("25.00"));

        // Act
        CriarMROCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(TipoMRO.PECA, command.getTipo());
    }

    @Test
    @DisplayName("Deve converter request para command com tipo em minúsculo")
    void deveConverterRequestParaCommandComTipoMinusculo() {
        // Arrange
        CriarMRORequest request = new CriarMRORequest();
        request.setNome("Produto");
        request.setTipo("insumo");
        request.setQuantidadeEstoque(10);
        request.setPrecoUnitario(new BigDecimal("10.00"));

        // Act
        CriarMROCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(TipoMRO.INSUMO, command.getTipo());
    }

    @Test
    @DisplayName("Deve converter request para command com descricao null")
    void deveConverterRequestParaCommandComDescricaoNull() {
        // Arrange
        CriarMRORequest request = new CriarMRORequest();
        request.setNome("Produto");
        request.setTipo("PECA");
        request.setDescricao(null);
        request.setQuantidadeEstoque(100);
        request.setPrecoUnitario(new BigDecimal("15.00"));

        // Act
        CriarMROCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertNull(command.getDescricao());
    }

    @Test
    @DisplayName("Deve converter request para command com estoque zero")
    void deveConverterRequestParaCommandComEstoqueZero() {
        // Arrange
        CriarMRORequest request = new CriarMRORequest();
        request.setNome("Produto");
        request.setTipo("INSUMO");
        request.setQuantidadeEstoque(0);
        request.setPrecoUnitario(new BigDecimal("10.00"));

        // Act
        CriarMROCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(0, command.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve lançar exceção quando tipo é inválido")
    void deveLancarExcecaoQuandoTipoEInvalido() {
        // Arrange
        CriarMRORequest request = new CriarMRORequest();
        request.setNome("Produto");
        request.setTipo("TIPO_INVALIDO");
        request.setQuantidadeEstoque(10);
        request.setPrecoUnitario(new BigDecimal("10.00"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> request.toCommand());
    }

    @Test
    @DisplayName("Deve lançar exceção quando tipo é null")
    void deveLancarExcecaoQuandoTipoENull() {
        // Arrange
        CriarMRORequest request = new CriarMRORequest();
        request.setNome("Produto");
        request.setTipo(null);
        request.setQuantidadeEstoque(10);
        request.setPrecoUnitario(new BigDecimal("10.00"));

        // Act & Assert
        assertThrows(NullPointerException.class, () -> request.toCommand());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é null")
    void deveLancarExcecaoQuandoPrecoENull() {
        // Arrange
        CriarMRORequest request = new CriarMRORequest();
        request.setNome("Produto");
        request.setTipo("PECA");
        request.setQuantidadeEstoque(10);
        request.setPrecoUnitario(null);

        // Act & Assert
        assertThrows(ValidacaoMROException.class, () -> request.toCommand());
    }
}
