package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - CriarOrdemServicoCommand")
class CriarOrdemServicoCommandTest {

    @Test
    @DisplayName("Deve criar comando com dados válidos")
    void deveCriarComandoComDadosValidos() {
        // Arrange
        String cpfOuCnpj = "52998224725";
        String placa = "ABC1234";
        List<String> codigosServico = List.of("SVC001");
        List<CriarOrdemServicoCommand.ItemMROCommand> itensMRO = List.of();

        // Act
        CriarOrdemServicoCommand command = new CriarOrdemServicoCommand(cpfOuCnpj, placa, codigosServico, itensMRO);

        // Assert
        assertNotNull(command);
        assertEquals(cpfOuCnpj, command.getCpfOuCnpj());
        assertEquals(placa, command.getPlaca());
        assertEquals(codigosServico, command.getCodigosServico());
        assertEquals(itensMRO, command.getItensMRO());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cpfOuCnpj é nulo")
    void deveLancarExcecaoQuandoCpfOuCnpjENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new CriarOrdemServicoCommand(null, "ABC1234", List.of("SVC001"), List.of())
        );

        assertEquals("CPF ou CNPJ do cliente é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando placa é nula")
    void deveLancarExcecaoQuandoPlacaENula() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new CriarOrdemServicoCommand("52998224725", null, List.of("SVC001"), List.of())
        );

        assertEquals("Placa do veículo é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando lista de serviços é vazia")
    void deveLancarExcecaoQuandoListaDeServicosEVazia() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new CriarOrdemServicoCommand("52998224725", "ABC1234", List.of(), List.of())
        );

        assertEquals("A lista de serviços não pode ser vazia", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar comando com múltiplos serviços")
    void deveCriarComandoComMultiplosServicos() {
        // Arrange
        String cpfOuCnpj = "52998224725";
        String placa = "ABC1234";
        List<String> codigosServico = List.of("SVC001", "SVC002");
        List<CriarOrdemServicoCommand.ItemMROCommand> itensMRO = List.of();

        // Act
        CriarOrdemServicoCommand command = new CriarOrdemServicoCommand(cpfOuCnpj, placa, codigosServico, itensMRO);

        // Assert
        assertNotNull(command);
        assertEquals(2, command.getCodigosServico().size());
    }

    @Test
    @DisplayName("Deve criar comando com itens MRO")
    void deveCriarComandoComItensMRO() {
        // Arrange
        String cpfOuCnpj = "52998224725";
        String placa = "ABC1234";
        List<String> codigosServico = List.of("SVC001");
        List<CriarOrdemServicoCommand.ItemMROCommand> itensMRO = List.of(
            new CriarOrdemServicoCommand.ItemMROCommand("PC001", 2)
        );

        // Act
        CriarOrdemServicoCommand command = new CriarOrdemServicoCommand(cpfOuCnpj, placa, codigosServico, itensMRO);

        // Assert
        assertNotNull(command);
        assertEquals(1, command.getItensMRO().size());
        assertEquals("PC001", command.getItensMRO().get(0).getCodigoMro());
        assertEquals(2, command.getItensMRO().get(0).getQuantidade());
    }

    @Test
    @DisplayName("Deve lançar exceção quando código MRO é nulo")
    void deveLancarExcecaoQuandoCodigoMroENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new CriarOrdemServicoCommand.ItemMROCommand(null, 1)
        );

        assertEquals("Código do MRO é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade MRO é zero")
    void deveLancarExcecaoQuandoQuantidadeMroEZero() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new CriarOrdemServicoCommand.ItemMROCommand("PC001", 0)
        );

        assertEquals("Quantidade do MRO deve ser maior que zero", exception.getMessage());
    }
}
