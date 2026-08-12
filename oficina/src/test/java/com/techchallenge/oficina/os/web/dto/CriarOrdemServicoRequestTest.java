package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.commands.CriarOrdemServicoCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - CriarOrdemServicoRequest")
class CriarOrdemServicoRequestTest {

    private CriarOrdemServicoRequest request;

    @BeforeEach
    void setUp() {
        request = new CriarOrdemServicoRequest();
    }

    @Test
    @DisplayName("Deve criar CriarOrdemServicoCommand com dados válidos")
    void deveCriarCriarOrdemServicoCommandComDadosValidos() {
        // Arrange
        String cpfOuCnpj = "52998224725";
        String placa = "ABC1234";
        List<String> codigosServico = List.of("SVC001");
        request.setCpfOuCnpj(cpfOuCnpj);
        request.setPlaca(placa);
        request.setCodigosServico(codigosServico);

        // Act
        CriarOrdemServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(cpfOuCnpj, command.getCpfOuCnpj());
        assertEquals(placa, command.getPlaca());
        assertEquals(codigosServico, command.getCodigosServico());
    }

    @Test
    @DisplayName("Deve permitir definir e obter cpfOuCnpj")
    void devePermitirDefinirEObterCpfOuCnpj() {
        // Arrange
        String cpfOuCnpj = "52998224725";

        // Act
        request.setCpfOuCnpj(cpfOuCnpj);

        // Assert
        assertEquals(cpfOuCnpj, request.getCpfOuCnpj());
    }

    @Test
    @DisplayName("Deve permitir definir e obter placa")
    void devePermitirDefinirEObterPlaca() {
        // Arrange
        String placa = "ABC1234";

        // Act
        request.setPlaca(placa);

        // Assert
        assertEquals(placa, request.getPlaca());
    }

    @Test
    @DisplayName("Deve permitir atualizar cpfOuCnpj múltiplas vezes")
    void devePermitirAtualizarCpfOuCnpjMultiplasVezes() {
        // Act & Assert
        String cpfOuCnpj1 = "52998224725";
        String cpfOuCnpj2 = "12345678901";

        request.setCpfOuCnpj(cpfOuCnpj1);
        assertEquals(cpfOuCnpj1, request.getCpfOuCnpj());

        request.setCpfOuCnpj(cpfOuCnpj2);
        assertEquals(cpfOuCnpj2, request.getCpfOuCnpj());
    }

    @Test
    @DisplayName("Deve permitir atualizar placa múltiplas vezes")
    void devePermitirAtualizarPlacaMultiplasVezes() {
        // Act & Assert
        String placa1 = "ABC1234";
        String placa2 = "XYZ5678";

        request.setPlaca(placa1);
        assertEquals(placa1, request.getPlaca());

        request.setPlaca(placa2);
        assertEquals(placa2, request.getPlaca());
    }

    @Test
    @DisplayName("Deve criar command com cpfOuCnpj específico")
    void deveCriarCommandComCpfOuCnpjEspecifico() {
        // Arrange
        String cpfOuCnpj = "52998224725";
        String placa = "ABC1234";
        List<String> codigosServico = List.of("SVC001");
        request.setCpfOuCnpj(cpfOuCnpj);
        request.setPlaca(placa);
        request.setCodigosServico(codigosServico);

        // Act
        CriarOrdemServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(cpfOuCnpj, command.getCpfOuCnpj());
    }

    @Test
    @DisplayName("Deve criar command com itens MRO")
    void deveCriarCommandComItensMRO() {
        // Arrange
        String cpfOuCnpj = "52998224725";
        String placa = "ABC1234";
        List<String> codigosServico = List.of("SVC001");
        CriarOrdemServicoRequest.ItemMRORequest itemMRO = new CriarOrdemServicoRequest.ItemMRORequest();
        itemMRO.setCodigoMro("PC001");
        itemMRO.setQuantidade(2);
        
        request.setCpfOuCnpj(cpfOuCnpj);
        request.setPlaca(placa);
        request.setCodigosServico(codigosServico);
        request.setItensMRO(List.of(itemMRO));

        // Act
        CriarOrdemServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(1, command.getItensMRO().size());
        assertEquals("PC001", command.getItensMRO().get(0).getCodigoMro());
        assertEquals(2, command.getItensMRO().get(0).getQuantidade());
    }
}
