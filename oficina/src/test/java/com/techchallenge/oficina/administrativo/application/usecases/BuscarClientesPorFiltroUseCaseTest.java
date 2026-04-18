package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import com.techchallenge.oficina.administrativo.web.dto.ClienteFilterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de BuscarClientesPorFiltroUseCase - Application Layer")
class BuscarClientesPorFiltroUseCaseTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private BuscarClientesPorFiltroUseCase useCase;

    @Test
    @DisplayName("Deve buscar clientes com filtro de nome")
    void deveBuscarPorNome() {
        // Arrange
        ClienteFilterRequest filter = ClienteFilterRequest.builder()
                .nome("João")
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        List<Cliente> clientes = List.of(
                Cliente.criar(Nome.of("João Silva"), CPF.of("12345678909"), Email.of("joao@teste.com")),
                Cliente.criar(Nome.of("João Santos"), CPF.of("98765432100"), Email.of("santos@teste.com"))
        );

        when(repository.findByFilter(filter)).thenReturn(clientes);

        // Act
        Page<ClienteResponse> result = useCase.execute(filter, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("João Silva", result.getContent().get(0).getNome());
        assertEquals("João Santos", result.getContent().get(1).getNome());

        verify(repository, times(1)).findByFilter(filter);
    }

    @Test
    @DisplayName("Deve buscar clientes com filtro de CPF")
    void deveBuscarPorCpf() {
        // Arrange
        ClienteFilterRequest filter = ClienteFilterRequest.builder()
                .cpf("123456789")
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        List<Cliente> clientes = List.of(
                Cliente.criar(Nome.of("João Silva"), CPF.of("12345678909"), Email.of("joao@teste.com"))
        );

        when(repository.findByFilter(filter)).thenReturn(clientes);

        // Act
        Page<ClienteResponse> result = useCase.execute(filter, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("João Silva", result.getContent().get(0).getNome());
        assertEquals("123.456.789-09", result.getContent().get(0).getCpf());

        verify(repository, times(1)).findByFilter(filter);
    }

    @Test
    @DisplayName("Deve buscar clientes com filtro de email")
    void deveBuscarPorEmail() {
        // Arrange
        ClienteFilterRequest filter = ClienteFilterRequest.builder()
                .email("@gmail.com")
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        List<Cliente> clientes = List.of(
                Cliente.criar(Nome.of("Maria Silva"), CPF.of("98765432100"), Email.of("maria@gmail.com"))
        );

        when(repository.findByFilter(filter)).thenReturn(clientes);

        // Act
        Page<ClienteResponse> result = useCase.execute(filter, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Maria Silva", result.getContent().get(0).getNome());
        assertEquals("maria@gmail.com", result.getContent().get(0).getEmail());

        verify(repository, times(1)).findByFilter(filter);
    }

    @Test
    @DisplayName("Deve buscar clientes com filtro de status")
    void deveBuscarPorStatus() {
        // Arrange
        ClienteFilterRequest filter = ClienteFilterRequest.builder()
                .status(StatusCliente.ATIVO)
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        List<Cliente> clientes = List.of(
                Cliente.criar(Nome.of("Cliente Ativo Um"), CPF.of("12345678909"), Email.of("ativo1@teste.com")),
                Cliente.criar(Nome.of("Cliente Ativo Dois"), CPF.of("98765432100"), Email.of("ativo2@teste.com"))
        );

        when(repository.findByFilter(filter)).thenReturn(clientes);

        // Act
        Page<ClienteResponse> result = useCase.execute(filter, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        result.getContent().forEach(response -> 
            assertEquals("ATIVO", response.getStatus().name()));

        verify(repository, times(1)).findByFilter(filter);
    }

    @Test
    @DisplayName("Deve buscar clientes com múltiplos filtros")
    void deveBuscarComMultiplosFiltros() {
        // Arrange
        ClienteFilterRequest filter = ClienteFilterRequest.builder()
                .nome("João")
                .status(StatusCliente.ATIVO)
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        List<Cliente> clientes = List.of(
                Cliente.criar(Nome.of("João Silva"), CPF.of("12345678909"), Email.of("joao@teste.com"))
        );

        when(repository.findByFilter(filter)).thenReturn(clientes);

        // Act
        Page<ClienteResponse> result = useCase.execute(filter, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        verify(repository, times(1)).findByFilter(filter);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum cliente encontrado")
    void deveRetornarPaginaVazia() {
        // Arrange
        ClienteFilterRequest filter = ClienteFilterRequest.builder()
                .nome("Inexistente")
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findByFilter(filter)).thenReturn(List.of());

        // Act
        Page<ClienteResponse> result = useCase.execute(filter, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
        assertTrue(result.getContent().isEmpty());

        verify(repository, times(1)).findByFilter(filter);
    }

    @Test
    @DisplayName("Deve aplicar paginação corretamente")
    void deveAplicarPaginacao() {
        // Arrange
        ClienteFilterRequest filter = ClienteFilterRequest.builder().build();
        Pageable pageable = PageRequest.of(0, 2);

        List<Cliente> clientes = List.of(
                Cliente.criar(Nome.of("Cliente Um"), CPF.of("12345678909"), Email.of("cliente1@teste.com")),
                Cliente.criar(Nome.of("Cliente Dois"), CPF.of("98765432100"), Email.of("cliente2@teste.com")),
                Cliente.criar(Nome.of("Cliente Tres"), CPF.of("12345678909"), Email.of("cliente3@teste.com"))
        );

        when(repository.findByFilter(filter)).thenReturn(clientes);

        // Act
        Page<ClienteResponse> result = useCase.execute(filter, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements()); // Total de elementos
        assertEquals(2, result.getContent().size()); // Elementos na página
        assertEquals(2, result.getSize()); // Tamanho da página
        assertEquals(0, result.getNumber()); // Número da página

        verify(repository, times(1)).findByFilter(filter);
    }

    @Test
    @DisplayName("Deve executar com filtro nulo (comportamento atual)")
    void deveExecutarComFiltroNulo() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert - O use case não valida nulidade, apenas executa
        assertDoesNotThrow(() -> useCase.execute(null, pageable));
    }

    @Test
    @DisplayName("Deve lançar exceção quando pageable é nulo")
    void deveLancarExcecaoPageableNulo() {
        // Arrange
        ClienteFilterRequest filter = ClienteFilterRequest.builder().build();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(filter, null));
    }
}
