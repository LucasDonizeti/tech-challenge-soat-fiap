package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.DebitarEstoqueMROUseCase;
import com.techchallenge.oficina.administrativo.application.usecases.commands.DebitarEstoqueMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.os.application.usecases.commands.IniciarServicoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.exceptions.ValidacaoEstoqueException;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemMRO;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusItemServico;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
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
@DisplayName("Testes do Use Case de Iniciar Serviço")
class IniciarServicoUseCaseTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private DebitarEstoqueMROUseCase debitarEstoqueMROUseCase;

    @InjectMocks
    private IniciarServicoUseCase useCase;

    private OrdemServico ordemServico;
    private ItemServico itemServico;
    private ItemMRO itemMRO;
    private UUID ordemServicoId;
    private UUID itemServicoId;
    private UUID mroId;
    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        ordemServicoId = UUID.randomUUID();
        mroId = UUID.randomUUID();

        cliente = Cliente.criar(Nome.of("João Silva"), CPF.of("12345678909"), Email.of("joao@email.com"));
        veiculo = new Veiculo(Placa.of("ABC1234"), "Toyota", "Corolla", 2020, "Prata");
        veiculo.setCliente(cliente);

        ordemServico = OrdemServico.criar(cliente, veiculo);

        itemServico = ItemServico.criarComDados(UUID.randomUUID(), "Troca de Óleo", "Troca completa", new BigDecimal("150.00"));
        ordemServico.adicionarItemServico(itemServico);
        itemServicoId = itemServico.getId();

        // Add a second item to test removal
        ItemServico itemServico2 = ItemServico.criarComDados(UUID.randomUUID(), "Troca de Filtro", "Troca de filtro de ar", new BigDecimal("50.00"));
        ordemServico.adicionarItemServico(itemServico2);

        itemMRO = ItemMRO.criarComDados(mroId, "Óleo 5W30", "Óleo sintético", new BigDecimal("50.00"), 2);
        itemServico.adicionarMRO(itemMRO);

        ordemServico.enviarParaDiagnostico();
        ordemServico.enviarOrcamentoAoCliente();
        ordemServico.aprovarOrcamento();
    }

    @Test
    @DisplayName("Deve iniciar serviço e debitar estoque")
    void deveIniciarServicoEDebitarEstoque() {
        // Arrange
        IniciarServicoCommand command = IniciarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(itemServicoId)
                .build();

        when(ordemServicoRepository.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoRepository.save(any(OrdemServico.class))).thenReturn(ordemServico);
        when(debitarEstoqueMROUseCase.execute(any(DebitarEstoqueMROCommand.class)))
                .thenReturn(MROResponse.builder().id(mroId).build());

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertEquals(StatusItemServico.EM_ANDAMENTO, itemServico.getStatus());
        verify(debitarEstoqueMROUseCase, times(1)).execute(any(DebitarEstoqueMROCommand.class));
        verify(ordemServicoRepository, times(1)).save(ordemServico);
    }

    @Test
    @DisplayName("Deve lançar exceção quando OS não encontrada")
    void deveLancarExcecaoQuandoOSNaoEncontrada() {
        // Arrange
        IniciarServicoCommand command = IniciarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(itemServicoId)
                .build();

        when(ordemServicoRepository.findById(ordemServicoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.execute(command));
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ItemServico não encontrado")
    void deveLancarExcecaoQuandoItemServicoNaoEncontrado() {
        // Arrange
        UUID nonExistentItemId = UUID.randomUUID();
        
        IniciarServicoCommand command = IniciarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(nonExistentItemId)
                .build();

        when(ordemServicoRepository.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        // Act & Assert
        assertThrows(ItemServicoNaoEncontradoException.class, () -> useCase.execute(command));
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando OS não está em EM_EXECUCAO")
    void deveLancarExcecaoQuandoOSNaoEstaEmExecucao() {
        // Arrange
        OrdemServico osEmDiagnostico = OrdemServico.criar(cliente, veiculo);
        ItemServico novoItem = ItemServico.criarComDados(UUID.randomUUID(), "Troca de Óleo", "Troca completa", new BigDecimal("150.00"));
        osEmDiagnostico.adicionarItemServico(novoItem);
        osEmDiagnostico.enviarParaDiagnostico();
        
        IniciarServicoCommand command = IniciarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(novoItem.getId())
                .build();

        when(ordemServicoRepository.findById(ordemServicoId)).thenReturn(Optional.of(osEmDiagnostico));

        // Act & Assert
        assertThrows(ValidacaoEstoqueException.class, () -> useCase.execute(command));
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando item não está PENDENTE")
    void deveLancarExcecaoQuandoItemNaoEstaPendente() {
        // Arrange
        itemServico.atualizarStatus(StatusItemServico.EM_ANDAMENTO);
        
        IniciarServicoCommand command = IniciarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(itemServicoId)
                .build();

        when(ordemServicoRepository.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));

        // Act & Assert
        assertThrows(ValidacaoEstoqueException.class, () -> useCase.execute(command));
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando debite de estoque falha")
    void deveLancarExcecaoQuandoDebitoEstoqueFalha() {
        // Arrange
        IniciarServicoCommand command = IniciarServicoCommand.builder()
                .ordemServicoId(ordemServicoId)
                .itemServicoId(itemServicoId)
                .build();

        when(ordemServicoRepository.findById(ordemServicoId)).thenReturn(Optional.of(ordemServico));
        when(debitarEstoqueMROUseCase.execute(any(DebitarEstoqueMROCommand.class)))
                .thenThrow(new RuntimeException("Estoque insuficiente"));

        // Act & Assert
        assertThrows(ValidacaoEstoqueException.class, () -> useCase.execute(command));
        verify(ordemServicoRepository, never()).save(any());
    }
}
