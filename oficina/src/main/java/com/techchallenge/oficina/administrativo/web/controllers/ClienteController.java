package com.techchallenge.oficina.administrativo.web.controllers;

import com.techchallenge.oficina.administrativo.application.usecases.*;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import com.techchallenge.oficina.administrativo.web.dto.*;
import com.techchallenge.oficina.administrativo.web.mappers.ClienteWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/clientes")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Cliente", description = "Endpoints de gestão de clientes")
@SecurityRequirement(name = "Bearer Authentication")
public class ClienteController {
    
    private final CriarClienteUseCase criarClienteUseCase;
    private final BuscarClienteUseCase buscarClienteUseCase;
    private final AtualizarClienteUseCase atualizarClienteUseCase;
    private final InativarClienteUseCase inativarClienteUseCase;
    private final ReativarClienteUseCase reativarClienteUseCase;
    private final DeletarClienteUseCase deletarClienteUseCase;
    private final ListarClientesUseCase listarClientesUseCase;
    private final BuscarClientesPorFiltroUseCase buscarClientesPorFiltroUseCase;
    private final ClienteWebMapper mapper;
    
    @PostMapping
    @Operation(summary = "Criar novo cliente", description = "Cadastra um novo cliente no sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição")
    })
        public ResponseEntity<ClienteResponseDto> criar(@Valid @RequestBody CriarClienteRequest request) {
        log.info("Recebendo requisição para criar cliente: {}", request.getNome());
        
        ClienteResponse response = criarClienteUseCase.execute(request.toCommand());
        ClienteResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna os dados de um cliente específico pelo seu UUID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
        public ResponseEntity<ClienteResponseDto> buscar(@PathVariable UUID id) {
        log.info("Buscando cliente por ID: {}", id);
        
        ClienteResponse response = buscarClienteUseCase.execute(id);
        ClienteResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping
    @Operation(summary = "Listar clientes", description = "Retorna uma lista paginada de todos os clientes")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    })
    public ResponseEntity<Page<ClienteResponseDto>> listar(Pageable pageable) {
        log.info("Listando clientes com paginação");
        
        Page<ClienteResponse> responses = listarClientesUseCase.execute(pageable);
        Page<ClienteResponseDto> dtos = mapper.toDtoPage(responses);
        
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/buscar")
    @Operation(summary = "Buscar clientes por filtro", description = "Retorna uma lista paginada de clientes baseada em filtros específicos")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de clientes filtrada retornada com sucesso")
    })
    public ResponseEntity<Page<ClienteResponseDto>> buscarPorFiltro(
            @Parameter(description = "Filtros de busca de clientes") @ModelAttribute ClienteFilterRequest filter,
            Pageable pageable) {
        log.info("Buscando clientes com filtros: {}", filter);
        
        Page<ClienteResponse> responses = buscarClientesPorFiltroUseCase.execute(filter, pageable);
        Page<ClienteResponseDto> dtos = mapper.toDtoPage(responses);
        
        return ResponseEntity.ok(dtos);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
        public ResponseEntity<ClienteResponseDto> atualizar(
            @Parameter(description = "UUID do cliente") @PathVariable UUID id, 
            @Valid @RequestBody AtualizarClienteRequest request) {
        log.info("Atualizando cliente ID: {}", id);
        
        ClienteResponse response = atualizarClienteUseCase.execute(id, request.toCommand());
        ClienteResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/{id}/inativar")
    @Operation(summary = "Inativar cliente", description = "Inativa um cliente existente no sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente inativado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
        public ResponseEntity<ClienteResponseDto> inativar(@PathVariable UUID id) {
        log.info("Inativando cliente ID: {}", id);
        
        ClienteResponse response = inativarClienteUseCase.execute(id);
        ClienteResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativar cliente", description = "Reativa um cliente inativado no sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente reativado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
        public ResponseEntity<ClienteResponseDto> reativar(@PathVariable UUID id) {
        log.info("Reativando cliente ID: {}", id);
        
        ClienteResponse response = reativarClienteUseCase.execute(id);
        ClienteResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar cliente", description = "Remove permanentemente um cliente do sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
        public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        log.info("Excluindo cliente ID: {}", id);
        
        deletarClienteUseCase.execute(id);
        
        return ResponseEntity.noContent().build();
    }
}
