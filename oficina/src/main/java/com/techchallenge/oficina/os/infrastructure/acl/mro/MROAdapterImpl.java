package com.techchallenge.oficina.os.infrastructure.acl.mro;

import com.techchallenge.oficina.administrativo.application.usecases.DebitarEstoqueMROUseCase;
import com.techchallenge.oficina.administrativo.application.usecases.ReporEstoqueMROUseCase;
import com.techchallenge.oficina.administrativo.application.usecases.commands.DebitarEstoqueMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.commands.ReporEstoqueMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.BuscarMROInput;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.DebitarEstoqueMROInput;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.ReporEstoqueMROInput;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.os.infrastructure.acl.dto.MROIntegrationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do Anti-Corruption Layer para MRO
 * Faz a ponte entre o contexto OS e o contexto administrativo
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MROAdapterImpl implements MROAdapter {
    
    private final BuscarMROInput buscarMROInput;
    private final DebitarEstoqueMROInput debitarEstoqueMROInput;
    private final ReporEstoqueMROInput reporEstoqueMROInput;
    
    @Override
    public Optional<MROIntegrationDto> buscarPorId(UUID id) {
        log.debug("Buscando MRO por ID via ACL: {}", id);
        try {
            MROResponse response = buscarMROInput.execute(id);
            return Optional.of(toIntegrationDto(response));
        } catch (Exception e) {
            log.warn("MRO não encontrado ao buscar via ACL: {}", id);
            return Optional.empty();
        }
    }
    
    @Override
    public boolean temEstoqueSuficiente(UUID mroId, Integer quantidade) {
        log.debug("Verificando estoque suficiente para MRO {} quantidade: {}", mroId, quantidade);
        Optional<MROIntegrationDto> mro = buscarPorId(mroId);
        return mro.isPresent() && mro.get().temEstoqueSuficiente(quantidade);
    }
    
    @Override
    public void debitarEstoque(UUID mroId, Integer quantidade) {
        log.info("Debitando estoque do MRO {} quantidade: {}", mroId, quantidade);
        debitarEstoqueMROInput.execute(new DebitarEstoqueMROCommand(mroId, quantidade));
    }
    
    @Override
    public void reporEstoque(UUID mroId, Integer quantidade) {
        log.info("Repondo estoque do MRO {} quantidade: {}", mroId, quantidade);
        reporEstoqueMROInput.execute(new ReporEstoqueMROCommand(mroId, quantidade));
    }
    
    private MROIntegrationDto toIntegrationDto(MROResponse response) {
        if (response == null) {
            return null;
        }
        return MROIntegrationDto.builder()
                .id(response.getId())
                .nome(response.getNome())
                .descricao(response.getDescricao())
                .tipo(response.getTipo())
                .quantidadeEstoque(response.getQuantidadeEstoque())
                .precoUnitario(response.getPrecoUnitario())
                .ativo(response.getAtivo())
                .build();
    }
}
