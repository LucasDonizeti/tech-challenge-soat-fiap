package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.BuscarMROUseCase;
import com.techchallenge.oficina.administrativo.application.usecases.BuscarServicoUseCase;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.os.application.usecases.responses.ItemMROResponse;
import com.techchallenge.oficina.os.application.usecases.responses.ItemServicoResponse;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListarOrdensServicoUseCase {
    
    private final OrdemServicoRepository ordemServicoRepository;
    private final BuscarServicoUseCase buscarServicoUseCase;
    private final BuscarMROUseCase buscarMROUseCase;
    
    @Transactional(readOnly = true)
    public Page<OrdemServicoResponse> execute(UUID clienteId, UUID veiculoId, StatusOS status, 
                                               LocalDateTime dataInicio, LocalDateTime dataFim, 
                                               Pageable pageable) {
        log.info("Listando ordens de serviço com filtros: clienteId={}, veiculoId={}, status={}, dataInicio={}, dataFim={}", 
                clienteId, veiculoId, status, dataInicio, dataFim);
        
        Page<OrdemServico> ordensServico = ordemServicoRepository.findByFilters(
                clienteId, veiculoId, status, dataInicio, dataFim, pageable);
        
        return ordensServico.map(this::enrichOrdemServicoResponse);
    }
    
    private OrdemServicoResponse enrichOrdemServicoResponse(OrdemServico ordemServico) {
        OrdemServicoResponse response = OrdemServicoResponse.from(ordemServico);
        
        // Buscar dados de serviços e MROs via ACL
        if (response.getItensServico() != null && !response.getItensServico().isEmpty()) {
            List<UUID> servicoIds = response.getItensServico().stream()
                    .map(ItemServicoResponse::getServicoId)
                    .distinct()
                    .toList();
            
            List<UUID> mroIds = response.getItensServico().stream()
                    .flatMap(item -> item.getMros() != null ? item.getMros().stream() : List.<ItemMROResponse>of().stream())
                    .map(ItemMROResponse::getMroId)
                    .distinct()
                    .toList();
            
            // Buscar serviços em lote
            Map<UUID, ServicoResponse> servicosMap = servicoIds.stream()
                    .collect(Collectors.toMap(
                            id -> id,
                            id -> buscarServicoUseCase.execute(id)
                    ));
            
            // Buscar MROs em lote
            Map<UUID, MROResponse> mrosMap = mroIds.stream()
                    .collect(Collectors.toMap(
                            id -> id,
                            id -> buscarMROUseCase.execute(id)
                    ));
            
            // Preencher dados nos itens de serviço
            response.getItensServico().forEach(item -> {
                ServicoResponse servico = servicosMap.get(item.getServicoId());
                if (servico != null) {
                    item.setServicoNome(servico.getNome());
                    item.setServicoDescricao(servico.getDescricao());
                }
                
                // Preencher dados nos MROs
                if (item.getMros() != null) {
                    item.getMros().forEach(mro -> {
                        MROResponse mroResponse = mrosMap.get(mro.getMroId());
                        if (mroResponse != null) {
                            mro.setMroNome(mroResponse.getNome());
                            mro.setMroDescricao(mroResponse.getDescricao());
                        }
                    });
                }
            });
        }
        
        return response;
    }
}
