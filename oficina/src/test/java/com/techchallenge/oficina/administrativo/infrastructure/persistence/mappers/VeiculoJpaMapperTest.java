package com.techchallenge.oficina.administrativo.infrastructure.persistence.mappers;

import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.entities.VeiculoRestauracaoParams;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.VeiculoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes Unitários - VeiculoJpaMapper")
class VeiculoJpaMapperTest {

    private VeiculoJpaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new VeiculoJpaMapper();
    }

    @Test
    @DisplayName("Deve converter Veículo para Entity com sucesso")
    void deveConverterVeiculoParaEntity() {
        // Arrange
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime atualizadoEm = LocalDateTime.now();
        
        VeiculoRestauracaoParams params = new VeiculoRestauracaoParams(
                UUID.randomUUID(),
                Placa.of("ABC1234"),
                "Toyota",
                "Corolla",
                2022,
                "Prata",
                criadoEm,
                atualizadoEm
        );
        Veiculo veiculo = Veiculo.restaurar(params);
        
        // Act
        VeiculoEntity entity = mapper.toEntity(veiculo);

        // Assert
        assertNotNull(entity);
        assertEquals(veiculo.getId(), entity.getId());
        assertEquals("ABC1234", entity.getPlaca());
        assertEquals("Toyota", entity.getMarca());
        assertEquals("Corolla", entity.getModelo());
        assertEquals(2022, entity.getAno());
        assertEquals("Prata", entity.getCor());
        assertNotNull(entity.getCriadoEm());
        assertNotNull(entity.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve converter Entity para Veículo com sucesso")
    void deveConverterEntityParaVeiculo() {
        // Arrange
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime atualizadoEm = LocalDateTime.now();
        
        VeiculoEntity entity = VeiculoEntity.builder()
                .id(java.util.UUID.randomUUID())
                .placa("XYZ5678")
                .marca("Honda")
                .modelo("Civic")
                .ano(2023)
                .cor("Preto")
                .criadoEm(criadoEm)
                .atualizadoEm(atualizadoEm)
                .build();

        // Act
        Veiculo veiculo = mapper.toDomain(entity);

        // Assert
        assertNotNull(veiculo);
        assertEquals(entity.getId(), veiculo.getId());
        assertEquals("XYZ5678", veiculo.getPlaca().getValor());
        assertEquals("Honda", veiculo.getMarca());
        assertEquals("Civic", veiculo.getModelo());
        assertEquals(2023, veiculo.getAno());
        assertEquals("Preto", veiculo.getCor());
        assertEquals(criadoEm, veiculo.getCriadoEm());
        assertEquals(atualizadoEm, veiculo.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve retornar null quando veículo é null")
    void deveRetornarNullQuandoVeiculoENull() {
        // Act
        VeiculoEntity entity = mapper.toEntity(null);

        // Assert
        assertNull(entity);
    }

    @Test
    @DisplayName("Deve retornar null quando entity é null")
    void deveRetornarNullQuandoEntityENull() {
        // Act
        Veiculo veiculo = mapper.toDomain(null);

        // Assert
        assertNull(veiculo);
    }

    @Test
    @DisplayName("Deve converter lista de veículos para entities")
    void deveConverterListaDeVeiculosParaEntities() {
        // Arrange
        VeiculoRestauracaoParams params1 = new VeiculoRestauracaoParams(UUID.randomUUID(), Placa.of("ABC1234"), "Toyota", "Corolla", 2022, "Prata", LocalDateTime.now().minusDays(1), LocalDateTime.now());
        VeiculoRestauracaoParams params2 = new VeiculoRestauracaoParams(UUID.randomUUID(), Placa.of("XYZ5678"), "Honda", "Civic", 2023, "Preto", LocalDateTime.now().minusDays(1), LocalDateTime.now());
        Veiculo veiculo1 = Veiculo.restaurar(params1);
        Veiculo veiculo2 = Veiculo.restaurar(params2);
        List<Veiculo> veiculos = List.of(veiculo1, veiculo2);

        // Act
        List<VeiculoEntity> entities = mapper.toEntityList(veiculos);

        // Assert
        assertNotNull(entities);
        assertEquals(2, entities.size());
        assertEquals("ABC1234", entities.get(0).getPlaca());
        assertEquals("XYZ5678", entities.get(1).getPlaca());
    }

    @Test
    @DisplayName("Deve converter lista de entities para veículos")
    void deveConverterListaDeEntitiesParaVeiculos() {
        // Arrange
        VeiculoEntity entity1 = VeiculoEntity.builder()
                .id(java.util.UUID.randomUUID())
                .placa("ABC1234")
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2022)
                .cor("Prata")
                .build();
        
        VeiculoEntity entity2 = VeiculoEntity.builder()
                .id(java.util.UUID.randomUUID())
                .placa("XYZ5678")
                .marca("Honda")
                .modelo("Civic")
                .ano(2023)
                .cor("Preto")
                .build();
        
        List<VeiculoEntity> entities = List.of(entity1, entity2);

        // Act
        List<Veiculo> veiculos = mapper.toDomainList(entities);

        // Assert
        assertNotNull(veiculos);
        assertEquals(2, veiculos.size());
        assertEquals("ABC1234", veiculos.get(0).getPlaca().getValor());
        assertEquals("XYZ5678", veiculos.get(1).getPlaca().getValor());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando lista de veículos é null")
    void deveRetornarListaVaziaQuandoListaDeVeiculosENull() {
        // Act
        List<VeiculoEntity> entities = mapper.toEntityList(null);

        // Assert
        assertNotNull(entities);
        assertTrue(entities.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando lista de entities é null")
    void deveRetornarListaVaziaQuandoListaDeEntitiesENull() {
        // Act
        List<Veiculo> veiculos = mapper.toDomainList(null);

        // Assert
        assertNotNull(veiculos);
        assertTrue(veiculos.isEmpty());
    }
}
