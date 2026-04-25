package com.techchallenge.oficina.os.application.usecases.responses;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {
    
    private UUID id;
    private String nome;
    private String cpf;
    private String cnpj;
    private String email;
    private String tipo;
    private String status;
    
    public static ClienteResponse from(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        
        String tipo = cliente.getCpf() != null ? "PESSOA_FISICA" : 
                      cliente.getCnpj() != null ? "PESSOA_JURIDICA" : null;
        
        return ClienteResponse.builder()
                .id(cliente.getId())
                .nome(cliente.getNome() != null ? cliente.getNome().getValor() : null)
                .cpf(cliente.getCpf() != null ? cliente.getCpf().getFormatado() : null)
                .cnpj(cliente.getCnpj() != null ? cliente.getCnpj().getFormatado() : null)
                .email(cliente.getEmail() != null ? cliente.getEmail().getEndereco() : null)
                .tipo(tipo)
                .status(cliente.getStatus() != null ? cliente.getStatus().name() : null)
                .build();
    }
}
