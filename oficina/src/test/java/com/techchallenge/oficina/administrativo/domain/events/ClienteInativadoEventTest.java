package com.techchallenge.oficina.administrativo.domain.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de ClienteInativadoEvent - Domain Layer")
class ClienteInativadoEventTest {

    @Test
    @DisplayName("Deve criar evento com ID do cliente")
    void deveCriarEventoComIdCliente() {
        // Arrange
        UUID clienteId = UUID.randomUUID();

        // Act
        ClienteInativadoEvent event = new ClienteInativadoEvent(clienteId);

        // Assert
        assertNotNull(event);
        assertEquals(clienteId, event.getClienteId());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("Deve criar evento com timestamp atual")
    void deveCriarEventoComTimestampAtual() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        long beforeCreation = System.currentTimeMillis();

        // Act
        ClienteInativadoEvent event = new ClienteInativadoEvent(clienteId);
        long afterCreation = System.currentTimeMillis();

        // Assert
        assertNotNull(event.getOccurredOn());
        assertTrue(event.getOccurredOn().isAfter(java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(beforeCreation), java.time.ZoneId.systemDefault())));
        assertTrue(event.getOccurredOn().isBefore(java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(afterCreation), java.time.ZoneId.systemDefault()).plusSeconds(1)));
    }

    @Test
    @DisplayName("Deve criar evento com UUID válido")
    void deveCriarEventoComUuidValido() {
        // Arrange
        UUID clienteId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        // Act
        ClienteInativadoEvent event = new ClienteInativadoEvent(clienteId);

        // Assert
        assertNotNull(event);
        assertEquals(clienteId, event.getClienteId());
    }
}
