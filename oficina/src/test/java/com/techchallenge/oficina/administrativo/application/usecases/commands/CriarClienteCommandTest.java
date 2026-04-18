package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoClienteException;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - CriarClienteCommand")
class CriarClienteCommandTest {

    @Test
    @DisplayName("Deve criar comando com CPF com sucesso")
    void deveCriarComandoComCpfComSucesso() {
        // Arrange
        String nome = "João Silva";
        String cpf = "12345678909";
        String cnpj = null;
        String email = "joao.silva@email.com";

        // Act
        CriarClienteCommand command = new CriarClienteCommand(nome, cpf, cnpj, email);

        // Assert
        assertNotNull(command);
        assertEquals(nome, command.getNome().getValor());
        assertEquals(cpf, command.getCpf().getValor());
        assertNull(command.getCnpj());
        assertEquals(email, command.getEmail().getEndereco());
        assertTrue(command.isPessoaFisica());
        assertFalse(command.isPessoaJuridica());
    }

    @Test
    @DisplayName("Deve criar comando com CNPJ com sucesso")
    void deveCriarComandoComCnpjComSucesso() {
        // Arrange
        String nome = "Empresa Ltda";
        String cpf = null;
        String cnpj = "11444777000161";
        String email = "contato@empresa.com";

        // Act
        CriarClienteCommand command = new CriarClienteCommand(nome, cpf, cnpj, email);

        // Assert
        assertNotNull(command);
        assertEquals(nome, command.getNome().getValor());
        assertNull(command.getCpf());
        assertEquals(cnpj, command.getCnpj().getValor());
        assertEquals(email, command.getEmail().getEndereco());
        assertFalse(command.isPessoaFisica());
        assertTrue(command.isPessoaJuridica());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é nulo")
    void deveLancarExcecaoQuandoNomeENulo() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new CriarClienteCommand(null, "12345678909", null, "joao@email.com")
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é vazio")
    void deveLancarExcecaoQuandoNomeEVazio() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new CriarClienteCommand("", "12345678909", null, "joao@email.com")
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é apenas espaços")
    void deveLancarExcecaoQuandoNomeEApenasEspacos() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new CriarClienteCommand("   ", "12345678909", null, "joao@email.com")
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email é nulo")
    void deveLancarExcecaoQuandoEmailENulo() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new CriarClienteCommand("João Silva", "12345678909", null, null)
        );

        assertEquals("Email é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email é vazio")
    void deveLancarExcecaoQuandoEmailEVazio() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new CriarClienteCommand("João Silva", "12345678909", null, "")
        );

        assertEquals("Email é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF e CNPJ são nulos")
    void deveLancarExcecaoQuandoCpfECnpjSaoNulos() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new CriarClienteCommand("João Silva", null, null, "joao@email.com")
        );

        assertEquals("CPF ou CNPJ é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF e CNPJ são vazios")
    void deveLancarExcecaoQuandoCpfECnpjSaoVazios() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new CriarClienteCommand("João Silva", "", "", "joao@email.com")
        );

        assertEquals("CPF ou CNPJ é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF e CNPJ são ambos fornecidos")
    void deveLancarExcecaoQuandoCpfECnpjSaoAmbosFornecidos() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new CriarClienteCommand("João Silva", "12345678909", "11444777000161", "joao@email.com")
        );

        assertEquals("Cliente não pode ter CPF e CNPJ simultaneamente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve converter nome para value object")
    void deveConverterNomeParaValueObject() {
        // Act
        CriarClienteCommand command = new CriarClienteCommand("João Silva", "12345678909", null, "joao@email.com");

        // Assert
        assertNotNull(command.getNome());
        assertEquals(Nome.class, command.getNome().getClass());
        assertEquals("João Silva", command.getNome().getValor());
    }

    @Test
    @DisplayName("Deve converter CPF para value object quando fornecido")
    void deveConverterCpfParaValueObjectQuandoFornecido() {
        // Act
        CriarClienteCommand command = new CriarClienteCommand("João Silva", "12345678909", null, "joao@email.com");

        // Assert
        assertNotNull(command.getCpf());
        assertEquals(CPF.class, command.getCpf().getClass());
        assertEquals("12345678909", command.getCpf().getValor());
    }

    @Test
    @DisplayName("Deve converter CNPJ para value object quando fornecido")
    void deveConverterCnpjParaValueObjectQuandoFornecido() {
        // Act
        CriarClienteCommand command = new CriarClienteCommand("Empresa Ltda", null, "11444777000161", "contato@empresa.com");

        // Assert
        assertNotNull(command.getCnpj());
        assertEquals(CNPJ.class, command.getCnpj().getClass());
        assertEquals("11444777000161", command.getCnpj().getValor());
    }

    @Test
    @DisplayName("Deve converter email para value object")
    void deveConverterEmailParaValueObject() {
        // Act
        CriarClienteCommand command = new CriarClienteCommand("João Silva", "12345678909", null, "joao@email.com");

        // Assert
        assertNotNull(command.getEmail());
        assertEquals(Email.class, command.getEmail().getClass());
        assertEquals("joao@email.com", command.getEmail().getEndereco());
    }

    @Test
    @DisplayName("Deve identificar corretamente pessoa física")
    void deveIdentificarCorretamentePessoaFisica() {
        // Act
        CriarClienteCommand command = new CriarClienteCommand("João Silva", "12345678909", null, "joao@email.com");

        // Assert
        assertTrue(command.isPessoaFisica());
        assertFalse(command.isPessoaJuridica());
    }

    @Test
    @DisplayName("Deve identificar corretamente pessoa jurídica")
    void deveIdentificarCorretamentePessoaJuridica() {
        // Act
        CriarClienteCommand command = new CriarClienteCommand("Empresa Ltda", null, "11444777000161", "contato@empresa.com");

        // Assert
        assertFalse(command.isPessoaFisica());
        assertTrue(command.isPessoaJuridica());
    }
}
