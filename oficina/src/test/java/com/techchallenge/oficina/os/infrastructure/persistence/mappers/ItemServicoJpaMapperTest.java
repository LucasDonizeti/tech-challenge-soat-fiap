package com.techchallenge.oficina.os.infrastructure.persistence.mappers;

import com.techchallenge.oficina.os.domain.model.entities.ItemMRO;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusItemServico;
import com.techchallenge.oficina.os.infrastructure.acl.dto.ServicoIntegrationDto;
import com.techchallenge.oficina.os.infrastructure.acl.servico.ServicoAdapter;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemMROEntity;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemServicoEntity;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemServicoEntity.StatusItemServicoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - ItemServicoJpaMapper")
class ItemServicoJpaMapperTest {

    @Mock
    private ItemMROJpaMapper itemMROJpaMapper;

    @Mock
    private ServicoAdapter servicoAdapter;

    private ItemServicoJpaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ItemServicoJpaMapper(itemMROJpaMapper, servicoAdapter);
    }

    @Test
    @DisplayName("Deve converter ItemServico para Entity com sucesso")
    void deveConverterItemServicoParaEntity() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID ordemServicoId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        LocalDateTime dataInicio = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime dataFim = LocalDateTime.of(2024, 1, 1, 11, 30);
        
        ItemServico itemServico = ItemServico.reconstruir(
            id,
            servicoId,
            "Troca de Óleo",
            "Troca completa",
            StatusItemServico.PENDENTE,
            null,
            new BigDecimal("150.00"),
            new BigDecimal("50.00"),
            dataInicio,
            dataFim
        );

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
        assertEquals(dataInicio, entity.getDataInicioExecucao());
        assertEquals(dataFim, entity.getDataFinalizacao());
    }

    @Test
    @DisplayName("Deve converter Entity para ItemServico com sucesso")
    void deveConverterEntityParaItemServico() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        LocalDateTime dataInicio = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime dataFim = LocalDateTime.of(2024, 1, 1, 11, 30);
        
        ItemServicoEntity entity = ItemServicoEntity.builder()
                .id(id)
                .ordemServicoId(UUID.randomUUID())
                .servicoId(servicoId)
                .status(StatusItemServicoEntity.CONCLUIDO)
                .observacoes("Observação")
                .valorServico(new BigDecimal("200.00"))
                .valorMro(new BigDecimal("75.00"))
                .dataInicioExecucao(dataInicio)
                .dataFinalizacao(dataFim)
                .mros(List.of())
                .build();

        ServicoIntegrationDto servicoDto = ServicoIntegrationDto.builder()
                .id(servicoId)
                .nome("Troca de Óleo")
                .descricao("Troca completa de óleo")
                .build();
        
        when(servicoAdapter.buscarPorId(servicoId)).thenReturn(Optional.of(servicoDto));

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
        assertEquals("Troca de Óleo", itemServico.getServicoNome());
        assertEquals("Troca completa de óleo", itemServico.getServicoDescricao());
        assertEquals(dataInicio, itemServico.getDataInicioExecucao());
        assertEquals(dataFim, itemServico.getDataFinalizacao());
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
            new BigDecimal("30.00"),
            null,
            null
        );
        
        ItemServico itemServico2 = ItemServico.reconstruir(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Serviço 2",
            "Descrição 2",
            StatusItemServico.EM_ANDAMENTO,
            null,
            new BigDecimal("150.00"),
            new BigDecimal("50.00"),
            null,
            null
        );
        
        List<ItemServico> itens = List.of(itemServico1, itemServico2);

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
            BigDecimal.ZERO,
            null,
            null
        );

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
            BigDecimal.ZERO,
            null,
            null
        );

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
            BigDecimal.ZERO,
            null,
            null
        );

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
            BigDecimal.ZERO,
            null,
            null
        );

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
            new BigDecimal("50.00"),
            null,
            null
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
