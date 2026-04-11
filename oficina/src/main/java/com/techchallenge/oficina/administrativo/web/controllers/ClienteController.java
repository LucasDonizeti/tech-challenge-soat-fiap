package com.techchallenge.oficina.administrativo.web.controllers;

import com.techchallenge.oficina.administrativo.application.usecases.*;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import com.techchallenge.oficina.administrativo.web.dto.*;
import com.techchallenge.oficina.administrativo.web.mappers.ClienteWebMapper;
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
        public ResponseEntity<ClienteResponseDto> criar(@Valid @RequestBody CriarClienteRequest request) {
        log.info("Recebendo requisição para criar cliente: {}", request.getNome());
        
        ClienteResponse response = criarClienteUseCase.execute(request.toCommand());
        ClienteResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
    
    @GetMapping("/{id}")
        public ResponseEntity<ClienteResponseDto> buscar(@PathVariable UUID id) {
        log.info("Buscando cliente por ID: {}", id);
        
        ClienteResponse response = buscarClienteUseCase.execute(id);
        ClienteResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping
    public ResponseEntity<Page<ClienteResponseDto>> listar(Pageable pageable) {
        log.info("Listando clientes com paginação");
        
        Page<ClienteResponse> responses = listarClientesUseCase.execute(pageable);
        Page<ClienteResponseDto> dtos = mapper.toDtoPage(responses);
        
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<Page<ClienteResponseDto>> buscarPorFiltro(
            @ModelAttribute ClienteFilterRequest filter,
            Pageable pageable) {
        log.info("Buscando clientes com filtros: {}", filter);
        
        Page<ClienteResponse> responses = buscarClientesPorFiltroUseCase.execute(filter, pageable);
        Page<ClienteResponseDto> dtos = mapper.toDtoPage(responses);
        
        return ResponseEntity.ok(dtos);
    }
    
    @PutMapping("/{id}")
        public ResponseEntity<ClienteResponseDto> atualizar(
            @PathVariable UUID id, 
            @Valid @RequestBody AtualizarClienteRequest request) {
        log.info("Atualizando cliente ID: {}", id);
        
        ClienteResponse response = atualizarClienteUseCase.execute(id, request.toCommand());
        ClienteResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/{id}/inativar")
        public ResponseEntity<ClienteResponseDto> inativar(@PathVariable UUID id) {
        log.info("Inativando cliente ID: {}", id);
        
        ClienteResponse response = inativarClienteUseCase.execute(id);
        ClienteResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/{id}/reativar")
        public ResponseEntity<ClienteResponseDto> reativar(@PathVariable UUID id) {
        log.info("Reativando cliente ID: {}", id);
        
        ClienteResponse response = reativarClienteUseCase.execute(id);
        ClienteResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @DeleteMapping("/{id}")
        public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        log.info("Excluindo cliente ID: {}", id);
        
        deletarClienteUseCase.execute(id);
        
        return ResponseEntity.noContent().build();
    }
}
