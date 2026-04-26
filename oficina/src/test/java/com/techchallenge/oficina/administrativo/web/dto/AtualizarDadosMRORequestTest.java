package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarDadosMROCommand;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarDadosMRORequest")
class AtualizarDadosMRORequestTest {

    @Test
    @DisplayName("Deve converter request para command com sucesso")
    void deveConverterRequestParaCommandComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();
        AtualizarDadosMRORequest request = new AtualizarDadosMRORequest();
        request.setNome("Óleo Motor 5W30");
        request.setDescricao("Óleo para motor automotivo");
        request.setTipo("INSUMO");

        // Act
        AtualizarDadosMROCommand command = request.toCommand(id);

        // Assert
        assertNotNull(command);
        assertEquals(id, command.getId());
        assertEquals("Óleo Motor 5W30", command.getNome());
        assertEquals("Óleo para motor automotivo", command.getDescricao());
        assertEquals(TipoMRO.INSUMO, command.getTipo());
    }

    @Test
    @DisplayName("Deve converter request para command com tipo PECA")
    void deveConverterRequestParaCommandComTipoPeca() {
        // Arrange
        UUID id = UUID.randomUUID();
        AtualizarDadosMRORequest request = new AtualizarDadosMRORequest();
        request.setNome("Filtro de Óleo");
        request.setDescricao("Filtro para motor");
        request.setTipo("PECA");

        // Act
        AtualizarDadosMROCommand command = request.toCommand(id);

        // Assert
        assertNotNull(command);
        assertEquals(TipoMRO.PECA, command.getTipo());
    }

    @Test
    @DisplayName("Deve converter request para command com tipo em minúsculo")
    void deveConverterRequestParaCommandComTipoMinusculo() {
        // Arrange
        UUID id = UUID.randomUUID();
        AtualizarDadosMRORequest request = new AtualizarDadosMRORequest();
        request.setNome("Produto");
        request.setTipo("insumo");

        // Act
        AtualizarDadosMROCommand command = request.toCommand(id);

        // Assert
        assertNotNull(command);
        assertEquals(TipoMRO.INSUMO, command.getTipo());
    }

    @Test
    @DisplayName("Deve converter request para command com descricao null")
    void deveConverterRequestParaCommandComDescricaoNull() {
        // Arrange
        UUID id = UUID.randomUUID();
        AtualizarDadosMRORequest request = new AtualizarDadosMRORequest();
        request.setNome("Produto");
        request.setTipo("PECA");
        request.setDescricao(null);

        // Act
        AtualizarDadosMROCommand command = request.toCommand(id);

        // Assert
        assertNotNull(command);
        assertNull(command.getDescricao());
    }

    @Test
    @DisplayName("Deve lançar exceção quando tipo é inválido")
    void deveLancarExcecaoQuandoTipoEInvalido() {
        // Arrange
        UUID id = UUID.randomUUID();
        AtualizarDadosMRORequest request = new AtualizarDadosMRORequest();
        request.setNome("Produto");
        request.setTipo("TIPO_INVALIDO");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> request.toCommand(id));
    }

    @Test
    @DisplayName("Deve lançar exceção quando tipo é null")
    void deveLancarExcecaoQuandoTipoENull() {
        // Arrange
        UUID id = UUID.randomUUID();
        AtualizarDadosMRORequest request = new AtualizarDadosMRORequest();
        request.setNome("Produto");
        request.setTipo(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> request.toCommand(id));
    }
}
