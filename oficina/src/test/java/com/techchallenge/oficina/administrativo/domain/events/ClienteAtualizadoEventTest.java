package com.techchallenge.oficina.administrativo.domain.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de ClienteAtualizadoEvent - Domain Layer")
class ClienteAtualizadoEventTest {

    @Test
    @DisplayName("Deve criar evento com dados de atualização")
    void deveCriarEventoComDadosAtualizacao() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        String nome = "João Silva Santos";

        // Act
        ClienteAtualizadoEvent event = new ClienteAtualizadoEvent(clienteId, nome);

        // Assert
        assertNotNull(event);
        assertEquals(clienteId, event.getClienteId());
        assertEquals(nome, event.getNome());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("Deve criar evento com timestamp atual")
    void deveCriarEventoComTimestampAtual() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        long beforeCreation = System.currentTimeMillis();

        // Act
        ClienteAtualizadoEvent event = new ClienteAtualizadoEvent(clienteId, "Nome Atualizado");
        long afterCreation = System.currentTimeMillis();

        // Assert
        assertNotNull(event.getOccurredOn());
        assertTrue(event.getOccurredOn().isAfter(java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(beforeCreation), java.time.ZoneId.systemDefault())));
        assertTrue(event.getOccurredOn().isBefore(java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(afterCreation), java.time.ZoneId.systemDefault()).plusSeconds(1)));
    }

    @Test
    @DisplayName("Deve criar evento com nome nulo")
    void deveCriarEventoComNomeNulo() {
        // Arrange
        UUID clienteId = UUID.randomUUID();

        // Act
        ClienteAtualizadoEvent event = new ClienteAtualizadoEvent(clienteId, null);

        // Assert
        assertNotNull(event);
        assertEquals(clienteId, event.getClienteId());
        assertNull(event.getNome());
    }

    @Test
    @DisplayName("Deve criar evento com nome vazio")
    void deveCriarEventoComNomeVazio() {
        // Arrange
        UUID clienteId = UUID.randomUUID();

        // Act
        ClienteAtualizadoEvent event = new ClienteAtualizadoEvent(clienteId, "");

        // Assert
        assertNotNull(event);
        assertEquals(clienteId, event.getClienteId());
        assertEquals("", event.getNome());
    }
}
