package com.techchallenge.oficina.os.infrastructure.persistence.gateways;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusVeiculo;
import com.techchallenge.oficina.administrativo.domain.repositories.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoPersistenceOSGatewayTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    private VeiculoPersistenceOSGateway gateway;

    private Veiculo veiculo;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        gateway = new VeiculoPersistenceOSGateway(veiculoRepository);
        
        cliente = Cliente.criar(
            Nome.of("João Silva"),
            CPF.of("52998224725"),
            Email.of("joao@email.com")
        );
        
        veiculo = new Veiculo(Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Branco");
        veiculo.setCliente(cliente);
    }

    @Test
    void save_deveDelegarParaRepository() {
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        Veiculo result = gateway.save(veiculo);

        assertNotNull(result);
        assertEquals(veiculo, result);
        verify(veiculoRepository, times(1)).save(veiculo);
    }

    @Test
    void findById_deveDelegarParaRepository() {
        UUID id = veiculo.getId();
        when(veiculoRepository.findById(id)).thenReturn(Optional.of(veiculo));

        Optional<Veiculo> result = gateway.findById(id);

        assertTrue(result.isPresent());
        assertEquals(veiculo, result.get());
        verify(veiculoRepository, times(1)).findById(id);
    }

    @Test
    void findByPlaca_deveDelegarParaRepository() {
        Placa placa = Placa.of("ABC1234");
        when(veiculoRepository.findByPlaca(placa)).thenReturn(Optional.of(veiculo));

        Optional<Veiculo> result = gateway.findByPlaca(placa);

        assertTrue(result.isPresent());
        assertEquals(veiculo, result.get());
        verify(veiculoRepository, times(1)).findByPlaca(placa);
    }

    @Test
    void existsByPlaca_deveDelegarParaRepository() {
        Placa placa = Placa.of("ABC1234");
        when(veiculoRepository.existsByPlaca(placa)).thenReturn(true);

        boolean result = gateway.existsByPlaca(placa);

        assertTrue(result);
        verify(veiculoRepository, times(1)).existsByPlaca(placa);
    }

    @Test
    void existsByPlacaAndClienteId_deveDelegarParaRepository() {
        Placa placa = Placa.of("ABC1234");
        UUID clienteId = cliente.getId();
        when(veiculoRepository.existsByPlacaAndClienteId(placa, clienteId)).thenReturn(true);

        boolean result = gateway.existsByPlacaAndClienteId(placa, clienteId);

        assertTrue(result);
        verify(veiculoRepository, times(1)).existsByPlacaAndClienteId(placa, clienteId);
    }

    @Test
    void existsByPlacaAndClienteIdAndIdNot_deveDelegarParaRepository() {
        Placa placa = Placa.of("ABC1234");
        UUID clienteId = cliente.getId();
        UUID veiculoId = veiculo.getId();
        when(veiculoRepository.existsByPlacaAndClienteIdAndIdNot(placa, clienteId, veiculoId)).thenReturn(false);

        boolean result = gateway.existsByPlacaAndClienteIdAndIdNot(placa, clienteId, veiculoId);

        assertFalse(result);
        verify(veiculoRepository, times(1)).existsByPlacaAndClienteIdAndIdNot(placa, clienteId, veiculoId);
    }

    @Test
    void findByClienteId_deveDelegarParaRepository() {
        UUID clienteId = cliente.getId();
        List<Veiculo> veiculos = List.of(veiculo);
        when(veiculoRepository.findByClienteId(clienteId)).thenReturn(veiculos);

        List<Veiculo> result = gateway.findByClienteId(clienteId);

        assertEquals(1, result.size());
        assertEquals(veiculo, result.get(0));
        verify(veiculoRepository, times(1)).findByClienteId(clienteId);
    }

    @Test
    void findByClienteIdAndStatus_deveDelegarParaRepository() {
        UUID clienteId = cliente.getId();
        List<Veiculo> veiculos = List.of(veiculo);
        when(veiculoRepository.findByClienteIdAndStatus(clienteId, StatusVeiculo.ATIVO))
            .thenReturn(veiculos);

        List<Veiculo> result = gateway.findByClienteIdAndStatus(clienteId, StatusVeiculo.ATIVO);

        assertEquals(1, result.size());
        verify(veiculoRepository, times(1)).findByClienteIdAndStatus(clienteId, StatusVeiculo.ATIVO);
    }

    @Test
    void findByStatus_deveDelegarParaRepository() {
        List<Veiculo> veiculos = List.of(veiculo);
        when(veiculoRepository.findByStatus(StatusVeiculo.ATIVO)).thenReturn(veiculos);

        List<Veiculo> result = gateway.findByStatus(StatusVeiculo.ATIVO);

        assertEquals(1, result.size());
        verify(veiculoRepository, times(1)).findByStatus(StatusVeiculo.ATIVO);
    }

    @Test
    void findAll_deveDelegarParaRepository() {
        List<Veiculo> veiculos = List.of(veiculo);
        when(veiculoRepository.findAll()).thenReturn(veiculos);

        List<Veiculo> result = gateway.findAll();

        assertEquals(1, result.size());
        assertEquals(veiculo, result.get(0));
        verify(veiculoRepository, times(1)).findAll();
    }

    @Test
    void findAllPageable_deveDelegarParaRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Veiculo> page = new PageImpl<>(List.of(veiculo), pageable, 1);
        when(veiculoRepository.findAll(pageable)).thenReturn(page);

        Page<Veiculo> result = gateway.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(veiculo, result.getContent().get(0));
        verify(veiculoRepository, times(1)).findAll(pageable);
    }

    @Test
    void deleteById_deveDelegarParaRepository() {
        UUID id = veiculo.getId();
        doNothing().when(veiculoRepository).deleteById(id);

        gateway.deleteById(id);

        verify(veiculoRepository, times(1)).deleteById(id);
    }
}
