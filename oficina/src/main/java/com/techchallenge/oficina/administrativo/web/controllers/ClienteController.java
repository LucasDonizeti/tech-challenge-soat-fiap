package com.techchallenge.oficina.administrativo.web.controllers;

import com.techchallenge.oficina.administrativo.application.usecases.ports.input.*;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.web.dto.*;
import com.techchallenge.oficina.administrativo.web.presenters.ClientePresenter;
import com.techchallenge.oficina.sharedkernel.common.PageableValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
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
@Transactional
@RequestMapping("/v1/admin/clientes")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Cliente", description = "Endpoints de gestão de clientes")
@SecurityRequirement(name = "Bearer Authentication")
public class ClienteController {
    
    private final CriarClienteInput criarClienteInput;
    private final BuscarClienteInput buscarClienteInput;
    private final AtualizarClienteInput atualizarClienteInput;
    private final InativarClienteInput inativarClienteInput;
    private final ReativarClienteInput reativarClienteInput;
    private final DeletarClienteInput deletarClienteInput;
    private final ListarClientesInput listarClientesInput;
    private final BuscarClientesPorFiltroInput buscarClientesPorFiltroInput;
    private final ClientePresenter presenter;
    private final PageableValidator pageableValidator;
    
    @PostMapping
    @Operation(summary = "Criar novo cliente", description = "Cadastra um novo cliente no sistema. É necessário informar CPF ou CNPJ.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cliente criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ClienteResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição")
    })
        public ResponseEntity<ClienteResponseDto> criar(@Valid @RequestBody CriarClienteRequest request) {
        log.info("Recebendo requisição para criar cliente: {}", request.getNome());
        
        ClienteResponse response = criarClienteInput.execute(request.toCommand());
        ClienteResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna os dados de um cliente específico pelo seu UUID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente encontrado",
                    content = @Content(schema = @Schema(implementation = ClienteResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
        public ResponseEntity<ClienteResponseDto> buscar(
            @Parameter(description = "UUID do cliente", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        log.info("Buscando cliente por ID: {}", id);
        
        ClienteResponse response = buscarClienteInput.execute(id);
        ClienteResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping
    @Operation(summary = "Listar clientes", description = "Retorna uma lista paginada de todos os clientes")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    })
    public ResponseEntity<Page<ClienteResponseDto>> listar(
            @Parameter(description = "Número da página (padrão: 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página (padrão: 10)", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenação (ex: nome, cpf, email, status)", example = "nome")
            @RequestParam(defaultValue = "nome") String sort,
            Pageable pageable) {
        log.info("Listando clientes com paginação");

        // Validar e limitar os campos de ordenação para evitar erros de Sort
        Set<String> allowedFields = Set.of("nome", "cpf", "cnpj", "email", "status", "criadoEm", "atualizadoEm");
        Pageable validatedPageable = pageableValidator.validate(pageable, allowedFields);

        Page<ClienteResponse> responses = listarClientesInput.execute(validatedPageable);
        Page<ClienteResponseDto> dtos = presenter.prepararViewModelPage(responses);

        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/buscar")
    @Operation(summary = "Buscar clientes por filtro", description = "Retorna uma lista paginada de clientes baseada em filtros específicos")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de clientes filtrada retornada com sucesso")
    })
    public ResponseEntity<Page<ClienteResponseDto>> buscarPorFiltro(
            @Parameter(description = "Filtros de busca de clientes") @ModelAttribute ClienteFilterRequest filter,
            @Parameter(description = "Número da página (padrão: 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página (padrão: 10)", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenação (ex: nome, cpf, email, status)", example = "nome")
            @RequestParam(defaultValue = "nome") String sort,
            Pageable pageable) {
        log.info("Buscando clientes com filtros: {}", filter);
        
        Page<ClienteResponse> responses = buscarClientesPorFiltroInput.execute(filter, pageable);
        Page<ClienteResponseDto> dtos = presenter.prepararViewModelPage(responses);
        
        return ResponseEntity.ok(dtos);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = ClienteResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
        public ResponseEntity<ClienteResponseDto> atualizar(
            @Parameter(description = "UUID do cliente", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id, 
            @Valid @RequestBody AtualizarClienteRequest request) {
        log.info("Atualizando cliente ID: {}", id);
        
        ClienteResponse response = atualizarClienteInput.execute(id, request.toCommand());
        ClienteResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/{id}/inativar")
    @Operation(summary = "Inativar cliente", description = "Inativa um cliente existente no sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente inativado com sucesso",
                    content = @Content(schema = @Schema(implementation = ClienteResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
        public ResponseEntity<ClienteResponseDto> inativar(
            @Parameter(description = "UUID do cliente", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        log.info("Inativando cliente ID: {}", id);
        
        ClienteResponse response = inativarClienteInput.execute(id);
        ClienteResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativar cliente", description = "Reativa um cliente inativado no sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente reativado com sucesso",
                    content = @Content(schema = @Schema(implementation = ClienteResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
        public ResponseEntity<ClienteResponseDto> reativar(
            @Parameter(description = "UUID do cliente", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        log.info("Reativando cliente ID: {}", id);
        
        ClienteResponse response = reativarClienteInput.execute(id);
        ClienteResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar cliente", description = "Remove permanentemente um cliente do sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
        public ResponseEntity<Void> deletar(
            @Parameter(description = "UUID do cliente", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        log.info("Excluindo cliente ID: {}", id);

        deletarClienteInput.execute(id);
        
        return ResponseEntity.noContent().build();
    }
}
