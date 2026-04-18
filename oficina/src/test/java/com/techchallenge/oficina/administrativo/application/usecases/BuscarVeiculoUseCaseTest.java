package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoVeiculoException;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.administrativo.domain.repositories.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de BuscarVeiculoUseCase - Application Layer")
class BuscarVeiculoUseCaseTest {

    @Mock
    private VeiculoRepository repository;

    @InjectMocks
    private BuscarVeiculoUseCase useCase;

    private UUID veiculoId;
    private Veiculo veiculo;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        veiculoId = UUID.randomUUID();
        cliente = Cliente.criar(
                Nome.of("João Silva"),
                CPF.of("12345678909"),
                Email.of("joao.silva@email.com")
        );

        veiculo = new Veiculo(
                Placa.of("ABC1234"),
                "Toyota",
                "Corolla",
                2022,
                "Prata"
        );
        veiculo.setCliente(cliente);
    }

    @Test
    @DisplayName("Deve buscar veículo com sucesso quando encontrado")
    void deveBuscarVeiculoComSucesso() {
        // Arrange
        when(repository.findById(veiculoId)).thenReturn(Optional.of(veiculo));

        // Act
        VeiculoResponse response = useCase.execute(veiculoId);

        // Assert
        assertNotNull(response);
        assertEquals("Toyota", response.getMarca());
        assertEquals("Corolla", response.getModelo());
        assertEquals(2022, response.getAno());
        assertEquals("Prata", response.getCor());
        assertEquals("ABC-1234", response.getPlaca());

        verify(repository, times(1)).findById(veiculoId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando veículo não encontrado")
    void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        // Arrange
        when(repository.findById(veiculoId)).thenReturn(Optional.empty());

        // Act & Assert
        ValidacaoVeiculoException exception = assertThrows(
                ValidacaoVeiculoException.class,
                () -> useCase.execute(veiculoId)
        );

        assertEquals("Veículo não encontrado: " + veiculoId, exception.getMessage());

        verify(repository, times(1)).findById(veiculoId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando veiculoId nulo")
    void deveLancarExcecaoQuandoVeiculoIdNulo() {
        // Act & Assert
        assertThrows(ValidacaoVeiculoException.class, () -> useCase.execute(null));

        verify(repository, times(1)).findById(isNull());
    }

    @Test
    @DisplayName("Deve buscar veículo inativo com sucesso")
    void deveBuscarVeiculoInativoComSucesso() {
        // Arrange
        veiculo.inativar();
        when(repository.findById(veiculoId)).thenReturn(Optional.of(veiculo));

        // Act
        VeiculoResponse response = useCase.execute(veiculoId);

        // Assert
        assertNotNull(response);
        assertEquals("Toyota", response.getMarca());
        assertEquals("INATIVO", response.getStatus().name());

        verify(repository, times(1)).findById(veiculoId);
    }

    @Test
    @DisplayName("Deve buscar veículo com cliente vinculado")
    void deveBuscarVeiculoComClienteVinculado() {
        // Arrange
        when(repository.findById(veiculoId)).thenReturn(Optional.of(veiculo));

        // Act
        VeiculoResponse response = useCase.execute(veiculoId);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getClienteId());
        assertEquals("João Silva", response.getClienteNome());

        verify(repository, times(1)).findById(veiculoId);
    }
}
