package com.techchallenge.oficina.os.domain.model.aggregates;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ordens_servico")
@Getter
public class OrdemServico extends AbstractAggregateRoot<OrdemServico> {
    
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusOS status;
    
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;
    
    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemServico> itensServico = new ArrayList<>();
    
    // Construtor padrão para JPA
    protected OrdemServico() {}
    
    // Factory method para criação
    public static OrdemServico criar(Cliente cliente, Veiculo veiculo) {
        if (cliente == null) {
            throw new ValidacaoOrdemServicoException("Cliente não pode ser nulo");
        }
        if (veiculo == null) {
            throw new ValidacaoOrdemServicoException("Veículo não pode ser nulo");
        }
        
        OrdemServico ordemServico = new OrdemServico();
        ordemServico.id = UUID.randomUUID();
        ordemServico.cliente = cliente;
        ordemServico.veiculo = veiculo;
        ordemServico.status = StatusOS.RECEBIDA;
        ordemServico.dataCriacao = LocalDateTime.now();
        
        return ordemServico;
    }
    
    // Factory method para reconstrução a partir de dados persistidos
    public static OrdemServico reconstruir(UUID id, Cliente cliente, Veiculo veiculo, 
                                           StatusOS status, LocalDateTime dataCriacao) {
        OrdemServico ordemServico = new OrdemServico();
        ordemServico.id = id;
        ordemServico.cliente = cliente;
        ordemServico.veiculo = veiculo;
        ordemServico.status = status;
        ordemServico.dataCriacao = dataCriacao;
        
        return ordemServico;
    }
    
    // Comportamentos de negócio
    public ItemServico adicionarItemServico(ItemServico itemServico) {
        if (itemServico == null) {
            throw new ValidacaoOrdemServicoException("Item de serviço não pode ser nulo");
        }
        itemServico.setOrdemServico(this);
        itensServico.add(itemServico);
        return itemServico;
    }
    
    public void removerItemServico(UUID itemServicoId) {
        itensServico.removeIf(item -> item.getId().equals(itemServicoId));
    }
    
    public void atualizarStatus(StatusOS novoStatus) {
        if (novoStatus == null) {
            throw new ValidacaoOrdemServicoException("Status não pode ser nulo");
        }
        this.status = novoStatus;
    }
    
    public BigDecimal calcularValorTotal() {
        return itensServico.stream()
                .map(ItemServico::calcularValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Validações
    public boolean podeSerFinalizada() {
        return status == StatusOS.EM_EXECUCAO && 
               itensServico.stream().allMatch(item -> item.getStatus() == StatusItemServico.CONCLUIDO);
    }
    
    public boolean possuiItensServico() {
        return !itensServico.isEmpty();
    }
    
    // Métodos de acesso para JPA
    public List<ItemServico> getItensServico() {
        return new ArrayList<>(itensServico);
    }
}
