package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoClienteException;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoVeiculoException;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.os.application.usecases.commands.CriarOrdemServicoCommand;
import com.techchallenge.oficina.os.application.usecases.ports.input.CriarOrdemServicoInpuit;
import com.techchallenge.oficina.os.application.usecases.ports.output.ClienteGateway;
import com.techchallenge.oficina.os.application.usecases.ports.output.OrdemServicoGateway;
import com.techchallenge.oficina.os.application.usecases.ports.output.VeiculoGateway;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.infrastructure.acl.dto.MROIntegrationDto;
import com.techchallenge.oficina.os.infrastructure.acl.dto.ServicoIntegrationDto;
import com.techchallenge.oficina.os.infrastructure.acl.mro.MROAdapter;
import com.techchallenge.oficina.os.infrastructure.acl.servico.ServicoAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class CriarOrdemServicoUseCase implements CriarOrdemServicoInpuit {

    private final OrdemServicoGateway ordemServicoGateway;
    private final ClienteGateway clienteGateway;
    private final VeiculoGateway veiculoGateway;
    private final ServicoAdapter servicoAdapter;
    private final MROAdapter mroAdapter;

    @Override
    public OrdemServicoResponse execute(CriarOrdemServicoCommand command) {
        log.info("Iniciando criação de ordem de serviço: cpfOuCnpj={}, placa={}",
                command.getCpfOuCnpj(), command.getPlaca());

        // 1. Resolver cliente por CPF ou CNPJ
        Cliente cliente = resolverCliente(command.getCpfOuCnpj());

        // 2. Resolver veículo por placa
        Placa placa = Placa.of(command.getPlaca());
        Veiculo veiculo = veiculoGateway.findByPlaca(placa)
                .orElseThrow(() -> new ValidacaoVeiculoException(
                        "Veículo não encontrado com placa: " + command.getPlaca()));

        // 3. Criar a OS
        OrdemServico ordemServico = OrdemServico.criar(cliente, veiculo);

        // 4. Adicionar serviços (por código)
        for (String codigoServico : command.getCodigosServico()) {
            ServicoIntegrationDto servicoDto = servicoAdapter.buscarPorCodigo(codigoServico)
                    .orElseThrow(() -> new ValidacaoOrdemServicoException(
                            "Serviço não encontrado com código: " + codigoServico));

            if (Boolean.FALSE.equals(servicoDto.getAtivo())) {
                throw new ValidacaoOrdemServicoException(
                        "Serviço inativo: " + codigoServico);
            }

            ItemServico itemServico = ItemServico.criarComDados(
                    servicoDto.getId(),
                    servicoDto.getNome(),
                    servicoDto.getDescricao(),
                    servicoDto.getPreco());

            ordemServico.adicionarItemServico(itemServico);
        }

        // 5. Distribuir MROs nos itens de serviço (todos os MROs vão para o primeiro item)
        if (!command.getItensMRO().isEmpty()) {
            ItemServico primeiroItem = ordemServico.getItensServico().get(0);

            for (CriarOrdemServicoCommand.ItemMROCommand mroCmd : command.getItensMRO()) {
                MROIntegrationDto mroDto = mroAdapter.buscarPorCodigo(mroCmd.getCodigoMro())
                        .orElseThrow(() -> new ValidacaoOrdemServicoException(
                                "MRO não encontrado com código: " + mroCmd.getCodigoMro()));

                if (Boolean.FALSE.equals(mroDto.getAtivo())) {
                    throw new ValidacaoOrdemServicoException(
                            "MRO inativo: " + mroCmd.getCodigoMro());
                }

                primeiroItem.adicionarMRO(
                        mroDto.getId(),
                        mroDto.getNome(),
                        mroDto.getDescricao(),
                        mroDto.getPrecoUnitario(),
                        mroCmd.getQuantidade());
            }
        }

        // 6. Persistir e retornar com orçamento calculado
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);

        log.info("Ordem de serviço criada com sucesso: ID={}, Status={}, ValorTotal={}",
                savedOrdemServico.getId(),
                savedOrdemServico.getStatus(),
                savedOrdemServico.calcularValorTotal());

        return OrdemServicoResponse.from(savedOrdemServico);
    }

    private Cliente resolverCliente(String cpfOuCnpj) {
        String documento = cpfOuCnpj.replaceAll("\\D", "");

        if (documento.length() == 11) {
            CPF cpf = CPF.of(documento);
            return clienteGateway.findByCPF(cpf)
                    .orElseThrow(() -> new ValidacaoClienteException(
                            "Cliente não encontrado com CPF: " + cpfOuCnpj));
        } else if (documento.length() == 14) {
            CNPJ cnpj = CNPJ.of(documento);
            return clienteGateway.findByCNPJ(cnpj)
                    .orElseThrow(() -> new ValidacaoClienteException(
                            "Cliente não encontrado com CNPJ: " + cpfOuCnpj));
        } else {
            throw new ValidacaoOrdemServicoException(
                    "Identificação inválida. Informe um CPF (11 dígitos) ou CNPJ (14 dígitos).");
        }
    }
}
