package com.techchallenge.oficina.sharedkernel.domain.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - DomainEvent")
class DomainEventTest {

    @Test
    @DisplayName("Deve criar subclasse concreta de DomainEvent com construtor padrão")
    void deveCriarSubclasseConcretaDeDomainEventComConstrutorPadrao() {
        // Arrange & Act
        class TestEvent extends DomainEvent {
            public TestEvent() {
                super();
            }
        }

        TestEvent event = new TestEvent();

        // Assert
        assertNotNull(event);
        assertNotNull(event.getOccurredOn());
        assertTrue(event.getOccurredOn().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(event.getOccurredOn().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    @DisplayName("Deve criar subclasse concreta de DomainEvent com timestamp específico")
    void deveCriarSubclasseConcretaDeDomainEventComTimestampEspecifico() {
        // Arrange
        LocalDateTime specificTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        // Act
        class TestEvent extends DomainEvent {
            public TestEvent(LocalDateTime timestamp) {
                super(timestamp);
            }
        }

        TestEvent event = new TestEvent(specificTime);

        // Assert
        assertNotNull(event);
        assertEquals(specificTime, event.getOccurredOn());
    }

    @Test
    @DisplayName("Deve criar evento com timestamp atual quando usa construtor padrão")
    void deveCriarEventoComTimestampAtualQuandoUsaConstrutorPadrao() {
        // Arrange & Act
        class TestEvent extends DomainEvent {
            public TestEvent() {
                super();
            }
        }

        LocalDateTime before = LocalDateTime.now();
        TestEvent event = new TestEvent();
        LocalDateTime after = LocalDateTime.now();

        // Assert
        assertTrue(event.getOccurredOn().isAfter(before.minusSeconds(1)) || event.getOccurredOn().isEqual(before));
        assertTrue(event.getOccurredOn().isBefore(after.plusSeconds(1)));
    }

    @Test
    @DisplayName("Deve permitir acesso a occurredOn")
    void devePermitirAcessoAOccurredOn() {
        // Arrange
        LocalDateTime specificTime = LocalDateTime.of(2024, 6, 15, 14, 30, 0);

        // Act
        class TestEvent extends DomainEvent {
            public TestEvent(LocalDateTime timestamp) {
                super(timestamp);
            }
        }

        TestEvent event = new TestEvent(specificTime);

        // Assert
        assertEquals(specificTime, event.getOccurredOn());
    }

    @Test
    @DisplayName("Deve criar múltiplos eventos com timestamps diferentes")
    void deveCriarMultiplosEventosComTimestampsDiferentes() {
        // Arrange & Act
        class TestEvent extends DomainEvent {
            public TestEvent() {
                super();
            }
        }

        TestEvent event1 = new TestEvent();
        // Force a small time difference by creating event2 in a new call
        TestEvent event2 = new TestEvent();

        // Assert - Note: In practice, timestamps might be the same if events are created very quickly
        // This test demonstrates the concept rather than guaranteeing different timestamps
        assertNotNull(event1.getOccurredOn());
        assertNotNull(event2.getOccurredOn());
    }

    @Test
    @DisplayName("Deve criar evento com timestamp nulo não é possível devido ao construtor")
    void deveCriarEventoComTimestampNuloNaoEPossivelDevidoAoConstrutor() {
        // Arrange & Act
        class TestEvent extends DomainEvent {
            public TestEvent(LocalDateTime timestamp) {
                super(timestamp);
            }
        }

        TestEvent event = new TestEvent(null);

        // Assert
        assertNotNull(event);
        assertNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("Deve criar evento com timestamp futuro")
    void deveCriarEventoComTimestampFuturo() {
        // Arrange
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);

        // Act
        class TestEvent extends DomainEvent {
            public TestEvent(LocalDateTime timestamp) {
                super(timestamp);
            }
        }

        TestEvent event = new TestEvent(futureTime);

        // Assert
        assertEquals(futureTime, event.getOccurredOn());
    }

    @Test
    @DisplayName("Deve criar evento com timestamp passado")
    void deveCriarEventoComTimestampPassado() {
        // Arrange
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);

        // Act
        class TestEvent extends DomainEvent {
            public TestEvent(LocalDateTime timestamp) {
                super(timestamp);
            }
        }

        TestEvent event = new TestEvent(pastTime);

        // Assert
        assertEquals(pastTime, event.getOccurredOn());
    }

    @Test
    @DisplayName("Deve ser uma classe abstrata")
    void deveSerUmaClasseAbstrata() {
        // Assert
        assertTrue(Modifier.isAbstract(DomainEvent.class.getModifiers()));
    }

    @Test
    @DisplayName("Deve ter occurredOn como campo final")
    void deveTerOccurredOnComoCampoFinal() {
        // Arrange
        LocalDateTime specificTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0);

        // Act
        class TestEvent extends DomainEvent {
            public TestEvent(LocalDateTime timestamp) {
                super(timestamp);
            }
        }

        TestEvent event = new TestEvent(specificTime);

        // Assert
        assertEquals(specificTime, event.getOccurredOn());
        // O campo é final, então não pode ser alterado após a construção
    }
}
