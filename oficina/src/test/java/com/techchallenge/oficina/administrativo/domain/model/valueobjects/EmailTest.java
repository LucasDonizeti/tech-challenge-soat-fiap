package com.techchallenge.oficina.administrativo.domain.model.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EmailTest {

    @Test
    @DisplayName("Deve criar email válido")
    void deveCriarEmailValido() {
        // Arrange
        String emailValido = "teste@exemplo.com";
        
        // Act
        Email email = Email.of(emailValido);
        
        // Assert
        assertNotNull(email);
        assertEquals("teste@exemplo.com", email.getEndereco());
        assertEquals("teste", email.getUsuario());
        assertEquals("exemplo.com", email.getDominio());
    }

    @Test
    @DisplayName("Deve lançar exceção para email nulo")
    void deveLancarExcecaoParaEmailNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Email.of(null)
        );
        
        assertEquals("Email não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para email vazio")
    void deveLancarExcecaoParaEmailVazio() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Email.of("")
        );
        
        assertEquals("Email não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para email em branco")
    void deveLancarExcecaoParaEmailEmBranco() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Email.of("   ")
        );
        
        assertEquals("Email não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para email com mais de 100 caracteres")
    void deveLancarExcecaoParaEmailComMaisDe100Caracteres() {
        // Arrange
        String emailLongo = "a".repeat(90) + "@exemplo.com";
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Email.of(emailLongo)
        );
        
        assertEquals("Email deve ter no máximo 100 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para email sem @")
    void deveLancarExcecaoParaEmailSemArroba() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Email.of("emailinvalido")
        );
        
        assertEquals("Email inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para email sem dominio")
    void deveLancarExcecaoParaEmailSemDominio() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Email.of("usuario@")
        );
        
        assertEquals("Email inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve converter email para lowercase")
    void deveConverterEmailParaLowercase() {
        // Arrange
        String emailMaiusculo = "TESTE@EXEMPLO.COM";
        
        // Act
        Email email = Email.of(emailMaiusculo);
        
        // Assert
        assertEquals("teste@exemplo.com", email.getEndereco());
    }

    @Test
    @DisplayName("Deve trim espaços do email")
    void deveTrimEspacosDoEmail() {
        // Arrange
        String emailComEspacos = "  teste@exemplo.com  ";
        
        // Act
        Email email = Email.of(emailComEspacos);
        
        // Assert
        assertEquals("teste@exemplo.com", email.getEndereco());
    }

    @Test
    @DisplayName("Deve retornar usuario corretamente")
    void deveRetornarUsuarioCorretamente() {
        // Arrange
        Email email = Email.of("joao.silva@empresa.com.br");
        
        // Act & Assert
        assertEquals("joao.silva", email.getUsuario());
    }

    @Test
    @DisplayName("Deve retornar dominio corretamente")
    void deveRetornarDominioCorretamente() {
        // Arrange
        Email email = Email.of("joao.silva@empresa.com.br");
        
        // Act & Assert
        assertEquals("empresa.com.br", email.getDominio());
    }


    @Test
    @DisplayName("Deve implementar equals e hashCode corretamente")
    void deveImplementarEqualsAndHashCodeCorretamente() {
        // Arrange
        Email email1 = Email.of("teste@exemplo.com");
        Email email2 = Email.of("teste@exemplo.com");
        Email email3 = Email.of("outro@exemplo.com");
        
        // Assert
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
        assertNotEquals(email1, email3);
        assertNotEquals(email1.hashCode(), email3.hashCode());
    }

    @Test
    @DisplayName("Deve retornar toString com endereco")
    void deveRetornarToStringComEndereco() {
        // Arrange
        Email email = Email.of("teste@exemplo.com");
        
        // Act & Assert
        assertEquals("teste@exemplo.com", email.toString());
    }
}
