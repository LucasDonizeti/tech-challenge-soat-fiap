package com.techchallenge.oficina.administrativo.domain.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de ClienteCriadoEvent - Domain Layer")
class ClienteCriadoEventTest {

    @Test
    @DisplayName("Deve criar evento com dados de cliente pessoa física")
    void deveCriarEventoComClientePessoaFisica() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        String nome = "João Silva";
        String cpf = "123.456.789-09";
        String cnpj = null;
        String email = "joao.silva@email.com";

        // Act
        ClienteCriadoEvent event = new ClienteCriadoEvent(clienteId, nome, cpf, cnpj, email);

        // Assert
        assertNotNull(event);
        assertEquals(clienteId, event.getClienteId());
        assertEquals(nome, event.getNome());
        assertEquals(cpf, event.getCpf());
        assertEquals(cnpj, event.getCnpj());
        assertEquals(email, event.getEmail());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("Deve criar evento com dados de cliente pessoa jurídica")
    void deveCriarEventoComClientePessoaJuridica() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        String nome = "Empresa XYZ Ltda";
        String cpf = null;
        String cnpj = "11.222.333/0001-81";
        String email = "contato@empresa.com";

        // Act
        ClienteCriadoEvent event = new ClienteCriadoEvent(clienteId, nome, cpf, cnpj, email);

        // Assert
        assertNotNull(event);
        assertEquals(clienteId, event.getClienteId());
        assertEquals(nome, event.getNome());
        assertEquals(cpf, event.getCpf());
        assertEquals(cnpj, event.getCnpj());
        assertEquals(email, event.getEmail());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("Deve criar evento com timestamp atual")
    void deveCriarEventoComTimestampAtual() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        long beforeCreation = System.currentTimeMillis();

        // Act
        ClienteCriadoEvent event = new ClienteCriadoEvent(clienteId, "Nome", "123.456.789-09", null, "email@teste.com");
        long afterCreation = System.currentTimeMillis();

        // Assert
        assertNotNull(event.getOccurredOn());
        assertTrue(event.getOccurredOn().isAfter(java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(beforeCreation), java.time.ZoneId.systemDefault())));
        assertTrue(event.getOccurredOn().isBefore(java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(afterCreation), java.time.ZoneId.systemDefault()).plusSeconds(1)));
    }

    @Test
    @DisplayName("Deve criar evento com todos os campos nulos")
    void deveCriarEventoComCamposNulos() {
        // Arrange
        UUID clienteId = UUID.randomUUID();

        // Act
        ClienteCriadoEvent event = new ClienteCriadoEvent(clienteId, null, null, null, null);

        // Assert
        assertNotNull(event);
        assertEquals(clienteId, event.getClienteId());
        assertNull(event.getNome());
        assertNull(event.getCpf());
        assertNull(event.getCnpj());
        assertNull(event.getEmail());
    }
}
