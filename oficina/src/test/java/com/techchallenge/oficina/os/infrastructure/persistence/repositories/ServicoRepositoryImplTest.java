package com.techchallenge.oficina.os.infrastructure.persistence.repositories;

import com.techchallenge.oficina.os.domain.model.entities.Servico;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ServicoEntity;
import com.techchallenge.oficina.os.infrastructure.persistence.mappers.ServicoJpaMapper;
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
@DisplayName("Testes Unitários - ServicoRepositoryImpl")
class ServicoRepositoryImplTest {

    private static final int EXPECTED_ONE_ELEMENT = 1;

    @Mock
    private ServicoJpaRepository jpaRepository;

    @Mock
    private ServicoJpaMapper mapper;

    @InjectMocks
    private ServicoRepositoryImpl repository;

    private Servico servico;
    private ServicoEntity entity;

    @BeforeEach
    void setUp() {
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime atualizadoEm = LocalDateTime.now();

        servico = Servico.criar(
                "Troca de Óleo",
                "Troca de óleo sintético",
                new BigDecimal("150.00")
        );

        entity = ServicoEntity.builder()
                .id(UUID.randomUUID())
                .nome("Troca de Óleo")
                .descricao("Troca de óleo sintético")
                .preco(new BigDecimal("150.00"))
                .ativo(true)
                .criadoEm(criadoEm)
                .atualizadoEm(atualizadoEm)
                .build();
    }

    @Test
    @DisplayName("Deve salvar serviço com sucesso")
    void deveSalvarServicoComSucesso() {
        // Arrange
        when(mapper.toEntity(any(Servico.class))).thenReturn(entity);
        when(jpaRepository.save(any(ServicoEntity.class))).thenReturn(entity);
        when(mapper.toDomain(any(ServicoEntity.class))).thenReturn(servico);

        // Act
        Servico result = repository.save(servico);

        // Assert
        assertNotNull(result);
        verify(jpaRepository, times(1)).save(any(ServicoEntity.class));
        verify(mapper, times(1)).toEntity(any(Servico.class));
        verify(mapper, times(1)).toDomain(any(ServicoEntity.class));
    }

    @Test
    @DisplayName("Deve buscar serviço por ID com sucesso")
    void deveBuscarServicoPorIdComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(any(ServicoEntity.class))).thenReturn(servico);

        // Act
        Optional<Servico> result = repository.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(servico, result.get());
        verify(jpaRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve retornar vazio quando serviço não encontrado por ID")
    void deveRetornarVazioQuandoServicoNaoEncontradoPorId() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Servico> result = repository.findById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(jpaRepository, times(1)).findById(id);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Deve buscar todos os serviços com paginação")
    void deveBuscarTodosOsServicosComPaginacao() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<ServicoEntity> entityPage = new PageImpl<>(List.of(entity));
        Page<Servico> servicoPage = new PageImpl<>(List.of(servico));

        when(jpaRepository.findAll(pageable)).thenReturn(entityPage);
        when(mapper.toDomain(entity)).thenReturn(servico);

        // Act
        Page<Servico> result = repository.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(EXPECTED_ONE_ELEMENT, result.getContent().size());
        verify(jpaRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve buscar todos os serviços sem paginação")
    void deveBuscarTodosOsServicosSemPaginacao() {
        // Arrange
        List<ServicoEntity> entities = List.of(entity);
        List<Servico> servicos = List.of(servico);

        when(jpaRepository.findAll()).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(servicos);

        // Act
        List<Servico> result = repository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(EXPECTED_ONE_ELEMENT, result.size());
        verify(jpaRepository, times(1)).findAll();
        verify(mapper, times(1)).toDomainList(entities);
    }

    @Test
    @DisplayName("Deve buscar serviços por status ativo")
    void deveBuscarServicosPorStatusAtivo() {
        // Arrange
        List<ServicoEntity> entities = List.of(entity);
        List<Servico> servicos = List.of(servico);

        when(jpaRepository.findByAtivo(true)).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(servicos);

        // Act
        List<Servico> result = repository.findByAtivo(true);

        // Assert
        assertNotNull(result);
        assertEquals(EXPECTED_ONE_ELEMENT, result.size());
        verify(jpaRepository, times(1)).findByAtivo(true);
        verify(mapper, times(1)).toDomainList(entities);
    }

    @Test
    @DisplayName("Deve buscar serviços por status inativo")
    void deveBuscarServicosPorStatusInativo() {
        // Arrange
        entity.setAtivo(false);
        servico.desativar();
        List<ServicoEntity> entities = List.of(entity);
        List<Servico> servicos = List.of(servico);

        when(jpaRepository.findByAtivo(false)).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(servicos);

        // Act
        List<Servico> result = repository.findByAtivo(false);

        // Assert
        assertNotNull(result);
        verify(jpaRepository, times(1)).findByAtivo(false);
        verify(mapper, times(1)).toDomainList(entities);
    }

    @Test
    @DisplayName("Deve buscar serviços por nome contendo termo")
    void deveBuscarServicosPorNomeContendoTermo() {
        // Arrange
        String termo = "Óleo";
        List<ServicoEntity> entities = List.of(entity);
        List<Servico> servicos = List.of(servico);

        when(jpaRepository.findByNomeContaining(termo)).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(servicos);

        // Act
        List<Servico> result = repository.findByNomeContaining(termo);

        // Assert
        assertNotNull(result);
        assertEquals(EXPECTED_ONE_ELEMENT, result.size());
        verify(jpaRepository, times(1)).findByNomeContaining(termo);
        verify(mapper, times(1)).toDomainList(entities);
    }

    @Test
    @DisplayName("Deve deletar serviço por ID")
    void deveDeletarServicoPorId() {
        // Arrange
        UUID id = UUID.randomUUID();
        doNothing().when(jpaRepository).deleteById(id);

        // Act
        repository.deleteById(id);

        // Assert
        verify(jpaRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando buscar por nome não encontra resultados")
    void deveRetornarListaVaziaQuandoBuscarPorNomeNaoEncontraResultados() {
        // Arrange
        String termo = "Inexistente";
        when(jpaRepository.findByNomeContaining(termo)).thenReturn(List.of());
        when(mapper.toDomainList(List.of())).thenReturn(List.of());

        // Act
        List<Servico> result = repository.findByNomeContaining(termo);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jpaRepository, times(1)).findByNomeContaining(termo);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando buscar por ativo não encontra resultados")
    void deveRetornarListaVaziaQuandoBuscarPorAtivoNaoEncontraResultados() {
        // Arrange
        when(jpaRepository.findByAtivo(true)).thenReturn(List.of());
        when(mapper.toDomainList(List.of())).thenReturn(List.of());

        // Act
        List<Servico> result = repository.findByAtivo(true);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jpaRepository, times(1)).findByAtivo(true);
    }
}
