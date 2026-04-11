package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ClienteResponseDto {
    
    private UUID id;
    private String nome;
    private String cpf;
    private String cnpj;
    private String email;
    private StatusCliente status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private boolean isPessoaFisica;
    private boolean isPessoaJuridica;
    private int quantidadeVeiculos;
    private String tipoPessoa;
    private String documento;
    
    public static ClienteResponseDto from(ClienteResponse response) {
        if (response == null) {
            return null;
        }
        
        ClienteResponseDto dto = new ClienteResponseDto();
        dto.setId(response.getId());
        dto.setNome(response.getNome());
        dto.setCpf(response.getCpf());
        dto.setCnpj(response.getCnpj());
        dto.setEmail(response.getEmail());
        dto.setStatus(response.getStatus());
        dto.setCriadoEm(response.getCriadoEm());
        dto.setAtualizadoEm(response.getAtualizadoEm());
        dto.setPessoaFisica(response.isPessoaFisica());
        dto.setPessoaJuridica(response.isPessoaJuridica());
        dto.setQuantidadeVeiculos(response.getQuantidadeVeiculos());
        dto.setTipoPessoa(response.getTipoPessoa());
        dto.setDocumento(response.getDocumento());
        
        return dto;
    }
}
