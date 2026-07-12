package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarDadosMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.MROGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.MRO;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de AtualizarDadosMROUseCase - Application Layer")
class AtualizarDadosMROUseCaseTest {

    @Mock
    private MROGateway gateway;

    @InjectMocks
    private AtualizarDadosMROUseCase useCase;

    private MRO mro;
    private UUID mroId;
    private AtualizarDadosMROCommand command;

    @BeforeEach
    void setUp() {
        mroId = UUID.randomUUID();
        mro = MRO.reconstruir(
                mroId,
                "Óleo Motor Antigo",
                "Descrição antiga",
                TipoMRO.INSUMO,
                100,
                new BigDecimal("45.90"),
                true,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        command = new AtualizarDadosMROCommand(
                mroId,
                "Óleo Motor Novo",
                "Nova descrição",
                TipoMRO.PECA
        );
    }

    @Test
    @DisplayName("Deve atualizar dados do MRO com sucesso")
    void deveAtualizarDadosMROComSucesso() {
        // Arrange
        when(gateway.findById(mroId)).thenReturn(Optional.of(mro));
        when(gateway.save(any(MRO.class))).thenReturn(mro);

        // Act
        MROResponse response = useCase.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals(mroId, response.getId());
        assertEquals("Óleo Motor Novo", response.getNome());
        assertEquals("Nova descrição", response.getDescricao());
        assertEquals("PECA", response.getTipo());

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando MRO não é encontrado")
    void deveLancarExcecaoQuandoMroNaoEncontrado() {
        // Arrange
        UUID idNaoExistente = UUID.randomUUID();
        AtualizarDadosMROCommand commandNaoExistente = new AtualizarDadosMROCommand(
                idNaoExistente,
                "Nome",
                "Descrição",
                TipoMRO.INSUMO
        );
        when(gateway.findById(idNaoExistente)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(commandNaoExistente)
        );

        assertTrue(exception.getMessage().contains("MRO não encontrado"));

        verify(gateway, times(1)).findById(idNaoExistente);
        verify(gateway, never()).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando comando é nulo")
    void deveLancarExcecaoQuandoComandoNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(gateway, never()).findById(any());
        verify(gateway, never()).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas nome mantendo outros dados")
    void deveAtualizarApenasNomeMantendoOutrosDados() {
        // Arrange
        AtualizarDadosMROCommand commandNome = new AtualizarDadosMROCommand(
                mroId,
                "Nome Atualizado",
                null,
                null
        );
        when(gateway.findById(mroId)).thenReturn(Optional.of(mro));
        when(gateway.save(any(MRO.class))).thenReturn(mro);

        // Act
        MROResponse response = useCase.execute(commandNome);

        // Assert
        assertNotNull(response);
        assertEquals("Nome Atualizado", response.getNome());

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, times(1)).save(any(MRO.class));
    }

    @Test
    @DisplayName("Deve propagar exceção quando repository falha")
    void devePropagarExcecaoQuandoRepositoryFalha() {
        // Arrange
        when(gateway.findById(mroId)).thenThrow(new RuntimeException("Erro de conexão"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Erro de conexão", exception.getMessage());

        verify(gateway, times(1)).findById(mroId);
        verify(gateway, never()).save(any(MRO.class));
    }
}
