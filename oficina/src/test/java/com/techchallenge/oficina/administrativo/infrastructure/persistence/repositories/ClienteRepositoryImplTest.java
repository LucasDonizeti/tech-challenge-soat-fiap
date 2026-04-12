package com.techchallenge.oficina.administrativo.infrastructure.persistence.repositories;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ClienteEntity;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ClienteEntity.StatusClienteEntity;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.mappers.ClienteJpaMapper;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.mappers.VeiculoJpaMapper;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.specifications.ClienteFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - ClienteRepositoryImpl")
class ClienteRepositoryImplTest {

    private static final int EXPECTED_ONE_ELEMENT = 1;

    @Mock
    private ClienteJpaRepository jpaRepository;

    @Mock
    private ClienteJpaMapper mapper;

    @Mock
    private VeiculoJpaMapper veiculoJpaMapper;

    @InjectMocks
    private ClienteRepositoryImpl repository;

    private Cliente cliente;
    private ClienteEntity entity;

    @BeforeEach
    void setUp() {
        Nome nome = Nome.of("João Silva");
        CPF cpf = CPF.of("12345678909");
        Email email = Email.of("joao.silva@exemplo.com");
        cliente = Cliente.criar(nome, cpf, email);

        entity = new ClienteEntity();
        entity.setId(UUID.randomUUID());
        entity.setNome("João Silva");
        entity.setCpf("12345678909");
        entity.setEmail("joao.silva@exemplo.com");
        entity.setStatus(StatusClienteEntity.ATIVO);
    }

