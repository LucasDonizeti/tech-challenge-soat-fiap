package com.techchallenge.oficina.administrativo.infrastructure.persistence.mappers;

import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ServicoEntity;
import org.junit.jupiter.api.Assertions;
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

        Servico servico = Servico.reconstruir(
                UUID.randomUUID(),
                "Limpeza do carro",
                "COD001",
                "Lavagem e polimento do veículo",
                BigDecimal.valueOf(99.99),
                true,
                criadoEm,
                atualizadoEm
        );

        // Act
        ServicoEntity entity = mapper.toEntity(servico);

        // Assert
        assertNotNull(entity);
        assertEquals(servico.getId(), entity.getId());
        assertEquals("Limpeza do carro", entity.getNome());
        assertEquals("Lavagem e polimento do veículo", entity.getDescricao());
        assertEquals(BigDecimal.valueOf(99.99), entity.getPreco());
        assertTrue(entity.getAtivo());
        assertEquals(criadoEm, entity.getCriadoEm());
        assertEquals(atualizadoEm, entity.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve converter Entity para Serviço com sucesso")
    void deveConverterEntityParaServico() {
        // Arrange
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime AtualizadoEm = LocalDateTime.now();

        ServicoEntity entity = ServicoEntity.builder()
                .id(UUID.randomUUID())
                .nome("Limpeza do carro")
                .descricao("Lavagem e polimento do veículo")
                .preco(BigDecimal.valueOf(99.99))
                .ativo(true)
                .criadoEm(criadoEm)
                .atualizadoEm(AtualizadoEm)
                .build();

        // Act
        Servico servico = mapper.toDomain(entity);

        // Assert
        assertNotNull(servico);
        assertEquals(entity.getId(), servico.getId());
        assertEquals("Limpeza do carro", servico.getNome());
        assertEquals("Lavagem e polimento do veículo", servico.getDescricao());
        assertEquals(BigDecimal.valueOf(99.99), servico.getPreco());
        assertTrue(servico.getAtivo());
        assertEquals(criadoEm, servico.getCriadoEm());
        assertEquals(AtualizadoEm, servico.getAtualizadoEm());
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
    @DisplayName("Deve converter lista de entities para serviços")
    void deveConverterListaDeEntitiesParaServicos() {
        // Arrange
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime AtualizadoEm = LocalDateTime.now();

        ServicoEntity entity1 = ServicoEntity.builder()
                .id(UUID.randomUUID())
                .nome("Limpeza do carro")
                .descricao("Lavagem e polimento do veículo")
                .preco(BigDecimal.valueOf(99.99))
                .ativo(true)
                .criadoEm(criadoEm)
                .atualizadoEm(AtualizadoEm)
                .build();

        ServicoEntity entity2 = ServicoEntity.builder()
                .id(UUID.randomUUID())
                .nome("Troca de oleo")
                .descricao("Mudar ooleo e fariação")
                .preco(BigDecimal.valueOf(150.00))
                .ativo(true)
                .criadoEm(criadoEm)
                .atualizadoEm(AtualizadoEm)
                .build();

        List<ServicoEntity> entities = List.of(entity1, entity2);

        // Act
        List<Servico> servicos = mapper.toDomainList(entities);

        // Assert
        assertNotNull(servicos);
        assertEquals(2, servicos.size());
        assertEquals("Limpeza do carro", servicos.get(0).getNome());
        assertEquals("Troca de oleo", servicos.get(1).getNome());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao passar lista nula")
    void deveLancarIllegalArgumentExceptionAoPassarListaENull() {
        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> mapper.toDomainList(null));
    }

}

