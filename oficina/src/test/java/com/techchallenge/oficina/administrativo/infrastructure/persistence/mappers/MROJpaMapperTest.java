package com.techchallenge.oficina.administrativo.infrastructure.persistence.mappers;

import com.techchallenge.oficina.administrativo.domain.model.entities.MRO;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.MROEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MROJpaMapperTest {

    private MROJpaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MROJpaMapper();
    }

    @Test
    @DisplayName("Conversão de MRO para Entity com valores nulos")
    void toEntity_nullMRO() {
        MRO mro = null;
        MROEntity entity = mapper.toEntity(mro);
        assertNull(entity);
    }

    @Test
    void toDomain_nullEntity() {
        MRO domain = mapper.toDomain(null);
        assertNull(domain);
    }

    @Test
    void toDomain_validEntity() {
        MROEntity entity = new MROEntity(
                UUID.randomUUID(),
                "Another Test",
                "Another Description",
                TipoMRO.PECA, // Use o enum diretamente
                20,
                BigDecimal.valueOf(20.0), // Mude de double para BigDecimal
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        MRO domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals("Another Test", domain.getNome());
        assertEquals("Another Description", domain.getDescricao());
        assertEquals(TipoMRO.PECA, domain.getTipo()); // Compara o enum diretamente
        assertEquals(20, domain.getQuantidadeEstoque());
        assertEquals(BigDecimal.valueOf(20.0), domain.getPrecoUnitario()); // Mude de double para BigDecimal
        assertFalse(domain.getAtivo());
    }

    @Test
    void toDomainList_listaNula() {
        List<MROEntity> entities = null; // Adicionar uma instância nula à lista
        List<MRO> domainList = mapper.toDomainList(entities);
        assertEquals(0, domainList.size()); // Verifica se a lista de domínio está vazia
    }

    @Test
    void toDomainList_validList() {
        List<MROEntity> entities = List.of(
                new MROEntity(UUID.randomUUID(), "Entity 1", "Desc 1", TipoMRO.PECA, 1, BigDecimal.valueOf(1.0), true, LocalDateTime.now(), LocalDateTime.now()),
                new MROEntity(UUID.randomUUID(), "Entity 2", "Desc 2", TipoMRO.INSUMO, 2, BigDecimal.valueOf(2.0), false, LocalDateTime.now(), LocalDateTime.now())
        );

        List<MRO> domainList = mapper.toDomainList(entities);

        assertEquals(2, domainList.size());
        assertEquals("Entity 1", domainList.get(0).getNome());
        assertEquals("Entity 2", domainList.get(1).getNome());
    }

    @Test
    void toDomainList_listaVazia() {
        List<MROEntity> entities = List.of();
        List<MRO> domainList = mapper.toDomainList(entities);
        assertEquals(0, domainList.size());
    }
}
