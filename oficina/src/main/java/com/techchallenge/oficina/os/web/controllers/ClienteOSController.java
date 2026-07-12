package com.techchallenge.oficina.os.web.controllers;

import com.techchallenge.oficina.os.application.usecases.commands.AprovarOrcamentoCommand;
import com.techchallenge.oficina.os.application.usecases.ports.input.AprovarOrcamentoInput;
import com.techchallenge.oficina.os.application.usecases.ports.input.ListarOrdensServicoPorClienteInput;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.web.dto.OrdemServicoResponseDto;
import com.techchallenge.oficina.os.web.presenters.ClienteOSPresenter;
import com.techchallenge.oficina.sharedkernel.common.PageableValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@Transactional
@RequestMapping("/api/cliente/os")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Cliente - Ordem de Serviço", description = "Endpoints públicos para clientes interagirem com ordens de serviço")
public class ClienteOSController {

    private final AprovarOrcamentoInput aprovarOrcamentoInput;
    private final ListarOrdensServicoPorClienteInput listarOrdensServicoPorClienteInput;
    private final ClienteOSPresenter presenter;
    private final PageableValidator pageableValidator;

    @GetMapping("/{id}")
    @Operation(summary = "Listar ordens de serviço por cliente", description = "Retorna todas as ordens de serviço de um cliente paginadas (endpoint público)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de ordens de serviço retornada com sucesso")
    })
    public ResponseEntity<Page<OrdemServicoResponseDto>> listarPorCliente(
            @Parameter(description = "ID do cliente", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Número da página (padrão: 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página (padrão: 10)", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenação (ex: status, dataCriacao, dataInicioExecucao)", example = "dataCriacao")
            @RequestParam(defaultValue = "dataCriacao") String sort,
            Pageable pageable) {
        log.info("Listando ordens de serviço por cliente: clienteId={}", id);

        // Validar e limitar os campos de ordenação para evitar erros de Sort
        Set<String> allowedFields = Set.of("status", "dataCriacao", "dataInicioExecucao", "dataFinalizacao", "valorTotal");
        Pageable validatedPageable = pageableValidator.validate(pageable, allowedFields);

        Page<OrdemServicoResponse> responses = listarOrdensServicoPorClienteInput.execute(id, validatedPageable);
        Page<OrdemServicoResponseDto> dtos = presenter.prepararViewModelPage(responses);

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{osId}/aprovar-orcamento")
    @Operation(summary = "Aprovar orçamento", description = "Cliente aprova o orçamento da ordem de serviço, movendo-a para EM_EXECUCAO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orçamento aprovado com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Status inválido para aprovação"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada")
    })
    public ResponseEntity<OrdemServicoResponseDto> aprovarOrcamento(
            @Parameter(description = "ID da ordem de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID osId) {
        log.info("Aprovando orçamento (cliente): ordemServicoId={}", osId);
        
        AprovarOrcamentoCommand command = new AprovarOrcamentoCommand(osId);
        OrdemServicoResponse response = aprovarOrcamentoInput.execute(command);
        OrdemServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }
}
