package com.techchallenge.oficina.administrativo.infrastructure.persistence.repositories;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusVeiculo;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ClienteEntity;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.VeiculoEntity;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.mappers.VeiculoJpaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - VeiculoRepositoryImpl")
class VeiculoRepositoryImplTest {

    @Mock
    private VeiculoJpaRepository jpaRepository;

    @Mock
    private VeiculoJpaMapper mapper;

    @Mock
    private ClienteJpaRepository clienteJpaRepository;

    @InjectMocks
    private VeiculoRepositoryImpl repository;

    private Veiculo veiculo;
    private VeiculoEntity veiculoEntity;
    private Cliente cliente;
    private ClienteEntity clienteEntity;

    @BeforeEach
    void setUp() {
        cliente = Cliente.criar(
                Nome.of("João Silva"),
                CPF.of("12345678909"),
                Email.of("joao.silva@email.com")
        );

        clienteEntity = ClienteEntity.builder()
                .id(cliente.getId())
                .nome(cliente.getNome().getValor())
                .cpf(cliente.getCpf().getValor())
                .email(cliente.getEmail().getEndereco())
                .status(ClienteEntity.StatusClienteEntity.ATIVO)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        veiculo = new Veiculo(
                Placa.of("ABC1234"),
                "Toyota",
                "Corolla",
                2022,
                "Prata"
        );
        veiculo.setCliente(cliente);

        veiculoEntity = VeiculoEntity.builder()
                .id(veiculo.getId())
                .placa("ABC1234")
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2022)
                .cor("Prata")
                .status(VeiculoEntity.StatusVeiculoEntity.ATIVO)
                .cliente(clienteEntity)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve salvar veículo com sucesso")
    void deveSalvarVeiculoComSucesso() {
        // Arrange
        when(mapper.toEntity(veiculo)).thenReturn(veiculoEntity);
        when(clienteJpaRepository.findById(cliente.getId())).thenReturn(Optional.of(clienteEntity));
        when(jpaRepository.save(veiculoEntity)).thenReturn(veiculoEntity);
        when(mapper.toDomain(veiculoEntity)).thenReturn(veiculo);

        // Act
        Veiculo saved = repository.save(veiculo);

        // Assert
        assertNotNull(saved);
        assertEquals(veiculo.getId(), saved.getId());
        verify(mapper, times(1)).toEntity(veiculo);
        verify(clienteJpaRepository, times(1)).findById(cliente.getId());
        verify(jpaRepository, times(1)).save(veiculoEntity);
        verify(mapper, times(1)).toDomain(veiculoEntity);
    }

    @Test
    @DisplayName("Deve salvar veículo sem cliente com sucesso")
    void deveSalvarVeiculoSemClienteComSucesso() {
        // Arrange
        veiculo.setCliente(null);
        veiculoEntity.setCliente(null);
        when(mapper.toEntity(veiculo)).thenReturn(veiculoEntity);
        when(jpaRepository.save(veiculoEntity)).thenReturn(veiculoEntity);
        when(mapper.toDomain(veiculoEntity)).thenReturn(veiculo);

        // Act
        Veiculo saved = repository.save(veiculo);

        // Assert
        assertNotNull(saved);
        verify(mapper, times(1)).toEntity(veiculo);
        verify(clienteJpaRepository, never()).findById(any());
        verify(jpaRepository, times(1)).save(veiculoEntity);
        verify(mapper, times(1)).toDomain(veiculoEntity);
    }

    @Test
    @DisplayName("Deve salvar veículo com cliente não encontrado")
    void deveSalvarVeiculoComClienteNaoEncontrado() {
        // Arrange
        when(mapper.toEntity(veiculo)).thenReturn(veiculoEntity);
        when(clienteJpaRepository.findById(cliente.getId())).thenReturn(Optional.empty());
        when(jpaRepository.save(veiculoEntity)).thenReturn(veiculoEntity);
        when(mapper.toDomain(veiculoEntity)).thenReturn(veiculo);

        // Act
        Veiculo saved = repository.save(veiculo);

        // Assert
        assertNotNull(saved);
        verify(clienteJpaRepository, times(1)).findById(cliente.getId());
        assertNull(veiculoEntity.getCliente());
    }

