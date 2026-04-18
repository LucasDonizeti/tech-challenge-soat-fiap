package com.techchallenge.oficina.administrativo.domain.model.entities;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoValorException;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusVeiculo;
import lombok.Getter;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "veiculos")
@Getter
public class Veiculo {
    
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    
    @Embedded
    @AttributeOverrides(
        @AttributeOverride(name = "valor", column = @Column(name = "placa", length = 7, nullable = false, unique = true))
    )
    private Placa placa;
    
    @Column(name = "marca", length = 50, nullable = false)
    private String marca;
    
    @Column(name = "modelo", length = 50, nullable = false)
    private String modelo;
    
    @Column(name = "ano", nullable = false)
    private Integer ano;
    
    @Column(name = "cor", length = 30)
    private String cor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusVeiculo status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false, columnDefinition = "BINARY(16)")
    private Cliente cliente;
    
    @Column(name = "criado_em", nullable = false)
    private java.time.LocalDateTime criadoEm;
    
    @Column(name = "atualizado_em", nullable = false)
    private java.time.LocalDateTime atualizadoEm;

    // Construtor padrão para JPA
    protected Veiculo() {}

    // Método estático para restauração (padrão DDD)
    public static Veiculo restaurar(VeiculoRestauracaoParams params) {
        Veiculo veiculo = new Veiculo(params.placa(), params.marca(), params.modelo(), params.ano(), params.cor(), params.status(), false);
        veiculo.id = params.id();
        veiculo.criadoEm = params.criadoEm();
        veiculo.atualizadoEm = params.atualizadoEm();
        return veiculo;
    }

    // Construtor para criação
    private Veiculo(Placa placa, String marca, String modelo, Integer ano, String cor, StatusVeiculo status, boolean gerarId) {
        if (gerarId) {
            this.id = UUID.randomUUID();
        }
        this.placa = placa;
        this.marca = Objects.requireNonNull(marca, "Marca não pode ser nula");
        this.modelo = Objects.requireNonNull(modelo, "Modelo não pode ser nulo");
        this.ano = Objects.requireNonNull(ano, "Ano não pode ser nulo");
        this.cor = cor;
        this.status = status != null ? status : StatusVeiculo.ATIVO;
        if (gerarId) {
            this.criadoEm = java.time.LocalDateTime.now();
            this.atualizadoEm = java.time.LocalDateTime.now();
        }

        validarAno(ano);
    }

    // Construtor público para criação
    public Veiculo(Placa placa, String marca, String modelo, Integer ano, String cor) {
        this(placa, marca, modelo, ano, cor, StatusVeiculo.ATIVO, true);
    }

    // Comportamentos de negócio
    public void atualizarDados(String marca, String modelo, Integer ano, String cor) {
        if (marca != null && !marca.isBlank()) {
            this.marca = marca;
        }
        if (modelo != null && !modelo.isBlank()) {
            this.modelo = modelo;
        }
        if (ano != null) {
            validarAno(ano);
            this.ano = ano;
        }
        this.cor = cor;
        this.atualizadoEm = java.time.LocalDateTime.now();
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void inativar() {
        if (this.status == StatusVeiculo.INATIVO) {
            throw new ValidacaoValorException("Veículo já está inativo");
        }
        this.status = StatusVeiculo.INATIVO;
        this.atualizadoEm = java.time.LocalDateTime.now();
    }

    public void reativar() {
        if (this.status == StatusVeiculo.ATIVO) {
            throw new ValidacaoValorException("Veículo já está ativo");
        }
        this.status = StatusVeiculo.ATIVO;
        this.atualizadoEm = java.time.LocalDateTime.now();
    }

    // Validações
    private void validarAno(Integer ano) {
        int anoAtual = java.time.Year.now().getValue();
        if (ano < 1900 || ano > anoAtual + 1) {
            throw new ValidacaoValorException("Ano inválido. Deve estar entre 1900 e " + (anoAtual + 1));
        }
    }
    
    public boolean isNovo() {
        int anoAtual = java.time.Year.now().getValue();
        return ano >= anoAtual;
    }
    
    public boolean isAntigo() {
        int anoAtual = java.time.Year.now().getValue();
        return ano < anoAtual - 10;
    }

    // Métodos de acesso
    public String getDescricaoCompleta() {
        return String.format("%s %s %d (%s)", marca, modelo, ano, placa.getFormatada());
    }

    // Equals e hashCode baseados na identidade
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Veiculo veiculo = (Veiculo) o;
        return Objects.equals(id, veiculo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getDescricaoCompleta();
    }
}
