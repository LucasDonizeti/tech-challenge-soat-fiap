package com.techchallenge.oficina.administrativo.infrastructure.gateways;

import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
import com.techchallenge.oficina.administrativo.domain.repositories.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoPersistenceGatewayTest {

    @Mock
    private ServicoRepository repository;

    private ServicoPersistenceGateway gateway;

    private Servico servico;

    @BeforeEach
    void setUp() {
        gateway = new ServicoPersistenceGateway(repository);
        servico = Servico.criar(
            "Troca de Óleo",
            "SVC001",
            "Troca de óleo sintético",
            new BigDecimal("150.00")
        );
    }

    @Test
    void save_deveDelegarParaRepository() {
        when(repository.save(any(Servico.class))).thenReturn(servico);

        Servico result = gateway.save(servico);

        assertNotNull(result);
        assertEquals(servico, result);
        verify(repository, times(1)).save(servico);
    }

    @Test
    void findById_deveDelegarParaRepository() {
        UUID id = servico.getId();
        when(repository.findById(id)).thenReturn(Optional.of(servico));

        Optional<Servico> result = gateway.findById(id);

        assertTrue(result.isPresent());
        assertEquals(servico, result.get());
        verify(repository, times(1)).findById(id);
    }

    @Test
    void findAllPageable_deveDelegarParaRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Servico> page = new PageImpl<>(List.of(servico), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);

        Page<Servico> result = gateway.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(servico, result.getContent().get(0));
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void findAll_deveDelegarParaRepository() {
        List<Servico> servicos = List.of(servico);
        when(repository.findAll()).thenReturn(servicos);

        List<Servico> result = gateway.findAll();

        assertEquals(1, result.size());
        assertEquals(servico, result.get(0));
        verify(repository, times(1)).findAll();
    }

    @Test
    void findByAtivo_deveDelegarParaRepository() {
        List<Servico> servicos = List.of(servico);
        when(repository.findByAtivo(true)).thenReturn(servicos);

        List<Servico> result = gateway.findByAtivo(true);

        assertEquals(1, result.size());
        verify(repository, times(1)).findByAtivo(true);
    }

    @Test
    void findByNomeContaining_deveDelegarParaRepository() {
        List<Servico> servicos = List.of(servico);
        when(repository.findByNomeContaining("Óleo")).thenReturn(servicos);

        List<Servico> result = gateway.findByNomeContaining("Óleo");

        assertEquals(1, result.size());
        verify(repository, times(1)).findByNomeContaining("Óleo");
    }

    @Test
    void deleteById_deveDelegarParaRepository() {
        UUID id = servico.getId();
        doNothing().when(repository).deleteById(id);

        gateway.deleteById(id);

        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void existsById_deveDelegarParaRepository() {
        UUID id = servico.getId();
        when(repository.existsById(id)).thenReturn(true);

        boolean result = gateway.existsById(id);

        assertTrue(result);
        verify(repository, times(1)).existsById(id);
    }
}
