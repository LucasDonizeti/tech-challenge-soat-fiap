package com.techchallenge.oficina.administrativo.infrastructure.gateways;

import com.techchallenge.oficina.administrativo.domain.model.entities.MRO;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
import com.techchallenge.oficina.administrativo.domain.repositories.MRORepository;
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
class MROPersistenceGatewayTest {

    @Mock
    private MRORepository repository;

    private MROPersistenceGateway gateway;

    private MRO mro;

    @BeforeEach
    void setUp() {
        gateway = new MROPersistenceGateway(repository);
        mro = MRO.criar(
            "Óleo Motor 5W30",
            "INC001",
            "Óleo para motor automotivo",
            TipoMRO.INSUMO,
            100,
            new BigDecimal("45.90")
        );
    }

    @Test
    void save_deveDelegarParaRepository() {
        when(repository.save(any(MRO.class))).thenReturn(mro);

        MRO result = gateway.save(mro);

        assertNotNull(result);
        assertEquals(mro, result);
        verify(repository, times(1)).save(mro);
    }

    @Test
    void findById_deveDelegarParaRepository() {
        UUID id = mro.getId();
        when(repository.findById(id)).thenReturn(Optional.of(mro));

        Optional<MRO> result = gateway.findById(id);

        assertTrue(result.isPresent());
        assertEquals(mro, result.get());
        verify(repository, times(1)).findById(id);
    }

    @Test
    void findAllPageable_deveDelegarParaRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MRO> page = new PageImpl<>(List.of(mro), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);

        Page<MRO> result = gateway.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(mro, result.getContent().get(0));
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void findAll_deveDelegarParaRepository() {
        List<MRO> mros = List.of(mro);
        when(repository.findAll()).thenReturn(mros);

        List<MRO> result = gateway.findAll();

        assertEquals(1, result.size());
        assertEquals(mro, result.get(0));
        verify(repository, times(1)).findAll();
    }

    @Test
    void findByAtivo_deveDelegarParaRepository() {
        List<MRO> mros = List.of(mro);
        when(repository.findByAtivo(true)).thenReturn(mros);

        List<MRO> result = gateway.findByAtivo(true);

        assertEquals(1, result.size());
        verify(repository, times(1)).findByAtivo(true);
    }

    @Test
    void findByTipo_deveDelegarParaRepository() {
        List<MRO> mros = List.of(mro);
        when(repository.findByTipo(TipoMRO.INSUMO)).thenReturn(mros);

        List<MRO> result = gateway.findByTipo(TipoMRO.INSUMO);

        assertEquals(1, result.size());
        verify(repository, times(1)).findByTipo(TipoMRO.INSUMO);
    }

    @Test
    void findByNomeContaining_deveDelegarParaRepository() {
        List<MRO> mros = List.of(mro);
        when(repository.findByNomeContaining("Óleo")).thenReturn(mros);

        List<MRO> result = gateway.findByNomeContaining("Óleo");

        assertEquals(1, result.size());
        verify(repository, times(1)).findByNomeContaining("Óleo");
    }

    @Test
    void findByQuantidadeEstoqueGreaterThan_deveDelegarParaRepository() {
        List<MRO> mros = List.of(mro);
        when(repository.findByQuantidadeEstoqueGreaterThan(50)).thenReturn(mros);

        List<MRO> result = gateway.findByQuantidadeEstoqueGreaterThan(50);

        assertEquals(1, result.size());
        verify(repository, times(1)).findByQuantidadeEstoqueGreaterThan(50);
    }

    @Test
    void deleteById_deveDelegarParaRepository() {
        UUID id = mro.getId();
        doNothing().when(repository).deleteById(id);

        gateway.deleteById(id);

        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void existsById_deveDelegarParaRepository() {
        UUID id = mro.getId();
        when(repository.existsById(id)).thenReturn(true);

        boolean result = gateway.existsById(id);

        assertTrue(result);
        verify(repository, times(1)).existsById(id);
    }
}
