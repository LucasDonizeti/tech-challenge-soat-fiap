package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoClienteException;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoVeiculoException;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import com.techchallenge.oficina.administrativo.domain.repositories.VeiculoRepository;
import com.techchallenge.oficina.os.application.usecases.commands.CriarOrdemServicoCommand;
import com.techchallenge.oficina.os.application.usecases.ports.input.CriarOrdemServicoInpuit;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CriarOrdemServicoUseCase implements CriarOrdemServicoInpuit {
    
    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    
    @Transactional
    public OrdemServicoResponse execute(CriarOrdemServicoCommand command) {
        log.info("Iniciando criação de ordem de serviço: clienteId={}, veiculoId={}", 
                command.getClienteId(), command.getVeiculoId());
        
        // Buscar cliente e veículo
        Cliente cliente = clienteRepository.findById(command.getClienteId())
                .orElseThrow(() -> new ValidacaoClienteException("Cliente não encontrado com ID: " + command.getClienteId()));
        
        Veiculo veiculo = veiculoRepository.findById(command.getVeiculoId())
                .orElseThrow(() -> new ValidacaoVeiculoException("Veículo não encontrado com ID: " + command.getVeiculoId()));
        
        // Criar ordem de serviço
        OrdemServico ordemServico = OrdemServico.criar(cliente, veiculo);
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoRepository.save(ordemServico);
        
        log.info("Ordem de serviço criada com sucesso: ID={}, Status={}", 
                savedOrdemServico.getId(), 
                savedOrdemServico.getStatus());
        
        return OrdemServicoResponse.from(savedOrdemServico);
    }
}
