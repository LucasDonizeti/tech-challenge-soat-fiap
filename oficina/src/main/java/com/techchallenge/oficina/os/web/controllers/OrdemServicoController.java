package com.techchallenge.oficina.os.web.controllers;

import com.techchallenge.oficina.os.application.usecases.*;
import com.techchallenge.oficina.os.application.usecases.commands.*;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.web.dto.*;
import com.techchallenge.oficina.os.web.mappers.OrdemServicoWebMapper;
import com.techchallenge.oficina.sharedkernel.common.PageableValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/v1/os")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Ordem de Serviço", description = "Endpoints de gestão de ordens de serviço")
public class OrdemServicoController {

    private final CriarOrdemServicoUseCase criarOrdemServicoUseCase;
    private final ListarOrdensServicoUseCase listarOrdensServicoUseCase;
    private final AdicionarServicoOrdemUseCase adicionarServicoOrdemUseCase;
    private final RemoverServicoOrdemUseCase removerServicoOrdemUseCase;
    private final AdicionarMROServicoUseCase adicionarMROServicoUseCase;
    private final RemoverMROServicoUseCase removerMROServicoUseCase;
    private final AtualizarQuantidadeMROUseCase atualizarQuantidadeMROUseCase;
    private final OrdemServicoWebMapper mapper;
    private final PageableValidator pageableValidator;

