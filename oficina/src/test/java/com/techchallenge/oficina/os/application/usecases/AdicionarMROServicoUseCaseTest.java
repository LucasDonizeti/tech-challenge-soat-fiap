package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.os.application.usecases.commands.AdicionarMROServicoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.entities.MRO;
import com.techchallenge.oficina.os.domain.model.entities.Servico;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import com.techchallenge.oficina.os.domain.repositories.MRORepository;
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
@DisplayName("Testes de AdicionarMROServicoUseCase - Application Layer")
class AdicionarMROServicoUseCaseTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private MRORepository mroRepository;

    @InjectMocks
    private AdicionarMROServicoUseCase useCase;

    private AdicionarMROServicoCommand command;
    private OrdemServico ordemServico;
    private ItemServico itemServico;
    private MRO mro;

    @BeforeEach
    void setUp() {
        Servico servico = Servico.criar("Troca de Óleo", "Troca completa de óleo", new BigDecimal("150.00"));
        itemServico = ItemServico.criar(servico);

        Cliente cliente = Cliente.criar(Nome.of("João Silva"), CPF.of("52998224725"), Email.of("joao@email.com"));
        Veiculo veiculo = new Veiculo(Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Branco");
        veiculo.setCliente(cliente);

        ordemServico = OrdemServico.criar(cliente, veiculo);
        ordemServico.adicionarItemServico(itemServico);

        UUID ordemServicoId = ordemServico.getId();
        UUID itemServicoId = itemServico.getId();
        UUID mroId = UUID.randomUUID();

        command = new AdicionarMROServicoCommand(ordemServicoId, itemServicoId, mroId, 5);

        mro = MRO.criar("Óleo Motor", "Óleo para motor", 
                com.techchallenge.oficina.os.domain.model.valueobjects.TipoMRO.INSUMO, 100, new BigDecimal("45.90"));
    }

    @Test
    @DisplayName("Deve adicionar MRO ao serviço com sucesso")
    void deveAdicionarMROAoServicoComSucesso() {
        // Arrange
        when(ordemServicoRepository.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServico));
        when(mroRepository.findById(command.getMroId())).thenReturn(Optional.of(mro));
        when(ordemServicoRepository.save(any(OrdemServico.class))).thenReturn(ordemServico);

        // Act
        OrdemServicoResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals(ordemServico.getId(), response.getId());

        verify(ordemServicoRepository, times(1)).findById(command.getOrdemServicoId());
        verify(mroRepository, times(1)).findById(command.getMroId());
        verify(ordemServicoRepository, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem de serviço não encontrada")
    void deveLancarExcecaoQuandoOrdemServicoNaoEncontrada() {
        // Arrange
        when(ordemServicoRepository.findById(command.getOrdemServicoId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Ordem de Serviço não encontrada"));

        verify(ordemServicoRepository, times(1)).findById(command.getOrdemServicoId());
        verify(mroRepository, never()).findById(any());
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando item de serviço não encontrado")
    void deveLancarExcecaoQuandoItemServicoNaoEncontrado() {
        // Arrange
        Cliente cliente = Cliente.criar(Nome.of("João Silva"), CPF.of("52998224725"), Email.of("joao@email.com"));
        Veiculo veiculo = new Veiculo(Placa.of("XYZ5678"), "Chevrolet", "Onix", 2021, "Prata");
        veiculo.setCliente(cliente);
        OrdemServico ordemServicoSemItem = OrdemServico.criar(cliente, veiculo);

        when(ordemServicoRepository.findById(command.getOrdemServicoId())).thenReturn(Optional.of(ordemServicoSemItem));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("Item de Serviço não encontrado"));

        verify(ordemServicoRepository, times(1)).findById(command.getOrdemServicoId());
        verify(mroRepository, never()).findById(any());
        verify(ordemServicoRepository, never()).save(any());
    }


    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(ordemServicoRepository, never()).findById(any());
        verify(mroRepository, never()).findById(any());
        verify(ordemServicoRepository, never()).save(any());
    }

}
