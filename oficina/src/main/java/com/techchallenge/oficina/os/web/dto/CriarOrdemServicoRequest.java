package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.commands.CriarOrdemServicoCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Requisição para abertura de Ordem de Serviço")
public class CriarOrdemServicoRequest {

    @NotBlank(message = "CPF ou CNPJ do cliente é obrigatório")
    @Schema(description = "CPF (11 dígitos) ou CNPJ (14 dígitos) do cliente, apenas números",
            example = "12345678909")
    private String cpfOuCnpj;

    @NotBlank(message = "Placa do veículo é obrigatória")
    @Schema(description = "Placa do veículo (formato antigo: ABC1234 ou Mercosul: ABC1D23)",
            example = "ABC1234")
    private String placa;

    @NotEmpty(message = "A lista de serviços não pode ser vazia")
    @Schema(description = "Lista de códigos dos serviços a serem realizados",
            example = "[\"SVC-001\", \"SVC-002\"]")
    private List<String> codigosServico;

    @Schema(description = "Lista de peças/insumos (MRO) a serem utilizados")
    private List<ItemMRORequest> itensMRO;

    public CriarOrdemServicoCommand toCommand() {
        List<CriarOrdemServicoCommand.ItemMROCommand> mroCommands = itensMRO != null
                ? itensMRO.stream()
                        .map(i -> new CriarOrdemServicoCommand.ItemMROCommand(i.getCodigoMro(), i.getQuantidade()))
                        .toList()
                : List.of();

        return new CriarOrdemServicoCommand(cpfOuCnpj, placa, codigosServico, mroCommands);
    }

    @Data
    @Schema(description = "Item de peça/insumo para a Ordem de Serviço")
    public static class ItemMRORequest {

        @NotBlank(message = "Código do MRO é obrigatório")
        @Schema(description = "Código único do MRO (peça ou insumo)", example = "MRO-0001")
        private String codigoMro;

        @NotNull(message = "Quantidade é obrigatória")
        @Positive(message = "Quantidade deve ser maior que zero")
        @Schema(description = "Quantidade a utilizar", example = "2")
        private Integer quantidade;
    }
}
