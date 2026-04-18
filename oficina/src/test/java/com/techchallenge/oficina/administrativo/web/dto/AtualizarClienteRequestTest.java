package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarClienteCommand;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoClienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarClienteRequest")
class AtualizarClienteRequestTest {

    private AtualizarClienteRequest request;

    @BeforeEach
    void setUp() {
        request = new AtualizarClienteRequest();
    }

    @Test
    @DisplayName("Deve criar AtualizarClienteCommand com dados válidos")
    void deveCriarAtualizarClienteCommandComDadosValidos() {
        // Arrange
        request.setNome("João Silva");
        request.setEmail("joao@example.com");

        // Act
        AtualizarClienteCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("João Silva", command.getNome().getValor());
        assertEquals("joao@example.com", command.getEmail().getEndereco());
    }

    @Test
    @DisplayName("Deve criar AtualizarClienteCommand com nome e email")
    void deveCriarAtualizarClienteCommandComNomeEEmail() {
        // Arrange
        request.setNome("Maria Santos");
        request.setEmail("maria.santos@example.com");

        // Act
        AtualizarClienteCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("Maria Santos", command.getNome().getValor());
        assertEquals("maria.santos@example.com", command.getEmail().getEndereco());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar AtualizarClienteCommand com dados vazios")
    void deveLancarExcecaoAoCriarAtualizarClienteCommandComDadosVazios() {
        // Arrange
        request.setNome("");
        request.setEmail("");

        // Act & Assert
        assertThrows(ValidacaoClienteException.class, request::toCommand);
    }

    @Test
    @DisplayName("Deve criar AtualizarClienteCommand mesmo com dados nulos")
    void deveCriarAtualizarClienteCommandMesmoComDadosNulos() {
        // Arrange
        request.setNome(null);
        request.setEmail(null);

        // Act & Assert
        assertThrows(ValidacaoClienteException.class, request::toCommand);
    }

    @Test
    @DisplayName("Deve permitir definir e obter nome")
    void devePermitirDefinirEObterNome() {
        // Act
        request.setNome("Carlos Oliveira");

        // Assert
        assertEquals("Carlos Oliveira", request.getNome());
    }

    @Test
    @DisplayName("Deve permitir definir e obter email")
    void devePermitirDefinirEObterEmail() {
        // Act
        request.setEmail("carlos@example.com");

        // Assert
        assertEquals("carlos@example.com", request.getEmail());
    }

    @ParameterizedTest
    @CsvSource({
        "teste.valido@dominio.com.br, teste.valido@dominio.com.br",
        "teste@example.com, teste@example.com",
        "TESTE@EXAMPLE.COM, teste@example.com",
        "  joao@example.com  , joao@example.com"
    })
    @DisplayName("Deve criar AtualizarClienteCommand com diferentes formatos de email")
    void deveCriarAtualizarClienteCommandComDiferentesFormatosDeEmail(String inputEmail, String expectedEmail) {
        // Arrange
        request.setNome("Teste");
        request.setEmail(inputEmail);

        // Act
        AtualizarClienteCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(expectedEmail, command.getEmail().getEndereco());
    }

    @Test
    @DisplayName("Deve criar AtualizarClienteCommand com nome longo")
    void deveCriarAtualizarClienteCommandComNomeLongo() {
        // Arrange
        String nomeLongo = "João Francisco da Silva Santos Oliveira Pereira Costa";
        request.setNome(nomeLongo);
        request.setEmail("joao@example.com");

        // Act
        AtualizarClienteCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(nomeLongo, command.getNome().getValor());
    }

    @Test
    @DisplayName("Deve criar AtualizarClienteCommand com caracteres especiais no nome")
    void deveCriarAtualizarClienteCommandComCaracteresEspeciaisNoNome() {
        // Arrange
        request.setNome("José Antônio da Conceição");
        request.setEmail("jose@example.com");

        // Act
        AtualizarClienteCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("José Antônio da Conceição", command.getNome().getValor());
    }

    @Test
    @DisplayName("Deve permitir atualizar nome multiple vezes")
    void devePermitirAtualizarNomeMultiplasVezes() {
        // Act & Assert
        request.setNome("Nome 1");
        assertEquals("Nome 1", request.getNome());

        request.setNome("Nome 2");
        assertEquals("Nome 2", request.getNome());

        request.setNome("Nome 3");
        assertEquals("Nome 3", request.getNome());
    }

    @Test
    @DisplayName("Deve permitir atualizar email multiple vezes")
    void devePermitirAtualizarEmailMultiplasVezes() {
        // Act & Assert
        request.setEmail("email1@example.com");
        assertEquals("email1@example.com", request.getEmail());

        request.setEmail("email2@example.com");
        assertEquals("email2@example.com", request.getEmail());

        request.setEmail("email3@example.com");
        assertEquals("email3@example.com", request.getEmail());
    }

    @Test
    @DisplayName("Deve criar command com espaços no nome")
    void deveCriarCommandComEspacosNoNome() {
        // Arrange
        request.setNome("  João Silva  ");
        request.setEmail("joao@example.com");

        // Act
        AtualizarClienteCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("João Silva", command.getNome().getValor());
    }

    @Test
    @DisplayName("Deve criar command com espaços no email")
    void deveCriarCommandComEspacosNoEmail() {
        // Arrange
        request.setNome("João Silva");
        request.setEmail("  joao@example.com  ");

        // Act
        AtualizarClienteCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("joao@example.com", command.getEmail().getEndereco());
    }
}
