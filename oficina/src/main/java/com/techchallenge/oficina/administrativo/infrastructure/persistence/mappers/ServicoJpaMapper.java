package com.techchallenge.oficina.administrativo.infrastructure.persistence.mappers;

import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ServicoEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServicoJpaMapper {
    
    public ServicoEntity toEntity(Servico servico) {
        if (servico == null) {
            return null;
        }
        
        return ServicoEntity.builder()
                .id(servico.getId())
                .nome(servico.getNome())
                .descricao(servico.getDescricao())
                .preco(servico.getPreco())
                .ativo(servico.getAtivo())
                .criadoEm(servico.getCriadoEm())
                .atualizadoEm(servico.getAtualizadoEm())
                .build();
    }
    
    public Servico toDomain(ServicoEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Servico.reconstruir(
            entity.getId(),
            entity.getNome(),
            entity.getDescricao(),
            entity.getPreco(),
            entity.getAtivo(),
            entity.getCriadoEm(),
            entity.getAtualizadoEm()
        );
    }

    public List<Servico> toDomainList(List<ServicoEntity> entities) {
        if (entities == null) {
            throw new IllegalArgumentException("List of ServicoEntities cannot be null");
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

}
