package com.techchallenge.oficina.os.infrastructure.persistence.mappers;

import com.techchallenge.oficina.os.domain.model.entities.MRO;
import com.techchallenge.oficina.os.domain.model.valueobjects.TipoMRO;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.MROEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - MROJpaMapper")
class MROJpaMapperTest {

    private MROJpaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MROJpaMapper();
    }

    @Test
    @DisplayName("Deve converter MRO para Entity com sucesso")
    void deveConverterMroParaEntity() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime atualizadoEm = LocalDateTime.now();
        
        MRO mro = MRO.reconstruir(
                id,
                "Óleo Motor 5W30",
                "Óleo para motor automotivo",
                TipoMRO.INSUMO,
                100,
                new BigDecimal("45.90"),
                true,
                criadoEm,
                atualizadoEm
        );

        // Act
        MROEntity entity = mapper.toEntity(mro);

        // Assert
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("Óleo Motor 5W30", entity.getNome());
        assertEquals("Óleo para motor automotivo", entity.getDescricao());
        assertEquals(TipoMRO.INSUMO, entity.getTipo());
        assertEquals(100, entity.getQuantidadeEstoque());
        assertEquals(new BigDecimal("45.90"), entity.getPrecoUnitario());
        assertTrue(entity.getAtivo());
        assertEquals(criadoEm, entity.getCriadoEm());
        assertEquals(atualizadoEm, entity.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve converter Entity para MRO com sucesso")
    void deveConverterEntityParaMro() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime atualizadoEm = LocalDateTime.now();
        
        MROEntity entity = MROEntity.builder()
                .id(id)
                .nome("Filtro de Óleo")
                .descricao("Filtro para motor")
                .tipo(TipoMRO.PECA)
                .quantidadeEstoque(50)
                .precoUnitario(new BigDecimal("25.00"))
                .ativo(true)
                .criadoEm(criadoEm)
                .atualizadoEm(atualizadoEm)
                .build();

        // Act
        MRO mro = mapper.toDomain(entity);

        // Assert
        assertNotNull(mro);
        assertEquals(id, mro.getId());
        assertEquals("Filtro de Óleo", mro.getNome());
        assertEquals("Filtro para motor", mro.getDescricao());
        assertEquals(TipoMRO.PECA, mro.getTipo());
        assertEquals(50, mro.getQuantidadeEstoque());
        assertEquals(new BigDecimal("25.00"), mro.getPrecoUnitario());
        assertTrue(mro.getAtivo());
        assertEquals(criadoEm, mro.getCriadoEm());
        assertEquals(atualizadoEm, mro.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve retornar null quando MRO é null")
    void deveRetornarNullQuandoMroENull() {
        // Act
        MROEntity entity = mapper.toEntity(null);

        // Assert
        assertNull(entity);
    }

    @Test
    @DisplayName("Deve retornar null quando Entity é null")
    void deveRetornarNullQuandoEntityENull() {
        // Act
        MRO mro = mapper.toDomain(null);

        // Assert
        assertNull(mro);
    }

    @Test
    @DisplayName("Deve converter MRO com tipo PECA")
    void deveConverterMroComTipoPeca() {
        // Arrange
        UUID id = UUID.randomUUID();
        MRO mro = MRO.reconstruir(
                id,
                "Parafuso M8",
                "Parafuso para montagem",
                TipoMRO.PECA,
                500,
                new BigDecimal("0.50"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Act
        MROEntity entity = mapper.toEntity(mro);

        // Assert
        assertNotNull(entity);
        assertEquals(TipoMRO.PECA, entity.getTipo());
    }

    @Test
    @DisplayName("Deve converter MRO inativo")
    void deveConverterMroInativo() {
        // Arrange
        UUID id = UUID.randomUUID();
        MRO mro = MRO.reconstruir(
                id,
                "Produto Descontinuado",
                "Descrição",
                TipoMRO.INSUMO,
                0,
                new BigDecimal("10.00"),
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Act
        MROEntity entity = mapper.toEntity(mro);

        // Assert
        assertNotNull(entity);
        assertFalse(entity.getAtivo());
    }

    @Test
    @DisplayName("Deve converter MRO com descrição null")
    void deveConverterMroComDescricaoNull() {
        // Arrange
        UUID id = UUID.randomUUID();
        MRO mro = MRO.reconstruir(
                id,
                "Produto Sem Descrição",
                null,
                TipoMRO.PECA,
                100,
                new BigDecimal("15.00"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Act
        MROEntity entity = mapper.toEntity(mro);

        // Assert
        assertNotNull(entity);
        assertNull(entity.getDescricao());
    }

    @Test
    @DisplayName("Deve converter lista de entities para MROs")
    void deveConverterListaDeEntitiesParaMros() {
        // Arrange
        MROEntity entity1 = MROEntity.builder()
                .id(UUID.randomUUID())
                .nome("Óleo Motor")
                .tipo(TipoMRO.INSUMO)
                .quantidadeEstoque(100)
                .precoUnitario(new BigDecimal("45.90"))
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();
        
        MROEntity entity2 = MROEntity.builder()
                .id(UUID.randomUUID())
                .nome("Filtro de Óleo")
                .tipo(TipoMRO.PECA)
                .quantidadeEstoque(50)
                .precoUnitario(new BigDecimal("25.00"))
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();
        
        List<MROEntity> entities = List.of(entity1, entity2);

        // Act
        List<MRO> mros = mapper.toDomainList(entities);

        // Assert
        assertNotNull(mros);
        assertEquals(2, mros.size());
        assertEquals("Óleo Motor", mros.get(0).getNome());
        assertEquals("Filtro de Óleo", mros.get(1).getNome());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando lista de entities é vazia")
    void deveRetornarListaVaziaQuandoListaDeEntitiesEVazia() {
        // Arrange
        List<MROEntity> entities = List.of();

        // Act
        List<MRO> mros = mapper.toDomainList(entities);

        // Assert
        assertNotNull(mros);
        assertTrue(mros.isEmpty());
    }
}
