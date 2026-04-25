package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
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
public class OrdemServicoResponseDto {
    
    private UUID id;
    private ClienteDto cliente;
    private VeiculoDto veiculo;
    private String status;
    private LocalDateTime dataCriacao;
    private BigDecimal valorTotal;
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
                .valorTotal(response.getValorTotal())
                .itensServico(itensServicoDtos)
                .build();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClienteDto {
        private UUID id;
        private String nome;
        private String cpf;
        private String cnpj;
        private String email;
        private String tipo;
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
    public static class VeiculoDto {
        private UUID id;
        private String placa;
        private String marca;
        private String modelo;
        private Integer ano;
        private String cor;
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
    public static class ItemServicoDto {
        private UUID id;
        private UUID servicoId;
        private String servicoNome;
        private String servicoDescricao;
        private String status;
        private String observacoes;
        private BigDecimal valorServico;
        private BigDecimal valorMro;
        private BigDecimal valorTotal;
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
                    .mros(mroDtos)
                    .build();
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemMroDto {
        private UUID id;
        private UUID mroId;
        private String mroNome;
        private String mroTipo;
        private Integer quantidade;
        private BigDecimal valorUnitario;
        private BigDecimal valorTotal;
        
        public static ItemMroDto from(com.techchallenge.oficina.os.application.usecases.responses.ItemMROResponse itemMro) {
            if (itemMro == null) {
                return null;
            }
            return ItemMroDto.builder()
                    .id(itemMro.getId())
                    .mroId(itemMro.getMroId())
                    .mroNome(itemMro.getMroNome())
                    .mroTipo(itemMro.getMroTipo())
                    .quantidade(itemMro.getQuantidade())
                    .valorUnitario(itemMro.getValorUnitario())
                    .valorTotal(itemMro.getValorTotal())
                    .build();
        }
    }
}
