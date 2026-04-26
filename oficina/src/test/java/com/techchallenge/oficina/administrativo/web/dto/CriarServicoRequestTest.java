package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarServicoCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - CriarServicoRequest")
class CriarServicoRequestTest {

    private CriarServicoRequest request;

    @BeforeEach
    void setUp() {
        request = new CriarServicoRequest();
    }

    @Test
    @DisplayName("Deve criar CriarServicoCommand com dados completos")
    void deveCriarCriarServicoCommandComDadosCompletos() {
        // Arrange
        request.setNome("Troca de Óleo");
        request.setDescricao("Troca completa de óleo");
        request.setPreco(new BigDecimal("150.00"));

        // Act
        CriarServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("Troca de Óleo", command.getNome());
        assertEquals("Troca completa de óleo", command.getDescricao());
        assertEquals(new BigDecimal("150.00"), command.getPreco());
    }

    @Test
    @DisplayName("Deve criar CriarServicoCommand sem descrição")
    void deveCriarCriarServicoCommandSemDescricao() {
        // Arrange
        request.setNome("Alinhamento");
        request.setPreco(new BigDecimal("100.00"));

        // Act
        CriarServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("Alinhamento", command.getNome());
        assertNull(command.getDescricao());
        assertEquals(new BigDecimal("100.00"), command.getPreco());
    }

    @Test
    @DisplayName("Deve permitir definir e obter nome")
    void devePermitirDefinirEObterNome() {
        // Act
        request.setNome("Freio");

        // Assert
        assertEquals("Freio", request.getNome());
    }

    @Test
    @DisplayName("Deve permitir definir e obter descrição")
    void devePermitirDefinirEObterDescricao() {
        // Act
        request.setDescricao("Troca de pastilhas");

        // Assert
        assertEquals("Troca de pastilhas", request.getDescricao());
    }

    @Test
    @DisplayName("Deve permitir definir e obter preço")
    void devePermitirDefinirEObterPreco() {
        // Arrange
        BigDecimal preco = new BigDecimal("200.00");

        // Act
        request.setPreco(preco);

        // Assert
        assertEquals(preco, request.getPreco());
    }

    @Test
    @DisplayName("Deve permitir atualizar nome múltiplas vezes")
    void devePermitirAtualizarNomeMultiplasVezes() {
        // Act & Assert
        request.setNome("Nome 1");
        assertEquals("Nome 1", request.getNome());

        request.setNome("Nome 2");
        assertEquals("Nome 2", request.getNome());
    }

    @Test
    @DisplayName("Deve permitir atualizar preço múltiplas vezes")
    void devePermitirAtualizarPrecoMultiplasVezes() {
        // Act & Assert
        request.setPreco(new BigDecimal("100.00"));
        assertEquals(new BigDecimal("100.00"), request.getPreco());

        request.setPreco(new BigDecimal("150.00"));
        assertEquals(new BigDecimal("150.00"), request.getPreco());
    }

    @Test
    @DisplayName("Deve criar command com preço decimal")
    void deveCriarCommandComPrecoDecimal() {
        // Arrange
        request.setNome("Serviço");
        request.setPreco(new BigDecimal("125.50"));

        // Act
        CriarServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(new BigDecimal("125.50"), command.getPreco());
    }

    @Test
    @DisplayName("Deve criar command com preço alto")
    void deveCriarCommandComPrecoAlto() {
        // Arrange
        request.setNome("Serviço");
        request.setPreco(new BigDecimal("5000.00"));

        // Act
        CriarServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(new BigDecimal("5000.00"), command.getPreco());
    }

    @Test
    @DisplayName("Deve criar command com descrição longa")
    void deveCriarCommandComDescricaoLonga() {
        // Arrange
        String descricaoLonga = "Serviço completo de manutenção preventiva incluindo troca de óleo, filtros e verificação";
        request.setNome("Serviço");
        request.setDescricao(descricaoLonga);
        request.setPreco(new BigDecimal("200.00"));

        // Act
        CriarServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(descricaoLonga, command.getDescricao());
    }

    @Test
    @DisplayName("Deve criar command com caracteres especiais no nome")
    void deveCriarCommandComCaracteresEspeciaisNoNome() {
        // Arrange
        request.setNome("Troca de Óleo & Filtros");
        request.setPreco(new BigDecimal("150.00"));

        // Act
        CriarServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("Troca de Óleo & Filtros", command.getNome());
    }
}
