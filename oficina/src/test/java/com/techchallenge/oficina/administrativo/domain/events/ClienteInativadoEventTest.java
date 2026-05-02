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
        java.time.LocalDateTime beforeCreation = java.time.LocalDateTime.now();

        // Act
        ClienteInativadoEvent event = new ClienteInativadoEvent(clienteId);
        java.time.LocalDateTime afterCreation = java.time.LocalDateTime.now();

        // Assert
        assertNotNull(event.getOccurredOn());
        assertTrue(event.getOccurredOn().isAfter(beforeCreation.minusSeconds(1)) || event.getOccurredOn().isEqual(beforeCreation));
        assertTrue(event.getOccurredOn().isBefore(afterCreation.plusSeconds(1)));
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
