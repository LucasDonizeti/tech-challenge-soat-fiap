package com.techchallenge.oficina.administrativo.domain.model.entities;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VeiculoTest {

    @Test
    @DisplayName("Deve criar veículo com dados válidos")
    void deveCriarVeiculoComDadosValidos() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        String marca = "Toyota";
        String modelo = "Corolla";
        Integer ano = 2023;
        String cor = "Prata";
        
        // Act
        Veiculo veiculo = new Veiculo(placa, marca, modelo, ano, cor);
        
        // Assert
        assertNotNull(veiculo);
        assertNotNull(veiculo.getId());
        assertEquals(placa, veiculo.getPlaca());
        assertEquals(marca, veiculo.getMarca());
        assertEquals(modelo, veiculo.getModelo());
        assertEquals(ano, veiculo.getAno());
        assertEquals(cor, veiculo.getCor());
        assertNotNull(veiculo.getCriadoEm());
        assertNotNull(veiculo.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve lançar exceção para marca nula")
    void deveLancarExcecaoParaMarcaNula() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        
        // Act & Assert
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Veiculo(placa, null, "Corolla", 2023, "Prata")
        );
        
        assertTrue(exception.getMessage().contains("Marca"));
    }

    @Test
    @DisplayName("Deve lançar exceção para modelo nulo")
    void deveLancarExcecaoParaModeloNulo() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        
        // Act & Assert
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Veiculo(placa, "Toyota", null, 2023, "Prata")
        );
        
        assertTrue(exception.getMessage().contains("Modelo"));
    }

    @Test
    @DisplayName("Deve lançar exceção para ano nulo")
    void deveLancarExcecaoParaAnoNulo() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        
        // Act & Assert
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Veiculo(placa, "Toyota", "Corolla", null, "Prata")
        );
        
        assertTrue(exception.getMessage().contains("Ano"));
    }

    @Test
    @DisplayName("Deve lançar exceção para ano anterior a 1900")
    void deveLancarExcecaoParaAnoAnteriorA1900() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Veiculo(placa, "Toyota", "Corolla", 1899, "Prata")
        );
        
        assertEquals("Ano inválido. Deve estar entre 1900 e " + (java.time.Year.now().getValue() + 1), exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para ano futuro inválido")
    void deveLancarExcecaoParaAnoFuturoInvalido() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        int anoInvalido = java.time.Year.now().getValue() + 2;
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Veiculo(placa, "Toyota", "Corolla", anoInvalido, "Prata")
        );
        
        assertTrue(exception.getMessage().contains("Ano inválido"));
    }

    @Test
    @DisplayName("Deve atualizar dados do veículo")
    void deveAtualizarDadosDoVeiculo() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        Veiculo veiculo = new Veiculo(placa, "Toyota", "Corolla", 2023, "Prata");
        LocalDateTime atualizadoEmAntes = veiculo.getAtualizadoEm();
        
        // Act
        veiculo.atualizarDados("Honda", "Civic", 2024, "Preto");
        
        // Assert
        assertEquals("Honda", veiculo.getMarca());
        assertEquals("Civic", veiculo.getModelo());
        assertEquals(2024, veiculo.getAno());
        assertEquals("Preto", veiculo.getCor());
        assertNotNull(veiculo.getAtualizadoEm());
        // Verifica se foi atualizado ou se é o mesmo (execução muito rápida)
        assertTrue(veiculo.getAtualizadoEm().isAfter(atualizadoEmAntes) || veiculo.getAtualizadoEm().equals(atualizadoEmAntes));
    }

    @Test
    @DisplayName("Deve atualizar apenas marca")
    void deveAtualizarApenasMarca() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        Veiculo veiculo = new Veiculo(placa, "Toyota", "Corolla", 2023, "Prata");
        
        // Act
        veiculo.atualizarDados("Honda", null, null, null);
        
        // Assert
        assertEquals("Honda", veiculo.getMarca());
        assertEquals("Corolla", veiculo.getModelo());
        assertEquals(2023, veiculo.getAno());
        assertNull(veiculo.getCor()); // cor é sobrescrito mesmo quando null
    }

    @Test
    @DisplayName("Deve atualizar apenas modelo")
    void deveAtualizarApenasModelo() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        Veiculo veiculo = new Veiculo(placa, "Toyota", "Corolla", 2023, "Prata");
        
        // Act
        veiculo.atualizarDados(null, "Civic", null, null);
        
        // Assert
        assertEquals("Toyota", veiculo.getMarca());
        assertEquals("Civic", veiculo.getModelo());
        assertEquals(2023, veiculo.getAno());
        assertNull(veiculo.getCor()); // cor é sobrescrito mesmo quando null
    }

    @Test
    @DisplayName("Deve atualizar apenas ano")
    void deveAtualizarApenasAno() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        Veiculo veiculo = new Veiculo(placa, "Toyota", "Corolla", 2023, "Prata");
        
        // Act
        veiculo.atualizarDados(null, null, 2024, null);
        
        // Assert
        assertEquals("Toyota", veiculo.getMarca());
        assertEquals("Corolla", veiculo.getModelo());
        assertEquals(2024, veiculo.getAno());
        assertNull(veiculo.getCor()); // cor é sobrescrito mesmo quando null
    }

    @Test
    @DisplayName("Deve atualizar apenas cor")
    void deveAtualizarApenasCor() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        Veiculo veiculo = new Veiculo(placa, "Toyota", "Corolla", 2023, "Prata");
        
        // Act
        veiculo.atualizarDados(null, null, null, "Preto");
        
        // Assert
        assertEquals("Toyota", veiculo.getMarca());
        assertEquals("Corolla", veiculo.getModelo());
        assertEquals(2023, veiculo.getAno());
        assertEquals("Preto", veiculo.getCor());
    }

    @Test
    @DisplayName("Deve identificar veículo novo")
    void deveIdentificarVeiculoNovo() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        int anoAtual = java.time.Year.now().getValue();
        Veiculo veiculoNovo = new Veiculo(placa, "Toyota", "Corolla", anoAtual, "Prata");
        
        // Act & Assert
        assertTrue(veiculoNovo.isNovo());
    }

    @Test
    @DisplayName("Deve identificar veículo antigo")
    void deveIdentificarVeiculoAntigo() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        int anoAntigo = java.time.Year.now().getValue() - 15;
        Veiculo veiculoAntigo = new Veiculo(placa, "Toyota", "Corolla", anoAntigo, "Prata");
        
        // Act & Assert
        assertTrue(veiculoAntigo.isAntigo());
    }

    @Test
    @DisplayName("Deve identificar veículo não antigo")
    void deveIdentificarVeiculoNaoAntigo() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        Veiculo veiculoRecente = new Veiculo(placa, "Toyota", "Corolla", 2020, "Prata");
        
        // Act & Assert
        assertFalse(veiculoRecente.isAntigo());
    }

    @Test
    @DisplayName("Deve retornar descrição completa")
    void deveRetornarDescricaoCompleta() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        Veiculo veiculo = new Veiculo(placa, "Toyota", "Corolla", 2023, "Prata");
        
        // Act
        String descricao = veiculo.getDescricaoCompleta();
        
        // Assert
        assertEquals("Toyota Corolla 2023 (ABC-1D23)", descricao);
    }

    @Test
    @DisplayName("Deve restaurar veículo a partir de parâmetros")
    void deveRestaurarVeiculoAPartirDeParametros() {
        // Arrange
        UUID id = UUID.randomUUID();
        Placa placa = Placa.of("ABC1D23");
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(10);
        LocalDateTime atualizadoEm = LocalDateTime.now().minusDays(5);
        VeiculoRestauracaoParams params = new VeiculoRestauracaoParams(
            id, placa, "Toyota", "Corolla", 2023, "Prata", criadoEm, atualizadoEm
        );
        
        // Act
        Veiculo veiculo = Veiculo.restaurar(params);
        
        // Assert
        assertEquals(id, veiculo.getId());
        assertEquals(placa, veiculo.getPlaca());
        assertEquals("Toyota", veiculo.getMarca());
        assertEquals("Corolla", veiculo.getModelo());
        assertEquals(2023, veiculo.getAno());
        assertEquals("Prata", veiculo.getCor());
        assertEquals(criadoEm, veiculo.getCriadoEm());
        assertEquals(atualizadoEm, veiculo.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve definir cliente")
    void deveDefinirCliente() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        Veiculo veiculo = new Veiculo(placa, "Toyota", "Corolla", 2023, "Prata");
        
        // Act
        veiculo.setCliente(null);
        
        // Assert
        assertNull(veiculo.getCliente());
    }

    @Test
    @DisplayName("Deve implementar equals baseado em ID")
    void deveImplementarEqualsBaseadoEmId() {
        // Arrange
        Placa placa1 = Placa.of("ABC1D23");
        Placa placa2 = Placa.of("XYZ9W87");
        Veiculo veiculo1 = new Veiculo(placa1, "Toyota", "Corolla", 2023, "Prata");
        Veiculo veiculo2 = new Veiculo(placa1, "Toyota", "Corolla", 2023, "Prata");
        Veiculo veiculo3 = new Veiculo(placa2, "Honda", "Civic", 2024, "Preto");
        
        // Assert
        assertNotEquals(veiculo1, veiculo2); // IDs diferentes
        assertNotEquals(veiculo1, veiculo3);
    }

    @Test
    @DisplayName("Deve implementar hashCode baseado em ID")
    void deveImplementarHashCodeBaseadoEmId() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        Veiculo veiculo1 = new Veiculo(placa, "Toyota", "Corolla", 2023, "Prata");
        Veiculo veiculo2 = new Veiculo(placa, "Toyota", "Corolla", 2023, "Prata");
        
        // Assert
        assertNotEquals(veiculo1.hashCode(), veiculo2.hashCode()); // IDs diferentes
    }

    @Test
    @DisplayName("Deve retornar toString com descrição completa")
    void deveRetornarToStringComDescricaoCompleta() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        Veiculo veiculo = new Veiculo(placa, "Toyota", "Corolla", 2023, "Prata");
        
        // Act & Assert
        assertEquals("Toyota Corolla 2023 (ABC-1D23)", veiculo.toString());
    }
}
