package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Resposta de cliente com dados completos")
public class ClienteResponseDto {
    
    @Schema(description = "UUID do cliente", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    
    @Schema(description = "Nome completo do cliente", example = "João Silva")
    private String nome;
    
    @Schema(description = "CPF do cliente (apenas números)", example = "12345678909")
    private String cpf;
    
    @Schema(description = "CNPJ do cliente (apenas números)", example = "12345678000190")
    private String cnpj;
    
    @Schema(description = "Email do cliente", example = "joao.silva@exemplo.com")
    private String email;
    
    @Schema(description = "Status do cliente (ATIVO, INATIVO)", allowableValues = {"ATIVO", "INATIVO"}, example = "ATIVO")
    private StatusCliente status;
    
    @Schema(description = "Data de criação do cliente", example = "2024-01-15T10:30:00")
    private LocalDateTime criadoEm;
    
    @Schema(description = "Data da última atualização", example = "2024-01-20T14:45:00")
    private LocalDateTime atualizadoEm;
    
    @Schema(description = "Indica se é pessoa física", example = "true")
    private boolean isPessoaFisica;
    
    @Schema(description = "Indica se é pessoa jurídica", example = "false")
    private boolean isPessoaJuridica;
    
    @Schema(description = "Quantidade de veículos do cliente", example = "2")
    private int quantidadeVeiculos;
    
    @Schema(description = "Tipo de pessoa (PF ou PJ)", example = "PF")
    private String tipoPessoa;
    
    @Schema(description = "Documento (CPF ou CNPJ)", example = "12345678909")
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
