package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.DebitarEstoqueMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.DebitarEstoqueMROInput;
import com.techchallenge.oficina.os.application.usecases.commands.IniciarServicoCommand;
import com.techchallenge.oficina.os.application.usecases.ports.input.IniciarServicoInput;
import com.techchallenge.oficina.os.application.usecases.ports.output.OrdemServicoGateway;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.exceptions.ValidacaoEstoqueException;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemMRO;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusItemServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class IniciarServicoUseCase implements IniciarServicoInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    private final DebitarEstoqueMROInput debitarEstoqueMROInput;

    public OrdemServicoResponse execute(IniciarServicoCommand command) {
        log.info("Iniciando serviço: ordemServicoId={}, itemServicoId={}", 
                command.getOrdemServicoId(), command.getItemServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Buscar item de serviço
        ItemServico itemServico = ordemServico.getItensServico().stream()
                .filter(item -> item.getId().equals(command.getItemServicoId()))
                .findFirst()
                .orElseThrow(() -> new ItemServicoNaoEncontradoException(command.getItemServicoId()));
        
        // Validar que OS está em EM_EXECUCAO
        if (ordemServico.getStatus() != com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS.EM_EXECUCAO) {
            throw new ValidacaoEstoqueException("Só é possível iniciar serviços quando a OS está em EM_EXECUCAO. Status atual: " + ordemServico.getStatus());
        }
        
        // Validar que item está PENDENTE
        if (itemServico.getStatus() != StatusItemServico.PENDENTE) {
            throw new ValidacaoEstoqueException("Só é possível iniciar serviços com status PENDENTE. Status atual: " + itemServico.getStatus());
        }
        
        // Debitar estoque dos MROs
        debitarEstoqueMROs(itemServico);
        
        // Atualizar status para EM_ANDAMENTO
        itemServico.atualizarStatus(StatusItemServico.EM_ANDAMENTO);
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);
        
        log.info("Serviço iniciado com sucesso: itemServicoId={}", itemServico.getId());
        
        return OrdemServicoResponse.from(savedOrdemServico);
    }
    
    private void debitarEstoqueMROs(ItemServico itemServico) {
        for (ItemMRO itemMRO : itemServico.getMrosServicos()) {
            try {
                DebitarEstoqueMROCommand command = new DebitarEstoqueMROCommand(
                        itemMRO.getMroId(),
                        itemMRO.getQuantidade()
                );

                debitarEstoqueMROInput.execute(command);
                
                log.info("Estoque debitado para MRO: mroId={}, quantidade={}", itemMRO.getMroId(), itemMRO.getQuantidade());
            } catch (Exception e) {
                log.error("Erro ao debitar estoque do MRO: mroId={}", itemMRO.getMroId(), e);
                throw new ValidacaoEstoqueException("Erro ao debitar estoque do MRO: " + itemMRO.getMroNome() + ". " + e.getMessage());
            }
        }
    }
}
