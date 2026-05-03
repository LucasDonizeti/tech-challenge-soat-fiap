package com.techchallenge.oficina.os.infrastructure.persistence.mappers;

import com.techchallenge.oficina.os.domain.model.entities.ItemMRO;
import com.techchallenge.oficina.os.infrastructure.acl.dto.MROIntegrationDto;
import com.techchallenge.oficina.os.infrastructure.acl.mro.MROAdapter;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemMROEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - ItemMROJpaMapper")
class ItemMROJpaMapperTest {

    @Mock
    private MROAdapter mroAdapter;

    private ItemMROJpaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ItemMROJpaMapper(mroAdapter);
    }

    @Test
    @DisplayName("Deve converter ItemMRO para Entity com sucesso")
    void deveConverterItemMROParaEntity() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID itemServicoId = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();
        
        ItemMRO itemMRO = ItemMRO.reconstruir(
            id,
            mroId,
            "Óleo Motor",
            "Óleo sintético 5W-30",
            5,
            new BigDecimal("50.00")
        );

        // Act
        ItemMROEntity entity = mapper.toEntity(itemMRO, itemServicoId);

        // Assert
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals(itemServicoId, entity.getItemServicoId());
        assertEquals(mroId, entity.getMroId());
        assertEquals(5, entity.getQuantidade());
        assertEquals(new BigDecimal("50.00"), entity.getValorUnitario());
    }

    @Test
    @DisplayName("Deve converter Entity para ItemMRO com sucesso")
    void deveConverterEntityParaItemMRO() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();
        
        ItemMROEntity entity = ItemMROEntity.builder()
                .id(id)
                .itemServicoId(UUID.randomUUID())
                .mroId(mroId)
                .quantidade(3)
                .valorUnitario(new BigDecimal("30.00"))
                .build();

        MROIntegrationDto mroDto = MROIntegrationDto.builder()
                .id(mroId)
                .nome("Óleo Motor")
                .descricao("Óleo sintético 5W-30")
                .build();
        
        when(mroAdapter.buscarPorId(mroId)).thenReturn(Optional.of(mroDto));

        // Act
        ItemMRO itemMRO = mapper.toDomain(entity);

        // Assert
        assertNotNull(itemMRO);
        assertEquals(id, itemMRO.getId());
        assertEquals(mroId, itemMRO.getMroId());
        assertEquals(3, itemMRO.getQuantidade());
        assertEquals(new BigDecimal("30.00"), itemMRO.getValorUnitario());
        assertEquals("Óleo Motor", itemMRO.getMroNome());
        assertEquals("Óleo sintético 5W-30", itemMRO.getMroDescricao());
    }

    @Test
    @DisplayName("Deve retornar null quando ItemMRO é null")
    void deveRetornarNullQuandoItemMROENull() {
        // Act
        ItemMROEntity entity = mapper.toEntity(null, UUID.randomUUID());

        // Assert
        assertNull(entity);
    }

    @Test
    @DisplayName("Deve retornar null quando Entity é null")
    void deveRetornarNullQuandoEntityENull() {
        // Act
        ItemMRO itemMRO = mapper.toDomain(null);

        // Assert
        assertNull(itemMRO);
    }

    @Test
    @DisplayName("Deve converter lista de ItemMROs para entities")
    void deveConverterListaDeItemMROsParaEntities() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        
        ItemMRO itemMRO1 = ItemMRO.reconstruir(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Nome 1",
            "Descrição 1",
            5,
            new BigDecimal("50.00")
        );
        
        ItemMRO itemMRO2 = ItemMRO.reconstruir(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Nome 2",
            "Descrição 2",
            3,
            new BigDecimal("30.00")
        );
        
        List<ItemMRO> mros = List.of(itemMRO1, itemMRO2);

        // Act
        List<ItemMROEntity> entities = mapper.toEntityList(mros, itemServicoId);

        // Assert
        assertNotNull(entities);
        assertEquals(2, entities.size());
    }

    @Test
    @DisplayName("Deve converter lista de entities para ItemMROs")
    void deveConverterListaDeEntitiesParaItemMROs() {
        // Arrange
        ItemMROEntity entity1 = ItemMROEntity.builder()
                .id(UUID.randomUUID())
                .mroId(UUID.randomUUID())
                .quantidade(5)
                .valorUnitario(new BigDecimal("50.00"))
                .build();
        
        ItemMROEntity entity2 = ItemMROEntity.builder()
                .id(UUID.randomUUID())
                .mroId(UUID.randomUUID())
                .quantidade(3)
                .valorUnitario(new BigDecimal("30.00"))
                .build();
        
        List<ItemMROEntity> entities = List.of(entity1, entity2);

        // Act
        List<ItemMRO> mros = mapper.toDomainList(entities);

        // Assert
        assertNotNull(mros);
        assertEquals(2, mros.size());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando lista de ItemMROs é null")
    void deveRetornarListaVaziaQuandoListaDeItemMROsENull() {
        // Act
        List<ItemMROEntity> entities = mapper.toEntityList(null, UUID.randomUUID());

        // Assert
        assertNotNull(entities);
        assertTrue(entities.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando lista de entities é null")
    void deveRetornarListaVaziaQuandoListaDeEntitiesENull() {
        // Act
        List<ItemMRO> mros = mapper.toDomainList(null);

        // Assert
        assertNotNull(mros);
        assertTrue(mros.isEmpty());
    }

    @Test
    @DisplayName("Deve converter ItemMRO com quantidade 1")
    void deveConverterItemMROComQuantidade1() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();
        
        ItemMRO itemMRO = ItemMRO.reconstruir(
            id,
            mroId,
            "Nome",
            "Descrição",
            1,
            new BigDecimal("50.00")
        );

        // Act
        ItemMROEntity entity = mapper.toEntity(itemMRO, UUID.randomUUID());

        // Assert
        assertNotNull(entity);
        assertEquals(1, entity.getQuantidade());
    }

    @Test
    @DisplayName("Deve converter ItemMRO com quantidade alta")
    void deveConverterItemMROComQuantidadeAlta() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();
        
        ItemMRO itemMRO = ItemMRO.reconstruir(
            id,
            mroId,
            "Nome",
            "Descrição",
            100,
            new BigDecimal("50.00")
        );

        // Act
        ItemMROEntity entity = mapper.toEntity(itemMRO, UUID.randomUUID());

        // Assert
        assertNotNull(entity);
        assertEquals(100, entity.getQuantidade());
    }

    @Test
    @DisplayName("Deve converter ItemMRO com preço decimal")
    void deveConverterItemMROComPrecoDecimal() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();
        
        ItemMRO itemMRO = ItemMRO.reconstruir(
            id,
            mroId,
            "Nome",
            "Descrição",
            5,
            new BigDecimal("125.50")
        );

        // Act
        ItemMROEntity entity = mapper.toEntity(itemMRO, UUID.randomUUID());

        // Assert
        assertNotNull(entity);
        assertEquals(new BigDecimal("125.50"), entity.getValorUnitario());
    }
}
