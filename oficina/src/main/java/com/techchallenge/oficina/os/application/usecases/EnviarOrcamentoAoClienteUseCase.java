package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.EnviarOrcamentoAoClienteCommand;
import com.techchallenge.oficina.os.application.usecases.ports.input.EnviarOrcamentoAoClienteInput;
import com.techchallenge.oficina.os.application.usecases.ports.output.OrdemServicoGateway;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public class EnviarOrcamentoAoClienteUseCase implements EnviarOrcamentoAoClienteInput {
    
    private final OrdemServicoGateway ordemServicoGateway;

    public OrdemServicoResponse execute(EnviarOrcamentoAoClienteCommand command) {
        log.info("Enviando orçamento ao cliente: ordemServicoId={}", command.getOrdemServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Enviar orçamento ao cliente (validações são feitas no domínio)
        ordemServico.enviarOrcamentoAoCliente();
        
        // Log de notificação ao cliente
        log.info("Notificação enviada ao cliente: clienteId={}, ordemServicoId={}, valorTotal={}", 
                ordemServico.getCliente().getId(), ordemServico.getId(), ordemServico.calcularValorTotal());
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);
        
        log.info("Orçamento enviado ao cliente com sucesso: ID={}, Status={}", 
                savedOrdemServico.getId(), savedOrdemServico.getStatus());
        
        return OrdemServicoResponse.from(savedOrdemServico);
    }
}
