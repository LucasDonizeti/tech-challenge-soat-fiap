package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarClienteCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - CriarClienteRequest")
class CriarClienteRequestTest {

    private CriarClienteRequest request;

    @BeforeEach
    void setUp() {
        request = new CriarClienteRequest();
    }

    @Test
    @DisplayName("Deve criar CriarClienteCommand com CPF com sucesso")
    void deveCriarCriarClienteCommandComCpfComSucesso() {
        // Arrange
        request.setNome("João Silva");
        request.setCpf("12345678909");
        request.setEmail("joao.silva@email.com");

        // Act
        CriarClienteCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("João Silva", command.getNome().getValor());
        assertEquals("12345678909", command.getCpf().getValor());
        assertNull(command.getCnpj());
        assertEquals("joao.silva@email.com", command.getEmail().getEndereco());
    }

    @Test
    @DisplayName("Deve criar CriarClienteCommand com CNPJ com sucesso")
    void deveCriarCriarClienteCommandComCnpjComSucesso() {
        // Arrange
        request.setNome("Empresa Ltda");
        request.setCnpj("11444777000161");
        request.setEmail("contato@empresa.com");

        // Act
        CriarClienteCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("Empresa Ltda", command.getNome().getValor());
        assertNull(command.getCpf());
        assertEquals("11444777000161", command.getCnpj().getValor());
        assertEquals("contato@empresa.com", command.getEmail().getEndereco());
    }

    @Test
    @DisplayName("Deve permitir definir e obter nome")
    void devePermitirDefinirEObterNome() {
        // Act
        request.setNome("Maria Santos");

        // Assert
        assertEquals("Maria Santos", request.getNome());
    }

    @Test
    @DisplayName("Deve permitir definir e obter CPF")
    void devePermitirDefinirEObterCpf() {
        // Act
        request.setCpf("98765432100");

        // Assert
        assertEquals("98765432100", request.getCpf());
    }

    @Test
    @DisplayName("Deve permitir definir e obter CNPJ")
    void devePermitirDefinirEObterCnpj() {
        // Act
        request.setCnpj("11222333000181");

        // Assert
        assertEquals("11222333000181", request.getCnpj());
    }

    @Test
    @DisplayName("Deve permitir definir e obter email")
    void devePermitirDefinirEObterEmail() {
        // Act
        request.setEmail("teste@example.com");

        // Assert
        assertEquals("teste@example.com", request.getEmail());
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
    @DisplayName("Deve permitir atualizar email múltiplas vezes")
    void devePermitirAtualizarEmailMultiplasVezes() {
        // Act & Assert
        request.setEmail("email1@example.com");
        assertEquals("email1@example.com", request.getEmail());

        request.setEmail("email2@example.com");
        assertEquals("email2@example.com", request.getEmail());
    }

    @Test
    @DisplayName("Deve criar command com nome longo")
    void deveCriarCommandComNomeLongo() {
        // Arrange
        String nomeLongo = "João Francisco da Silva Santos Oliveira Pereira Costa";
        request.setNome(nomeLongo);
        request.setCpf("12345678909");
        request.setEmail("joao@example.com");

        // Act
        CriarClienteCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(nomeLongo, command.getNome().getValor());
    }

    @Test
    @DisplayName("Deve criar command com caracteres especiais no nome")
    void deveCriarCommandComCaracteresEspeciaisNoNome() {
        // Arrange
        request.setNome("José Antônio da Conceição");
        request.setCpf("12345678909");
        request.setEmail("jose@example.com");

        // Act
        CriarClienteCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("José Antônio da Conceição", command.getNome().getValor());
    }

    @Test
    @DisplayName("Deve criar command com CPF válido")
    void deveCriarCommandComCpfValido() {
        // Arrange
        request.setNome("Cliente");
        request.setCpf("12345678909");
        request.setEmail("cliente@example.com");

        // Act
        CriarClienteCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("12345678909", command.getCpf().getValor());
    }

    @Test
    @DisplayName("Deve criar command com CNPJ válido")
    void deveCriarCommandComCnpjValido() {
        // Arrange
        request.setNome("Empresa");
        request.setCnpj("11444777000161");
        request.setEmail("empresa@example.com");

        // Act
        CriarClienteCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("11444777000161", command.getCnpj().getValor());
    }
}
