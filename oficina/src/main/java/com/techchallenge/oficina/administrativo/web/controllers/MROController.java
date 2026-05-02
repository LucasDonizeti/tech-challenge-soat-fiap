package com.techchallenge.oficina.administrativo.web.controllers;

import com.techchallenge.oficina.administrativo.application.usecases.*;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.administrativo.web.dto.*;
import com.techchallenge.oficina.administrativo.web.mappers.MROWebMapper;
import com.techchallenge.oficina.sharedkernel.common.PageableValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/mros")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "MRO", description = "Endpoints de gestão de MROs (Materiais, Reparos e Óleo)")
@SecurityRequirement(name = "Bearer Authentication")
public class MROController {
    
    private final CriarMROUseCase criarMROUseCase;
    private final BuscarMROUseCase buscarMROUseCase;
    private final ListarMROsUseCase listarMROsUseCase;
    private final InativarMROUseCase inativarMROUseCase;
    private final AtivarMROUseCase ativarMROUseCase;
    private final AtualizarPrecoMROUseCase atualizarPrecoMROUseCase;
    private final AtualizarDadosMROUseCase atualizarDadosMROUseCase;
    private final ReporEstoqueMROUseCase reporEstoqueMROUseCase;
    private final DebitarEstoqueMROUseCase debitarEstoqueMROUseCase;
    private final MROWebMapper mapper;
    private final PageableValidator pageableValidator;
    
    @PostMapping
    @Operation(summary = "Criar novo MRO", description = "Cadastra um novo MRO (peça ou insumo) no sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "MRO criado com sucesso",
                    content = @Content(schema = @Schema(implementation = MROResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição")
    })
    public ResponseEntity<MROResponseDto> criar(@Valid @RequestBody CriarMRORequest request) {
        log.info("Recebendo requisição para criar MRO: {}", request.getNome());
        
        MROResponse response = criarMROUseCase.execute(request.toCommand());
        MROResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar MRO por ID", description = "Retorna os dados de um MRO específico pelo seu UUID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "MRO encontrado",
                    content = @Content(schema = @Schema(implementation = MROResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MRO não encontrado")
    })
    public ResponseEntity<MROResponseDto> buscar(
            @Parameter(description = "UUID do MRO", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        log.info("Buscando MRO por ID: {}", id);
        
        MROResponse response = buscarMROUseCase.execute(id);
        MROResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping
    @Operation(summary = "Listar MROs", description = "Retorna uma lista paginada de todos os MROs")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de MROs retornada com sucesso")
    })
    public ResponseEntity<Page<MROResponseDto>> listar(
            @Parameter(description = "Número da página (padrão: 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página (padrão: 10)", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenação (ex: nome, tipo, quantidadeEstoque, precoUnitario)", example = "nome")
            @RequestParam(defaultValue = "nome") String sort,
            Pageable pageable) {
        log.info("Listando MROs com paginação");

        // Validar e limitar os campos de ordenação para evitar erros de Sort
        Set<String> allowedFields = Set.of("nome", "tipo", "quantidadeEstoque", "precoUnitario", "ativo", "criadoEm", "atualizadoEm");
        Pageable validatedPageable = pageableValidator.validate(pageable, allowedFields);

        Page<MROResponse> responses = listarMROsUseCase.execute(validatedPageable);
        Page<MROResponseDto> dtos = mapper.toDtoPage(responses);

        return ResponseEntity.ok(dtos);
    }
    
    @PatchMapping("/{id}/inativar")
    @Operation(summary = "Inativar MRO", description = "Inativa um MRO existente no sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "MRO inativado com sucesso",
                    content = @Content(schema = @Schema(implementation = MROResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MRO não encontrado")
    })
    public ResponseEntity<MROResponseDto> inativar(
            @Parameter(description = "UUID do MRO", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        log.info("Inativando MRO ID: {}", id);
        
        MROResponse response = inativarMROUseCase.execute(id);
        MROResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar MRO", description = "Ativa um MRO inativado no sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "MRO ativado com sucesso",
                    content = @Content(schema = @Schema(implementation = MROResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MRO não encontrado")
    })
    public ResponseEntity<MROResponseDto> ativar(
            @Parameter(description = "UUID do MRO", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        log.info("Ativando MRO ID: {}", id);
        
        MROResponse response = ativarMROUseCase.execute(id);
        MROResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/{id}/preco")
    @Operation(summary = "Atualizar preço unitário do MRO", description = "Atualiza o preço unitário de um MRO existente")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Preço unitário atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = MROResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MRO não encontrado")
    })
    public ResponseEntity<MROResponseDto> atualizarPreco(
            @Parameter(description = "UUID do MRO", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarPrecoMRORequest request) {
        log.info("Atualizando preço unitário do MRO ID: {}", id);
        
        MROResponse response = atualizarPrecoMROUseCase.execute(request.toCommand(id));
        MROResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/{id}/dados")
    @Operation(summary = "Atualizar dados do MRO", description = "Atualiza o nome, descrição e tipo de um MRO existente")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dados atualizados com sucesso",
                    content = @Content(schema = @Schema(implementation = MROResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MRO não encontrado")
    })
    public ResponseEntity<MROResponseDto> atualizarDados(
            @Parameter(description = "UUID do MRO", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarDadosMRORequest request) {
        log.info("Atualizando dados do MRO ID: {}", id);
        
        MROResponse response = atualizarDadosMROUseCase.execute(request.toCommand(id));
        MROResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @PostMapping("/{id}/estoque/incrementar")
    @Operation(summary = "Incrementar estoque do MRO", description = "Adiciona uma quantidade ao estoque atual do MRO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Estoque incrementado com sucesso",
                    content = @Content(schema = @Schema(implementation = MROResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MRO não encontrado")
    })
    public ResponseEntity<MROResponseDto> incrementarEstoque(
            @Parameter(description = "UUID do MRO", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody IncrementarEstoqueMRORequest request) {
        log.info("Incrementando estoque do MRO ID: {}, Quantidade: {}", id, request.getQuantidade());
        
        com.techchallenge.oficina.administrativo.application.usecases.commands.ReporEstoqueMROCommand command = 
                new com.techchallenge.oficina.administrativo.application.usecases.commands.ReporEstoqueMROCommand(id, request.getQuantidade());
        
        MROResponse response = reporEstoqueMROUseCase.execute(command);
        MROResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @PostMapping("/{id}/estoque/retirar")
    @Operation(summary = "Retirar estoque do MRO", description = "Remove uma quantidade do estoque atual do MRO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Estoque retirado com sucesso",
                    content = @Content(schema = @Schema(implementation = MROResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição ou estoque insuficiente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MRO não encontrado")
    })
    public ResponseEntity<MROResponseDto> retirarEstoque(
            @Parameter(description = "UUID do MRO", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody RetirarEstoqueMRORequest request) {
        log.info("Retirando estoque do MRO ID: {}, Quantidade: {}", id, request.getQuantidade());
        
        com.techchallenge.oficina.administrativo.application.usecases.commands.DebitarEstoqueMROCommand command = 
                new com.techchallenge.oficina.administrativo.application.usecases.commands.DebitarEstoqueMROCommand(id, request.getQuantidade());
        
        MROResponse response = debitarEstoqueMROUseCase.execute(command);
        MROResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
}
