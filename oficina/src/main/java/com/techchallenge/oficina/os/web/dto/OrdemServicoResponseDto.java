package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de Ordem de Serviço")
public class OrdemServicoResponseDto {
    
    @Schema(description = "UUID da ordem de serviço", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    
    @Schema(description = "Dados do cliente")
    private ClienteDto cliente;
    
    @Schema(description = "Dados do veículo")
    private VeiculoDto veiculo;
    
    @Schema(description = "Status da ordem de serviço (RECEBIDA, EM_DIAGNOSTICO, AGUARDANDO_APROVACAO, EM_EXECUCAO, FINALIZADA, ENTREGUE)", allowableValues = {"RECEBIDA", "EM_DIAGNOSTICO", "AGUARDANDO_APROVACAO", "EM_EXECUCAO", "FINALIZADA", "ENTREGUE"}, example = "RECEBIDA")
    private String status;
    
    @Schema(description = "Data de criação da ordem", example = "2024-01-15T10:30:00")
    private LocalDateTime dataCriacao;
    
    @Schema(description = "Data de início da execução", example = "2024-01-15T11:00:00")
    private LocalDateTime dataInicioExecucao;
    
    @Schema(description = "Data de finalização", example = "2024-01-15T14:30:00")
    private LocalDateTime dataFinalizacao;
    
    @Schema(description = "Valor total da ordem de serviço", example = "500.00")
    private BigDecimal valorTotal;
    
    @Schema(description = "Lista de itens de serviço")
    private List<ItemServicoDto> itensServico;
    
    public static OrdemServicoResponseDto from(OrdemServicoResponse response) {
        if (response == null) {
            return null;
        }
        
        ClienteDto clienteDto = response.getCliente() != null ? ClienteDto.from(response.getCliente()) : null;
        VeiculoDto veiculoDto = response.getVeiculo() != null ? VeiculoDto.from(response.getVeiculo()) : null;
        
        List<ItemServicoDto> itensServicoDtos = response.getItensServico() != null ?
                response.getItensServico().stream()
                        .map(ItemServicoDto::from)
                        .toList() :
                List.of();
        
        return OrdemServicoResponseDto.builder()
                .id(response.getId())
                .cliente(clienteDto)
                .veiculo(veiculoDto)
                .status(response.getStatus())
                .dataCriacao(response.getDataCriacao())
                .dataInicioExecucao(response.getDataInicioExecucao())
                .dataFinalizacao(response.getDataFinalizacao())
                .valorTotal(response.getValorTotal())
                .itensServico(itensServicoDtos)
                .build();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Dados do cliente na ordem de serviço")
    public static class ClienteDto {
        @Schema(description = "UUID do cliente", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID id;
        
        @Schema(description = "Nome do cliente", example = "João Silva")
        private String nome;
        
        @Schema(description = "CPF do cliente", example = "12345678909")
        private String cpf;
        
        @Schema(description = "CNPJ do cliente", example = "12345678000190")
        private String cnpj;
        
        @Schema(description = "Email do cliente", example = "joao.silva@exemplo.com")
        private String email;
        
        @Schema(description = "Tipo de pessoa (PF/PJ)", example = "PF")
        private String tipo;
        
        @Schema(description = "Status do cliente", example = "ATIVO")
        private String status;
        
        public static ClienteDto from(com.techchallenge.oficina.os.application.usecases.responses.ClienteResponse cliente) {
            if (cliente == null) {
                return null;
            }
            return ClienteDto.builder()
                    .id(cliente.getId())
                    .nome(cliente.getNome())
                    .cpf(cliente.getCpf())
                    .cnpj(cliente.getCnpj())
                    .email(cliente.getEmail())
                    .tipo(cliente.getTipo())
                    .status(cliente.getStatus())
                    .build();
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Dados do veículo na ordem de serviço")
    public static class VeiculoDto {
        @Schema(description = "UUID do veículo", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID id;
        
        @Schema(description = "Placa do veículo", example = "ABC1234")
        private String placa;
        
        @Schema(description = "Marca do veículo", example = "Toyota")
        private String marca;
        
        @Schema(description = "Modelo do veículo", example = "Corolla")
        private String modelo;
        
        @Schema(description = "Ano do veículo", example = "2020")
        private Integer ano;
        
        @Schema(description = "Cor do veículo", example = "Prata")
        private String cor;
        
        @Schema(description = "Status do veículo", example = "ATIVO")
        private String status;
        
        public static VeiculoDto from(com.techchallenge.oficina.os.application.usecases.responses.VeiculoResponse veiculo) {
            if (veiculo == null) {
                return null;
            }
            return VeiculoDto.builder()
                    .id(veiculo.getId())
                    .placa(veiculo.getPlaca())
                    .marca(veiculo.getMarca())
                    .modelo(veiculo.getModelo())
                    .ano(veiculo.getAno())
                    .cor(veiculo.getCor())
                    .status(veiculo.getStatus())
                    .build();
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Item de serviço da ordem")
    public static class ItemServicoDto {
        @Schema(description = "UUID do item de serviço", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID id;
        
        @Schema(description = "UUID do serviço", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID servicoId;
        
        @Schema(description = "Nome do serviço", example = "Troca de Óleo")
        private String servicoNome;
        
        @Schema(description = "Descrição do serviço", example = "Troca completa de óleo")
        private String servicoDescricao;
        
        @Schema(description = "Status do item (PENDENTE, EM_ANDAMENTO, CONCLUIDO, CANCELADO)", allowableValues = {"PENDENTE", "EM_ANDAMENTO", "CONCLUIDO", "CANCELADO"}, example = "PENDENTE")
        private String status;
        
        @Schema(description = "Observações do item", example = "Verificar nível de óleo")
        private String observacoes;
        
        @Schema(description = "Valor do serviço", example = "150.00")
        private BigDecimal valorServico;
        
        @Schema(description = "Valor total dos MROs", example = "45.90")
        private BigDecimal valorMro;
        
        @Schema(description = "Valor total do item", example = "195.90")
        private BigDecimal valorTotal;
        
        @Schema(description = "Data de início da execução do serviço", example = "2024-01-15T11:00:00")
        private LocalDateTime dataInicioExecucao;
        
        @Schema(description = "Data de finalização do serviço", example = "2024-01-15T11:30:00")
        private LocalDateTime dataFinalizacao;
        
        @Schema(description = "Lista de MROs do item")
        private List<ItemMroDto> mros;
        
        public static ItemServicoDto from(com.techchallenge.oficina.os.application.usecases.responses.ItemServicoResponse itemServico) {
            if (itemServico == null) {
                return null;
            }
            
            List<ItemMroDto> mroDtos = itemServico.getMros() != null ?
                    itemServico.getMros().stream()
                            .map(ItemMroDto::from)
                            .toList() :
                    List.of();
            
            return ItemServicoDto.builder()
                    .id(itemServico.getId())
                    .servicoId(itemServico.getServicoId())
                    .servicoNome(itemServico.getServicoNome())
                    .servicoDescricao(itemServico.getServicoDescricao())
                    .status(itemServico.getStatus() != null ? itemServico.getStatus().name() : null)
                    .observacoes(itemServico.getObservacoes())
                    .valorServico(itemServico.getValorServico())
                    .valorMro(itemServico.getValorMro())
                    .valorTotal(itemServico.getValorTotal())
                    .dataInicioExecucao(itemServico.getDataInicioExecucao())
                    .dataFinalizacao(itemServico.getDataFinalizacao())
                    .mros(mroDtos)
                    .build();
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Item de MRO do serviço")
    public static class ItemMroDto {
        @Schema(description = "UUID do item de MRO", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID id;
        
        @Schema(description = "UUID do MRO", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID mroId;
        
        @Schema(description = "Nome do MRO", example = "Óleo de Motor 5W30")
        private String mroNome;
        
        @Schema(description = "Descrição do MRO", example = "Óleo sintético para motor")
        private String mroDescricao;
        
        @Schema(description = "Quantidade utilizada", example = "4")
        private Integer quantidade;
        
        @Schema(description = "Valor unitário", example = "45.90")
        private BigDecimal valorUnitario;
        
        @Schema(description = "Valor total", example = "183.60")
        private BigDecimal valorTotal;
        
        public static ItemMroDto from(com.techchallenge.oficina.os.application.usecases.responses.ItemMROResponse itemMro) {
            if (itemMro == null) {
                return null;
            }
            return ItemMroDto.builder()
                    .id(itemMro.getId())
                    .mroId(itemMro.getMroId())
                    .mroNome(itemMro.getMroNome())
                    .mroDescricao(itemMro.getMroDescricao())
                    .quantidade(itemMro.getQuantidade())
                    .valorUnitario(itemMro.getValorUnitario())
                    .valorTotal(itemMro.getValorTotal())
                    .build();
        }
    }
}
