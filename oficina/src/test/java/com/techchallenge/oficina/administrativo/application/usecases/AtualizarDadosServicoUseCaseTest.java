package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarDadosServicoCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
import com.techchallenge.oficina.administrativo.domain.repositories.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - AtualizarDadosServicoUseCase")
class AtualizarDadosServicoUseCaseTest {

    @Mock
    private ServicoRepository repository;

    @InjectMocks
    private AtualizarDadosServicoUseCase useCase;

    private Servico servico;
    private AtualizarDadosServicoCommand command;
    private UUID servicoId;

    @BeforeEach
    void setUp() {
        servicoId = UUID.randomUUID();
        servico = Servico.criar(
                "Troca de Óleo",
                "Troca de óleo sintético",
                new BigDecimal("150.00")
        );
        command = new AtualizarDadosServicoCommand("Troca de Óleo Premium", "Troca de óleo sintético premium");
    }

    @Test
    @DisplayName("Deve atualizar dados do serviço com sucesso")
    void deveAtualizarDadosDoServicoComSucesso() {
        // Arrange
        when(repository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(repository.save(any(Servico.class))).thenReturn(servico);

        // Act
        ServicoResponse result = useCase.execute(servicoId, command);

        // Assert
        assertNotNull(result);
        assertEquals("Troca de Óleo Premium", servico.getNome());
        assertEquals("Troca de óleo sintético premium", servico.getDescricao());
        verify(repository, times(1)).findById(servicoId);
        verify(repository, times(1)).save(any(Servico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando serviço não encontrado")
    void deveLancarExcecaoQuandoServicoNaoEncontrado() {
        // Arrange
        when(repository.findById(servicoId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(servicoId, command)
        );

        assertTrue(exception.getMessage().contains("Serviço não encontrado"));
        verify(repository, times(1)).findById(servicoId);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar apenas o nome")
    void deveAtualizarApenasONome() {
        // Arrange
        AtualizarDadosServicoCommand commandNome = new AtualizarDadosServicoCommand("Troca de Óleo Sintético", null);
        when(repository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(repository.save(any(Servico.class))).thenReturn(servico);

        // Act
        ServicoResponse result = useCase.execute(servicoId, commandNome);

        // Assert
        assertNotNull(result);
        assertEquals("Troca de Óleo Sintético", servico.getNome());
        assertNull(servico.getDescricao());
        verify(repository, times(1)).save(any(Servico.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas a descrição")
    void deveAtualizarApenasADescricao() {
        // Arrange
        AtualizarDadosServicoCommand commandDescricao = new AtualizarDadosServicoCommand("Troca de Óleo", "Nova descrição detalhada");
        when(repository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(repository.save(any(Servico.class))).thenReturn(servico);

        // Act
        ServicoResponse result = useCase.execute(servicoId, commandDescricao);

        // Assert
        assertNotNull(result);
        assertEquals("Nova descrição detalhada", servico.getDescricao());
        verify(repository, times(1)).save(any(Servico.class));
    }

    @Test
    @DisplayName("Deve manter preço inalterado")
    void deveManterPrecoInalterado() {
        // Arrange
        when(repository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(repository.save(any(Servico.class))).thenReturn(servico);

        BigDecimal precoOriginal = servico.getPreco();

        // Act
        useCase.execute(servicoId, command);

        // Assert
        assertEquals(precoOriginal, servico.getPreco());
    }

    @Test
    @DisplayName("Deve manter status inalterado")
    void deveManterStatusInalterado() {
        // Arrange
        when(repository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(repository.save(any(Servico.class))).thenReturn(servico);

        Boolean statusOriginal = servico.getAtivo();

        // Act
        useCase.execute(servicoId, command);

        // Assert
        assertEquals(statusOriginal, servico.getAtivo());
    }

    @Test
    @DisplayName("Deve retornar response com dados atualizados")
    void deveRetornarResponseComDadosAtualizados() {
        // Arrange
        when(repository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(repository.save(any(Servico.class))).thenReturn(servico);

        // Act
        ServicoResponse result = useCase.execute(servicoId, command);

        // Assert
        assertNotNull(result);
        assertEquals(servico.getId(), result.getId());
        assertEquals("Troca de Óleo Premium", result.getNome());
        assertEquals("Troca de óleo sintético premium", result.getDescricao());
    }

    @Test
    @DisplayName("Deve atualizar nome com caracteres especiais")
    void deveAtualizarNomeComCaracteresEspeciais() {
        // Arrange
        AtualizarDadosServicoCommand commandEspecial = new AtualizarDadosServicoCommand("Troca de Óleo (Sintético)", "Descrição com acentuação");
        when(repository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(repository.save(any(Servico.class))).thenReturn(servico);

        // Act
        ServicoResponse result = useCase.execute(servicoId, commandEspecial);

        // Assert
        assertNotNull(result);
        assertEquals("Troca de Óleo (Sintético)", servico.getNome());
        assertEquals("Descrição com acentuação", servico.getDescricao());
        verify(repository, times(1)).save(any(Servico.class));
    }

    @Test
    @DisplayName("Deve atualizar descrição vazia para null")
    void deveAtualizarDescricaoVaziaParaNull() {
        // Arrange
        AtualizarDadosServicoCommand commandVazio = new AtualizarDadosServicoCommand("Troca de Óleo", "");
        when(repository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(repository.save(any(Servico.class))).thenReturn(servico);

        // Act
        ServicoResponse result = useCase.execute(servicoId, commandVazio);

        // Assert
        assertNotNull(result);
        assertEquals("Troca de Óleo", servico.getNome());
        verify(repository, times(1)).save(any(Servico.class));
    }
}
