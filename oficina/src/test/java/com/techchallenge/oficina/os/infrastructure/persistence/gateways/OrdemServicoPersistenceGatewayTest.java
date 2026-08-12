package com.techchallenge.oficina.os.infrastructure.persistence.gateways;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdemServicoPersistenceGatewayTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    private OrdemServicoPersistenceGateway gateway;

    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        gateway = new OrdemServicoPersistenceGateway(ordemServicoRepository);
        
        Cliente cliente = Cliente.criar(
            Nome.of("João Silva"),
            CPF.of("52998224725"),
            Email.of("joao@email.com")
        );
        
        Veiculo veiculo = new Veiculo(Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Branco");
        veiculo.setCliente(cliente);
        
        ordemServico = OrdemServico.criar(cliente, veiculo);
    }

    @Test
    void save_deveDelegarParaRepository() {
        when(ordemServicoRepository.save(any(OrdemServico.class))).thenReturn(ordemServico);

        OrdemServico result = gateway.save(ordemServico);

        assertNotNull(result);
        assertEquals(ordemServico, result);
        verify(ordemServicoRepository, times(1)).save(ordemServico);
    }

    @Test
    void findById_deveDelegarParaRepository() {
        UUID id = ordemServico.getId();
        when(ordemServicoRepository.findById(id)).thenReturn(Optional.of(ordemServico));

        Optional<OrdemServico> result = gateway.findById(id);

        assertTrue(result.isPresent());
        assertEquals(ordemServico, result.get());
        verify(ordemServicoRepository, times(1)).findById(id);
    }

    @Test
    void findAllPageable_deveDelegarParaRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrdemServico> page = new PageImpl<>(List.of(ordemServico), pageable, 1);
        when(ordemServicoRepository.findAll(pageable)).thenReturn(page);

        Page<OrdemServico> result = gateway.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(ordemServico, result.getContent().get(0));
        verify(ordemServicoRepository, times(1)).findAll(pageable);
    }

    @Test
    void findByFilters_deveDelegarParaRepository() {
        UUID clienteId = ordemServico.getCliente().getId();
        UUID veiculoId = ordemServico.getVeiculo().getId();
        StatusOS status = StatusOS.RECEBIDA;
        LocalDateTime dataInicio = LocalDateTime.now().minusDays(1);
        LocalDateTime dataFim = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<OrdemServico> page = new PageImpl<>(List.of(ordemServico), pageable, 1);
        when(ordemServicoRepository.findByFilters(clienteId, veiculoId, status, dataInicio, dataFim, pageable))
            .thenReturn(page);

        Page<OrdemServico> result = gateway.findByFilters(clienteId, veiculoId, status, dataInicio, dataFim, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(ordemServico, result.getContent().get(0));
        verify(ordemServicoRepository, times(1))
            .findByFilters(clienteId, veiculoId, status, dataInicio, dataFim, pageable);
    }

    @Test
    void findByFilters_comParametrosNulos_deveDelegarParaRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<OrdemServico> page = new PageImpl<>(List.of(ordemServico), pageable, 1);
        when(ordemServicoRepository.findByFilters(null, null, null, null, null, pageable))
            .thenReturn(page);

        Page<OrdemServico> result = gateway.findByFilters(null, null, null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(ordemServicoRepository, times(1))
            .findByFilters(null, null, null, null, null, pageable);
    }
}
