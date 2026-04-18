package com.techchallenge.oficina.administrativo.application.usecases.responses;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ClienteResponse {
    
    private final UUID id;
    private final String nome;
    private final String cpf;
    private final String cnpj;
    private final String email;
    private final StatusCliente status;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;
    private final boolean isPessoaFisica;
    private final boolean isPessoaJuridica;
    private final int quantidadeVeiculos;
    
    public static ClienteResponse from(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        
        return ClienteResponse.builder()
                .id(cliente.getId())
                .nome(cliente.getNome().getValor())
                .cpf(cliente.getCpf() != null ? cliente.getCpf().getFormatado() : null)
                .cnpj(cliente.getCnpj() != null ? cliente.getCnpj().getFormatado() : null)
                .email(cliente.getEmail().getEndereco())
                .status(cliente.getStatus())
                .criadoEm(cliente.getCriadoEm())
                .atualizadoEm(cliente.getAtualizadoEm())
                .isPessoaFisica(cliente.isPessoaFisica())
                .isPessoaJuridica(cliente.isPessoaJuridica())
                .quantidadeVeiculos(cliente.getQuantidadeVeiculos())
                .build();
    }
    
    public String getTipoPessoa() {
        if (isPessoaFisica) {
            return "Pessoa Física";
        } else if (isPessoaJuridica) {
            return "Pessoa Jurídica";
        }
        return "Não definido";
    }
    
    public String getDocumento() {
        return cpf != null ? cpf : cnpj;
    }
}
