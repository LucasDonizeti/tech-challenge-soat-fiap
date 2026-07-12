package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarVeiculoCommand;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.CriarVeiculoInput;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.ClienteGateway;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.VeiculoGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoVeiculoException;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import com.techchallenge.oficina.administrativo.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public class CriarVeiculoUseCase implements CriarVeiculoInput {

        private final VeiculoGateway veiculoGateway;
    
        private final ClienteGateway clienteGateway;

    public VeiculoResponse execute(CriarVeiculoCommand command) {
        log.info("Iniciando criação de veículo: Placa={}, ClienteID={}",
                command.getPlaca().getFormatada(), command.getClienteId());

        // Buscar cliente existente
        Cliente cliente = clienteGateway.findById(command.getClienteId())
                .orElseThrow(() -> {
                    log.warn("Cliente não encontrado para vincular veículo: ID={}", command.getClienteId());
                    return new ValidacaoVeiculoException("Cliente não encontrado: " + command.getClienteId());
                });

        // Validar se cliente está ativo
        if (!cliente.isAtivo()) {
            log.warn("Cliente inativo não pode ter veículos cadastrados: ID={}", command.getClienteId());
            throw new ValidacaoVeiculoException("Cliente inativo não pode ter veículos cadastrados");
        }

        // Validar se placa já existe para o mesmo cliente
        if (veiculoGateway.existsByPlacaAndClienteId(command.getPlaca(), command.getClienteId())) {
            log.warn("Placa já cadastrada para o mesmo cliente: Placa={}, ClienteID={}", 
                    command.getPlaca().getFormatada(), command.getClienteId());
            throw new ValidacaoVeiculoException("Placa já cadastrada para este cliente: " + command.getPlaca().getFormatada());
        }

        // Criar veículo
        Veiculo veiculo = new Veiculo(
                command.getPlaca(),
                command.getMarca(),
                command.getModelo(),
                command.getAno(),
                command.getCor()
        );

        // Vincular veículo ao cliente (apenas no lado do Veiculo)
        veiculo.setCliente(cliente);

        // Persistir veículo
        Veiculo savedVeiculo = veiculoGateway.save(veiculo);

        log.info("Veículo criado com sucesso: ID={}, Placa={}, Cliente={}",
                savedVeiculo.getId(),
                savedVeiculo.getPlaca().getFormatada(),
                cliente.getNome().getValor());

        return VeiculoResponse.from(savedVeiculo);
    }
}
