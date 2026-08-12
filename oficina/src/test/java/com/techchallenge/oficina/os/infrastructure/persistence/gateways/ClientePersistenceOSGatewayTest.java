package com.techchallenge.oficina.os.infrastructure.persistence.gateways;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
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
class ClientePersistenceOSGatewayTest {

    @Mock
    private ClienteRepository clienteRepository;

    private ClientePersistenceOSGateway gateway;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        gateway = new ClientePersistenceOSGateway(clienteRepository);
        cliente = Cliente.criar(
            Nome.of("João Silva"),
            CPF.of("52998224725"),
            Email.of("joao@email.com")
        );
    }

    @Test
    void save_deveDelegarParaRepository() {
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente result = gateway.save(cliente);

        assertNotNull(result);
        assertEquals(cliente, result);
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    void findById_deveDelegarParaRepository() {
        UUID id = cliente.getId();
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

        Optional<Cliente> result = gateway.findById(id);

        assertTrue(result.isPresent());
        assertEquals(cliente, result.get());
        verify(clienteRepository, times(1)).findById(id);
    }

    @Test
    void findAll_deveDelegarParaRepository() {
        List<Cliente> clientes = List.of(cliente);
        when(clienteRepository.findAll()).thenReturn(clientes);

        List<Cliente> result = gateway.findAll();

        assertEquals(1, result.size());
        assertEquals(cliente, result.get(0));
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void findAllPageable_deveDelegarParaRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> page = new PageImpl<>(List.of(cliente), pageable, 1);
        when(clienteRepository.findAll(pageable)).thenReturn(page);

        Page<Cliente> result = gateway.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(cliente, result.getContent().get(0));
        verify(clienteRepository, times(1)).findAll(pageable);
    }

    @Test
    void findByStatus_deveDelegarParaRepository() {
        List<Cliente> clientes = List.of(cliente);
        when(clienteRepository.findByStatus(StatusCliente.ATIVO)).thenReturn(clientes);

        List<Cliente> result = gateway.findByStatus(StatusCliente.ATIVO);

        assertEquals(1, result.size());
        verify(clienteRepository, times(1)).findByStatus(StatusCliente.ATIVO);
    }

    @Test
    void findByCPF_deveDelegarParaRepository() {
        CPF cpf = CPF.of("52998224725");
        when(clienteRepository.findByCPF(cpf)).thenReturn(Optional.of(cliente));

        Optional<Cliente> result = gateway.findByCPF(cpf);

        assertTrue(result.isPresent());
        assertEquals(cliente, result.get());
        verify(clienteRepository, times(1)).findByCPF(cpf);
    }

    @Test
    void findByCNPJ_deveDelegarParaRepository() {
        Cliente clientePJ = Cliente.criarPJ(
            Nome.of("Empresa LTDA"),
            CNPJ.of("11444777000161"),
            Email.of("empresa@email.com")
        );
        CNPJ cnpj = CNPJ.of("11444777000161");
        when(clienteRepository.findByCNPJ(cnpj)).thenReturn(Optional.of(clientePJ));

        Optional<Cliente> result = gateway.findByCNPJ(cnpj);

        assertTrue(result.isPresent());
        assertEquals(clientePJ, result.get());
        verify(clienteRepository, times(1)).findByCNPJ(cnpj);
    }

    @Test
    void findByEmail_deveDelegarParaRepository() {
        Email email = Email.of("joao@email.com");
        when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(cliente));

        Optional<Cliente> result = gateway.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(cliente, result.get());
        verify(clienteRepository, times(1)).findByEmail(email);
    }

    @Test
    void existsByCPF_deveDelegarParaRepository() {
        CPF cpf = CPF.of("52998224725");
        when(clienteRepository.existsByCPF(cpf)).thenReturn(true);

        boolean result = gateway.existsByCPF(cpf);

        assertTrue(result);
        verify(clienteRepository, times(1)).existsByCPF(cpf);
    }

    @Test
    void existsByCNPJ_deveDelegarParaRepository() {
        CNPJ cnpj = CNPJ.of("11444777000161");
        when(clienteRepository.existsByCNPJ(cnpj)).thenReturn(false);

        boolean result = gateway.existsByCNPJ(cnpj);

        assertFalse(result);
        verify(clienteRepository, times(1)).existsByCNPJ(cnpj);
    }

    @Test
    void existsByEmail_deveDelegarParaRepository() {
        Email email = Email.of("joao@email.com");
        when(clienteRepository.existsByEmail(email)).thenReturn(true);

        boolean result = gateway.existsByEmail(email);

        assertTrue(result);
        verify(clienteRepository, times(1)).existsByEmail(email);
    }

    @Test
    void deleteById_deveDelegarParaRepository() {
        UUID id = cliente.getId();
        doNothing().when(clienteRepository).deleteById(id);

        gateway.deleteById(id);

        verify(clienteRepository, times(1)).deleteById(id);
    }

    @Test
    void findByFilter_deveDelegarParaRepository() {
        Object filter = new Object();
        List<Cliente> clientes = List.of(cliente);
        when(clienteRepository.findByFilter(filter)).thenReturn(clientes);

        List<Cliente> result = gateway.findByFilter(filter);

        assertEquals(1, result.size());
        verify(clienteRepository, times(1)).findByFilter(filter);
    }
}
