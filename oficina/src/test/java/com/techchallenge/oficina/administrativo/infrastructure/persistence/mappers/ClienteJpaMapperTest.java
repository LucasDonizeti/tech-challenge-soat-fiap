package com.techchallenge.oficina.administrativo.infrastructure.persistence.mappers;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.ClienteRestauracaoParams;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.*;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ClienteEntity;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ClienteEntity.StatusClienteEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;

@DisplayName("Testes Unitários - ClienteJpaMapper")
class ClienteJpaMapperTest {

    @Mock
    private VeiculoJpaMapper veiculoJpaMapper;

    private ClienteJpaMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new ClienteJpaMapper(veiculoJpaMapper);
    }

    @Test
    @DisplayName("Deve converter Cliente Pessoa Física para Entity com sucesso")
    void deveConverterClientePessoaFisicaParaEntity() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime atualizadoEm = LocalDateTime.now();
        
        ClienteRestauracaoParams params = new ClienteRestauracaoParams(
                id,
                Nome.of("João Silva"),
                CPF.of("12345678909"),
                null,
                Email.of("joao@exemplo.com"),
                StatusCliente.ATIVO,
                criadoEm,
                atualizadoEm
        );
        Cliente cliente = Cliente.restaurar(params);

        when(veiculoJpaMapper.toEntityList(any())).thenReturn(List.of());

        // Act
        ClienteEntity entity = mapper.toEntity(cliente);

        // Assert
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("João Silva", entity.getNome());
        assertEquals("12345678909", entity.getCpf());
        assertNull(entity.getCnpj());
        assertEquals("joao@exemplo.com", entity.getEmail());
        assertEquals(StatusClienteEntity.ATIVO, entity.getStatus());
        assertEquals(criadoEm, entity.getCriadoEm());
        assertEquals(atualizadoEm, entity.getAtualizadoEm());
        verify(veiculoJpaMapper, times(1)).toEntityList(any());
    }

    @Test
    @DisplayName("Deve converter Cliente Pessoa Jurídica para Entity com sucesso")
    void deveConverterClientePessoaJuridicaParaEntity() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime atualizadoEm = LocalDateTime.now();
        
        ClienteRestauracaoParams params = new ClienteRestauracaoParams(
                id,
                Nome.of("Empresa ABC"),
                null,
                CNPJ.of("11222333000181"),
                Email.of("contato@empresa.com"),
                StatusCliente.INATIVO,
                criadoEm,
                atualizadoEm
        );
        Cliente cliente = Cliente.restaurar(params);

        when(veiculoJpaMapper.toEntityList(any())).thenReturn(List.of());

        // Act
        ClienteEntity entity = mapper.toEntity(cliente);

        // Assert
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("Empresa ABC", entity.getNome());
        assertNull(entity.getCpf());
        assertEquals("11222333000181", entity.getCnpj());
        assertEquals("contato@empresa.com", entity.getEmail());
        assertEquals(StatusClienteEntity.INATIVO, entity.getStatus());
        assertEquals(criadoEm, entity.getCriadoEm());
        assertEquals(atualizadoEm, entity.getAtualizadoEm());
        verify(veiculoJpaMapper, times(1)).toEntityList(any());
    }

    @Test
    @DisplayName("Deve converter Entity para Cliente com sucesso")
    void deveConverterEntityParaCliente() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime atualizadoEm = LocalDateTime.now();
        
        ClienteEntity entity = ClienteEntity.builder()
                .id(id)
                .nome("Maria Santos")
                .cpf("98765432100")
                .cnpj(null)
                .email("maria@exemplo.com")
                .status(StatusClienteEntity.ATIVO)
                .criadoEm(criadoEm)
                .atualizadoEm(atualizadoEm)
                .veiculos(List.of())
                .build();

        when(veiculoJpaMapper.toDomainList(any())).thenReturn(List.of());

        // Act
        Cliente cliente = mapper.toDomain(entity);

        // Assert
        assertNotNull(cliente);
        assertEquals(id, cliente.getId());
        assertEquals("Maria Santos", cliente.getNome().getValor());
        assertEquals("987.654.321-00", cliente.getCpf().getFormatado());
        assertNull(cliente.getCnpj());
        assertEquals("maria@exemplo.com", cliente.getEmail().getEndereco());
        assertEquals(StatusCliente.ATIVO, cliente.getStatus());
        assertEquals(criadoEm, cliente.getCriadoEm());
        assertEquals(atualizadoEm, cliente.getAtualizadoEm());
        verify(veiculoJpaMapper, times(1)).toDomainList(any());
    }

    @Test
    @DisplayName("Deve retornar null quando cliente é null")
    void deveRetornarNullQuandoClienteENull() {
        // Act
        ClienteEntity entity = mapper.toEntity(null);

        // Assert
        assertNull(entity);
        verifyNoInteractions(veiculoJpaMapper);
    }

    @Test
    @DisplayName("Deve retornar null quando entity é null")
    void deveRetornarNullQuandoEntityENull() {
        // Act
        Cliente cliente = mapper.toDomain(null);

        // Assert
        assertNull(cliente);
        verifyNoInteractions(veiculoJpaMapper);
    }

    @Test
    @DisplayName("Deve converter lista de clientes para entities")
    void deveConverterListaDeClientesParaEntities() {
        // Arrange
        Cliente cliente1 = Cliente.criar(
                Nome.of("João Silva"),
                CPF.of("12345678909"),
                Email.of("joao@exemplo.com")
        );
        
        Cliente cliente2 = Cliente.criarPJ(
                Nome.of("Empresa ABC"),
                CNPJ.of("11222333000181"),
                Email.of("contato@empresa.com")
        );
        
        List<Cliente> clientes = List.of(cliente1, cliente2);

        when(veiculoJpaMapper.toEntityList(any())).thenReturn(List.of());

        // Act
        List<ClienteEntity> entities = mapper.toEntityList(clientes);

        // Assert
        assertNotNull(entities);
        assertEquals(2, entities.size());
        assertEquals("João Silva", entities.get(0).getNome());
        assertEquals("Empresa ABC", entities.get(1).getNome());
        verify(veiculoJpaMapper, times(2)).toEntityList(any());
    }

    @Test
    @DisplayName("Deve converter lista de entities para clientes")
    void deveConverterListaDeEntitiesParaClientes() {
        // Arrange
        ClienteEntity entity1 = ClienteEntity.builder()
                .id(UUID.randomUUID())
                .nome("João Silva")
                .cpf("12345678909")
                .email("joao@exemplo.com")
                .status(StatusClienteEntity.ATIVO)
                .veiculos(List.of())
                .build();
        
        ClienteEntity entity2 = ClienteEntity.builder()
                .id(UUID.randomUUID())
                .nome("Empresa ABC")
                .cnpj("11222333000181")
                .email("contato@empresa.com")
                .status(StatusClienteEntity.ATIVO)
                .veiculos(List.of())
                .build();
        
        List<ClienteEntity> entities = List.of(entity1, entity2);

        when(veiculoJpaMapper.toDomainList(any())).thenReturn(List.of());

        // Act
        List<Cliente> clientes = mapper.toDomainList(entities);

        // Assert
        assertNotNull(clientes);
        assertEquals(2, clientes.size());
        assertEquals("João Silva", clientes.get(0).getNome().getValor());
        assertEquals("Empresa ABC", clientes.get(1).getNome().getValor());
        verify(veiculoJpaMapper, times(2)).toDomainList(any());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando lista de clientes é null")
    void deveRetornarListaVaziaQuandoListaDeClientesENull() {
        // Act
        List<ClienteEntity> entities = mapper.toEntityList(null);

        // Assert
        assertNotNull(entities);
        assertTrue(entities.isEmpty());
        verifyNoInteractions(veiculoJpaMapper);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando lista de entities é null")
    void deveRetornarListaVaziaQuandoListaDeEntitiesENull() {
        // Act
        List<Cliente> clientes = mapper.toDomainList(null);

        // Assert
        assertNotNull(clientes);
        assertTrue(clientes.isEmpty());
        verifyNoInteractions(veiculoJpaMapper);
    }
}
