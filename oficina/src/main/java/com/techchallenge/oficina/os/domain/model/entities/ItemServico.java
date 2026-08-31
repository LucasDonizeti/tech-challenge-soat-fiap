package com.techchallenge.oficina.os.domain.model.entities;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusItemServico;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Slf4j
public class ItemServico {
    
    private UUID id;
    private UUID servicoId;
    private String servicoNome;
    private String servicoDescricao;
    private StatusItemServico status;
    private String observacoes;
    private BigDecimal valorServico;
    private BigDecimal valorMro;
    private LocalDateTime dataInicioExecucao;
    private LocalDateTime dataFinalizacao;
    private OrdemServico ordemServico;
    private List<ItemMRO> mrosServicos = new ArrayList<>();
    
    // Construtor padrão para JPA
    protected ItemServico() {}
    
    // Factory method para criação com dados do serviço (ACL)
    public static ItemServico criarComDados(UUID servicoId, String nome, String descricao, BigDecimal preco) {
        if (servicoId == null) {
            throw new ValidacaoOrdemServicoException("ID do serviço não pode ser nulo");
        }

        ItemServico itemServico = new ItemServico();
        itemServico.id = UUID.randomUUID();
        itemServico.servicoId = servicoId;
        itemServico.servicoNome = nome;
        itemServico.servicoDescricao = descricao;
        itemServico.status = StatusItemServico.PENDENTE;
        itemServico.observacoes = null;
        itemServico.valorServico = preco;
        itemServico.valorMro = BigDecimal.ZERO;

        itemServico.logMudancaStatus(null, itemServico.status, LocalDateTime.now());

        return itemServico;
    }
    
    // Factory method para reconstrução a partir de dados persistidos
    public static ItemServico reconstruir(UUID id, UUID servicoId, String servicoNome, String servicoDescricao, 
                                          StatusItemServico status, String observacoes, 
                                          BigDecimal valorServico, BigDecimal valorMro,
                                          LocalDateTime dataInicioExecucao, LocalDateTime dataFinalizacao) {
        ItemServico itemServico = new ItemServico();
        itemServico.id = id;
        itemServico.servicoId = servicoId;
        itemServico.servicoNome = servicoNome;
        itemServico.servicoDescricao = servicoDescricao;
        itemServico.status = status;
        itemServico.observacoes = observacoes;
        itemServico.valorServico = valorServico;
        itemServico.valorMro = valorMro;
        itemServico.dataInicioExecucao = dataInicioExecucao;
        itemServico.dataFinalizacao = dataFinalizacao;
        
        return itemServico;
    }
    
    // Comportamentos de negócio
    public void adicionarMRO(UUID mroId, String mroNome, String mroDescricao, BigDecimal precoUnitario, Integer quantidade) {
        if (mroId == null) {
            throw new ValidacaoOrdemServicoException("ID do MRO não pode ser nulo");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new ValidacaoOrdemServicoException("Quantidade deve ser maior que zero");
        }
        
        ItemMRO itemMRO = ItemMRO.criarComDados(mroId, mroNome, mroDescricao, precoUnitario, quantidade);
        mrosServicos.add(itemMRO);
        atualizarValorMRO();
    }
    
    // Método para adicionar ItemMRO já construído (usado em reconstrução)
    public void adicionarMRO(ItemMRO itemMRO) {
        if (itemMRO == null) {
            throw new ValidacaoOrdemServicoException("ItemMRO não pode ser nulo");
        }
        mrosServicos.add(itemMRO);
        atualizarValorMRO();
    }
    
    public void removerMRO(UUID itemMroId) {
        mrosServicos.removeIf(item -> item.getId().equals(itemMroId));
        atualizarValorMRO();
    }
    
    public void atualizarStatus(StatusItemServico novoStatus) {
        if (novoStatus == null) {
            throw new ValidacaoOrdemServicoException("Status não pode ser nulo");
        }

        StatusItemServico statusAnterior = this.status;

        // Registrar data de início quando status muda para EM_ANDAMENTO
        if (novoStatus == StatusItemServico.EM_ANDAMENTO && this.status != StatusItemServico.EM_ANDAMENTO) {
            this.dataInicioExecucao = LocalDateTime.now();
        }

        // Registrar data de finalização quando status muda para CONCLUIDO
        if (novoStatus == StatusItemServico.CONCLUIDO && this.status != StatusItemServico.CONCLUIDO) {
            this.dataFinalizacao = LocalDateTime.now();
        }

        this.status = novoStatus;

        logMudancaStatus(statusAnterior, this.status, LocalDateTime.now());

        // Verificar se todos os serviços estão concluídos para finalizar automaticamente
        if (novoStatus == StatusItemServico.CONCLUIDO && ordemServico != null) {
            ordemServico.verificarEFinalizarAutomaticamente();
        }
    }
    
    public void atualizarObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public void atualizarValorMRO() {
        BigDecimal totalMro = mrosServicos.stream()
                .map(ItemMRO::calcularValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.valorMro = totalMro;
    }
    
    public void atualizarValorServico(BigDecimal novoPreco) {
        if (novoPreco != null) {
            this.valorServico = novoPreco;
        }
    }

    private void logMudancaStatus(StatusItemServico statusAnterior, StatusItemServico statusAtual, LocalDateTime timestamp) {
        log.info("Status do ItemServico alterado - ItemServico ID: {}, OS ID: {}, Status Anterior: {}, Status Atual: {}, Timestamp: {}",
                this.id, ordemServico != null ? ordemServico.getId() : "N/A", statusAnterior != null ? statusAnterior : "N/A", statusAtual, timestamp);
    }
    
    public BigDecimal calcularValorTotal() {
        return valorServico.add(valorMro);
    }
    
    // Setter para JPA e uso interno
    public void setOrdemServico(OrdemServico ordemServico) {
        this.ordemServico = ordemServico;
    }
    
    // Equals e hashCode baseados na identidade
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemServico that = (ItemServico) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
