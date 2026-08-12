package com.techchallenge.oficina.os.domain.model.aggregates;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.os.domain.events.*;
import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Slf4j
public class OrdemServico extends AbstractAggregateRoot<OrdemServico> {
    
    private UUID id;
    private Cliente cliente;
    private Veiculo veiculo;
    private StatusOS status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataInicioExecucao;
    private LocalDateTime dataFinalizacao;
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
                                           StatusOS status, LocalDateTime dataCriacao,
                                           LocalDateTime dataInicioExecucao, LocalDateTime dataFinalizacao) {
        OrdemServico ordemServico = new OrdemServico();
        ordemServico.id = id;
        ordemServico.cliente = cliente;
        ordemServico.veiculo = veiculo;
        ordemServico.status = status;
        ordemServico.dataCriacao = dataCriacao;
        ordemServico.dataInicioExecucao = dataInicioExecucao;
        ordemServico.dataFinalizacao = dataFinalizacao;
        
        return ordemServico;
    }
    
    // Comportamentos de negócio
    public ItemServico adicionarItemServico(ItemServico itemServico) {
        validarAlteracaoServicosPermitida();
        if (itemServico == null) {
            throw new ValidacaoOrdemServicoException("Item de serviço não pode ser nulo");
        }
        itemServico.setOrdemServico(this);
        itensServico.add(itemServico);
        return itemServico;
    }
    
    // Método interno para reconstrução do banco (sem validações)
    public void adicionarItemServicoSemValidacao(ItemServico itemServico) {
        if (itemServico != null) {
            itemServico.setOrdemServico(this);
            itensServico.add(itemServico);
        }
    }
    
    public void removerItemServico(UUID itemServicoId) {
        validarAlteracaoServicosPermitida();
        itensServico.removeIf(item -> item.getId().equals(itemServicoId));
    }
    
    public void atualizarCliente(Cliente cliente) {
        validarAlteracaoClienteVeiculoPermitida();
        if (cliente == null) {
            throw new ValidacaoOrdemServicoException("Cliente não pode ser nulo");
        }
        this.cliente = cliente;
    }
    
    public void atualizarVeiculo(Veiculo veiculo) {
        validarAlteracaoClienteVeiculoPermitida();
        if (veiculo == null) {
            throw new ValidacaoOrdemServicoException("Veículo não pode ser nulo");
        }
        this.veiculo = veiculo;
    }
    
    private void validarAlteracaoClienteVeiculoPermitida() {
        if (this.status == StatusOS.EM_DIAGNOSTICO || 
            this.status == StatusOS.AGUARDANDO_APROVACAO || 
            this.status == StatusOS.EM_EXECUCAO ||
            this.status == StatusOS.FINALIZADA ||
            this.status == StatusOS.ENTREGUE) {
            throw new ValidacaoOrdemServicoException(
                "Não é permitido alterar cliente ou veículo quando a OS está no status " + this.status + 
                ". Alterações apenas permitidas no status RECEBIDA.");
        }
    }
    
    private void validarAlteracaoServicosPermitida() {
        if (this.status != StatusOS.RECEBIDA) {
            throw new ValidacaoOrdemServicoException(
                "Não é permitido adicionar ou remover serviços quando a OS está no status " + this.status + 
                ". Alterações de serviços apenas permitidas no status RECEBIDA.");
        }
    }
    
    private void validarAlteracaoPermitida() {
        if (this.status == StatusOS.EM_DIAGNOSTICO || 
            this.status == StatusOS.AGUARDANDO_APROVACAO) {
            throw new ValidacaoOrdemServicoException(
                "Não é permitido alterar a OS quando ela está no status " + this.status + 
                ". Apenas a aprovação do orçamento é permitida no status AGUARDANDO_APROVACAO.");
        }
    }
    
    public void atualizarStatus(StatusOS novoStatus) {
        if (novoStatus == null) {
            throw new ValidacaoOrdemServicoException("Status não pode ser nulo");
        }
        
        // Validar se a alteração de status é permitida no status atual
        // Em AGUARDANDO_APROVACAO, apenas aprovarOrcamento (transição para EM_EXECUCAO) é permitida
        if (this.status == StatusOS.AGUARDANDO_APROVACAO && novoStatus != StatusOS.EM_EXECUCAO) {
            throw new ValidacaoOrdemServicoException(
                "No status AGUARDANDO_APROVACAO, apenas a aprovação do orçamento é permitida. Status atual: " + this.status);
        }
        
        this.status = novoStatus;
    }
    
    public void enviarParaDiagnostico() {
        if (this.status != StatusOS.RECEBIDA) {
            throw new ValidacaoOrdemServicoException("Só é possível enviar para diagnóstico quando a OS está no status RECEBIDA. Status atual: " + this.status);
        }
        if (!possuiItensServico()) {
            throw new ValidacaoOrdemServicoException("OS deve ter pelo menos um serviço para ser enviada para diagnóstico");
        }
        this.status = StatusOS.EM_DIAGNOSTICO;
    }
    
    public void enviarOrcamentoAoCliente() {
        if (this.status != StatusOS.EM_DIAGNOSTICO) {
            throw new ValidacaoOrdemServicoException("Só é possível enviar orçamento ao cliente quando a OS está no status EM_DIAGNOSTICO. Status atual: " + this.status);
        }
        if (!possuiItensServico()) {
            throw new ValidacaoOrdemServicoException("OS deve ter pelo menos um serviço para enviar orçamento ao cliente");
        }
        this.status = StatusOS.AGUARDANDO_APROVACAO;
        
        // Emitir evento de domínio
        String clienteNome = cliente != null && cliente.getNome() != null ? cliente.getNome().getValor() : "Cliente";
        String clienteEmail = cliente != null && cliente.getEmail() != null ? cliente.getEmail().getEndereco() : null;
        String veiculoMarca = veiculo != null ? veiculo.getMarca() : null;
        String veiculoModelo = veiculo != null ? veiculo.getModelo() : null;
        String veiculoPlaca = veiculo != null && veiculo.getPlaca() != null ? veiculo.getPlaca().getFormatada() : null;
        
        registerEvent(new OrcamentoProntoEvent(id, clienteNome, clienteEmail, veiculoMarca, veiculoModelo, veiculoPlaca));
    }
    
    public void aprovarOrcamento() {
        if (this.status != StatusOS.AGUARDANDO_APROVACAO) {
            throw new ValidacaoOrdemServicoException("Só é possível aprovar orçamento quando a OS está no status AGUARDANDO_APROVACAO. Status atual: " + this.status);
        }
        this.status = StatusOS.EM_EXECUCAO;
        this.dataInicioExecucao = LocalDateTime.now();
        log.info("Orçamento aprovado - OS ID: {}, Status alterado para EM_EXECUCAO, Cliente: {}", 
                this.id, this.cliente != null ? this.cliente.getNome() : "N/A");
        
        // Emitir evento de domínio
        String clienteNome = cliente != null && cliente.getNome() != null ? cliente.getNome().getValor() : "Cliente";
        String clienteEmail = cliente != null && cliente.getEmail() != null ? cliente.getEmail().getEndereco() : null;
        String veiculoMarca = veiculo != null ? veiculo.getMarca() : null;
        String veiculoModelo = veiculo != null ? veiculo.getModelo() : null;
        String veiculoPlaca = veiculo != null && veiculo.getPlaca() != null ? veiculo.getPlaca().getFormatada() : null;
        
        registerEvent(new ServicoIniciadoEvent(id, clienteNome, clienteEmail, veiculoMarca, veiculoModelo, veiculoPlaca));
    }

    public void recusarOrcamento(String motivo) {
        if (this.status != StatusOS.AGUARDANDO_APROVACAO) {
            throw new ValidacaoOrdemServicoException(
                "Só é possível recusar orçamento quando a OS está no status AGUARDANDO_APROVACAO. Status atual: " + this.status);
        }
        this.status = StatusOS.CANCELADA;
        this.dataFinalizacao = LocalDateTime.now();
        log.info("Orçamento recusado - OS ID: {}, Status alterado para CANCELADA, Cliente: {}, Motivo: {}",
                this.id, this.cliente != null ? this.cliente.getNome() : "N/A", motivo);

        // Emitir evento de domínio
        String clienteNome = cliente != null && cliente.getNome() != null ? cliente.getNome().getValor() : "Cliente";
        String clienteEmail = cliente != null && cliente.getEmail() != null ? cliente.getEmail().getEndereco() : null;
        String veiculoMarca = veiculo != null ? veiculo.getMarca() : null;
        String veiculoModelo = veiculo != null ? veiculo.getModelo() : null;
        String veiculoPlaca = veiculo != null && veiculo.getPlaca() != null ? veiculo.getPlaca().getFormatada() : null;

        registerEvent(new OrcamentoRecusadoEvent(id, clienteNome, clienteEmail, veiculoMarca, veiculoModelo, veiculoPlaca, motivo));
    }
    
    public void finalizar() {
        if (this.status != StatusOS.EM_EXECUCAO) {
            throw new ValidacaoOrdemServicoException("Só é possível finalizar quando a OS está no status EM_EXECUCAO. Status atual: " + this.status);
        }
        if (!podeSerFinalizada()) {
            throw new ValidacaoOrdemServicoException("OS só pode ser finalizada quando todos os serviços estiverem CONCLUIDO");
        }
        this.status = StatusOS.FINALIZADA;
        this.dataFinalizacao = LocalDateTime.now();
        log.info("OS finalizada - OS ID: {}, Status alterado para FINALIZADA, Cliente: {}, Data Finalização: {}", 
                this.id, this.cliente != null ? this.cliente.getNome() : "N/A", this.dataFinalizacao);
        
        // Emitir evento de domínio
        String clienteNome = cliente != null && cliente.getNome() != null ? cliente.getNome().getValor() : "Cliente";
        String clienteEmail = cliente != null && cliente.getEmail() != null ? cliente.getEmail().getEndereco() : null;
        String veiculoMarca = veiculo != null ? veiculo.getMarca() : null;
        String veiculoModelo = veiculo != null ? veiculo.getModelo() : null;
        String veiculoPlaca = veiculo != null && veiculo.getPlaca() != null ? veiculo.getPlaca().getFormatada() : null;
        BigDecimal valorTotal = calcularValorTotal();
        
        registerEvent(new ServicoFinalizadoEvent(id, clienteNome, clienteEmail, veiculoMarca, veiculoModelo, veiculoPlaca, valorTotal));
    }
    
    public void verificarEFinalizarAutomaticamente() {
        if (this.status == StatusOS.EM_EXECUCAO && podeSerFinalizada()) {
            finalizar();
        }
    }
    
    public void entregar() {
        if (this.status != StatusOS.FINALIZADA) {
            throw new ValidacaoOrdemServicoException("Só é possível entregar quando a OS está no status FINALIZADA. Status atual: " + this.status);
        }
        this.status = StatusOS.ENTREGUE;
        log.info("OS entregue - OS ID: {}, Status alterado para ENTREGUE, Cliente: {}", 
                this.id, this.cliente != null ? this.cliente.getNome() : "N/A");
        
        // Emitir evento de domínio
        String clienteNome = cliente != null && cliente.getNome() != null ? cliente.getNome().getValor() : "Cliente";
        String clienteEmail = cliente != null && cliente.getEmail() != null ? cliente.getEmail().getEndereco() : null;
        String veiculoMarca = veiculo != null ? veiculo.getMarca() : null;
        String veiculoModelo = veiculo != null ? veiculo.getModelo() : null;
        String veiculoPlaca = veiculo != null && veiculo.getPlaca() != null ? veiculo.getPlaca().getFormatada() : null;
        
        registerEvent(new VeiculoEntregueEvent(id, clienteNome, clienteEmail, veiculoMarca, veiculoModelo, veiculoPlaca));
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
