package com.techchallenge.oficina.sharedkernel.application.ports;

import com.techchallenge.oficina.sharedkernel.application.usecases.ports.input.CadastrarSenhaClienteInput;
import com.techchallenge.oficina.sharedkernel.web.dto.CadastrarSenhaRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Gerenciamento de senha para clientes via CPF ou CNPJ.
 *
 * Fluxo:
 *   1. Cliente cadastra ou redefine a senha via POST /v1/auth/cliente/senha
 *   2. O login de clientes passa a ser feito em /v1/auth/login
 */
@RestController
@RequestMapping("/v1/auth/cliente")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticação - Cliente", description = "Gerenciamento de senha de clientes via CPF ou CNPJ")
public class AuthClienteController {

    private final CadastrarSenhaClienteInput cadastrarSenhaClienteInput;

    // -------------------------------------------------------------------------
    // Cadastro / reset de senha
    // -------------------------------------------------------------------------

    @PostMapping("/senha")
    @Operation(
            summary = "Cadastrar ou redefinir senha do cliente",
            description = """
                    Permite que o cliente defina (ou redefina) sua senha de acesso
                    usando CPF ou CNPJ como identificador. O documento deve estar cadastrado e ativo.
                    A senha é armazenada como hash BCrypt — nunca em texto plano.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha cadastrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado ou cliente inativo"),
            @ApiResponse(responseCode = "400", description = "Documento inválido")
    })
    public ResponseEntity<Void> cadastrarSenha(
            @Valid @RequestBody CadastrarSenhaRequestDto request) {
        cadastrarSenhaClienteInput.execute(request.toCommand());
        log.info("Senha cadastrada para cliente com documento: {}***", request.getCpf().replaceAll("\\D", "").substring(0, 3));
        return ResponseEntity.noContent().build();
    }
}
