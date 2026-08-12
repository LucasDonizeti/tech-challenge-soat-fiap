package com.techchallenge.oficina.os.web.controllers;

import com.techchallenge.oficina.os.application.usecases.commands.*;
import com.techchallenge.oficina.os.application.usecases.ports.input.*;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.web.dto.*;
import com.techchallenge.oficina.os.web.presenters.OrdemServicoPresenter;
import com.techchallenge.oficina.sharedkernel.common.PageableValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@RestController
@Transactional
@RequestMapping("/v1/os")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Ordem de Serviço", description = "Endpoints de gestão de ordens de serviço")
public class OrdemServicoController {

    private final CriarOrdemServicoInpuit criarOrdemServicoInpuit;
    private final ListarOrdensServicoInput listarOrdensServicoInput;
    private final BuscarOrdemServicoInput buscarOrdemServicoInput;
    private final AdicionarServicoOrdemInput adicionarServicoOrdemInput;
    private final RemoverServicoOrdemInput removerServicoOrdemInput;
    private final AdicionarMROServicoInput adicionarMROServicoInput;
    private final RemoverMROServicoInput removerMROServicoInput;
    private final AtualizarQuantidadeMROInput atualizarQuantidadeMROInput;
    private final EnviarParaDiagnosticoInput enviarParaDiagnosticoInput;
    private final EnviarOrcamentoAoClienteInput enviarOrcamentoAoClienteInput;
    private final AtualizarObservacoesServicoInput atualizarObservacoesServicoInput;
    private final IniciarServicoInput iniciarServicoInput;
    private final ConcluirServicoInput concluirServicoInput;
    private final CancelarServicoInput cancelarServicoInput;
    private final EntregarOrdemServicoInput entregarOrdemServicoInput;
    private final CalcularTempoMedioExecucaoInput calcularTempoMedioExecucaoInput;
    private final AprovarOrcamentoInput aprovarOrcamentoInput;
    private final RecusarOrcamentoInput recusarOrcamentoInput;
    private final OrdemServicoPresenter presenter;
    private final PageableValidator pageableValidator;