    @PostMapping
    @Operation(summary = "Criar ordem de serviço", description = "Cria uma nova ordem de serviço com status RECEBIDA")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Ordem de serviço criada com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente ou veículo não encontrado")
    })
    public ResponseEntity<OrdemServicoResponseDto> criar(@Valid @RequestBody CriarOrdemServicoRequest request) {
        log.info("Recebendo requisição para criar ordem de serviço: clienteId={}, veiculoId={}", 
                request.getClienteId(), request.getVeiculoId());
        
        OrdemServicoResponse response = criarOrdemServicoUseCase.execute(request.toCommand());
        OrdemServicoResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    @Operation(summary = "Listar ordens de serviço", description = "Retorna uma lista paginada de ordens de serviço com filtros opcionais")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de ordens de serviço retornada com sucesso")
    })
    public ResponseEntity<Page<OrdemServicoResponseDto>> listar(
            @Parameter(description = "ID do cliente para filtro") @RequestParam(required = false) UUID clienteId,
            @Parameter(description = "ID do veículo para filtro") @RequestParam(required = false) UUID veiculoId,
            @Parameter(description = "Status da ordem de serviço para filtro") @RequestParam(required = false) StatusOS status,
            @Parameter(description = "Data de início para filtro (dataCriacao >= dataInicio)") @RequestParam(required = false) LocalDateTime dataInicio,
            @Parameter(description = "Data de fim para filtro (dataCriacao <= dataFim)") @RequestParam(required = false) LocalDateTime dataFim,
            Pageable pageable) {
        log.info("Listando ordens de serviço com filtros: clienteId={}, veiculoId={}, status={}, dataInicio={}, dataFim={}", 
                clienteId, veiculoId, status, dataInicio, dataFim);

        // Validar e limitar os campos de ordenação para evitar erros de Sort
        Set<String> allowedFields = Set.of("dataCriacao", "status", "clienteId", "veiculoId");
        Pageable validatedPageable = pageableValidator.validate(pageable, allowedFields);

        Page<OrdemServicoResponse> responses = listarOrdensServicoUseCase.execute(
                clienteId, veiculoId, status, dataInicio, dataFim, validatedPageable);
        Page<OrdemServicoResponseDto> dtos = mapper.toDtoPage(responses);

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{id}/servicos")
    @Operation(summary = "Adicionar serviço à ordem de serviço", description = "Adiciona um serviço existente à ordem de serviço")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Serviço adicionado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço ou serviço não encontrado")
    })
    public ResponseEntity<OrdemServicoResponseDto> adicionarServico(
            @Parameter(description = "ID da ordem de serviço") @PathVariable UUID id,
            @Valid @RequestBody AdicionarServicoOrdemRequest request) {
        log.info("Adicionando serviço à ordem de serviço: ordemServicoId={}, servicoId={}", id, request.getServicoId());
        
        OrdemServicoResponse response = adicionarServicoOrdemUseCase.execute(request.toCommand(id));
        OrdemServicoResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}/servicos/{itemServicoId}")
    @Operation(summary = "Remover serviço da ordem de serviço", description = "Remove um serviço da ordem de serviço")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Serviço removido com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço ou item de serviço não encontrado")
    })
    public ResponseEntity<OrdemServicoResponseDto> removerServico(
            @Parameter(description = "ID da ordem de serviço") @PathVariable UUID id,
            @Parameter(description = "ID do item de serviço") @PathVariable UUID itemServicoId) {
        log.info("Removendo serviço da ordem de serviço: ordemServicoId={}, itemServicoId={}", id, itemServicoId);
        
        RemoverServicoOrdemCommand command = new RemoverServicoOrdemCommand(id, itemServicoId);
        OrdemServicoResponse response = removerServicoOrdemUseCase.execute(command);
        OrdemServicoResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/mros")
    @Operation(summary = "Adicionar MRO ao serviço", description = "Adiciona um item de MRO a um serviço dentro da ordem de serviço")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "MRO adicionado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço, item de serviço ou MRO não encontrado")
    })
    public ResponseEntity<OrdemServicoResponseDto> adicionarMRO(
            @Parameter(description = "ID da ordem de serviço") @PathVariable UUID id,
            @Valid @RequestBody AdicionarMROServicoRequest request) {
        log.info("Adicionando MRO ao serviço: ordemServicoId={}, itemServicoId={}, mroId={}, quantidade={}", 
                id, request.getItemServicoId(), request.getMroId(), request.getQuantidade());
        
        OrdemServicoResponse response = adicionarMROServicoUseCase.execute(request.toCommand(id));
        OrdemServicoResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}/servicos/{itemServicoId}/mros/{itemMroId}")
    @Operation(summary = "Remover MRO do serviço", description = "Remove um item de MRO de um serviço dentro da ordem de serviço")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "MRO removido com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço, item de serviço ou item de MRO não encontrado")
    })
    public ResponseEntity<OrdemServicoResponseDto> removerMRO(
            @Parameter(description = "ID da ordem de serviço") @PathVariable UUID id,
            @Parameter(description = "ID do item de serviço") @PathVariable UUID itemServicoId,
            @Parameter(description = "ID do item de MRO") @PathVariable UUID itemMroId) {
        log.info("Removendo MRO do serviço: ordemServicoId={}, itemServicoId={}, itemMroId={}", 
                id, itemServicoId, itemMroId);
        
        RemoverMROServicoCommand command = new RemoverMROServicoCommand(id, itemServicoId, itemMroId);
        OrdemServicoResponse response = removerMROServicoUseCase.execute(command);
        OrdemServicoResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/mros/quantidade")
    @Operation(summary = "Atualizar quantidade de MRO", description = "Atualiza a quantidade de um item de MRO em um serviço")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quantidade atualizada com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço, item de serviço ou item de MRO não encontrado")
    })
    public ResponseEntity<OrdemServicoResponseDto> atualizarQuantidadeMRO(
            @Parameter(description = "ID da ordem de serviço") @PathVariable UUID id,
            @Valid @RequestBody AtualizarQuantidadeMRORequest request) {
        log.info("Atualizando quantidade de MRO: ordemServicoId={}, itemServicoId={}, itemMroId={}, novaQuantidade={}", 
                id, request.getItemServicoId(), request.getItemMroId(), request.getQuantidade());
        
        OrdemServicoResponse response = atualizarQuantidadeMROUseCase.execute(request.toCommand(id));
        OrdemServicoResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/health")
    @Operation(summary = "Verificar saúde do serviço", description = "Endpoint para verificação de saúde do serviço de Ordem de Serviço")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Serviço funcionando normalmente")
    })
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("service is running");
    }

    @GetMapping("/ping")
    @Operation(summary = "Ping do serviço", description = "Endpoint simples para teste de conectividade")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pong")
    })
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("GG");
    }
}
