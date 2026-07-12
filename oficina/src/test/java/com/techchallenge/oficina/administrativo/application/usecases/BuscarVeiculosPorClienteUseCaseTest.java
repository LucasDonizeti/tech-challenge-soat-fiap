package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.output.VeiculoGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de BuscarVeiculosPorClienteUseCase - Application Layer")
class BuscarVeiculosPorClienteUseCaseTest {

    @Mock
    private VeiculoGateway gateway;

    @InjectMocks
    private BuscarVeiculosPorClienteUseCase useCase;

    private UUID clienteId;
    private Cliente cliente;
    private Veiculo veiculo1;
    private Veiculo veiculo2;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        cliente = Cliente.criar(
                Nome.of("João Silva"),
                CPF.of("12345678909"),
                Email.of("joao.silva@email.com")
        );

        veiculo1 = new Veiculo(
                Placa.of("ABC1234"),
                "Toyota",
                "Corolla",
                2022,
                "Prata"
        );
        veiculo1.setCliente(cliente);

        veiculo2 = new Veiculo(
                Placa.of("XYZ5678"),
                "Honda",
                "Civic",
                2023,
                "Preto"
        );
        veiculo2.setCliente(cliente);
    }

    @Test
    @DisplayName("Deve buscar veículos por cliente com sucesso")
    void deveBuscarVeiculosPorClienteComSucesso() {
        // Arrange
        List<Veiculo> veiculos = List.of(veiculo1, veiculo2);
        when(gateway.findByClienteId(clienteId)).thenReturn(veiculos);

        // Act
        List<VeiculoResponse> responses = useCase.execute(clienteId);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Toyota", responses.get(0).getMarca());
        assertEquals("Honda", responses.get(1).getMarca());

        verify(gateway, times(1)).findByClienteId(clienteId);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando cliente não possui veículos")
    void deveRetornarListaVaziaQuandoClienteSemVeiculos() {
        // Arrange
        when(gateway.findByClienteId(clienteId)).thenReturn(List.of());

        // Act
        List<VeiculoResponse> responses = useCase.execute(clienteId);

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(gateway, times(1)).findByClienteId(clienteId);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando clienteId nulo")
    void deveRetornarListaVaziaQuandoClienteIdNulo() {
        // Arrange
        when(gateway.findByClienteId(null)).thenReturn(List.of());

        // Act
        List<VeiculoResponse> responses = useCase.execute(null);

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(gateway, times(1)).findByClienteId(isNull());
    }

    @Test
    @DisplayName("Deve buscar veículo inativo do cliente")
    void deveBuscarVeiculoInativoDoCliente() {
        // Arrange
        veiculo1.inativar();
        List<Veiculo> veiculos = List.of(veiculo1);
        when(gateway.findByClienteId(clienteId)).thenReturn(veiculos);

        // Act
        List<VeiculoResponse> responses = useCase.execute(clienteId);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("INATIVO", responses.get(0).getStatus().name());

        verify(gateway, times(1)).findByClienteId(clienteId);
    }

    @Test
    @DisplayName("Deve buscar veículos incluindo ativos e inativos")
    void deveBuscarVeiculosIncluindoAtivosEInativos() {
        // Arrange
        veiculo2.inativar();
        List<Veiculo> veiculos = List.of(veiculo1, veiculo2);
        when(gateway.findByClienteId(clienteId)).thenReturn(veiculos);

        // Act
        List<VeiculoResponse> responses = useCase.execute(clienteId);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("ATIVO", responses.get(0).getStatus().name());
        assertEquals("INATIVO", responses.get(1).getStatus().name());

        verify(gateway, times(1)).findByClienteId(clienteId);
    }
}
