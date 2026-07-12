package com.techchallenge.oficina.administrativo.application.usecases.ports.output;

import com.techchallenge.oficina.administrativo.domain.model.entities.MRO;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MROGateway {
    MRO save(MRO mro);

    Optional<MRO> findById(UUID id);

    Page<MRO> findAll(Pageable pageable);

    List<MRO> findAll();

    List<MRO> findByAtivo(Boolean ativo);

    List<MRO> findByTipo(TipoMRO tipo);

    List<MRO> findByNomeContaining(String nome);

    List<MRO> findByQuantidadeEstoqueGreaterThan(Integer quantidade);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
