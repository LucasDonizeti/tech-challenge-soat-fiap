package com.techchallenge.oficina.administrativo.domain.model.aggregates;

import com.techchallenge.oficina.administrativo.domain.events.*;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoValorException;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.*;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clientes")
@Getter
public class Cliente extends AbstractAggregateRoot<Cliente> {
    
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    
    @Embedded
    @AttributeOverrides(
        @AttributeOverride(name = "valor", column = @Column(name = "nome", length = 100, nullable = false))
    )
    private Nome nome;
    
    @Embedded
    @AttributeOverrides(
        @AttributeOverride(name = "valor", column = @Column(name = "cpf", length = 11))
    )
    private CPF cpf;
    
    @Embedded
    @AttributeOverrides(
        @AttributeOverride(name = "valor", column = @Column(name = "cnpj", length = 14))
    )
    private CNPJ cnpj;
    
    @Embedded
    @AttributeOverrides(
        @AttributeOverride(name = "endereco", column = @Column(name = "email", length = 100, nullable = false, unique = true))
    )
    private Email email;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusCliente status;
    
    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;
    
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
    
    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<Veiculo> veiculos = new ArrayList<>();

    // Construtor padrão para JPA
    protected Cliente() {}

    // Factory method para criação de Pessoa Física
    public static Cliente criar(Nome nome, CPF cpf, Email email) {
        Cliente cliente = new Cliente();
        cliente.id = UUID.randomUUID();
        cliente.nome = nome;
        cliente.cpf = cpf;
        cliente.email = email;
        cliente.status = StatusCliente.ATIVO;
        cliente.criadoEm = LocalDateTime.now();
        cliente.atualizadoEm = LocalDateTime.now();
        
        // Domain Event
        cliente.registerEvent(new ClienteCriadoEvent(
            cliente.id, 
            cliente.nome.getValor(), 
            cliente.cpf != null ? cliente.cpf.getValor() : null,
            cliente.cnpj != null ? cliente.cnpj.getValor() : null,
            cliente.email.getEndereco()
        ));
        
        return cliente;
    }
    
    // Factory method para criação de Pessoa Jurídica
    public static Cliente criarPJ(Nome nome, CNPJ cnpj, Email email) {
        Cliente cliente = new Cliente();
        cliente.id = UUID.randomUUID();
        cliente.nome = nome;
        cliente.cnpj = cnpj;
        cliente.email = email;
        cliente.status = StatusCliente.ATIVO;
        cliente.criadoEm = LocalDateTime.now();
        cliente.atualizadoEm = LocalDateTime.now();
        
        // Domain Event
        cliente.registerEvent(new ClienteCriadoEvent(
            cliente.id, 
            cliente.nome.getValor(), 
            cliente.cpf != null ? cliente.cpf.getValor() : null,
            cliente.cnpj != null ? cliente.cnpj.getValor() : null,
            cliente.email.getEndereco()
        ));
        
        return cliente;
    }

    // Comportamentos de negócio
    public void atualizarNome(Nome novoNome) {
        if (novoNome == null) {
            throw new ValidacaoValorException("Nome não pode ser nulo");
        }
        this.nome = novoNome;
        this.atualizadoEm = LocalDateTime.now();

        registerEvent(new ClienteAtualizadoEvent(this.id, this.nome.getValor()));
    }
    
    public void atualizarEmail(Email novoEmail) {
        if (novoEmail == null) {
            throw new ValidacaoValorException("Email não pode ser nulo");
        }
        this.email = novoEmail;
        this.atualizadoEm = LocalDateTime.now();

        registerEvent(new ClienteAtualizadoEvent(this.id, this.nome.getValor()));
    }
    
    public void inativar() {
        if (this.status == StatusCliente.INATIVO) {
            throw new ValidacaoValorException("Cliente já está inativo");
        }
        this.status = StatusCliente.INATIVO;
        this.atualizadoEm = LocalDateTime.now();

        registerEvent(new ClienteInativadoEvent(this.id));
    }
    
    public void reativar() {
        if (this.status == StatusCliente.ATIVO) {
            throw new ValidacaoValorException("Cliente já está ativo");
        }
        this.status = StatusCliente.ATIVO;
        this.atualizadoEm = LocalDateTime.now();

        registerEvent(new ClienteReativadoEvent(this.id));
    }
    
    // Lista de veículos é apenas para consulta - manipulação deve ser feita através do VeiculoRepository
    public void adicionarVeiculo(Veiculo veiculo) {
        if (veiculo == null) {
            throw new ValidacaoValorException("Veículo não pode ser nulo");
        }
        if (!veiculos.contains(veiculo)) {
            veiculos.add(veiculo);
            this.atualizadoEm = LocalDateTime.now();
        }
    }

    public void removerVeiculo(Veiculo veiculo) {
        if (veiculo != null && veiculos.contains(veiculo)) {
            veiculos.remove(veiculo);
            this.atualizadoEm = LocalDateTime.now();
        }
    }

    // Validações
    public boolean isPessoaFisica() {
        return cpf != null && cnpj == null;
    }
    
    public boolean isPessoaJuridica() {
        return cnpj != null && cpf == null;
    }
    
    public boolean isAtivo() {
        return status == StatusCliente.ATIVO;
    }
    
    public boolean possuiVeiculos() {
        return !veiculos.isEmpty();
    }
    
    public int getQuantidadeVeiculos() {
        return veiculos.size();
    }
    
    // Métodos de acesso para JPA
    public List<Veiculo> getVeiculos() {
        return new ArrayList<>(veiculos);
    }

    // Método para restaurar aggregate do banco
    public static Cliente restaurar(ClienteRestauracaoParams params) {
        Cliente cliente = new Cliente();
        cliente.id = params.id();
        cliente.nome = params.nome();
        cliente.cpf = params.cpf();
        cliente.cnpj = params.cnpj();
        cliente.email = params.email();
        cliente.status = params.status();
        cliente.criadoEm = params.criadoEm();
        cliente.atualizadoEm = params.atualizadoEm();
        return cliente;
    }
}
