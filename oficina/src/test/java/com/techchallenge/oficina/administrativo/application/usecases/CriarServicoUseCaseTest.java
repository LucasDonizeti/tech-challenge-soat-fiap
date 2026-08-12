package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarServicoCommand;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.ServicoGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de CriarServicoUseCase - Application Layer")
class CriarServicoUseCaseTest {

    @Mock
    private ServicoGateway gateway;

    @InjectMocks
    private CriarServicoUseCase criarServicoUseCase;

    private CriarServicoCommand command;
    private Servico servico;

    @BeforeEach
    void setUp() {
        String nome = "Troca de Óleo";
        String descricao = "Troca de óleo do motor";
        BigDecimal preco = new BigDecimal("150.00");
        command = new CriarServicoCommand(nome, "COD-TESTE", descricao, preco);
        servico = Servico.criar(nome, "COD-TESTE", descricao, preco);
    }

    @Test
    @DisplayName("Deve criar serviço com sucesso")
    void deveCriarServicoComSucesso() {
        // Arrange
        when(gateway.save(any(Servico.class))).thenReturn(servico);

        // Act
        ServicoResponse response = criarServicoUseCase.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals("Troca de Óleo", response.getNome());
        assertEquals("Troca de óleo do motor", response.getDescricao());
        assertEquals(new BigDecimal("150.00"), response.getPreco());
        assertTrue(response.getAtivo());
        verify(gateway, times(1)).save(any(Servico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando command é nulo")
    void deveLancarExcecaoQuandoCommandNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> criarServicoUseCase.execute(null));
        verify(gateway, never()).save(any(Servico.class));
    }
}
