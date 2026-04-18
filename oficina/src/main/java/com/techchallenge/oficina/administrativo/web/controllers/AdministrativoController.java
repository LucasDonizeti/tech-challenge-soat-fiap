package com.techchallenge.oficina.administrativo.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin")
@Tag(name = "Administrativo", description = "Endpoints administrativos do sistema")
@SecurityRequirement(name = "Bearer Authentication")
public class AdministrativoController {

    @GetMapping("/health")
    @Operation(summary = "Verificar saúde do serviço administrativo", description = "Endpoint para verificação de saúde do serviço administrativo")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Serviço funcionando normalmente")
    })
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("service is running");
    }

    @GetMapping("/ping")
    @Operation(summary = "Ping do serviço administrativo", description = "Endpoint simples para teste de conectividade")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pong")
    })
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("GG");
    }
}
