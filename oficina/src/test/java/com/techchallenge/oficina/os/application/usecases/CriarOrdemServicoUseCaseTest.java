package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoClienteException;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoVeiculoException;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.os.application.usecases.commands.CriarOrdemServicoCommand;
import com.techchallenge.oficina.os.application.usecases.ports.output.ClienteGateway;
import com.techchallenge.oficina.os.application.usecases.ports.output.OrdemServicoGateway;
import com.techchallenge.oficina.os.application.usecases.ports.output.VeiculoGateway;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.infrastructure.acl.dto.ServicoIntegrationDto;
import com.techchallenge.oficina.os.infrastructure.acl.mro.MROAdapter;
import com.techchallenge.oficina.os.infrastructure.acl.servico.ServicoAdapter;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de CriarOrdemServicoUseCase - Application Layer")
class CriarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private ClienteGateway clienteGateway;

    @Mock
    private VeiculoGateway veiculoGateway;

    @Mock
    private ServicoAdapter servicoAdapter;

    @Mock
    private MROAdapter mroAdapter;

    private CriarOrdemServicoUseCase useCase;

    private CriarOrdemServicoCommand command;
    private CriarOrdemServicoCommand commandSemMRO;
    private Cliente cliente;
    private Veiculo veiculo;
    private ServicoIntegrationDto servicoDto;

    private static final String CPF_VALIDO = "52998224725";
    private static final String PLACA_VALIDA = "ABC1234";
    private static final String CODIGO_SERVICO = "SVC-001";

    @BeforeEach
    void setUp() {
        useCase = new CriarOrdemServicoUseCase(
                ordemServicoGateway, clienteGateway, veiculoGateway, servicoAdapter, mroAdapter);

        command = new CriarOrdemServicoCommand(
                CPF_VALIDO, PLACA_VALIDA,
                List.of(CODIGO_SERVICO),
                List.of());

        cliente = Cliente.criar(
                Nome.of("João Silva"),
                CPF.of(CPF_VALIDO),
                Email.of("joao@email.com"));

        veiculo = new Veiculo(Placa.of(PLACA_VALIDA), "Fiat", "Uno", 2020, "Branco");
        veiculo.setCliente(cliente);

        servicoDto = ServicoIntegrationDto.builder()
                .id(java.util.UUID.randomUUID())
                .nome("Troca de Óleo")
                .codigo(CODIGO_SERVICO)
                .descricao("Troca completa de óleo")
                .preco(new BigDecimal("150.00"))
                .ativo(true)
                .build();
    }

    @Test
    @DisplayName("Deve criar ordem de serviço com sucesso buscando cliente por CPF")
    void deveCriarOrdemServicoComSucessoPorCPF() {
        // Arrange
        OrdemServico osEsperada = OrdemServico.criar(cliente, veiculo);
        osEsperada.adicionarItemServico(
                com.techchallenge.oficina.os.domain.model.entities.ItemServico.criarComDados(
                        servicoDto.getId(), servicoDto.getNome(),
                        servicoDto.getDescricao(), servicoDto.getPreco()));

        when(clienteGateway.findByCPF(CPF.of(CPF_VALIDO))).thenReturn(Optional.of(cliente));
        when(veiculoGateway.findByPlaca(Placa.of(PLACA_VALIDA))).thenReturn(Optional.of(veiculo));
        when(servicoAdapter.buscarPorCodigo(CODIGO_SERVICO)).thenReturn(Optional.of(servicoDto));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(osEsperada);

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        assertNotNull(response.getStatus());
        assertEquals(StatusOS.RECEBIDA.name(), response.getStatus());
        assertNotNull(response.getValorTotal());
        assertTrue(response.getValorTotal().compareTo(BigDecimal.ZERO) > 0);

        verify(clienteGateway).findByCPF(CPF.of(CPF_VALIDO));
        verify(veiculoGateway).findByPlaca(Placa.of(PLACA_VALIDA));
        verify(servicoAdapter).buscarPorCodigo(CODIGO_SERVICO);
        verify(ordemServicoGateway).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não encontrado pelo CPF")
    void deveLancarExcecaoQuandoClienteNaoEncontradoPorCPF() {
        // Arrange
        when(clienteGateway.findByCPF(CPF.of(CPF_VALIDO))).thenReturn(Optional.empty());

        // Act & Assert
        ValidacaoClienteException ex = assertThrows(
                ValidacaoClienteException.class,
                () -> useCase.execute(command));

        assertTrue(ex.getMessage().contains("Cliente não encontrado"));

        verify(clienteGateway).findByCPF(CPF.of(CPF_VALIDO));
        verify(veiculoGateway, never()).findByPlaca(any());
        verify(ordemServicoGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando veículo não encontrado pela placa")
    void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        // Arrange
        when(clienteGateway.findByCPF(CPF.of(CPF_VALIDO))).thenReturn(Optional.of(cliente));
        when(veiculoGateway.findByPlaca(Placa.of(PLACA_VALIDA))).thenReturn(Optional.empty());

        // Act & Assert
        ValidacaoVeiculoException ex = assertThrows(
                ValidacaoVeiculoException.class,
                () -> useCase.execute(command));

        assertTrue(ex.getMessage().contains("Veículo não encontrado"));

        verify(veiculoGateway).findByPlaca(Placa.of(PLACA_VALIDA));
        verify(ordemServicoGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando serviço não encontrado pelo código")
    void deveLancarExcecaoQuandoServicoNaoEncontrado() {
        // Arrange
        when(clienteGateway.findByCPF(CPF.of(CPF_VALIDO))).thenReturn(Optional.of(cliente));
        when(veiculoGateway.findByPlaca(Placa.of(PLACA_VALIDA))).thenReturn(Optional.of(veiculo));
        when(servicoAdapter.buscarPorCodigo(CODIGO_SERVICO)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command));

        assertTrue(ex.getMessage().contains("Serviço não encontrado"));

        verify(servicoAdapter).buscarPorCodigo(CODIGO_SERVICO);
        verify(ordemServicoGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando serviço está inativo")
    void deveLancarExcecaoQuandoServicoInativo() {
        // Arrange
        ServicoIntegrationDto servicoInativo = ServicoIntegrationDto.builder()
                .id(java.util.UUID.randomUUID())
                .nome("Serviço Inativo")
                .codigo(CODIGO_SERVICO)
                .preco(new BigDecimal("100.00"))
                .ativo(false)
                .build();

        when(clienteGateway.findByCPF(CPF.of(CPF_VALIDO))).thenReturn(Optional.of(cliente));
        when(veiculoGateway.findByPlaca(Placa.of(PLACA_VALIDA))).thenReturn(Optional.of(veiculo));
        when(servicoAdapter.buscarPorCodigo(CODIGO_SERVICO)).thenReturn(Optional.of(servicoInativo));

        // Act & Assert
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command));

        assertTrue(ex.getMessage().contains("inativo"));

        verify(ordemServicoGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando lista de serviços é vazia")
    void deveLancarExcecaoQuandoListaDeServicosVazia() {
        // Act & Assert — validação acontece no construtor do command
        assertThrows(
                com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException.class,
                () -> new CriarOrdemServicoCommand(CPF_VALIDO, PLACA_VALIDA, List.of(), List.of()));

        verify(ordemServicoGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF/CNPJ é inválido")
    void deveLancarExcecaoQuandoCpfCnpjInvalido() {
        // Arrange
        CriarOrdemServicoCommand commandInvalido = new CriarOrdemServicoCommand(
                "123", PLACA_VALIDA, List.of(CODIGO_SERVICO), List.of());

        // Act & Assert — identificação com tamanho inválido
        assertThrows(
                com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException.class,
                () -> useCase.execute(commandInvalido));
    }

    @Test
    @DisplayName("Deve propagar exceção quando gateway falha ao salvar")
    void devePropagarExcecaoQuandoGatewayFalha() {
        // Arrange
        when(clienteGateway.findByCPF(CPF.of(CPF_VALIDO))).thenReturn(Optional.of(cliente));
        when(veiculoGateway.findByPlaca(Placa.of(PLACA_VALIDA))).thenReturn(Optional.of(veiculo));
        when(servicoAdapter.buscarPorCodigo(CODIGO_SERVICO)).thenReturn(Optional.of(servicoDto));
        when(ordemServicoGateway.save(any(OrdemServico.class)))
                .thenThrow(new RuntimeException("Erro ao salvar no banco"));

        // Act & Assert
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command));

        assertEquals("Erro ao salvar no banco", ex.getMessage());

        verify(ordemServicoGateway).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve retornar resposta com orçamento calculado")
    void deveRetornarRespostaComOrcamentoCalculado() {
        // Arrange
        OrdemServico osComServico = OrdemServico.criar(cliente, veiculo);
        osComServico.adicionarItemServico(
                com.techchallenge.oficina.os.domain.model.entities.ItemServico.criarComDados(
                        servicoDto.getId(), servicoDto.getNome(),
                        servicoDto.getDescricao(), servicoDto.getPreco()));

        when(clienteGateway.findByCPF(CPF.of(CPF_VALIDO))).thenReturn(Optional.of(cliente));
        when(veiculoGateway.findByPlaca(Placa.of(PLACA_VALIDA))).thenReturn(Optional.of(veiculo));
        when(servicoAdapter.buscarPorCodigo(CODIGO_SERVICO)).thenReturn(Optional.of(servicoDto));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(osComServico);

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response.getValorTotal());
        assertEquals(new BigDecimal("150.00"), response.getValorTotal());
        assertFalse(response.getItensServico().isEmpty());
    }

    @Test
    @DisplayName("Deve criar OS com múltiplos serviços")
    void deveCriarOsComMultiplosServicos() {
        // Arrange
        String codigoServico2 = "SVC-002";
        ServicoIntegrationDto servico2 = ServicoIntegrationDto.builder()
                .id(java.util.UUID.randomUUID())
                .nome("Alinhamento")
                .codigo(codigoServico2)
                .descricao("Alinhamento de rodas")
                .preco(new BigDecimal("80.00"))
                .ativo(true)
                .build();

        CriarOrdemServicoCommand commandMultiplos = new CriarOrdemServicoCommand(
                CPF_VALIDO, PLACA_VALIDA,
                List.of(CODIGO_SERVICO, codigoServico2),
                List.of());

        OrdemServico osMultiplos = OrdemServico.criar(cliente, veiculo);
        osMultiplos.adicionarItemServico(
                com.techchallenge.oficina.os.domain.model.entities.ItemServico.criarComDados(
                        servicoDto.getId(), servicoDto.getNome(),
                        servicoDto.getDescricao(), servicoDto.getPreco()));
        osMultiplos.adicionarItemServico(
                com.techchallenge.oficina.os.domain.model.entities.ItemServico.criarComDados(
                        servico2.getId(), servico2.getNome(),
                        servico2.getDescricao(), servico2.getPreco()));

        when(clienteGateway.findByCPF(CPF.of(CPF_VALIDO))).thenReturn(Optional.of(cliente));
        when(veiculoGateway.findByPlaca(Placa.of(PLACA_VALIDA))).thenReturn(Optional.of(veiculo));
        when(servicoAdapter.buscarPorCodigo(CODIGO_SERVICO)).thenReturn(Optional.of(servicoDto));
        when(servicoAdapter.buscarPorCodigo(codigoServico2)).thenReturn(Optional.of(servico2));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(osMultiplos);

        // Act
        OrdemServicoResponse response = useCase.execute(commandMultiplos);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getItensServico().size());
        assertEquals(new BigDecimal("230.00"), response.getValorTotal());

        verify(servicoAdapter).buscarPorCodigo(CODIGO_SERVICO);
        verify(servicoAdapter).buscarPorCodigo(codigoServico2);
    }

    @Test
    @DisplayName("Deve criar OS com cliente PJ usando CNPJ")
    void deveCriarOsComClientePJ() {
        // Arrange
        Cliente clientePJ = Cliente.criarPJ(
                Nome.of("Empresa LTDA"),
                com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ.of("11444777000161"),
                Email.of("empresa@email.com")
        );

        String cnpj = "11444777000161";
        CriarOrdemServicoCommand commandCNPJ = new CriarOrdemServicoCommand(
                cnpj, PLACA_VALIDA, List.of(CODIGO_SERVICO), List.of());

        OrdemServico osPJ = OrdemServico.criar(clientePJ, veiculo);
        osPJ.adicionarItemServico(
                com.techchallenge.oficina.os.domain.model.entities.ItemServico.criarComDados(
                        servicoDto.getId(), servicoDto.getNome(),
                        servicoDto.getDescricao(), servicoDto.getPreco()));

        when(clienteGateway.findByCNPJ(com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ.of(cnpj)))
                .thenReturn(Optional.of(clientePJ));
        when(veiculoGateway.findByPlaca(Placa.of(PLACA_VALIDA))).thenReturn(Optional.of(veiculo));
        when(servicoAdapter.buscarPorCodigo(CODIGO_SERVICO)).thenReturn(Optional.of(servicoDto));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(osPJ);

        // Act
        OrdemServicoResponse response = useCase.execute(commandCNPJ);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        verify(clienteGateway).findByCNPJ(any(com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ.class));
        verify(clienteGateway, never()).findByCPF(any(com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando documento tem tamanho inválido")
    void deveLancarExcecaoQuandoDocumentoTamanhoInvalido() {
        // Arrange
        CriarOrdemServicoCommand commandInvalido = new CriarOrdemServicoCommand(
                "123456789", PLACA_VALIDA, List.of(CODIGO_SERVICO), List.of());

        // Act & Assert
        com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException ex = assertThrows(
                com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException.class,
                () -> useCase.execute(commandInvalido));

        assertTrue(ex.getMessage().contains("Identificação inválida"));
    }

    @Test
    @DisplayName("Deve criar OS com itens MRO")
    void deveCriarOsComItensMRO() {
        // Arrange
        String codigoMro = "PC001";
        com.techchallenge.oficina.os.infrastructure.acl.dto.MROIntegrationDto mroDto =
                com.techchallenge.oficina.os.infrastructure.acl.dto.MROIntegrationDto.builder()
                        .id(UUID.randomUUID())
                        .nome("Filtro de Óleo")
                        .codigo(codigoMro)
                        .descricao("Filtro para motor")
                        .precoUnitario(new BigDecimal("25.00"))
                        .ativo(true)
                        .build();

        CriarOrdemServicoCommand commandMRO = new CriarOrdemServicoCommand(
                CPF_VALIDO, PLACA_VALIDA,
                List.of(CODIGO_SERVICO),
                List.of(new CriarOrdemServicoCommand.ItemMROCommand(codigoMro, 2)));

        OrdemServico osComMRO = OrdemServico.criar(cliente, veiculo);
        com.techchallenge.oficina.os.domain.model.entities.ItemServico itemServico =
                com.techchallenge.oficina.os.domain.model.entities.ItemServico.criarComDados(
                        servicoDto.getId(), servicoDto.getNome(),
                        servicoDto.getDescricao(), servicoDto.getPreco());
        itemServico.adicionarMRO(mroDto.getId(), mroDto.getNome(), mroDto.getDescricao(),
                mroDto.getPrecoUnitario(), 2);
        osComMRO.adicionarItemServico(itemServico);

        when(clienteGateway.findByCPF(CPF.of(CPF_VALIDO))).thenReturn(Optional.of(cliente));
        when(veiculoGateway.findByPlaca(Placa.of(PLACA_VALIDA))).thenReturn(Optional.of(veiculo));
        when(servicoAdapter.buscarPorCodigo(CODIGO_SERVICO)).thenReturn(Optional.of(servicoDto));
        when(mroAdapter.buscarPorCodigo(codigoMro)).thenReturn(Optional.of(mroDto));
        when(ordemServicoGateway.save(any(OrdemServico.class))).thenReturn(osComMRO);

        // Act
        OrdemServicoResponse response = useCase.execute(commandMRO);

        // Assert
        assertNotNull(response);
        verify(mroAdapter).buscarPorCodigo(codigoMro);
        assertFalse(response.getItensServico().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção quando MRO não encontrado")
    void deveLancarExcecaoQuandoMRONaoEncontrado() {
        // Arrange
        String codigoMro = "PC999";
        CriarOrdemServicoCommand commandMRO = new CriarOrdemServicoCommand(
                CPF_VALIDO, PLACA_VALIDA,
                List.of(CODIGO_SERVICO),
                List.of(new CriarOrdemServicoCommand.ItemMROCommand(codigoMro, 1)));

        when(clienteGateway.findByCPF(CPF.of(CPF_VALIDO))).thenReturn(Optional.of(cliente));
        when(veiculoGateway.findByPlaca(Placa.of(PLACA_VALIDA))).thenReturn(Optional.of(veiculo));
        when(servicoAdapter.buscarPorCodigo(CODIGO_SERVICO)).thenReturn(Optional.of(servicoDto));
        when(mroAdapter.buscarPorCodigo(codigoMro)).thenReturn(Optional.empty());

        // Act & Assert
        com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException ex = assertThrows(
                com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException.class,
                () -> useCase.execute(commandMRO));

        assertTrue(ex.getMessage().contains("MRO não encontrado"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando MRO está inativo")
    void deveLancarExcecaoQuandoMROInativo() {
        // Arrange
        String codigoMro = "PC001";
        com.techchallenge.oficina.os.infrastructure.acl.dto.MROIntegrationDto mroInativo =
                com.techchallenge.oficina.os.infrastructure.acl.dto.MROIntegrationDto.builder()
                        .id(UUID.randomUUID())
                        .nome("Filtro de Óleo")
                        .codigo(codigoMro)
                        .precoUnitario(new BigDecimal("25.00"))
                        .ativo(false)
                        .build();

        CriarOrdemServicoCommand commandMRO = new CriarOrdemServicoCommand(
                CPF_VALIDO, PLACA_VALIDA,
                List.of(CODIGO_SERVICO),
                List.of(new CriarOrdemServicoCommand.ItemMROCommand(codigoMro, 1)));

        when(clienteGateway.findByCPF(CPF.of(CPF_VALIDO))).thenReturn(Optional.of(cliente));
        when(veiculoGateway.findByPlaca(Placa.of(PLACA_VALIDA))).thenReturn(Optional.of(veiculo));
        when(servicoAdapter.buscarPorCodigo(CODIGO_SERVICO)).thenReturn(Optional.of(servicoDto));
        when(mroAdapter.buscarPorCodigo(codigoMro)).thenReturn(Optional.of(mroInativo));

        // Act & Assert
        com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException ex = assertThrows(
                com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException.class,
                () -> useCase.execute(commandMRO));

        assertTrue(ex.getMessage().contains("MRO inativo"));
    }
}
