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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de ListarVeiculosUseCase - Application Layer")
class ListarVeiculosUseCaseTest {

    @Mock
    private VeiculoGateway gateway;

    @InjectMocks
    private ListarVeiculosUseCase useCase;

    private Veiculo veiculo1;
    private Veiculo veiculo2;
    private Veiculo veiculo3;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
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

        veiculo3 = new Veiculo(
                Placa.of("DEF9012"),
                "Chevrolet",
                "Onix",
                2021,
                "Branco"
        );
        veiculo3.setCliente(cliente);
    }

    @Test
    @DisplayName("Deve listar veículos com paginação")
    void deveListarVeiculosComPaginacao() {
        // Arrange
        List<Veiculo> veiculos = List.of(veiculo1, veiculo2, veiculo3);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Veiculo> pageVeiculos = new PageImpl<>(veiculos, pageable, veiculos.size());
        when(gateway.findAll(pageable)).thenReturn(pageVeiculos);

        // Act
        Page<VeiculoResponse> response = useCase.execute(pageable);

        // Assert
        assertNotNull(response);
        assertEquals(3, response.getTotalElements());
        assertEquals(3, response.getContent().size());
        assertEquals("Toyota", response.getContent().get(0).getMarca());
        assertEquals("Honda", response.getContent().get(1).getMarca());
        assertEquals("Chevrolet", response.getContent().get(2).getMarca());

        verify(gateway, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não há veículos")
    void deveRetornarPaginaVaziaQuandoSemVeiculos() {
        // Arrange
        List<Veiculo> veiculos = List.of();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Veiculo> pageVeiculos = new PageImpl<>(veiculos, pageable, 0);
        when(gateway.findAll(pageable)).thenReturn(pageVeiculos);

        // Act
        Page<VeiculoResponse> response = useCase.execute(pageable);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getTotalElements());
        assertTrue(response.getContent().isEmpty());

        verify(gateway, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve aplicar paginação corretamente")
    void deveAplicarPaginacaoCorretamente() {
        // Arrange
        List<Veiculo> veiculos = List.of(veiculo1, veiculo2, veiculo3);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Veiculo> pageVeiculos = new PageImpl<>(veiculos.subList(0, 2), pageable, veiculos.size());
        when(gateway.findAll(pageable)).thenReturn(pageVeiculos);

        // Act
        Page<VeiculoResponse> response = useCase.execute(pageable);

        // Assert
        assertNotNull(response);
        assertEquals(3, response.getTotalElements());
        assertEquals(2, response.getContent().size());
        assertEquals(0, response.getNumber());
        assertEquals(2, response.getSize());

        verify(gateway, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando página além do limite")
    void deveRetornarPaginaVaziaQuandoPaginaAlemLimite() {
        // Arrange
        List<Veiculo> veiculos = List.of(veiculo1, veiculo2);
        Pageable pageable = PageRequest.of(5, 10);
        Page<Veiculo> pageVeiculos = new PageImpl<>(List.of(), pageable, veiculos.size());
        when(gateway.findAll(pageable)).thenReturn(pageVeiculos);

        // Act
        Page<VeiculoResponse> response = useCase.execute(pageable);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getTotalElements());
        assertEquals(0, response.getContent().size());

        verify(gateway, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve listar veículos incluindo inativos")
    void deveListarVeiculosIncluindoInativos() {
        // Arrange
        veiculo2.inativar();
        List<Veiculo> veiculos = List.of(veiculo1, veiculo2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Veiculo> pageVeiculos = new PageImpl<>(veiculos, pageable, veiculos.size());
        when(gateway.findAll(pageable)).thenReturn(pageVeiculos);

        // Act
        Page<VeiculoResponse> response = useCase.execute(pageable);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getTotalElements());
        assertEquals("ATIVO", response.getContent().get(0).getStatus().name());
        assertEquals("INATIVO", response.getContent().get(1).getStatus().name());

        verify(gateway, times(1)).findAll(pageable);
    }
}
