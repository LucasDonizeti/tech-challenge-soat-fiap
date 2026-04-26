package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarPrecoServicoCommand;
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
@DisplayName("Testes Unitários - AtualizarPrecoServicoUseCase")
class AtualizarPrecoServicoUseCaseTest {

    @Mock
    private ServicoRepository repository;

    @InjectMocks
    private AtualizarPrecoServicoUseCase useCase;

    private Servico servico;
    private AtualizarPrecoServicoCommand command;
    private UUID servicoId;

    @BeforeEach
    void setUp() {
        servicoId = UUID.randomUUID();
        servico = Servico.criar(
                "Troca de Óleo",
                "Troca de óleo sintético",
                new BigDecimal("150.00")
        );
        command = new AtualizarPrecoServicoCommand(new BigDecimal("180.00"));
    }

    @Test
    @DisplayName("Deve atualizar preço do serviço com sucesso")
    void deveAtualizarPrecoDoServicoComSucesso() {
        // Arrange
        when(repository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(repository.save(any(Servico.class))).thenReturn(servico);

        // Act
        ServicoResponse result = useCase.execute(servicoId, command);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("180.00"), servico.getPreco());
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
    @DisplayName("Deve atualizar preço para valor maior")
    void deveAtualizarPrecoParaValorMaior() {
        // Arrange
        AtualizarPrecoServicoCommand commandMaior = new AtualizarPrecoServicoCommand(new BigDecimal("200.00"));
        when(repository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(repository.save(any(Servico.class))).thenReturn(servico);

        // Act
        ServicoResponse result = useCase.execute(servicoId, commandMaior);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("200.00"), servico.getPreco());
        verify(repository, times(1)).save(any(Servico.class));
    }

    @Test
    @DisplayName("Deve atualizar preço para valor menor")
    void deveAtualizarPrecoParaValorMenor() {
        // Arrange
        AtualizarPrecoServicoCommand commandMenor = new AtualizarPrecoServicoCommand(new BigDecimal("100.00"));
        when(repository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(repository.save(any(Servico.class))).thenReturn(servico);

        // Act
        ServicoResponse result = useCase.execute(servicoId, commandMenor);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), servico.getPreco());
        verify(repository, times(1)).save(any(Servico.class));
    }

    @Test
    @DisplayName("Deve manter outros dados do serviço inalterados")
    void deveManterOutrosDadosDoServicoInalterados() {
        // Arrange
        when(repository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(repository.save(any(Servico.class))).thenReturn(servico);

        String nomeOriginal = servico.getNome();
        String descricaoOriginal = servico.getDescricao();
        Boolean ativoOriginal = servico.getAtivo();

        // Act
        useCase.execute(servicoId, command);

        // Assert
        assertEquals(nomeOriginal, servico.getNome());
        assertEquals(descricaoOriginal, servico.getDescricao());
        assertEquals(ativoOriginal, servico.getAtivo());
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
        assertEquals("Troca de Óleo", result.getNome());
        assertEquals(new BigDecimal("180.00"), result.getPreco());
    }

    @Test
    @DisplayName("Deve atualizar preço com valor decimal")
    void deveAtualizarPrecoComValorDecimal() {
        // Arrange
        AtualizarPrecoServicoCommand commandDecimal = new AtualizarPrecoServicoCommand(new BigDecimal("175.50"));
        when(repository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(repository.save(any(Servico.class))).thenReturn(servico);

        // Act
        ServicoResponse result = useCase.execute(servicoId, commandDecimal);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("175.50"), servico.getPreco());
        verify(repository, times(1)).save(any(Servico.class));
    }

    @Test
    @DisplayName("Deve atualizar preço com valor muito pequeno")
    void deveAtualizarPrecoComValorMuitoPequeno() {
        // Arrange
        AtualizarPrecoServicoCommand commandPequeno = new AtualizarPrecoServicoCommand(new BigDecimal("0.01"));
        when(repository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(repository.save(any(Servico.class))).thenReturn(servico);

        // Act
        ServicoResponse result = useCase.execute(servicoId, commandPequeno);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("0.01"), servico.getPreco());
        verify(repository, times(1)).save(any(Servico.class));
    }
}
