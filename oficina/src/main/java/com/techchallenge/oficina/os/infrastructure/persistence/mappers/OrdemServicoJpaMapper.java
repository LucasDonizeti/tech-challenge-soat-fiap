package com.techchallenge.oficina.os.infrastructure.persistence.mappers;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import com.techchallenge.oficina.administrativo.domain.repositories.VeiculoRepository;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.OrdemServicoEntity;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.OrdemServicoEntity.StatusOSEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrdemServicoJpaMapper {
    
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final ItemServicoJpaMapper itemServicoJpaMapper;
    
    public OrdemServicoEntity toEntity(OrdemServico ordemServico) {
        if (ordemServico == null) {
            return null;
        }
        
        OrdemServicoEntity entity = OrdemServicoEntity.builder()
                .id(ordemServico.getId())
                .clienteId(ordemServico.getCliente() != null ? ordemServico.getCliente().getId() : null)
                .veiculoId(ordemServico.getVeiculo() != null ? ordemServico.getVeiculo().getId() : null)
                .status(ordemServico.getStatus() != null ? StatusOSEntity.valueOf(ordemServico.getStatus().name()) : null)
                .dataCriacao(ordemServico.getDataCriacao())
                .build();
        
        if (ordemServico.getItensServico() != null && !ordemServico.getItensServico().isEmpty()) {
            entity.setItensServico(itemServicoJpaMapper.toEntityList(ordemServico.getItensServico(), ordemServico.getId()));
        }
        
        return entity;
    }
    
    public OrdemServico toDomain(OrdemServicoEntity entity, ClienteRepository clienteRepo, VeiculoRepository veiculoRepo) {
        if (entity == null) {
            return null;
        }
        
        Optional<Cliente> cliente = clienteRepo.findById(entity.getClienteId());
        Optional<Veiculo> veiculo = veiculoRepo.findById(entity.getVeiculoId());
        
        StatusOS statusOS = entity.getStatus() != null ? StatusOS.valueOf(entity.getStatus().name()) : null;
        
        OrdemServico ordemServico = OrdemServico.reconstruir(
                entity.getId(),
                cliente.orElse(null),
                veiculo.orElse(null),
                statusOS,
                entity.getDataCriacao()
        );
        
        // Load and add items if they exist
        if (entity.getItensServico() != null && !entity.getItensServico().isEmpty()) {
            var servicoRepository = itemServicoJpaMapper.getServicoRepository();
            var items = itemServicoJpaMapper.toDomainList(entity.getItensServico(), servicoRepository);
            items.forEach(ordemServico::adicionarItemServico);
        }
        
        return ordemServico;
    }
}
