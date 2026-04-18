package com.techchallenge.oficina.administrativo.web.controllers;

import com.techchallenge.oficina.administrativo.application.usecases.*;
import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.administrativo.web.dto.CriarVeiculoRequest;
import com.techchallenge.oficina.administrativo.web.dto.VeiculoResponseDto;
import com.techchallenge.oficina.administrativo.web.mappers.VeiculoWebMapper;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/veiculos")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Veículo", description = "Endpoints de gestão de veículos")
@SecurityRequirement(name = "Bearer Authentication")
public class VeiculoController {

    private final CriarVeiculoUseCase criarVeiculoUseCase;
    private final BuscarVeiculoUseCase buscarVeiculoUseCase;
    private final BuscarVeiculosPorClienteUseCase buscarVeiculosPorClienteUseCase;
    private final InativarVeiculoUseCase inativarVeiculoUseCase;
    private final ReativarVeiculoUseCase reativarVeiculoUseCase;
    private final ListarVeiculosUseCase listarVeiculosUseCase;
    private final VeiculoWebMapper mapper;

    @PostMapping
    @Operation(summary = "Criar novo veículo", description = "Cadastra um novo veículo vinculado a um cliente existente")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Veículo criado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<VeiculoResponseDto> criar(@Valid @RequestBody CriarVeiculoRequest request) {
        log.info("Recebendo requisição para criar veículo: Placa={}, ClienteID={}",
                request.getPlaca(), request.getClienteId());

        VeiculoResponse response = criarVeiculoUseCase.execute(request.toCommand());
        VeiculoResponseDto dto = mapper.toDto(response);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar veículo por ID", description = "Retorna os dados de um veículo específico pelo seu UUID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Veículo encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    })
    public ResponseEntity<VeiculoResponseDto> buscar(@PathVariable UUID id) {
        log.info("Buscando veículo por ID: {}", id);

        VeiculoResponse response = buscarVeiculoUseCase.execute(id);
        VeiculoResponseDto dto = mapper.toDto(response);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Buscar veículos por cliente", description = "Retorna uma lista de veículos de um cliente específico")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de veículos retornada com sucesso")
    })
    public ResponseEntity<List<VeiculoResponseDto>> buscarPorCliente(
            @Parameter(description = "UUID do cliente") @PathVariable UUID clienteId) {
        log.info("Buscando veículos do cliente: ID={}", clienteId);

        List<VeiculoResponse> responses = buscarVeiculosPorClienteUseCase.execute(clienteId);
        List<VeiculoResponseDto> dtos = mapper.toDtoList(responses);

        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    @Operation(summary = "Listar veículos", description = "Retorna uma lista paginada de todos os veículos")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de veículos retornada com sucesso")
    })
    public ResponseEntity<Page<VeiculoResponseDto>> listar(Pageable pageable) {
        log.info("Listando veículos com paginação");

        Page<VeiculoResponse> responses = listarVeiculosUseCase.execute(pageable);
        Page<VeiculoResponseDto> dtos = mapper.toDtoPage(responses);

        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/{id}/inativar")
    @Operation(summary = "Inativar veículo", description = "Inativa um veículo existente no sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Veículo inativado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    })
    public ResponseEntity<VeiculoResponseDto> inativar(@PathVariable UUID id) {
        log.info("Inativando veículo ID: {}", id);

        VeiculoResponse response = inativarVeiculoUseCase.execute(id);
        VeiculoResponseDto dto = mapper.toDto(response);

        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativar veículo", description = "Reativa um veículo inativado no sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Veículo reativado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    })
    public ResponseEntity<VeiculoResponseDto> reativar(@PathVariable UUID id) {
        log.info("Reativando veículo ID: {}", id);

        VeiculoResponse response = reativarVeiculoUseCase.execute(id);
        VeiculoResponseDto dto = mapper.toDto(response);

        return ResponseEntity.ok(dto);
    }
}