    @PostMapping
    @Operation(summary = "Abrir ordem de serviço",
            description = "Cria uma nova OS com status RECEBIDA. Recebe CPF/CNPJ do cliente, placa do veículo, " +
                          "lista de códigos de serviços e lista de códigos de MRO (peças/insumos). " +
                          "Calcula o orçamento automaticamente e retorna o UUID da OS junto com o valor total.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Ordem de serviço criada com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente, veículo, serviço ou MRO não encontrado")
    })
    public ResponseEntity<OrdemServicoResponseDto> criar(@Valid @RequestBody CriarOrdemServicoRequest request) {
        log.info("Recebendo requisição para criar ordem de serviço: cpfOuCnpj={}, placa={}",
                request.getCpfOuCnpj(), request.getPlaca());

        OrdemServicoResponse response = criarOrdemServicoInpuit.execute(request.toCommand());
        OrdemServicoResponseDto dto = presenter.prepararViewModel(response);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ordem de serviço por ID", description = "Retorna os detalhes de uma ordem de serviço específica")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ordem de serviço encontrada",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada")
    })
    public ResponseEntity<OrdemServicoResponseDto> buscarPorId(
            @Parameter(description = "ID da ordem de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        log.info("Buscando ordem de serviço por ID: ordemServicoId={}", id);
        
        OrdemServicoResponse response = buscarOrdemServicoInput.execute(id);
        OrdemServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @Operation(summary = "Listar ordens de serviço", description = "Retorna uma lista paginada de ordens de serviço com filtros opcionais")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de ordens de serviço retornada com sucesso")
    })
    public ResponseEntity<Page<OrdemServicoResponseDto>> listar(
            @Parameter(description = "ID do cliente para filtro", example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestParam(required = false) UUID clienteId,
            @Parameter(description = "ID do veículo para filtro", example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestParam(required = false) UUID veiculoId,
            @Parameter(description = "Status da ordem de serviço para filtro (RECEBIDA, EM_DIAGNOSTICO, AGUARDANDO_APROVACAO, EM_EXECUCAO, FINALIZADA, ENTREGUE)", example = "RECEBIDA")
            @RequestParam(required = false) StatusOS status,
            @Parameter(description = "Data de início para filtro (dataCriacao >= dataInicio)", example = "2024-01-01T00:00:00")
            @RequestParam(required = false) LocalDateTime dataInicio,
            @Parameter(description = "Data de fim para filtro (dataCriacao <= dataFim)", example = "2024-12-31T23:59:59")
            @RequestParam(required = false) LocalDateTime dataFim,
            @Parameter(description = "Número da página (padrão: 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página (padrão: 10)", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenação (ex: dataCriacao, status)", example = "dataCriacao")
            @RequestParam(defaultValue = "dataCriacao") String sort,
            Pageable pageable) {
        log.info("Listando ordens de serviço com filtros: clienteId={}, veiculoId={}, status={}, dataInicio={}, dataFim={}", 
                clienteId, veiculoId, status, dataInicio, dataFim);

        // Validar e limitar os campos de ordenação para evitar erros de Sort
        Set<String> allowedFields = Set.of("dataCriacao", "status", "clienteId", "veiculoId");
        Pageable validatedPageable = pageableValidator.validate(pageable, allowedFields);

        Page<OrdemServicoResponse> responses = listarOrdensServicoInput.execute(
                clienteId, veiculoId, status, dataInicio, dataFim, validatedPageable);
        Page<OrdemServicoResponseDto> dtos = presenter.prepararViewModelPage(responses);

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{id}/servicos")
    @Operation(summary = "Adicionar serviço à ordem de serviço", description = "Adiciona um serviço existente à ordem de serviço")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Serviço adicionado com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço ou serviço não encontrado")
    })
    public ResponseEntity<OrdemServicoResponseDto> adicionarServico(
            @Parameter(description = "ID da ordem de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody AdicionarServicoOrdemRequest request) {
        log.info("Adicionando serviço à ordem de serviço: ordemServicoId={}, servicoId={}", id, request.getServicoId());
        
        OrdemServicoResponse response = adicionarServicoOrdemInput.execute(request.toCommand(id));
        OrdemServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}/servicos/{itemServicoId}")
    @Operation(summary = "Remover serviço da ordem de serviço", description = "Remove um serviço da ordem de serviço")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Serviço removido com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço ou item de serviço não encontrado")
    })
    public ResponseEntity<OrdemServicoResponseDto> removerServico(
            @Parameter(description = "ID da ordem de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id,
            @Parameter(description = "ID do item de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID itemServicoId) {
        log.info("Removendo serviço da ordem de serviço: ordemServicoId={}, itemServicoId={}", id, itemServicoId);
        
        RemoverServicoOrdemCommand command = new RemoverServicoOrdemCommand(id, itemServicoId);
        OrdemServicoResponse response = removerServicoOrdemInput.execute(command);
        OrdemServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/mros")
    @Operation(summary = "Adicionar MRO ao serviço", description = "Adiciona um item de MRO a um serviço dentro da ordem de serviço")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "MRO adicionado com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço, item de serviço ou MRO não encontrado")
    })
    public ResponseEntity<OrdemServicoResponseDto> adicionarMRO(
            @Parameter(description = "ID da ordem de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody AdicionarMROServicoRequest request) {
        log.info("Adicionando MRO ao serviço: ordemServicoId={}, itemServicoId={}, mroId={}, quantidade={}", 
                id, request.getItemServicoId(), request.getMroId(), request.getQuantidade());
        
        OrdemServicoResponse response = adicionarMROServicoInput.execute(request.toCommand(id));
        OrdemServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}/servicos/{itemServicoId}/mros/{itemMroId}")
    @Operation(summary = "Remover MRO do serviço", description = "Remove um item de MRO de um serviço dentro da ordem de serviço")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "MRO removido com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço, item de serviço ou item de MRO não encontrado")
    })
    public ResponseEntity<OrdemServicoResponseDto> removerMRO(
            @Parameter(description = "ID da ordem de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id,
            @Parameter(description = "ID do item de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID itemServicoId,
            @Parameter(description = "ID do item de MRO", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID itemMroId) {
        log.info("Removendo MRO do serviço: ordemServicoId={}, itemServicoId={}, itemMroId={}", 
                id, itemServicoId, itemMroId);
        
        RemoverMROServicoCommand command = new RemoverMROServicoCommand(id, itemServicoId, itemMroId);
        OrdemServicoResponse response = removerMROServicoInput.execute(command);
        OrdemServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/mros/quantidade")
    @Operation(summary = "Atualizar quantidade de MRO", description = "Atualiza a quantidade de um item de MRO em um serviço")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quantidade atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço, item de serviço ou item de MRO não encontrado")
    })
    public ResponseEntity<OrdemServicoResponseDto> atualizarQuantidadeMRO(
            @Parameter(description = "ID da ordem de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarQuantidadeMRORequest request) {
        log.info("Atualizando quantidade de MRO: ordemServicoId={}, itemServicoId={}, itemMroId={}, novaQuantidade={}", 
                id, request.getItemServicoId(), request.getItemMroId(), request.getQuantidade());
        
        OrdemServicoResponse response = atualizarQuantidadeMROInput.execute(request.toCommand(id));
        OrdemServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/enviar-para-diagnostico")
    @Operation(summary = "Enviar ordem de serviço para diagnóstico", description = "Envia a ordem de serviço do status RECEBIDA para EM_DIAGNOSTICO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ordem de serviço enviada para diagnóstico com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Status inválido ou OS sem serviços"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada")
    })
    public ResponseEntity<OrdemServicoResponseDto> enviarParaDiagnostico(
            @Parameter(description = "ID da ordem de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        log.info("Enviando ordem de serviço para diagnóstico: ordemServicoId={}", id);
        
        EnviarParaDiagnosticoCommand command = new EnviarParaDiagnosticoCommand(id);
        OrdemServicoResponse response = enviarParaDiagnosticoInput.execute(command);
        OrdemServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/enviar-orcamento-ao-cliente")
    @Operation(summary = "Enviar orçamento ao cliente", description = "Envia a ordem de serviço do status EM_DIAGNOSTICO para AGUARDANDO_APROVACAO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orçamento enviado ao cliente com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Status inválido ou OS sem serviços"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada")
    })
    public ResponseEntity<OrdemServicoResponseDto> enviarOrcamentoAoCliente(
            @Parameter(description = "ID da ordem de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        log.info("Enviando orçamento ao cliente: ordemServicoId={}", id);
        
        EnviarOrcamentoAoClienteCommand command = new EnviarOrcamentoAoClienteCommand(id);
        OrdemServicoResponse response = enviarOrcamentoAoClienteInput.execute(command);
        OrdemServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/servicos/{itemServicoId}/observacoes")
    @Operation(summary = "Atualizar observações do serviço", description = "Atualiza as observações de um item de serviço na ordem de serviço")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Observações atualizadas com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos na requisição ou status inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço ou item de serviço não encontrado")
    })
    public ResponseEntity<OrdemServicoResponseDto> atualizarObservacoesServico(
            @Parameter(description = "ID da ordem de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id,
            @Parameter(description = "ID do item de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID itemServicoId,
            @Valid @RequestBody AtualizarObservacoesServicoRequest request) {
        log.info("Atualizando observações do serviço: ordemServicoId={}, itemServicoId={}", id, itemServicoId);
        
        request.setItemServicoId(itemServicoId);
        OrdemServicoResponse response = atualizarObservacoesServicoInput.execute(request.toCommand(id));
        OrdemServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/servicos/{itemServicoId}/iniciar")
    @Operation(summary = "Iniciar serviço", description = "Inicia um item de serviço, mudando seu status para EM_ANDAMENTO e debitando o estoque dos MROs associados")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Serviço iniciado com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Transição de status inválida ou estoque insuficiente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço ou item de serviço não encontrado")
    })
    public ResponseEntity<OrdemServicoResponseDto> iniciarServico(
            @Parameter(description = "ID da ordem de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id,
            @Parameter(description = "ID do item de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID itemServicoId) {
        log.info("Iniciando serviço: ordemServicoId={}, itemServicoId={}", id, itemServicoId);
        
        IniciarServicoCommand command = IniciarServicoCommand.builder()
                .ordemServicoId(id)
                .itemServicoId(itemServicoId)
                .build();
        
        OrdemServicoResponse response = iniciarServicoInput.execute(command);
        OrdemServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/servicos/{itemServicoId}/concluir")
    @Operation(summary = "Concluir serviço", description = "Conclui um item de serviço, mudando seu status para CONCLUIDO. Se todos serviços estiverem concluídos, a OS é finalizada automaticamente")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Serviço concluído com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Transição de status inválida"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço ou item de serviço não encontrado")
    })
    public ResponseEntity<OrdemServicoResponseDto> concluirServico(
            @Parameter(description = "ID da ordem de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id,
            @Parameter(description = "ID do item de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID itemServicoId) {
        log.info("Concluindo serviço: ordemServicoId={}, itemServicoId={}", id, itemServicoId);
        
        ConcluirServicoCommand command = ConcluirServicoCommand.builder()
                .ordemServicoId(id)
                .itemServicoId(itemServicoId)
                .build();
        
        OrdemServicoResponse response = concluirServicoInput.execute(command);
        OrdemServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/servicos/{itemServicoId}/cancelar")
    @Operation(summary = "Cancelar serviço", description = "Cancela um item de serviço, mudando seu status para CANCELADO")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Serviço cancelado com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Transição de status inválida"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço ou item de serviço não encontrado")
    })
    public ResponseEntity<OrdemServicoResponseDto> cancelarServico(
            @Parameter(description = "ID da ordem de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id,
            @Parameter(description = "ID do item de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID itemServicoId) {
        log.info("Cancelando serviço: ordemServicoId={}, itemServicoId={}", id, itemServicoId);
        
        CancelarServicoCommand command = CancelarServicoCommand.builder()
                .ordemServicoId(id)
                .itemServicoId(itemServicoId)
                .build();
        
        OrdemServicoResponse response = cancelarServicoInput.execute(command);
        OrdemServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/entregar")
    @Operation(summary = "Entregar ordem de serviço", description = "Entrega a ordem de serviço, mudando seu status de FINALIZADA para ENTREGUE")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ordem de serviço entregue com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Transição de status inválida"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada")
    })
    public ResponseEntity<OrdemServicoResponseDto> entregar(
            @Parameter(description = "ID da ordem de serviço", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        log.info("Entregando ordem de serviço: ordemServicoId={}", id);
        
        EntregarOrdemServicoCommand command = new EntregarOrdemServicoCommand(id);
        OrdemServicoResponse response = entregarOrdemServicoInput.execute(command);
        OrdemServicoResponseDto dto = presenter.prepararViewModel(response);
        
        return ResponseEntity.ok(dto);
    }



    // -------------------------------------------------------------------------
    // Endpoint 1: Callback externo de decisão de orçamento (Aprovação / Recusa)
    // -------------------------------------------------------------------------

    @PostMapping("/{id}/decisao-orcamento")
    @Operation(
            summary = "Callback de decisão de orçamento",
            description = """
                    Endpoint de callback externo que recebe a decisão do cliente sobre o orçamento.
                    
                    **APROVADO**: transiciona a OS de AGUARDANDO_APROVACAO para EM_EXECUCAO e dispara notificação de início dos serviços.
                    
                    **RECUSADO**: transiciona a OS para CANCELADA e dispara notificação informando o cancelamento.
                    
                    Em ambos os casos um evento de domínio é emitido e processado de forma assíncrona via Observer/Domain Events,
                    gerando um log de notificação ao cliente (simulação de e-mail via ConsoleNotificacaoService).
                    """)
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Decisão processada com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Decisão inválida ou OS não está aguardando aprovação"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Ordem de serviço não encontrada")
    })
    public ResponseEntity<OrdemServicoResponseDto> processarDecisaoOrcamento(
            @Parameter(description = "ID da ordem de serviço", required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,
            @Valid @RequestBody DecisaoOrcamentoRequest request) {

        log.info("Recebendo decisão de orçamento via callback: ordemServicoId={}, decisao={}",
                id, request.getDecisao());

        OrdemServicoResponse response;

        if (request.getDecisao() == DecisaoOrcamentoRequest.Decisao.APROVADO) {
            response = aprovarOrcamentoInput.execute(new AprovarOrcamentoCommand(id));
            log.info("Orçamento APROVADO via callback: ordemServicoId={}", id);
        } else {
            response = recusarOrcamentoInput.execute(
                    new RecusarOrcamentoCommand(id, request.getMotivo()));
            log.info("Orçamento RECUSADO via callback: ordemServicoId={}, motivo={}",
                    id, request.getMotivo());
        }

        return ResponseEntity.ok(presenter.prepararViewModel(response));
    }

    // -------------------------------------------------------------------------
    // Endpoint 2: Simulação/mock de notificação de mudança de status
    // -------------------------------------------------------------------------

    @PostMapping("/{id}/notificar-status")
    @Operation(
            summary = "Simular notificação de atualização de status",
            description = """
                    Endpoint de simulação que dispara manualmente uma notificação ao cliente
                    sobre o status atual da OS (mock/log via ConsoleNotificacaoService).
                    
                    Em produção as notificações são disparadas automaticamente via Domain Events
                    (@TransactionalEventListener) sempre que o status da OS muda. Este endpoint
                    permite testar o mecanismo sem precisar avançar o fluxo da OS.
                    
                    A notificação é assíncrona e registrada no log da aplicação.
                    """)
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204", description = "Notificação disparada com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Ordem de serviço não encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Status da OS não possui notificação associada")
    })
    public ResponseEntity<Void> simularNotificacaoStatus(
            @Parameter(description = "ID da ordem de serviço", required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {

        log.info("Simulando notificação de status: ordemServicoId={}", id);

        OrdemServicoResponse os = buscarOrdemServicoInput.execute(id);
        String statusAtual = os.getStatus();

        log.info("Status atual da OS {}: {}. O sistema dispararia notificação automática " +
                "via Domain Event ao cliente {} sobre este status.",
                id, statusAtual,
                os.getCliente() != null ? os.getCliente().getNome() : "N/A");

        // As notificações reais são disparadas automaticamente pelos eventos de domínio:
        //   AGUARDANDO_APROVACAO → OrcamentoProntoEvent      → NotificacaoEventHandler#handleOrcamentoPronto
        //   EM_EXECUCAO          → ServicoIniciadoEvent      → NotificacaoEventHandler#handleServicoIniciado
        //   FINALIZADA           → ServicoFinalizadoEvent    → NotificacaoEventHandler#handleServicoFinalizado
        //   ENTREGUE             → VeiculoEntregueEvent      → NotificacaoEventHandler#handleVeiculoEntregue
        //   CANCELADA            → OrcamentoRecusadoEvent    → NotificacaoEventHandler#handleOrcamentoRecusado
        //
        // Todos delegam para NotificacaoOSService → ConsoleNotificacaoService (log simulado).

        if (statusAtual == null ||
                (statusAtual.equals(StatusOS.RECEBIDA.name()) ||
                 statusAtual.equals(StatusOS.EM_DIAGNOSTICO.name()))) {
            log.warn("Status {} não possui notificação automática associada para OS {}",
                    statusAtual, id);
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/tempo-medio-execucao")
    @Operation(summary = "Calcular tempo médio de execução", description = "Calcula o tempo médio de execução das ordens de serviço finalizadas")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Métricas calculadas com sucesso",
                    content = @Content(schema = @Schema(implementation = TempoMedioExecucaoResponseDto.class)))
    })
    public ResponseEntity<TempoMedioExecucaoResponseDto> calcularTempoMedioExecucao(
            @Parameter(description = "ID do serviço para filtro (opcional)", example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestParam(required = false) UUID servicoId,
            @Parameter(description = "Data de início para filtro (opcional)", example = "2024-01-01T00:00:00")
            @RequestParam(required = false) LocalDateTime dataInicio,
            @Parameter(description = "Data de fim para filtro (opcional)", example = "2024-12-31T23:59:59")
            @RequestParam(required = false) LocalDateTime dataFim) {
        log.info("Calculando tempo médio de execução: servicoId={}, dataInicio={}, dataFim={}", 
                servicoId, dataInicio, dataFim);
        
        TempoMedioExecucaoResponseDto response = calcularTempoMedioExecucaoInput.execute(servicoId, dataInicio, dataFim);
        
        return ResponseEntity.ok(response);
    }
}
