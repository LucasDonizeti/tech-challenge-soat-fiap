package com.techchallenge.oficina.administrativo.web.controllers;

import com.techchallenge.oficina.administrativo.application.usecases.*;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.*;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.administrativo.web.dto.*;
import com.techchallenge.oficina.administrativo.web.mappers.ServicoWebMapper;
import com.techchallenge.oficina.administrativo.web.presenters.ServicoPresenter;
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
@RequestMapping("/v1/admin/servicos")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Serviço", description = "Endpoints de gestão de Serviços")
@SecurityRequirement(name = "Bearer Authentication")
public class ServicoController {
    
    private final CriarServicoInput criarServicoInput;
    private final BuscarServicoInput buscarServicoInput;
    private final ListarServicosInput listarServicosInput;
    private final InativarServicoInput inativarServicoInput;
    private final AtivarServicoInput ativarServicoInput;
    private final AtualizarPrecoServicoInput atualizarPrecoServicoInput;
    private final AtualizarDadosServicoInput atualizarDadosServicoInput;
    private final ServicoPresenter presenter;
    private final PageableValidator pageableValidator;
    
    @PostMapping
    @Operation(summary = "Criar novo Serviço", description = "Cadastra um novo serviço no sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Serviço criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição")
    })
    public ResponseEntity<ServicoResponseDto> criar(@Valid @RequestBody CriarServicoRequest request) {
        log.info("Recebendo requisição para criar serviço: {}", request.getNome());
        
        ServicoResponse response = criarServicoInput.execute(request.toCommand());
        ServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar Serviço por ID", description = "Retorna os dados de um serviço específico pelo seu UUID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Serviço encontrado",
                    content = @Content(schema = @Schema(implementation = ServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<ServicoResponseDto> buscar(
            @Parameter(description = "UUID do serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        log.info("Buscando serviço por ID: {}", id);
        
        ServicoResponse response = buscarServicoInput.execute(id);
        ServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping
    @Operation(summary = "Listar Serviços", description = "Retorna uma lista paginada de todos os serviços")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de serviços retornada com sucesso")
    })
    public ResponseEntity<Page<ServicoResponseDto>> listar(
            @Parameter(description = "Número da página (padrão: 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página (padrão: 10)", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenação (ex: nome, preco, ativo)", example = "nome")
            @RequestParam(defaultValue = "nome") String sort,
            Pageable pageable) {
        log.info("Listando serviços com paginação");

        // Validar e limitar os campos de ordenação para evitar erros de Sort
        Set<String> allowedFields = Set.of("nome", "preco", "ativo", "criadoEm", "atualizadoEm");
        Pageable validatedPageable = pageableValidator.validate(pageable, allowedFields);

        Page<ServicoResponse> responses = listarServicosInput.execute(validatedPageable);
        Page<ServicoResponseDto> dtos = presenter.prepararViewModelPage(responses);

        return ResponseEntity.ok(dtos);
    }
    
    @PatchMapping("/{id}/inativar")
    @Operation(summary = "Inativar Serviço", description = "Inativa um serviço existente no sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Serviço inativado com sucesso",
                    content = @Content(schema = @Schema(implementation = ServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<ServicoResponseDto> inativar(
            @Parameter(description = "UUID do serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        log.info("Inativando serviço ID: {}", id);
        
        ServicoResponse response = inativarServicoInput.execute(id);
        ServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar Serviço", description = "Ativa um serviço inativado no sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Serviço ativado com sucesso",
                    content = @Content(schema = @Schema(implementation = ServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<ServicoResponseDto> ativar(
            @Parameter(description = "UUID do serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        log.info("Ativando serviço ID: {}", id);
        
        ServicoResponse response = ativarServicoInput.execute(id);
        ServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/{id}/preco")
    @Operation(summary = "Atualizar preço do Serviço", description = "Atualiza o preço de um serviço existente")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Preço atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = ServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<ServicoResponseDto> atualizarPreco(
            @Parameter(description = "UUID do serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarPrecoServicoRequest request) {
        log.info("Atualizando preço do serviço ID: {}", id);
        
        ServicoResponse response = atualizarPrecoServicoInput.execute(id, request.toCommand());
        ServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/{id}/dados")
    @Operation(summary = "Atualizar dados do Serviço", description = "Atualiza o nome e descrição de um serviço existente")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dados atualizados com sucesso",
                    content = @Content(schema = @Schema(implementation = ServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<ServicoResponseDto> atualizarDados(
            @Parameter(description = "UUID do serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarDadosServicoRequest request) {
        log.info("Atualizando dados do serviço ID: {}", id);
        
        ServicoResponse response = atualizarDadosServicoInput.execute(id, request.toCommand());
        ServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }
}
