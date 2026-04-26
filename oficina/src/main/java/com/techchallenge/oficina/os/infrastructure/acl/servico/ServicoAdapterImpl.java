package com.techchallenge.oficina.os.infrastructure.acl.servico;

import com.techchallenge.oficina.administrativo.application.usecases.BuscarServicoUseCase;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.os.infrastructure.acl.dto.ServicoIntegrationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do Anti-Corruption Layer para Serviço
 * Faz a ponte entre o contexto OS e o contexto administrativo
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ServicoAdapterImpl implements ServicoAdapter {
    
    private final BuscarServicoUseCase buscarServicoUseCase;
    
    @Override
    public Optional<ServicoIntegrationDto> buscarPorId(UUID id) {
        log.debug("Buscando Serviço por ID via ACL: {}", id);
        try {
            ServicoResponse response = buscarServicoUseCase.execute(id);
            return Optional.of(toIntegrationDto(response));
        } catch (Exception e) {
            log.warn("Serviço não encontrado ao buscar via ACL: {}", id);
            return Optional.empty();
        }
    }
    
    private ServicoIntegrationDto toIntegrationDto(ServicoResponse response) {
        if (response == null) {
            return null;
        }
        return ServicoIntegrationDto.builder()
                .id(response.getId())
                .nome(response.getNome())
                .descricao(response.getDescricao())
                .preco(response.getPreco())
                .ativo(response.getAtivo())
                .build();
    }
}
