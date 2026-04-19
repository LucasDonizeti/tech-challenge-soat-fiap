package com.techchallenge.oficina.os.web.controllers;

import com.techchallenge.oficina.os.application.usecases.*;
import com.techchallenge.oficina.os.application.usecases.commands.AtualizarDadosServicoCommand;
import com.techchallenge.oficina.os.application.usecases.commands.AtualizarPrecoServicoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.os.web.dto.*;
import com.techchallenge.oficina.os.web.mappers.ServicoWebMapper;
import com.techchallenge.oficina.sharedkernel.common.PageableValidator;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/servicos")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Serviço", description = "Endpoints de gestão de serviços")
@SecurityRequirement(name = "Bearer Authentication")
public class ServicoController {
    
    private final CriarServicoUseCase criarServicoUseCase;
    private final BuscarServicoUseCase buscarServicoUseCase;
    private final ListarServicosUseCase listarServicosUseCase;
    private final InativarServicoUseCase inativarServicoUseCase;
    private final AtivarServicoUseCase ativarServicoUseCase;
    private final AtualizarPrecoServicoUseCase atualizarPrecoServicoUseCase;
    private final AtualizarDadosServicoUseCase atualizarDadosServicoUseCase;
    private final ServicoWebMapper mapper;
    private final PageableValidator pageableValidator;
    
    @PostMapping
    @Operation(summary = "Criar novo serviço", description = "Cadastra um novo serviço no sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Serviço criado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição")
    })
    public ResponseEntity<ServicoResponseDto> criar(@Valid @RequestBody CriarServicoRequest request) {
        log.info("Recebendo requisição para criar serviço: {}", request.getNome());
        
        ServicoResponse response = criarServicoUseCase.execute(request.toCommand());
        ServicoResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço por ID", description = "Retorna os dados de um serviço específico pelo seu UUID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Serviço encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<ServicoResponseDto> buscar(@PathVariable UUID id) {
        log.info("Buscando serviço por ID: {}", id);
        
        ServicoResponse response = buscarServicoUseCase.execute(id);
        ServicoResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping
    @Operation(summary = "Listar serviços", description = "Retorna uma lista paginada de todos os serviços")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de serviços retornada com sucesso")
    })
    public ResponseEntity<Page<ServicoResponseDto>> listar(Pageable pageable) {
        log.info("Listando serviços com paginação");

        // Validar e limitar os campos de ordenação para evitar erros de Sort
        Set<String> allowedFields = Set.of("nome", "preco", "ativo", "criadoEm", "atualizadoEm");
        Pageable validatedPageable = pageableValidator.validate(pageable, allowedFields);

        Page<ServicoResponse> responses = listarServicosUseCase.execute(validatedPageable);
        Page<ServicoResponseDto> dtos = mapper.toDtoPage(responses);

        return ResponseEntity.ok(dtos);
    }
    
    @PatchMapping("/{id}/inativar")
    @Operation(summary = "Inativar serviço", description = "Inativa um serviço existente no sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Serviço inativado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<ServicoResponseDto> inativar(@PathVariable UUID id) {
        log.info("Inativando serviço ID: {}", id);
        
        ServicoResponse response = inativarServicoUseCase.execute(id);
        ServicoResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar serviço", description = "Ativa um serviço inativado no sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Serviço ativado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<ServicoResponseDto> ativar(@PathVariable UUID id) {
        log.info("Ativando serviço ID: {}", id);
        
        ServicoResponse response = ativarServicoUseCase.execute(id);
        ServicoResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/{id}/preco")
    @Operation(summary = "Atualizar preço do serviço", description = "Atualiza o preço de um serviço existente")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Preço atualizado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<ServicoResponseDto> atualizarPreco(
            @Parameter(description = "UUID do serviço") @PathVariable UUID id,
            @Valid @RequestBody AtualizarPrecoServicoRequest request) {
        log.info("Atualizando preço do serviço ID: {}", id);
        
        ServicoResponse response = atualizarPrecoServicoUseCase.execute(id, request.toCommand());
        ServicoResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/{id}/dados")
    @Operation(summary = "Atualizar dados do serviço", description = "Atualiza o nome e descrição de um serviço existente")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dados atualizados com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<ServicoResponseDto> atualizarDados(
            @Parameter(description = "UUID do serviço") @PathVariable UUID id,
            @Valid @RequestBody AtualizarDadosServicoRequest request) {
        log.info("Atualizando dados do serviço ID: {}", id);
        
        ServicoResponse response = atualizarDadosServicoUseCase.execute(id, request.toCommand());
        ServicoResponseDto dto = mapper.toDto(response);
        
        return ResponseEntity.ok(dto);
    }
}