    @Test
    @DisplayName("Deve salvar cliente com sucesso")
    void deveSalvarClienteComSucesso() {
        // Arrange
        when(mapper.toEntity(any(Cliente.class))).thenReturn(entity);
        when(jpaRepository.save(any(ClienteEntity.class))).thenReturn(entity);
        when(mapper.toDomain(any(ClienteEntity.class))).thenReturn(cliente);

        // Act
        Cliente result = repository.save(cliente);

        // Assert
        assertNotNull(result);
        verify(jpaRepository, times(1)).save(any(ClienteEntity.class));
        verify(mapper, times(1)).toEntity(any(Cliente.class));
        verify(mapper, times(1)).toDomain(any(ClienteEntity.class));
    }

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    void deveBuscarClientePorIdComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(any(ClienteEntity.class))).thenReturn(cliente);

        // Act
        Optional<Cliente> result = repository.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(cliente, result.get());
        verify(jpaRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve retornar vazio quando cliente não encontrado por ID")
    void deveRetornarVazioQuandoClienteNaoEncontradoPorId() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Cliente> result = repository.findById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(jpaRepository, times(1)).findById(id);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Deve buscar todos os clientes")
    void deveBuscarTodosOsClientes() {
        // Arrange
        List<ClienteEntity> entities = List.of(entity);
        List<Cliente> clientes = List.of(cliente);
        when(jpaRepository.findAll()).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(clientes);

        // Act
        List<Cliente> result = repository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(EXPECTED_ONE_ELEMENT, result.size());
        verify(jpaRepository, times(1)).findAll();
        verify(mapper, times(1)).toDomainList(entities);
    }

    @Test
    @DisplayName("Deve buscar clientes por status")
    void deveBuscarClientesPorStatus() {
        // Arrange
        List<ClienteEntity> entities = List.of(entity);
        List<Cliente> clientes = List.of(cliente);
        when(jpaRepository.findByStatus(StatusClienteEntity.ATIVO)).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(clientes);

        // Act
        List<Cliente> result = repository.findByStatus(StatusCliente.ATIVO);

        // Assert
        assertNotNull(result);
        assertEquals(EXPECTED_ONE_ELEMENT, result.size());
        verify(jpaRepository, times(1)).findByStatus(StatusClienteEntity.ATIVO);
        verify(mapper, times(1)).toDomainList(entities);
    }

    @Test
    @DisplayName("Deve buscar cliente por CPF com sucesso")
    void deveBuscarClientePorCpfComSucesso() {
        // Arrange
        CPF cpf = CPF.of("12345678909");
        when(jpaRepository.findByCpf(cpf.getValor())).thenReturn(Optional.of(entity));
        when(mapper.toDomain(any(ClienteEntity.class))).thenReturn(cliente);

        // Act
        Optional<Cliente> result = repository.findByCPF(cpf);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(cliente, result.get());
        verify(jpaRepository, times(1)).findByCpf(cpf.getValor());
    }

    @Test
    @DisplayName("Deve buscar cliente por CNPJ com sucesso")
    void deveBuscarClientePorCnpjComSucesso() {
        // Arrange
        CNPJ cnpj = CNPJ.of("11444777000161");
        entity.setCnpj("11444777000161");
        when(jpaRepository.findByCnpj(cnpj.getValor())).thenReturn(Optional.of(entity));
        when(mapper.toDomain(any(ClienteEntity.class))).thenReturn(cliente);

        // Act
        Optional<Cliente> result = repository.findByCNPJ(cnpj);

        // Assert
        assertTrue(result.isPresent());
        verify(jpaRepository, times(1)).findByCnpj(cnpj.getValor());
    }

    @Test
    @DisplayName("Deve buscar cliente por email com sucesso")
    void deveBuscarClientePorEmailComSucesso() {
        // Arrange
        Email email = Email.of("joao.silva@exemplo.com");
        when(jpaRepository.findByEmail(email.getEndereco())).thenReturn(Optional.of(entity));
        when(mapper.toDomain(any(ClienteEntity.class))).thenReturn(cliente);

        // Act
        Optional<Cliente> result = repository.findByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        verify(jpaRepository, times(1)).findByEmail(email.getEndereco());
    }

    @Test
    @DisplayName("Deve verificar se CPF existe")
    void deveVerificarSeCpfExiste() {
        // Arrange
        CPF cpf = CPF.of("12345678909");
        when(jpaRepository.existsByCpf(cpf.getValor())).thenReturn(true);

        // Act
        boolean result = repository.existsByCPF(cpf);

        // Assert
        assertTrue(result);
        verify(jpaRepository, times(1)).existsByCpf(cpf.getValor());
    }

    @Test
    @DisplayName("Deve verificar se CNPJ existe")
    void deveVerificarSeCnpjExiste() {
        // Arrange
        CNPJ cnpj = CNPJ.of("11444777000161");
        when(jpaRepository.existsByCnpj(cnpj.getValor())).thenReturn(true);

        // Act
        boolean result = repository.existsByCNPJ(cnpj);

        // Assert
        assertTrue(result);
        verify(jpaRepository, times(1)).existsByCnpj(cnpj.getValor());
    }

    @Test
    @DisplayName("Deve verificar se email existe")
    void deveVerificarSeEmailExiste() {
        // Arrange
        Email email = Email.of("joao.silva@exemplo.com");
        when(jpaRepository.existsByEmail(email.getEndereco())).thenReturn(true);

        // Act
        boolean result = repository.existsByEmail(email);

        // Assert
        assertTrue(result);
        verify(jpaRepository, times(1)).existsByEmail(email.getEndereco());
    }

    @Test
    @DisplayName("Deve deletar cliente por ID")
    void deveDeletarClientePorId() {
        // Arrange
        UUID id = UUID.randomUUID();
        doNothing().when(jpaRepository).deleteById(id);

        // Act
        repository.deleteById(id);

        // Assert
        verify(jpaRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Deve buscar clientes por filtro ClienteFilter")
    void deveBuscarClientesPorFiltroClienteFilter() {
        // Arrange
        ClienteFilter filter = ClienteFilter.builder()
                .nome("João")
                .status(StatusCliente.ATIVO)
                .build();
        
        List<ClienteEntity> entities = List.of(entity);
        List<Cliente> clientes = List.of(cliente);
        
        when(jpaRepository.findByFilter(
                "João", null, null, null, StatusClienteEntity.ATIVO))
                .thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(clientes);

        // Act
        List<Cliente> result = repository.findByFilter(filter);

        // Assert
        assertNotNull(result);
        assertEquals(EXPECTED_ONE_ELEMENT, result.size());
        verify(jpaRepository, times(1)).findByFilter(
                "João", null, null, null, StatusClienteEntity.ATIVO);
    }

    @Test
    @DisplayName("Deve lançar exceção para tipo de filtro não suportado")
    void deveLancarExcecaoParaTipoDeFiltroNaoSuportado() {
        // Arrange
        Object filtroNaoSuportado = new Object();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> repository.findByFilter(filtroNaoSuportado));
    }

    @Test
    @DisplayName("Deve converter StatusCliente ATIVO para StatusClienteEntity ATIVO")
    void deveConverterStatusClienteAtivoParaEntity() {
        // Arrange
        List<ClienteEntity> entities = List.of(entity);
        List<Cliente> clientes = List.of(cliente);
        
        when(jpaRepository.findByStatus(StatusClienteEntity.ATIVO)).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(clientes);

        // Act
        List<Cliente> result = repository.findByStatus(StatusCliente.ATIVO);

        // Assert
        assertNotNull(result);
        verify(jpaRepository, times(1)).findByStatus(StatusClienteEntity.ATIVO);
    }

    @Test
    @DisplayName("Deve converter StatusCliente INATIVO para StatusClienteEntity INATIVO")
    void deveConverterStatusClienteInativoParaEntity() {
        // Arrange
        entity.setStatus(StatusClienteEntity.INATIVO);
        List<ClienteEntity> entities = List.of(entity);
        List<Cliente> clientes = List.of(cliente);
        cliente.inativar();
        
        when(jpaRepository.findByStatus(StatusClienteEntity.INATIVO)).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(clientes);

        // Act
        List<Cliente> result = repository.findByStatus(StatusCliente.INATIVO);

        // Assert
        assertNotNull(result);
        verify(jpaRepository, times(1)).findByStatus(StatusClienteEntity.INATIVO);
    }
}
