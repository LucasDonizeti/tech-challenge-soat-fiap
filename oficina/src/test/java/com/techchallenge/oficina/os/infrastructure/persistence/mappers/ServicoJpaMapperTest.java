package com.techchallenge.oficina.os.infrastructure.persistence.mappers;

import com.techchallenge.oficina.os.domain.model.entities.Servico;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ServicoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - ServicoJpaMapper")
class ServicoJpaMapperTest {

    private ServicoJpaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ServicoJpaMapper();
    }

    @Test
    @DisplayName("Deve converter Serviço para Entity com sucesso")
    void deveConverterServicoParaEntity() {
        // Arrange
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime atualizadoEm = LocalDateTime.now();

        Servico servico = Servico.criar(
                "Troca de Óleo",
                "Troca de óleo sintético",
                new BigDecimal("150.00")
        );

        // Act
        ServicoEntity entity = mapper.toEntity(servico);

        // Assert
        assertNotNull(entity);
        assertEquals(servico.getId(), entity.getId());
        assertEquals("Troca de Óleo", entity.getNome());
        assertEquals("Troca de óleo sintético", entity.getDescricao());
        assertEquals(new BigDecimal("150.00"), entity.getPreco());
        assertTrue(entity.getAtivo());
        assertNotNull(entity.getCriadoEm());
        assertNotNull(entity.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve converter Entity para Serviço com sucesso")
    void deveConverterEntityParaServico() {
        // Arrange
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime atualizadoEm = LocalDateTime.now();

        ServicoEntity entity = ServicoEntity.builder()
                .id(UUID.randomUUID())
                .nome("Troca de Óleo")
                .descricao("Troca de óleo sintético")
                .preco(new BigDecimal("150.00"))
                .ativo(true)
                .criadoEm(criadoEm)
                .atualizadoEm(atualizadoEm)
                .build();

        // Act
        Servico servico = mapper.toDomain(entity);

        // Assert
        assertNotNull(servico);
        assertEquals(entity.getId(), servico.getId());
        assertEquals("Troca de Óleo", servico.getNome());
        assertEquals("Troca de óleo sintético", servico.getDescricao());
        assertEquals(new BigDecimal("150.00"), servico.getPreco());
        assertTrue(servico.getAtivo());
        assertEquals(criadoEm, servico.getCriadoEm());
        assertEquals(atualizadoEm, servico.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve retornar null quando serviço é null")
    void deveRetornarNullQuandoServicoENull() {
        // Act
        ServicoEntity entity = mapper.toEntity(null);

        // Assert
        assertNull(entity);
    }

    @Test
    @DisplayName("Deve retornar null quando entity é null")
    void deveRetornarNullQuandoEntityENull() {
        // Act
        Servico servico = mapper.toDomain(null);

        // Assert
        assertNull(servico);
    }

    @Test
    @DisplayName("Deve converter lista de serviços para entities")
    void deveConverterListaDeServicosParaEntities() {
        // Arrange
        Servico servico1 = Servico.criar("Troca de Óleo", "Troca de óleo sintético", new BigDecimal("150.00"));
        Servico servico2 = Servico.criar("Troca de Pneu", "Troca de pneu", new BigDecimal("200.00"));
        List<Servico> servicos = List.of(servico1, servico2);

        // Act
        List<ServicoEntity> entities = servicos.stream()
                .map(mapper::toEntity)
                .toList();

        // Assert
        assertNotNull(entities);
        assertEquals(2, entities.size());
        assertEquals("Troca de Óleo", entities.get(0).getNome());
        assertEquals("Troca de Pneu", entities.get(1).getNome());
    }

    @Test
    @DisplayName("Deve converter lista de entities para serviços")
    void deveConverterListaDeEntitiesParaServicos() {
        // Arrange
        ServicoEntity entity1 = ServicoEntity.builder()
                .id(UUID.randomUUID())
                .nome("Troca de Óleo")
                .descricao("Troca de óleo sintético")
                .preco(new BigDecimal("150.00"))
                .ativo(true)
                .build();

        ServicoEntity entity2 = ServicoEntity.builder()
                .id(UUID.randomUUID())
                .nome("Troca de Pneu")
                .descricao("Troca de pneu")
                .preco(new BigDecimal("200.00"))
                .ativo(true)
                .build();

        List<ServicoEntity> entities = List.of(entity1, entity2);

        // Act
        List<Servico> servicos = mapper.toDomainList(entities);

        // Assert
        assertNotNull(servicos);
        assertEquals(2, servicos.size());
        assertEquals("Troca de Óleo", servicos.get(0).getNome());
        assertEquals("Troca de Pneu", servicos.get(1).getNome());
    }

    @Test
    @DisplayName("Deve converter serviço inativo para entity")
    void deveConverterServicoInativoParaEntity() {
        // Arrange
        Servico servico = Servico.criar("Troca de Óleo", "Troca de óleo sintético", new BigDecimal("150.00"));
        servico.desativar();

        // Act
        ServicoEntity entity = mapper.toEntity(servico);

        // Assert
        assertNotNull(entity);
        assertFalse(entity.getAtivo());
    }

    @Test
    @DisplayName("Deve converter entity inativo para serviço")
    void deveConverterEntityInativoParaServico() {
        // Arrange
        ServicoEntity entity = ServicoEntity.builder()
                .id(UUID.randomUUID())
                .nome("Troca de Óleo")
                .descricao("Troca de óleo sintético")
                .preco(new BigDecimal("150.00"))
                .ativo(false)
                .build();

        // Act
        Servico servico = mapper.toDomain(entity);

        // Assert
        assertNotNull(servico);
        assertFalse(servico.getAtivo());
    }

    @Test
    @DisplayName("Deve converter serviço com preço atualizado para entity")
    void deveConverterServicoComPrecoAtualizadoParaEntity() {
        // Arrange
        Servico servico = Servico.criar("Troca de Óleo", "Troca de óleo sintético", new BigDecimal("150.00"));
        servico.atualizarPreco(new BigDecimal("180.00"));

        // Act
        ServicoEntity entity = mapper.toEntity(servico);

        // Assert
        assertNotNull(entity);
        assertEquals(new BigDecimal("180.00"), entity.getPreco());
    }

    @Test
    @DisplayName("Deve converter serviço com dados atualizados para entity")
    void deveConverterServicoComDadosAtualizadosParaEntity() {
        // Arrange
        Servico servico = Servico.criar("Troca de Óleo", "Troca de óleo sintético", new BigDecimal("150.00"));
        servico.atualizarDados("Troca de Óleo Premium", "Troca de óleo sintético premium");

        // Act
        ServicoEntity entity = mapper.toEntity(servico);

        // Assert
        assertNotNull(entity);
        assertEquals("Troca de Óleo Premium", entity.getNome());
        assertEquals("Troca de óleo sintético premium", entity.getDescricao());
    }

    @Test
    @DisplayName("Deve converter lista vazia de serviços para entities")
    void deveConverterListaVaziaDeServicosParaEntities() {
        // Arrange
        List<Servico> servicos = List.of();

        // Act
        List<ServicoEntity> entities = servicos.stream()
                .map(mapper::toEntity)
                .toList();

        // Assert
        assertNotNull(entities);
        assertTrue(entities.isEmpty());
    }

    @Test
    @DisplayName("Deve converter lista vazia de entities para serviços")
    void deveConverterListaVaziaDeEntitiesParaServicos() {
        // Arrange
        List<ServicoEntity> entities = List.of();

        // Act
        List<Servico> servicos = mapper.toDomainList(entities);

        // Assert
        assertNotNull(servicos);
        assertTrue(servicos.isEmpty());
    }
}
