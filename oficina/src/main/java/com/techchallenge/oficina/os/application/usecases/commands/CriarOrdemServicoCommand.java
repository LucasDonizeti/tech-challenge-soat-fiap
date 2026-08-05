package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import lombok.Getter;

import java.util.List;

@Getter
public class CriarOrdemServicoCommand {

    private final String cpfOuCnpj;
    private final String placa;
    private final List<String> codigosServico;
    private final List<ItemMROCommand> itensMRO;

    public CriarOrdemServicoCommand(String cpfOuCnpj, String placa,
                                    List<String> codigosServico,
                                    List<ItemMROCommand> itensMRO) {
        validate(cpfOuCnpj, placa, codigosServico);
        this.cpfOuCnpj = cpfOuCnpj;
        this.placa = placa;
        this.codigosServico = codigosServico != null ? codigosServico : List.of();
        this.itensMRO = itensMRO != null ? itensMRO : List.of();
    }

    private void validate(String cpfOuCnpj, String placa, List<String> codigosServico) {
        if (cpfOuCnpj == null || cpfOuCnpj.isBlank()) {
            throw new ValidacaoOrdemServicoException("CPF ou CNPJ do cliente é obrigatório");
        }
        if (placa == null || placa.isBlank()) {
            throw new ValidacaoOrdemServicoException("Placa do veículo é obrigatória");
        }
        if (codigosServico == null || codigosServico.isEmpty()) {
            throw new ValidacaoOrdemServicoException("A lista de serviços não pode ser vazia");
        }
    }

    @Getter
    public static class ItemMROCommand {
        private final String codigoMro;
        private final Integer quantidade;

        public ItemMROCommand(String codigoMro, Integer quantidade) {
            if (codigoMro == null || codigoMro.isBlank()) {
                throw new ValidacaoOrdemServicoException("Código do MRO é obrigatório");
            }
            if (quantidade == null || quantidade <= 0) {
                throw new ValidacaoOrdemServicoException("Quantidade do MRO deve ser maior que zero");
            }
            this.codigoMro = codigoMro;
            this.quantidade = quantidade;
        }
    }
}
