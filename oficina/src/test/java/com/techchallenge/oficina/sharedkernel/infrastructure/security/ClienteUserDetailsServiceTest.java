package com.techchallenge.oficina.sharedkernel.infrastructure.security;

import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ClienteEntity;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.repositories.ClienteJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - ClienteUserDetailsService")
class ClienteUserDetailsServiceTest {

    @Mock
    private ClienteJpaRepository clienteJpaRepository;

    @InjectMocks
    private ClienteUserDetailsService userDetailsService;

    @Test
    @DisplayName("Deve carregar cliente ativo por CPF")
    void deveCarregarClienteAtivoPorCpf() {
        String cpfLimpo = "52998224725";
        ClienteEntity clienteEntity = ClienteEntity.builder()
                .id(UUID.randomUUID())
                .nome("Cliente PF")
                .cpf(cpfLimpo)
                .status(ClienteEntity.StatusClienteEntity.ATIVO)
                .senhaHash("$2a$10$abcdefghijklmnopqrstuv")
                .build();

        when(clienteJpaRepository.findByCpf(cpfLimpo)).thenReturn(Optional.of(clienteEntity));

        UserDetails userDetails = userDetailsService.loadUserByUsername(cpfLimpo);

        assertNotNull(userDetails);
        assertEquals(cpfLimpo, userDetails.getUsername());
        assertEquals(clienteEntity.getSenhaHash(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE")));
        verify(clienteJpaRepository, times(1)).findByCpf(cpfLimpo);
        verify(clienteJpaRepository, never()).findByCnpj(anyString());
    }

    @Test
    @DisplayName("Deve carregar cliente ativo por CNPJ")
    void deveCarregarClienteAtivoPorCnpj() {
        String cnpjLimpo = "12345678000190";
        ClienteEntity clienteEntity = ClienteEntity.builder()
                .id(UUID.randomUUID())
                .nome("Cliente PJ")
                .cnpj(cnpjLimpo)
                .status(ClienteEntity.StatusClienteEntity.ATIVO)
                .senhaHash("$2a$10$abcdefghijklmnopqrstuv")
                .build();

        when(clienteJpaRepository.findByCpf(cnpjLimpo)).thenReturn(Optional.empty());
        when(clienteJpaRepository.findByCnpj(cnpjLimpo)).thenReturn(Optional.of(clienteEntity));

        UserDetails userDetails = userDetailsService.loadUserByUsername(cnpjLimpo);

        assertNotNull(userDetails);
        assertEquals(cnpjLimpo, userDetails.getUsername());
        assertEquals(clienteEntity.getSenhaHash(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE")));
        verify(clienteJpaRepository, times(1)).findByCpf(cnpjLimpo);
        verify(clienteJpaRepository, times(1)).findByCnpj(cnpjLimpo);
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando cliente não é encontrado")
    void deveLancarQuandoClienteNaoEncontrado() {
        String documento = "00000000000000";
        when(clienteJpaRepository.findByCpf(documento)).thenReturn(Optional.empty());
        when(clienteJpaRepository.findByCnpj(documento)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(documento));

        assertEquals("Cliente não encontrado com CPF/CNPJ: " + documento, exception.getMessage());
        verify(clienteJpaRepository, times(1)).findByCpf(documento);
        verify(clienteJpaRepository, times(1)).findByCnpj(documento);
    }
}
