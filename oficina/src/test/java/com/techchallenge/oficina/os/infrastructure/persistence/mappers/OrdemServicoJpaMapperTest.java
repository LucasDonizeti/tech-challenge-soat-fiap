package com.techchallenge.oficina.os.infrastructure.persistence.mappers;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import com.techchallenge.oficina.administrativo.domain.repositories.VeiculoRepository;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemServicoEntity;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.OrdemServicoEntity;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.OrdemServicoEntity.StatusOSEntity;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - OrdemServicoJpaMapper")
class OrdemServicoJpaMapperTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private ItemServicoJpaMapper itemServicoJpaMapper;

    private OrdemServicoJpaMapper mapper;

    private UUID id;
    private UUID clienteId;
    private UUID veiculoId;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataInicioExecucao;
    private LocalDateTime dataFinalizacao;
    private StatusOS statusOS;
    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        mapper = new OrdemServicoJpaMapper(clienteRepository, veiculoRepository, itemServicoJpaMapper);
        
        id = UUID.randomUUID();
        clienteId = UUID.randomUUID();
        veiculoId = UUID.randomUUID();
        dataCriacao = LocalDateTime.now().minusDays(1);
        dataInicioExecucao = LocalDateTime.now().minusHours(2);
        dataFinalizacao = LocalDateTime.now();
        statusOS = StatusOS.EM_EXECUCAO;
        
        // Criando cliente com factory method correto (Pessoa Física)
        cliente = Cliente.criar(
            Nome.of("João Silva"),
            CPF.of("12345678909"),
            Email.of("joao.silva@email.com")
        );
        
        // Criando veículo com construtor padrão usado nos testes
        veiculo = new Veiculo(Placa.of("ABC1234"), "Toyota", "Corolla", 2022, "Prata");
        veiculo.setCliente(cliente);
        
        // Usar IDs gerados automaticamente para os testes
        id = cliente.getId();
        clienteId = cliente.getId();
        veiculoId = veiculo.getId();
    }

    @Test
    @DisplayName("Deve converter OrdemServico para Entity com sucesso")
    void deveConverterOrdemServicoParaEntity() {
        // Arrange
        OrdemServico ordemServico = OrdemServico.reconstruir(
                id,
                cliente,
                veiculo,
                statusOS,
                dataCriacao,
                dataInicioExecucao,
                dataFinalizacao
        );

        // Act
        OrdemServicoEntity entity = mapper.toEntity(ordemServico);

        // Assert
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals(clienteId, entity.getClienteId());
        assertEquals(veiculoId, entity.getVeiculoId());
        assertEquals(StatusOSEntity.EM_EXECUCAO, entity.getStatus());
        assertEquals(dataCriacao, entity.getDataCriacao());
        assertEquals(dataInicioExecucao, entity.getDataInicioExecucao());
        assertEquals(dataFinalizacao, entity.getDataFinalizacao());
        assertTrue(entity.getItensServico().isEmpty());
    }

    @Test
    @DisplayName("Deve converter OrdemServico para Entity com itens de serviço")
    void deveConverterOrdemServicoParaEntityComItensServico() {
        // Arrange
        ItemServico itemServico = ItemServico.criarComDados(
                UUID.randomUUID(),
                "Serviço Teste",
                "Descrição do serviço",
                new BigDecimal("100.00")
        );
        
        // Use RECEBIDA status to allow adding items
        OrdemServico ordemServico = OrdemServico.reconstruir(
                id,
                cliente,
                veiculo,
                StatusOS.RECEBIDA,
                dataCriacao,
                dataInicioExecucao,
                dataFinalizacao
        );
        ordemServico.adicionarItemServico(itemServico);

        ItemServicoEntity itemEntity = ItemServicoEntity.builder()
                .id(itemServico.getId())
                .ordemServicoId(id)
                .servicoId(itemServico.getServicoId())
                .status(ItemServicoEntity.StatusItemServicoEntity.PENDENTE)
                .valorServico(itemServico.getValorServico())
                .valorMro(itemServico.getValorMro())
                .build();

        when(itemServicoJpaMapper.toEntityList(any(), any())).thenReturn(List.of(itemEntity));

        // Act
        OrdemServicoEntity entity = mapper.toEntity(ordemServico);

        // Assert
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals(1, entity.getItensServico().size());
        assertEquals(itemEntity, entity.getItensServico().get(0));
        
        verify(itemServicoJpaMapper, times(1)).toEntityList(any(), eq(id));
    }

    @Test
    @DisplayName("Deve retornar null quando OrdemServico é null")
    void deveRetornarNullQuandoOrdemServicoENull() {
        // Act
        OrdemServicoEntity entity = mapper.toEntity(null);

        // Assert
        assertNull(entity);
        
        verify(itemServicoJpaMapper, never()).toEntityList(any(), any());
    }

    @Test
    @DisplayName("Deve converter Entity para OrdemServico com sucesso")
    void deveConverterEntityParaOrdemServico() {
        // Arrange
        OrdemServicoEntity entity = OrdemServicoEntity.builder()
                .id(id)
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .status(StatusOSEntity.EM_EXECUCAO)
                .dataCriacao(dataCriacao)
                .dataInicioExecucao(dataInicioExecucao)
                .dataFinalizacao(dataFinalizacao)
                .build();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));

        // Act
        OrdemServico ordemServico = mapper.toDomain(entity, clienteRepository, veiculoRepository);

        // Assert
        assertNotNull(ordemServico);
        assertEquals(id, ordemServico.getId());
        assertEquals(cliente, ordemServico.getCliente());
        assertEquals(veiculo, ordemServico.getVeiculo());
        assertEquals(StatusOS.EM_EXECUCAO, ordemServico.getStatus());
        assertEquals(dataCriacao, ordemServico.getDataCriacao());
        assertEquals(dataInicioExecucao, ordemServico.getDataInicioExecucao());
        assertEquals(dataFinalizacao, ordemServico.getDataFinalizacao());
        assertTrue(ordemServico.getItensServico().isEmpty());
        
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(veiculoRepository, times(1)).findById(veiculoId);
    }

    @Test
    @DisplayName("Deve converter Entity para OrdemServico com itens de serviço")
    void deveConverterEntityParaOrdemServicoComItensServico() {
        // Arrange
        ItemServicoEntity itemEntity = ItemServicoEntity.builder()
                .id(UUID.randomUUID())
                .ordemServicoId(id)
                .servicoId(UUID.randomUUID())
                .status(ItemServicoEntity.StatusItemServicoEntity.PENDENTE)
                .valorServico(new BigDecimal("100.00"))
                .valorMro(BigDecimal.ZERO)
                .build();

        // Use RECEBIDA status to allow adding items during reconstruction
        OrdemServicoEntity entity = OrdemServicoEntity.builder()
                .id(id)
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .status(StatusOSEntity.RECEBIDA)
                .dataCriacao(dataCriacao)
                .dataInicioExecucao(dataInicioExecucao)
                .dataFinalizacao(dataFinalizacao)
                .itensServico(List.of(itemEntity))
                .build();

        ItemServico itemServico = ItemServico.reconstruir(
                itemEntity.getId(),
                itemEntity.getServicoId(),
                "Serviço Teste",
                "Descrição",
                StatusItemServico.PENDENTE,
                null,
                itemEntity.getValorServico(),
                itemEntity.getValorMro()
        );

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));
        when(itemServicoJpaMapper.toDomainList(any())).thenReturn(List.of(itemServico));

        // Act
        OrdemServico ordemServico = mapper.toDomain(entity, clienteRepository, veiculoRepository);

        // Assert
        assertNotNull(ordemServico);
        assertEquals(1, ordemServico.getItensServico().size());
        assertEquals(itemServico, ordemServico.getItensServico().get(0));
        
        verify(itemServicoJpaMapper, times(1)).toDomainList(any());
    }

    @Test
    @DisplayName("Deve retornar null quando Entity é null")
    void deveRetornarNullQuandoEntityENull() {
        // Act
        OrdemServico ordemServico = mapper.toDomain(null, clienteRepository, veiculoRepository);

        // Assert
        assertNull(ordemServico);
        
        verify(clienteRepository, never()).findById(any());
        verify(veiculoRepository, never()).findById(any());
        verify(itemServicoJpaMapper, never()).toDomainList(any());
    }

    @Test
    @DisplayName("Deve converter Entity para OrdemServico quando cliente não encontrado")
    void deveConverterEntityParaOrdemServicoQuandoClienteNaoEncontrado() {
        // Arrange
        OrdemServicoEntity entity = OrdemServicoEntity.builder()
                .id(id)
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .status(StatusOSEntity.EM_EXECUCAO)
                .dataCriacao(dataCriacao)
                .dataInicioExecucao(dataInicioExecucao)
                .dataFinalizacao(dataFinalizacao)
                .build();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));

        // Act
        OrdemServico ordemServico = mapper.toDomain(entity, clienteRepository, veiculoRepository);

        // Assert
        assertNotNull(ordemServico);
        assertNull(ordemServico.getCliente());
        assertEquals(veiculo, ordemServico.getVeiculo());
        
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(veiculoRepository, times(1)).findById(veiculoId);
    }

    @Test
    @DisplayName("Deve converter Entity para OrdemServico quando veículo não encontrado")
    void deveConverterEntityParaOrdemServicoQuandoVeiculoNaoEncontrado() {
        // Arrange
        OrdemServicoEntity entity = OrdemServicoEntity.builder()
                .id(id)
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .status(StatusOSEntity.EM_EXECUCAO)
                .dataCriacao(dataCriacao)
                .dataInicioExecucao(dataInicioExecucao)
                .dataFinalizacao(dataFinalizacao)
                .build();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.empty());

        // Act
        OrdemServico ordemServico = mapper.toDomain(entity, clienteRepository, veiculoRepository);

        // Assert
        assertNotNull(ordemServico);
        assertEquals(cliente, ordemServico.getCliente());
        assertNull(ordemServico.getVeiculo());
        
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(veiculoRepository, times(1)).findById(veiculoId);
    }

    @Test
    @DisplayName("Deve converter OrdemServico com cliente e veículo null para Entity")
    void deveConverterOrdemServicoComClienteEVeiculoNullParaEntity() {
        // Arrange
        OrdemServico ordemServico = OrdemServico.reconstruir(
                id,
                null,
                null,
                statusOS,
                dataCriacao,
                dataInicioExecucao,
                dataFinalizacao
        );

        // Act
        OrdemServicoEntity entity = mapper.toEntity(ordemServico);

        // Assert
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertNull(entity.getClienteId());
        assertNull(entity.getVeiculoId());
        assertEquals(StatusOSEntity.EM_EXECUCAO, entity.getStatus());
    }

    @Test
    @DisplayName("Deve converter OrdemServico com status null para Entity")
    void deveConverterOrdemServicoComStatusNullParaEntity() {
        // Arrange
        OrdemServico ordemServico = OrdemServico.reconstruir(
                id,
                cliente,
                veiculo,
                null,
                dataCriacao,
                dataInicioExecucao,
                dataFinalizacao
        );

        // Act
        OrdemServicoEntity entity = mapper.toEntity(ordemServico);

        // Assert
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals(clienteId, entity.getClienteId());
        assertEquals(veiculoId, entity.getVeiculoId());
        assertNull(entity.getStatus());
    }

    @Test
    @DisplayName("Deve converter Entity com status null para OrdemServico")
    void deveConverterEntityComStatusNullParaOrdemServico() {
        // Arrange
        OrdemServicoEntity entity = OrdemServicoEntity.builder()
                .id(id)
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .status(null)
                .dataCriacao(dataCriacao)
                .dataInicioExecucao(dataInicioExecucao)
                .dataFinalizacao(dataFinalizacao)
                .build();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));

        // Act
        OrdemServico ordemServico = mapper.toDomain(entity, clienteRepository, veiculoRepository);

        // Assert
        assertNotNull(ordemServico);
        assertNull(ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve converter Entity com datas null para OrdemServico")
    void deveConverterEntityComDatasNullParaOrdemServico() {
        // Arrange
        OrdemServicoEntity entity = OrdemServicoEntity.builder()
                .id(id)
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .status(StatusOSEntity.RECEBIDA)
                .dataCriacao(null)
                .dataInicioExecucao(null)
                .dataFinalizacao(null)
                .build();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));

        // Act
        OrdemServico ordemServico = mapper.toDomain(entity, clienteRepository, veiculoRepository);

        // Assert
        assertNotNull(ordemServico);
        assertNull(ordemServico.getDataCriacao());
        assertNull(ordemServico.getDataInicioExecucao());
        assertNull(ordemServico.getDataFinalizacao());
    }

    @Test
    @DisplayName("Deve converter todos os status de OrdemServico para Entity")
    void deveConverterTodosOsStatusDeOrdemServicoParaEntity() {
        // Testando todos os status
        testStatusConversion(StatusOS.RECEBIDA, StatusOSEntity.RECEBIDA);
        testStatusConversion(StatusOS.EM_DIAGNOSTICO, StatusOSEntity.EM_DIAGNOSTICO);
        testStatusConversion(StatusOS.AGUARDANDO_APROVACAO, StatusOSEntity.AGUARDANDO_APROVACAO);
        testStatusConversion(StatusOS.EM_EXECUCAO, StatusOSEntity.EM_EXECUCAO);
        testStatusConversion(StatusOS.FINALIZADA, StatusOSEntity.FINALIZADA);
        testStatusConversion(StatusOS.ENTREGUE, StatusOSEntity.ENTREGUE);
    }

    private void testStatusConversion(StatusOS domainStatus, StatusOSEntity entityStatus) {
        // Arrange
        OrdemServico ordemServico = OrdemServico.reconstruir(
                UUID.randomUUID(),
                cliente,
                veiculo,
                domainStatus,
                dataCriacao,
                dataInicioExecucao,
                dataFinalizacao
        );

        // Act
        OrdemServicoEntity entity = mapper.toEntity(ordemServico);

        // Assert
        assertEquals(entityStatus, entity.getStatus());
    }

    @Test
    @DisplayName("Deve converter todos os status de Entity para OrdemServico")
    void deveConverterTodosOsStatusDeEntityParaOrdemServico() {
        // Testando todos os status
        testEntityStatusConversion(StatusOSEntity.RECEBIDA, StatusOS.RECEBIDA);
        testEntityStatusConversion(StatusOSEntity.EM_DIAGNOSTICO, StatusOS.EM_DIAGNOSTICO);
        testEntityStatusConversion(StatusOSEntity.AGUARDANDO_APROVACAO, StatusOS.AGUARDANDO_APROVACAO);
        testEntityStatusConversion(StatusOSEntity.EM_EXECUCAO, StatusOS.EM_EXECUCAO);
        testEntityStatusConversion(StatusOSEntity.FINALIZADA, StatusOS.FINALIZADA);
        testEntityStatusConversion(StatusOSEntity.ENTREGUE, StatusOS.ENTREGUE);
    }

    private void testEntityStatusConversion(StatusOSEntity entityStatus, StatusOS domainStatus) {
        // Arrange
        OrdemServicoEntity entity = OrdemServicoEntity.builder()
                .id(UUID.randomUUID())
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .status(entityStatus)
                .dataCriacao(dataCriacao)
                .build();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));

        // Act
        OrdemServico ordemServico = mapper.toDomain(entity, clienteRepository, veiculoRepository);

        // Assert
        assertEquals(domainStatus, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve converter OrdemServico com lista vazia de itens para Entity")
    void deveConverterOrdemServicoComListaVaziaDeItensParaEntity() {
        // Arrange
        OrdemServico ordemServico = OrdemServico.reconstruir(
                id,
                cliente,
                veiculo,
                statusOS,
                dataCriacao,
                dataInicioExecucao,
                dataFinalizacao
        );

        // Act
        OrdemServicoEntity entity = mapper.toEntity(ordemServico);

        // Assert
        assertNotNull(entity);
        assertNotNull(entity.getItensServico());
        assertTrue(entity.getItensServico().isEmpty());
        
        verify(itemServicoJpaMapper, never()).toEntityList(any(), any());
    }

    @Test
    @DisplayName("Deve converter Entity com lista vazia de itens para OrdemServico")
    void deveConverterEntityComListaVaziaDeItensParaOrdemServico() {
        // Arrange
        OrdemServicoEntity entity = OrdemServicoEntity.builder()
                .id(id)
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .status(StatusOSEntity.EM_EXECUCAO)
                .dataCriacao(dataCriacao)
                .dataInicioExecucao(dataInicioExecucao)
                .dataFinalizacao(dataFinalizacao)
                .itensServico(List.of())
                .build();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));

        // Act
        OrdemServico ordemServico = mapper.toDomain(entity, clienteRepository, veiculoRepository);

        // Assert
        assertNotNull(ordemServico);
        assertNotNull(ordemServico.getItensServico());
        assertTrue(ordemServico.getItensServico().isEmpty());
        
        verify(itemServicoJpaMapper, never()).toDomainList(any());
    }
}
