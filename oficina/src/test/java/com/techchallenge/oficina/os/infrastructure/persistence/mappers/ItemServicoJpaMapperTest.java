package com.techchallenge.oficina.os.infrastructure.persistence.mappers;

import com.techchallenge.oficina.os.domain.model.entities.ItemMRO;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusItemServico;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemMROEntity;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemServicoEntity;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemServicoEntity.StatusItemServicoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Testes Unitários - ItemServicoJpaMapper")
class ItemServicoJpaMapperTest {

    @Mock
    private ItemMROJpaMapper itemMROJpaMapper;

    private ItemServicoJpaMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new ItemServicoJpaMapper(itemMROJpaMapper);
    }

    @Test
    @DisplayName("Deve converter ItemServico para Entity com sucesso")
    void deveConverterItemServicoParaEntity() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID ordemServicoId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        
        ItemServico itemServico = ItemServico.reconstruir(
            id,
            servicoId,
            "Troca de Óleo",
            "Troca completa",
            StatusItemServico.PENDENTE,
            null,
            new BigDecimal("150.00"),
            new BigDecimal("50.00")
        );

        when(itemMROJpaMapper.toEntityList(any(), any())).thenReturn(List.of());

        // Act
        ItemServicoEntity entity = mapper.toEntity(itemServico, ordemServicoId);

        // Assert
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals(ordemServicoId, entity.getOrdemServicoId());
        assertEquals(servicoId, entity.getServicoId());
        assertEquals(StatusItemServicoEntity.PENDENTE, entity.getStatus());
        assertEquals(new BigDecimal("150.00"), entity.getValorServico());
        assertEquals(new BigDecimal("50.00"), entity.getValorMro());
    }

    @Test
    @DisplayName("Deve converter Entity para ItemServico com sucesso")
    void deveConverterEntityParaItemServico() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        
        ItemServicoEntity entity = ItemServicoEntity.builder()
                .id(id)
                .ordemServicoId(UUID.randomUUID())
                .servicoId(servicoId)
                .status(StatusItemServicoEntity.CONCLUIDO)
                .observacoes("Observação")
                .valorServico(new BigDecimal("200.00"))
                .valorMro(new BigDecimal("75.00"))
                .mros(List.of())
                .build();

        when(itemMROJpaMapper.toDomainList(any())).thenReturn(List.of());

        // Act
        ItemServico itemServico = mapper.toDomain(entity);

        // Assert
        assertNotNull(itemServico);
        assertEquals(id, itemServico.getId());
        assertEquals(servicoId, itemServico.getServicoId());
        assertEquals(StatusItemServico.CONCLUIDO, itemServico.getStatus());
        assertEquals("Observação", itemServico.getObservacoes());
        assertEquals(new BigDecimal("200.00"), itemServico.getValorServico());
        assertEquals(new BigDecimal("75.00"), itemServico.getValorMro());
        assertNull(itemServico.getServicoNome());
        assertNull(itemServico.getServicoDescricao());
    }

    @Test
    @DisplayName("Deve retornar null quando ItemServico é null")
    void deveRetornarNullQuandoItemServicoENull() {
        // Act
        ItemServicoEntity entity = mapper.toEntity(null, UUID.randomUUID());

        // Assert
        assertNull(entity);
    }

    @Test
    @DisplayName("Deve retornar null quando Entity é null")
    void deveRetornarNullQuandoEntityENull() {
        // Act
        ItemServico itemServico = mapper.toDomain(null);

        // Assert
        assertNull(itemServico);
    }

    @Test
    @DisplayName("Deve converter lista de ItemServicos para entities")
    void deveConverterListaDeItemServicosParaEntities() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        
        ItemServico itemServico1 = ItemServico.reconstruir(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Serviço 1",
            "Descrição 1",
            StatusItemServico.PENDENTE,
            null,
            new BigDecimal("100.00"),
            new BigDecimal("30.00")
        );
        
        ItemServico itemServico2 = ItemServico.reconstruir(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Serviço 2",
            "Descrição 2",
            StatusItemServico.EM_ANDAMENTO,
            null,
            new BigDecimal("150.00"),
            new BigDecimal("50.00")
        );
        
        List<ItemServico> itens = List.of(itemServico1, itemServico2);

        when(itemMROJpaMapper.toEntityList(any(), any())).thenReturn(List.of());

        // Act
        List<ItemServicoEntity> entities = mapper.toEntityList(itens, ordemServicoId);

        // Assert
        assertNotNull(entities);
        assertEquals(2, entities.size());
    }

    @Test
    @DisplayName("Deve converter lista de entities para ItemServicos")
    void deveConverterListaDeEntitiesParaItemServicos() {
        // Arrange
        ItemServicoEntity entity1 = ItemServicoEntity.builder()
                .id(UUID.randomUUID())
                .servicoId(UUID.randomUUID())
                .status(StatusItemServicoEntity.PENDENTE)
                .valorServico(new BigDecimal("100.00"))
                .mros(List.of())
                .build();
        
        ItemServicoEntity entity2 = ItemServicoEntity.builder()
                .id(UUID.randomUUID())
                .servicoId(UUID.randomUUID())
                .status(StatusItemServicoEntity.CONCLUIDO)
                .valorServico(new BigDecimal("150.00"))
                .mros(List.of())
                .build();
        
        List<ItemServicoEntity> entities = List.of(entity1, entity2);

        when(itemMROJpaMapper.toDomainList(any())).thenReturn(List.of());

        // Act
        List<ItemServico> itens = mapper.toDomainList(entities);

        // Assert
        assertNotNull(itens);
        assertEquals(2, itens.size());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando lista de ItemServicos é null")
    void deveRetornarListaVaziaQuandoListaDeItemServicosENull() {
        // Act
        List<ItemServicoEntity> entities = mapper.toEntityList(null, UUID.randomUUID());

        // Assert
        assertNotNull(entities);
        assertTrue(entities.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando lista de entities é null")
    void deveRetornarListaVaziaQuandoListaDeEntitiesENull() {
        // Act
        List<ItemServico> itens = mapper.toDomainList(null);

        // Assert
        assertNotNull(itens);
        assertTrue(itens.isEmpty());
    }

    @Test
    @DisplayName("Deve converter ItemServico com status PENDENTE")
    void deveConverterItemServicoComStatusPendente() {
        // Arrange
        ItemServico itemServico = ItemServico.reconstruir(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Serviço",
            "Descrição",
            StatusItemServico.PENDENTE,
            null,
            new BigDecimal("100.00"),
            BigDecimal.ZERO
        );

        when(itemMROJpaMapper.toEntityList(any(), any())).thenReturn(List.of());

        // Act
        ItemServicoEntity entity = mapper.toEntity(itemServico, UUID.randomUUID());

        // Assert
        assertNotNull(entity);
        assertEquals(StatusItemServicoEntity.PENDENTE, entity.getStatus());
    }

    @Test
    @DisplayName("Deve converter ItemServico com status EM_ANDAMENTO")
    void deveConverterItemServicoComStatusEmAndamento() {
        // Arrange
        ItemServico itemServico = ItemServico.reconstruir(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Serviço",
            "Descrição",
            StatusItemServico.EM_ANDAMENTO,
            null,
            new BigDecimal("100.00"),
            BigDecimal.ZERO
        );

        when(itemMROJpaMapper.toEntityList(any(), any())).thenReturn(List.of());

        // Act
        ItemServicoEntity entity = mapper.toEntity(itemServico, UUID.randomUUID());

        // Assert
        assertNotNull(entity);
        assertEquals(StatusItemServicoEntity.EM_ANDAMENTO, entity.getStatus());
    }

    @Test
    @DisplayName("Deve converter ItemServico com status CONCLUIDO")
    void deveConverterItemServicoComStatusConcluido() {
        // Arrange
        ItemServico itemServico = ItemServico.reconstruir(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Serviço",
            "Descrição",
            StatusItemServico.CONCLUIDO,
            null,
            new BigDecimal("100.00"),
            BigDecimal.ZERO
        );

        when(itemMROJpaMapper.toEntityList(any(), any())).thenReturn(List.of());

        // Act
        ItemServicoEntity entity = mapper.toEntity(itemServico, UUID.randomUUID());

        // Assert
        assertNotNull(entity);
        assertEquals(StatusItemServicoEntity.CONCLUIDO, entity.getStatus());
    }

    @Test
    @DisplayName("Deve converter ItemServico com status null")
    void deveConverterItemServicoComStatusNull() {
        // Arrange
        ItemServico itemServico = ItemServico.reconstruir(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Serviço",
            "Descrição",
            null,
            null,
            new BigDecimal("100.00"),
            BigDecimal.ZERO
        );

        when(itemMROJpaMapper.toEntityList(any(), any())).thenReturn(List.of());

        // Act
        ItemServicoEntity entity = mapper.toEntity(itemServico, UUID.randomUUID());

        // Assert
        assertNotNull(entity);
        assertNull(entity.getStatus());
    }

    @Test
    @DisplayName("Deve converter ItemServico com MROs")
    void deveConverterItemServicoComMROs() {
        // Arrange
        ItemMRO itemMRO = ItemMRO.reconstruir(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Nome",
            "Descrição",
            5,
            new BigDecimal("50.00")
        );
        
        ItemServico itemServico = ItemServico.reconstruir(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Serviço",
            "Descrição",
            StatusItemServico.PENDENTE,
            null,
            new BigDecimal("100.00"),
            new BigDecimal("50.00")
        );
        itemServico.adicionarMRO(itemMRO);

        when(itemMROJpaMapper.toEntityList(any(), any())).thenReturn(List.of(new ItemMROEntity()));

        // Act
        ItemServicoEntity entity = mapper.toEntity(itemServico, UUID.randomUUID());

        // Assert
        assertNotNull(entity);
        assertNotNull(entity.getMros());
        assertFalse(entity.getMros().isEmpty());
    }
}