    @Test
    @DisplayName("Deve buscar veículo por ID com sucesso")
    void deveBuscarVeiculoPorIdComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.of(veiculoEntity));
        when(mapper.toDomain(veiculoEntity)).thenReturn(veiculo);

        // Act
        Optional<Veiculo> result = repository.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(veiculo.getId(), result.get().getId());
        verify(jpaRepository, times(1)).findById(id);
        verify(mapper, times(1)).toDomain(veiculoEntity);
    }

    @Test
    @DisplayName("Deve retornar vazio quando veículo não encontrado por ID")
    void deveRetornarVazioQuandoVeiculoNaoEncontradoPorId() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Veiculo> result = repository.findById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(jpaRepository, times(1)).findById(id);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Deve buscar veículo por placa com sucesso")
    void deveBuscarVeiculoPorPlacaComSucesso() {
        // Arrange
        Placa placa = Placa.of("ABC1234");
        when(jpaRepository.findByPlaca("ABC1234")).thenReturn(Optional.of(veiculoEntity));
        when(mapper.toDomain(veiculoEntity)).thenReturn(veiculo);

        // Act
        Optional<Veiculo> result = repository.findByPlaca(placa);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(veiculo.getId(), result.get().getId());
        verify(jpaRepository, times(1)).findByPlaca("ABC1234");
        verify(mapper, times(1)).toDomain(veiculoEntity);
    }

    @Test
    @DisplayName("Deve retornar vazio quando veículo não encontrado por placa")
    void deveRetornarVazioQuandoVeiculoNaoEncontradoPorPlaca() {
        // Arrange
        Placa placa = Placa.of("XYZ9999");
        when(jpaRepository.findByPlaca("XYZ9999")).thenReturn(Optional.empty());

        // Act
        Optional<Veiculo> result = repository.findByPlaca(placa);

        // Assert
        assertFalse(result.isPresent());
        verify(jpaRepository, times(1)).findByPlaca("XYZ9999");
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Deve verificar se placa existe")
    void deveVerificarSePlacaExiste() {
        // Arrange
        Placa placa = Placa.of("ABC1234");
        when(jpaRepository.existsByPlaca("ABC1234")).thenReturn(true);

        // Act
        boolean exists = repository.existsByPlaca(placa);

        // Assert
        assertTrue(exists);
        verify(jpaRepository, times(1)).existsByPlaca("ABC1234");
    }

    @Test
    @DisplayName("Deve verificar se placa não existe")
    void deveVerificarSePlacaNaoExiste() {
        // Arrange
        Placa placa = Placa.of("XYZ9999");
        when(jpaRepository.existsByPlaca("XYZ9999")).thenReturn(false);

        // Act
        boolean exists = repository.existsByPlaca(placa);

        // Assert
        assertFalse(exists);
        verify(jpaRepository, times(1)).existsByPlaca("XYZ9999");
    }

    @Test
    @DisplayName("Deve buscar veículos por cliente ID")
    void deveBuscarVeiculosPorClienteId() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        List<VeiculoEntity> entities = List.of(veiculoEntity);
        List<Veiculo> veiculos = List.of(veiculo);
        when(jpaRepository.findByClienteId(clienteId)).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(veiculos);

        // Act
        List<Veiculo> result = repository.findByClienteId(clienteId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(jpaRepository, times(1)).findByClienteId(clienteId);
        verify(mapper, times(1)).toDomainList(entities);
    }

    @Test
    @DisplayName("Deve buscar veículos por cliente ID e status")
    void deveBuscarVeiculosPorClienteIdEStatus() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        List<VeiculoEntity> entities = List.of(veiculoEntity);
        List<Veiculo> veiculos = List.of(veiculo);
        when(jpaRepository.findByClienteIdAndStatus(eq(clienteId), any(VeiculoEntity.StatusVeiculoEntity.class)))
                .thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(veiculos);

        // Act
        List<Veiculo> result = repository.findByClienteIdAndStatus(clienteId, StatusVeiculo.ATIVO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(jpaRepository, times(1)).findByClienteIdAndStatus(eq(clienteId), any(VeiculoEntity.StatusVeiculoEntity.class));
        verify(mapper, times(1)).toDomainList(entities);
    }

    @Test
    @DisplayName("Deve buscar veículos por status")
    void deveBuscarVeiculosPorStatus() {
        // Arrange
        List<VeiculoEntity> entities = List.of(veiculoEntity);
        List<Veiculo> veiculos = List.of(veiculo);
        when(jpaRepository.findByStatus(any(VeiculoEntity.StatusVeiculoEntity.class))).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(veiculos);

        // Act
        List<Veiculo> result = repository.findByStatus(StatusVeiculo.ATIVO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(jpaRepository, times(1)).findByStatus(any(VeiculoEntity.StatusVeiculoEntity.class));
        verify(mapper, times(1)).toDomainList(entities);
    }

    @Test
    @DisplayName("Deve buscar todos os veículos")
    void deveBuscarTodosOsVeiculos() {
        // Arrange
        List<VeiculoEntity> entities = List.of(veiculoEntity);
        List<Veiculo> veiculos = List.of(veiculo);
        when(jpaRepository.findAll()).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(veiculos);

        // Act
        List<Veiculo> result = repository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(jpaRepository, times(1)).findAll();
        verify(mapper, times(1)).toDomainList(entities);
    }

    @Test
    @DisplayName("Deve deletar veículo por ID")
    void deveDeletarVeiculoPorId() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        repository.deleteById(id);

        // Assert
        verify(jpaRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Deve converter status ATIVO para entity status")
    void deveConverterStatusAtivoParaEntityStatus() {
        // This is tested implicitly through findByStatus and findByClienteIdAndStatus tests
        // The private method toEntityStatus is tested through public methods
        List<VeiculoEntity> entities = List.of(veiculoEntity);
        List<Veiculo> veiculos = List.of(veiculo);
        when(jpaRepository.findByStatus(VeiculoEntity.StatusVeiculoEntity.ATIVO)).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(veiculos);

        List<Veiculo> result = repository.findByStatus(StatusVeiculo.ATIVO);

        assertNotNull(result);
        verify(jpaRepository, times(1)).findByStatus(VeiculoEntity.StatusVeiculoEntity.ATIVO);
    }

    @Test
    @DisplayName("Deve converter status INATIVO para entity status")
    void deveConverterStatusInativoParaEntityStatus() {
        // This is tested implicitly through findByStatus and findByClienteIdAndStatus tests
        veiculoEntity.setStatus(VeiculoEntity.StatusVeiculoEntity.INATIVO);
        veiculo.inativar();
        List<VeiculoEntity> entities = List.of(veiculoEntity);
        List<Veiculo> veiculos = List.of(veiculo);
        when(jpaRepository.findByStatus(VeiculoEntity.StatusVeiculoEntity.INATIVO)).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(veiculos);

        List<Veiculo> result = repository.findByStatus(StatusVeiculo.INATIVO);

        assertNotNull(result);
        verify(jpaRepository, times(1)).findByStatus(VeiculoEntity.StatusVeiculoEntity.INATIVO);
    }
}
