package com.techchallenge.oficina.administrativo.infrastructure.persistence.repositories;

import com.techchallenge.oficina.administrativo.domain.model.entities.MRO;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.MROEntity;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.mappers.MROJpaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - MRORepositoryImpl")
class MRORepositoryImplTest {

    @Mock
    private MROJpaRepository jpaRepository;

    @Mock
    private MROJpaMapper mapper;

    @InjectMocks
    private MRORepositoryImpl repository;

    private MRO mro;
    private MROEntity entity;
    private UUID mroId;

    @BeforeEach
    void setUp() {
        mroId = UUID.randomUUID();
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime atualizadoEm = LocalDateTime.now();

        mro = MRO.reconstruir(
                mroId,
                "Óleo Motor 5W30",
                "Óleo para motor automotivo",
                TipoMRO.INSUMO,
                100,
                new BigDecimal("45.90"),
                true,
                criadoEm,
                atualizadoEm
        );

        entity = MROEntity.builder()
                .id(mroId)
                .nome("Óleo Motor 5W30")
                .descricao("Óleo para motor automotivo")
                .tipo(TipoMRO.INSUMO)
                .quantidadeEstoque(100)
                .precoUnitario(new BigDecimal("45.90"))
                .ativo(true)
                .criadoEm(criadoEm)
                .atualizadoEm(atualizadoEm)
                .build();
    }

    @Test
    @DisplayName("Deve salvar MRO com sucesso")
    void deveSalvarMROComSucesso() {
        // Arrange
        when(mapper.toEntity(any(MRO.class))).thenReturn(entity);
        when(jpaRepository.save(any(MROEntity.class))).thenReturn(entity);
        when(mapper.toDomain(any(MROEntity.class))).thenReturn(mro);

        // Act
        MRO saved = repository.save(mro);

        // Assert
        assertNotNull(saved);
        assertEquals(mroId, saved.getId());

        verify(mapper, times(1)).toEntity(any(MRO.class));
        verify(jpaRepository, times(1)).save(any(MROEntity.class));
        verify(mapper, times(1)).toDomain(any(MROEntity.class));
    }

    @Test
    @DisplayName("Deve buscar MRO por ID com sucesso")
    void deveBuscarMroPorIdComSucesso() {
        // Arrange
        when(jpaRepository.findById(mroId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(any(MROEntity.class))).thenReturn(mro);

        // Act
        Optional<MRO> found = repository.findById(mroId);

        // Assert
        assertTrue(found.isPresent());
        assertEquals(mroId, found.get().getId());

        verify(jpaRepository, times(1)).findById(mroId);
        verify(mapper, times(1)).toDomain(any(MROEntity.class));
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando MRO não encontrado")
    void deveRetornarOptionalVazioQuandoMroNaoEncontrado() {
        // Arrange
        UUID idNaoExistente = UUID.randomUUID();
        when(jpaRepository.findById(idNaoExistente)).thenReturn(Optional.empty());

        // Act
        Optional<MRO> found = repository.findById(idNaoExistente);

        // Assert
        assertFalse(found.isPresent());

        verify(jpaRepository, times(1)).findById(idNaoExistente);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Deve listar todos MROs com paginação")
    void deveListarTodosMrosComPaginacao() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<MROEntity> entityPage = new PageImpl<>(List.of(entity), pageable, 1);
        when(jpaRepository.findAll(pageable)).thenReturn(entityPage);
        when(mapper.toDomain(any(MROEntity.class))).thenReturn(mro);

        // Act
        Page<MRO> result = repository.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(jpaRepository, times(1)).findAll(pageable);
        verify(mapper, times(1)).toDomain(any(MROEntity.class));
    }

    @Test
    @DisplayName("Deve listar todos MROs sem paginação")
    void deveListarTodosMrosSemPaginacao() {
        // Arrange
        List<MROEntity> entities = List.of(entity);
        when(jpaRepository.findAll()).thenReturn(entities);
        when(mapper.toDomainList(any())).thenReturn(List.of(mro));

        // Act
        List<MRO> result = repository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(jpaRepository, times(1)).findAll();
        verify(mapper, times(1)).toDomainList(any());
    }

    @Test
    @DisplayName("Deve buscar MROs por status ativo")
    void deveBuscarMrosPorStatusAtivo() {
        // Arrange
        List<MROEntity> entities = List.of(entity);
        when(jpaRepository.findByAtivo(true)).thenReturn(entities);
        when(mapper.toDomainList(any())).thenReturn(List.of(mro));

        // Act
        List<MRO> result = repository.findByAtivo(true);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(jpaRepository, times(1)).findByAtivo(true);
        verify(mapper, times(1)).toDomainList(any());
    }

    @Test
    @DisplayName("Deve buscar MROs por tipo")
    void deveBuscarMrosPorTipo() {
        // Arrange
        List<MROEntity> entities = List.of(entity);
        when(jpaRepository.findByTipo(TipoMRO.INSUMO)).thenReturn(entities);
        when(mapper.toDomainList(any())).thenReturn(List.of(mro));

        // Act
        List<MRO> result = repository.findByTipo(TipoMRO.INSUMO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(jpaRepository, times(1)).findByTipo(TipoMRO.INSUMO);
        verify(mapper, times(1)).toDomainList(any());
    }

    @Test
    @DisplayName("Deve buscar MROs por nome contendo texto")
    void deveBuscarMrosPorNomeContendoTexto() {
        // Arrange
        List<MROEntity> entities = List.of(entity);
        when(jpaRepository.findByNomeContaining("Óleo")).thenReturn(entities);
        when(mapper.toDomainList(any())).thenReturn(List.of(mro));

        // Act
        List<MRO> result = repository.findByNomeContaining("Óleo");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(jpaRepository, times(1)).findByNomeContaining("Óleo");
        verify(mapper, times(1)).toDomainList(any());
    }

    @Test
    @DisplayName("Deve buscar MROs com quantidade de estoque maior que")
    void deveBuscarMrosComQuantidadeEstoqueMaiorQue() {
        // Arrange
        List<MROEntity> entities = List.of(entity);
        when(jpaRepository.findByQuantidadeEstoqueGreaterThan(50)).thenReturn(entities);
        when(mapper.toDomainList(any())).thenReturn(List.of(mro));

        // Act
        List<MRO> result = repository.findByQuantidadeEstoqueGreaterThan(50);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(jpaRepository, times(1)).findByQuantidadeEstoqueGreaterThan(50);
        verify(mapper, times(1)).toDomainList(any());
    }

    @Test
    @DisplayName("Deve deletar MRO por ID")
    void deveDeletarMroPorId() {
        // Act
        repository.deleteById(mroId);

        // Assert
        verify(jpaRepository, times(1)).deleteById(mroId);
    }
}
