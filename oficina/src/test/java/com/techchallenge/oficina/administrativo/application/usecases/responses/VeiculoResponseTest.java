package com.techchallenge.oficina.administrativo.application.usecases.responses;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - VeiculoResponse")
class VeiculoResponseTest {

    @Test
    @DisplayName("Deve criar response a partir de Veiculo com sucesso")
    void deveCriarResponseApartirDeVeiculo() {
        // Arrange
        Veiculo veiculo = new Veiculo(
                Placa.of("ABC1234"),
                "Toyota",
                "Corolla",
                2020,
                "Prata"
        );
        
        Cliente cliente = Cliente.criar(
                Nome.of("João Silva"),
                CPF.of("52998224725"),
                Email.of("joao@exemplo.com")
        );
        veiculo.setCliente(cliente);

        // Act
        VeiculoResponse response = VeiculoResponse.from(veiculo);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("ABC-1234", response.getPlaca());
        assertEquals("Toyota", response.getMarca());
        assertEquals("Corolla", response.getModelo());
        assertEquals(2020, response.getAno());
        assertEquals("Prata", response.getCor());
        assertEquals(StatusVeiculo.ATIVO, response.getStatus());
        assertEquals(cliente.getId(), response.getClienteId());
        assertEquals("João Silva", response.getClienteNome());
    }

    @Test
    @DisplayName("Deve retornar null quando Veiculo é null")
    void deveRetornarNullQuandoVeiculoENull() {
        // Act
        VeiculoResponse response = VeiculoResponse.from(null);

        // Assert
        assertNull(response);
    }

    @Test
    @DisplayName("Deve retornar descricao completa")
    void deveRetornarDescricaoCompleta() {
        // Arrange
        Cliente cliente = Cliente.criar(
                Nome.of("Maria Santos"),
                CPF.of("52998224725"),
                Email.of("maria@exemplo.com")
        );
        
        Veiculo veiculo = new Veiculo(
                Placa.of("XYZ9876"),
                "Honda",
                "Civic",
                2021,
                "Preto"
        );
        veiculo.setCliente(cliente);

        // Act
        VeiculoResponse response = VeiculoResponse.from(veiculo);

        // Assert
        assertEquals("Honda Civic 2021 (XYZ-9876)", response.getDescricaoCompleta());
    }

    @Test
    @DisplayName("Deve criar response com veiculo sem cliente")
    void deveCriarResponseComVeiculoSemCliente() {
        // Arrange
        Veiculo veiculo = new Veiculo(
                Placa.of("DEF5678"),
                "Fiat",
                "Uno",
                2015,
                "Branco"
        );

        // Act
        VeiculoResponse response = VeiculoResponse.from(veiculo);

        // Assert
        assertNotNull(response);
        assertNull(response.getClienteId());
        assertNull(response.getClienteNome());
    }

    @Test
    @DisplayName("Deve criar response com veiculo inativo")
    void deveCriarResponseComVeiculoInativo() {
        // Arrange
        Cliente cliente = Cliente.criar(
                Nome.of("Pedro Oliveira"),
                CPF.of("52998224725"),
                Email.of("pedro@exemplo.com")
        );
        
        Veiculo veiculo = new Veiculo(
                Placa.of("GHI3456"),
                "Chevrolet",
                "Onix",
                2019,
                "Vermelho"
        );
        veiculo.setCliente(cliente);
        veiculo.inativar();

        // Act
        VeiculoResponse response = VeiculoResponse.from(veiculo);

        // Assert
        assertNotNull(response);
        assertEquals(StatusVeiculo.INATIVO, response.getStatus());
    }
}
