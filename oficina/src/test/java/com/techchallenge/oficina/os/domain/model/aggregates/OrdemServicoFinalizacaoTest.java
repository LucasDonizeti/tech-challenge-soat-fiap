package com.techchallenge.oficina.os.domain.model.aggregates;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Finalização de Ordem de Serviço")
class OrdemServicoFinalizacaoTest {

    private Cliente cliente;
    private Veiculo veiculo;
    private OrdemServico ordemServico;
    private ItemServico itemServico1;
    private ItemServico itemServico2;

    @BeforeEach
    void setUp() {
        cliente = Cliente.criar(Nome.of("João Silva"), CPF.of("12345678909"), Email.of("joao@email.com"));
        veiculo = new Veiculo(Placa.of("ABC1234"), "Toyota", "Corolla", 2020, "Prata");
        veiculo.setCliente(cliente);
        ordemServico = OrdemServico.criar(cliente, veiculo);
        
        itemServico1 = ItemServico.criarComDados(UUID.randomUUID(), "Troca de Óleo", "Troca completa", new BigDecimal("150.00"));
        itemServico2 = ItemServico.criarComDados(UUID.randomUUID(), "Revisão Freios", "Troca de pastilhas", new BigDecimal("200.00"));
        
        ordemServico.adicionarItemServico(itemServico1);
        ordemServico.adicionarItemServico(itemServico2);
    }

    @Test
    @DisplayName("Deve definir dataInicioExecucao ao aprovar orçamento")
    void deveDefinirDataInicioExecucaoAoAprovarOrcamento() {
        // Arrange
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        
        // Act
        ordemServico.aprovarOrcamento();
        
        // Assert
        assertNotNull(ordemServico.getDataInicioExecucao());
        assertEquals(StatusOS.EM_EXECUCAO, ordemServico.getStatus());
        assertTrue(ordemServico.getDataInicioExecucao().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(ordemServico.getDataInicioExecucao().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    @DisplayName("Deve finalizar OS com sucesso quando todos serviços estão CONCLUIDO")
    void deveFinalizarOSSucesso() {
        // Arrange
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();
        
        itemServico1.atualizarStatus(StatusItemServico.EM_ANDAMENTO);
        itemServico1.atualizarStatus(StatusItemServico.CONCLUIDO);
        itemServico2.atualizarStatus(StatusItemServico.EM_ANDAMENTO);
        // Não conclui o último serviço para evitar finalização automática
        
        // Act - concluir último serviço (isso deve finalizar automaticamente)
        itemServico2.atualizarStatus(StatusItemServico.CONCLUIDO);
        
        // Assert
        assertEquals(StatusOS.FINALIZADA, ordemServico.getStatus());
        assertNotNull(ordemServico.getDataFinalizacao());
        assertTrue(ordemServico.getDataFinalizacao().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(ordemServico.getDataFinalizacao().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar finalizar OS com status inválido")
    void deveLancarExcecaoAoFinalizarComStatusInvalido() {
        // Arrange
        ordemServico.enviarParaDiagnostico();
        
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.finalizar()
        );
        
        assertTrue(exception.getMessage().contains("Só é possível finalizar quando a OS está no status EM_EXECUCAO"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar finalizar OS com serviços não concluídos")
    void deveLancarExcecaoAoFinalizarComServicosNaoConcluidos() {
        // Arrange
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();
        
        itemServico1.atualizarStatus(StatusItemServico.CONCLUIDO);
        // itemServico2 ainda está PENDENTE
        
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> ordemServico.finalizar()
        );
        
        assertTrue(exception.getMessage().contains("OS só pode ser finalizada quando todos os serviços estiverem CONCLUIDO"));
    }

    @Test
    @DisplayName("Deve finalizar automaticamente ao concluir último serviço")
    void deveFinalizarAutomaticamenteAoConcluirUltimoServico() {
        // Arrange
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();
        
        itemServico1.atualizarStatus(StatusItemServico.EM_ANDAMENTO);
        itemServico1.atualizarStatus(StatusItemServico.CONCLUIDO);
        itemServico2.atualizarStatus(StatusItemServico.EM_ANDAMENTO);
        
        assertEquals(StatusOS.EM_EXECUCAO, ordemServico.getStatus());
        
        // Act - concluir último serviço
        itemServico2.atualizarStatus(StatusItemServico.CONCLUIDO);
        
        // Assert - finalização automática
        assertEquals(StatusOS.FINALIZADA, ordemServico.getStatus());
        assertNotNull(ordemServico.getDataFinalizacao());
    }

    @Test
    @DisplayName("Não deve finalizar automaticamente se ainda há serviços pendentes")
    void naoDeveFinalizarAutomaticamenteSeHaServicosPendentes() {
        // Arrange
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();
        
        itemServico1.atualizarStatus(StatusItemServico.CONCLUIDO);
        // itemServico2 ainda PENDENTE
        
        // Act
        itemServico1.atualizarStatus(StatusItemServico.CONCLUIDO);
        
        // Assert
        assertEquals(StatusOS.EM_EXECUCAO, ordemServico.getStatus());
        assertNull(ordemServico.getDataFinalizacao());
    }

    @Test
    @DisplayName("Deve verificarEFinalizarAutomaticamente não fazer nada se não pode finalizar")
    void deveVerificarEFinalizarNaoFazerNadaSeNaoPodeFinalizar() {
        // Arrange
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();
        
        itemServico1.atualizarStatus(StatusItemServico.CONCLUIDO);
        
        // Act
        ordemServico.verificarEFinalizarAutomaticamente();
        
        // Assert
        assertEquals(StatusOS.EM_EXECUCAO, ordemServico.getStatus());
        assertNull(ordemServico.getDataFinalizacao());
    }

    @Test
    @DisplayName("Deve verificarEFinalizarAutomaticamente finalizar se pode finalizar")
    void deveVerificarEFinalizarFinalizarSePodeFinalizar() {
        // Arrange
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();
        
        itemServico1.atualizarStatus(StatusItemServico.CONCLUIDO);
        itemServico2.atualizarStatus(StatusItemServico.CONCLUIDO);
        
        // Act
        ordemServico.verificarEFinalizarAutomaticamente();
        
        // Assert
        assertEquals(StatusOS.FINALIZADA, ordemServico.getStatus());
        assertNotNull(ordemServico.getDataFinalizacao());
    }

    @Test
    @DisplayName("Deve manter timestamps após reconstrução")
    void deveManterTimestampsAposReconstrucao() {
        // Arrange
        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();
        
        LocalDateTime dataInicio = ordemServico.getDataInicioExecucao();
        
        itemServico1.atualizarStatus(StatusItemServico.CONCLUIDO);
        itemServico2.atualizarStatus(StatusItemServico.CONCLUIDO);
        
        LocalDateTime dataFinal = ordemServico.getDataFinalizacao();
        
        // Act
        OrdemServico reconstruida = OrdemServico.reconstruir(
                ordemServico.getId(),
                ordemServico.getCliente(),
                ordemServico.getVeiculo(),
                ordemServico.getStatus(),
                ordemServico.getDataCriacao(),
                ordemServico.getDataInicioExecucao(),
                ordemServico.getDataFinalizacao()
        );
        
        // Assert
        assertEquals(dataInicio, reconstruida.getDataInicioExecucao());
        assertEquals(dataFinal, reconstruida.getDataFinalizacao());
    }
}
