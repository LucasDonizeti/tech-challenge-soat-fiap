package com.techchallenge.oficina.os.domain.model.aggregates;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de OrdemServico - Domain Layer")
class OrdemServicoTest {

    private Cliente cliente;
    private Veiculo veiculo;
    private OrdemServico ordemServico;
    private ItemServico itemServico;

    @BeforeEach
    void setUp() {
        cliente = Cliente.criar(Nome.of("João Silva"), CPF.of("52998224725"), Email.of("joao@email.com"));
        veiculo = new Veiculo(Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Branco");
        veiculo.setCliente(cliente);

        ordemServico = OrdemServico.criar(cliente, veiculo);

        UUID servicoId = UUID.randomUUID();
        itemServico = ItemServico.criarComDados(servicoId, "Troca de Óleo", "Troca completa de óleo", new BigDecimal("150.00"));
        ordemServico.adicionarItemServico(itemServico);
    }

    @Test
    @DisplayName("Deve enviar orçamento ao cliente com sucesso")
    void deveEnviarOrcamentoAoClienteComSucesso() {
        // Arrange
        ordemServico.enviarParaDiagnostico();
        assertEquals(StatusOS.EM_DIAGNOSTICO, ordemServico.getStatus());

        // Act
        ordemServico.enviarOrcamentoAoCliente();

        // Assert
        assertEquals(StatusOS.AGUARDANDO_APROVACAO, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar orçamento quando status não é EM_DIAGNOSTICO")
    void deveLancarExcecaoAoEnviarOrcamentoQuandoStatusNaoEMDiagnostico() {
        // Arrange
        assertEquals(StatusOS.RECEBIDA, ordemServico.getStatus());

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.enviarOrcamentoAoCliente()
        );

        assertTrue(exception.getMessage().contains("Só é possível enviar orçamento ao cliente quando a OS está no status EM_DIAGNOSTICO"));
        assertEquals(StatusOS.RECEBIDA, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar orçamento quando não há serviços")
    void deveLancarExcecaoAoEnviarOrcamentoQuandoNaoHaServicos() {
        // Arrange
        Cliente cliente2 = Cliente.criar(Nome.of("Maria Silva"), CPF.of("98765432100"), Email.of("maria@email.com"));
        Veiculo veiculo2 = new Veiculo(Placa.of("XYZ5678"), "Chevrolet", "Onix", 2021, "Prata");
        veiculo2.setCliente(cliente2);
        OrdemServico ordemServicoSemServicos = OrdemServico.criar(cliente2, veiculo2);
        // Simula que a OS já está em EM_DIAGNOSTICO mas sem serviços (caso edge)
        ordemServicoSemServicos.atualizarStatus(StatusOS.EM_DIAGNOSTICO);

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServicoSemServicos.enviarOrcamentoAoCliente()
        );

        assertTrue(exception.getMessage().contains("OS deve ter pelo menos um serviço para enviar orçamento ao cliente"));
    }

    @Test
    @DisplayName("Deve aprovar orçamento com sucesso")
    void deveAprovarOrcamentoComSucesso() {
        // Arrange
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        assertEquals(StatusOS.AGUARDANDO_APROVACAO, ordemServico.getStatus());

        // Act
        ordemServico.aprovarOrcamento();

        // Assert
        assertEquals(StatusOS.EM_EXECUCAO, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao aprovar orçamento quando status não é AGUARDANDO_APROVACAO")
    void deveLancarExcecaoAoAprovarOrcamentoQuandoStatusNaoAguardandoAprovacao() {
        // Arrange
        assertEquals(StatusOS.RECEBIDA, ordemServico.getStatus());

        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.aprovarOrcamento()
        );

        assertTrue(exception.getMessage().contains("Só é possível aprovar orçamento quando a OS está no status AGUARDANDO_APROVACAO"));
        assertEquals(StatusOS.RECEBIDA, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve manter fluxo completo de status corretamente")
    void deveManterFluxoCompletoDeStatusCorretamente() {
        // Arrange - Status inicial
        assertEquals(StatusOS.RECEBIDA, ordemServico.getStatus());

        // Act & Assert - Enviar para diagnóstico
        ordemServico.enviarParaDiagnostico();
        assertEquals(StatusOS.EM_DIAGNOSTICO, ordemServico.getStatus());

        // Act & Assert - Enviar orçamento ao cliente
        ordemServico.enviarOrcamentoAoCliente();
        assertEquals(StatusOS.AGUARDANDO_APROVACAO, ordemServico.getStatus());

        // Act & Assert - Aprovar orçamento
        ordemServico.aprovarOrcamento();
        assertEquals(StatusOS.EM_EXECUCAO, ordemServico.getStatus());
    }
}
