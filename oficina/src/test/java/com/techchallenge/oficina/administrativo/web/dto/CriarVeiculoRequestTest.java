package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarVeiculoCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - CriarVeiculoRequest")
class CriarVeiculoRequestTest {

    private CriarVeiculoRequest request;

    @BeforeEach
    void setUp() {
        request = new CriarVeiculoRequest();
    }

    @Test
    @DisplayName("Deve criar CriarVeiculoCommand com dados completos")
    void deveCriarCriarVeiculoCommandComDadosCompletos() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        request.setPlaca("ABC1234");
        request.setMarca("Toyota");
        request.setModelo("Corolla");
        request.setAno(2020);
        request.setCor("Prata");
        request.setClienteId(clienteId);

        // Act
        CriarVeiculoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("ABC1234", command.getPlaca().getValor());
        assertEquals("Toyota", command.getMarca());
        assertEquals("Corolla", command.getModelo());
        assertEquals(2020, command.getAno());
        assertEquals("Prata", command.getCor());
        assertEquals(clienteId, command.getClienteId());
    }

    @Test
    @DisplayName("Deve criar CriarVeiculoCommand sem cor")
    void deveCriarCriarVeiculoCommandSemCor() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        request.setPlaca("ABC1234");
        request.setMarca("Toyota");
        request.setModelo("Corolla");
        request.setAno(2020);
        request.setClienteId(clienteId);

        // Act
        CriarVeiculoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("ABC1234", command.getPlaca().getValor());
        assertEquals("Toyota", command.getMarca());
        assertEquals("Corolla", command.getModelo());
        assertEquals(2020, command.getAno());
        assertNull(command.getCor());
        assertEquals(clienteId, command.getClienteId());
    }

    @Test
    @DisplayName("Deve permitir definir e obter placa")
    void devePermitirDefinirEObterPlaca() {
        // Act
        request.setPlaca("XYZ9876");

        // Assert
        assertEquals("XYZ9876", request.getPlaca());
    }

    @Test
    @DisplayName("Deve permitir definir e obter marca")
    void devePermitirDefinirEObterMarca() {
        // Act
        request.setMarca("Honda");

        // Assert
        assertEquals("Honda", request.getMarca());
    }

    @Test
    @DisplayName("Deve permitir definir e obter modelo")
    void devePermitirDefinirEObterModelo() {
        // Act
        request.setModelo("Civic");

        // Assert
        assertEquals("Civic", request.getModelo());
    }

    @Test
    @DisplayName("Deve permitir definir e obter ano")
    void devePermitirDefinirEObterAno() {
        // Act
        request.setAno(2021);

        // Assert
        assertEquals(2021, request.getAno());
    }

    @Test
    @DisplayName("Deve permitir definir e obter cor")
    void devePermitirDefinirEObterCor() {
        // Act
        request.setCor("Preto");

        // Assert
        assertEquals("Preto", request.getCor());
    }

    @Test
    @DisplayName("Deve permitir definir e obter clienteId")
    void devePermitirDefinirEObterClienteId() {
        // Arrange
        UUID clienteId = UUID.randomUUID();

        // Act
        request.setClienteId(clienteId);

        // Assert
        assertEquals(clienteId, request.getClienteId());
    }

    @Test
    @DisplayName("Deve permitir atualizar placa múltiplas vezes")
    void devePermitirAtualizarPlacaMultiplasVezes() {
        // Act & Assert
        request.setPlaca("ABC1234");
        assertEquals("ABC1234", request.getPlaca());

        request.setPlaca("DEF5678");
        assertEquals("DEF5678", request.getPlaca());
    }

    @Test
    @DisplayName("Deve criar command com ano mínimo")
    void deveCriarCommandComAnoMinimo() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        request.setPlaca("ABC1234");
        request.setMarca("Ford");
        request.setModelo("Fiesta");
        request.setAno(1900);
        request.setClienteId(clienteId);

        // Act
        CriarVeiculoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(1900, command.getAno());
    }

    @Test
    @DisplayName("Deve criar command com ano máximo")
    void deveCriarCommandComAnoMaximo() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        request.setPlaca("ABC1234");
        request.setMarca("Chevrolet");
        request.setModelo("Onix");
        request.setAno(2100);
        request.setClienteId(clienteId);

        // Act
        CriarVeiculoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(2100, command.getAno());
    }

    @Test
    @DisplayName("Deve criar command com placa no formato antigo")
    void deveCriarCommandComPlacaNoFormatoAntigo() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        request.setPlaca("ABC1234");
        request.setMarca("Volkswagen");
        request.setModelo("Gol");
        request.setAno(2015);
        request.setClienteId(clienteId);

        // Act
        CriarVeiculoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("ABC1234", command.getPlaca().getValor());
    }

    @Test
    @DisplayName("Deve criar command com placa no formato novo")
    void deveCriarCommandComPlacaNoFormatoNovo() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        request.setPlaca("ABC1D23");
        request.setMarca("Hyundai");
        request.setModelo("HB20");
        request.setAno(2022);
        request.setClienteId(clienteId);

        // Act
        CriarVeiculoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("ABC1D23", command.getPlaca().getValor());
    }
}
