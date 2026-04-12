package com.techchallenge.oficina.administrativo.infrastructure.persistence.specifications;

import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - ClienteFilter")
class ClienteFilterTest {

    @Test
    @DisplayName("Deve criar ClienteFilter usando builder")
    void deveCriarClienteFilterUsandoBuilder() {
        // Act
        ClienteFilter filter = ClienteFilter.builder()
                .nome("João Silva")
                .cpf("12345678909")
                .cnpj("11444777000161")
                .email("joao.silva@exemplo.com")
                .status(StatusCliente.ATIVO)
                .build();

        // Assert
        assertNotNull(filter);
        assertEquals("João Silva", filter.getNome());
        assertEquals("12345678909", filter.getCpf());
        assertEquals("11444777000161", filter.getCnpj());
        assertEquals("joao.silva@exemplo.com", filter.getEmail());
        assertEquals(StatusCliente.ATIVO, filter.getStatus());
    }

    @Test
    @DisplayName("Deve criar ClienteFilter vazio usando builder")
    void deveCriarClienteFilterVazioUsandoBuilder() {
        // Act
        ClienteFilter filter = ClienteFilter.builder().build();

        // Assert
        assertNotNull(filter);
        assertNull(filter.getNome());
        assertNull(filter.getCpf());
        assertNull(filter.getCnpj());
        assertNull(filter.getEmail());
        assertNull(filter.getStatus());
    }

    @Test
    @DisplayName("Deve criar ClienteFilter usando construtor padrão")
    void deveCriarClienteFilterUsandoConstrutorPadrao() {
        // Act
        ClienteFilter filter = new ClienteFilter();

        // Assert
        assertNotNull(filter);
        assertNull(filter.getNome());
        assertNull(filter.getCpf());
        assertNull(filter.getCnpj());
        assertNull(filter.getEmail());
        assertNull(filter.getStatus());
    }

    @Test
    @DisplayName("Deve criar ClienteFilter usando construtor com todos os parâmetros")
    void deveCriarClienteFilterUsandoConstrutorComTodosParametros() {
        // Act
        ClienteFilter filter = new ClienteFilter(
                "João Silva",
                "12345678909",
                "11444777000161",
                "joao.silva@exemplo.com",
                StatusCliente.ATIVO
        );

        // Assert
        assertNotNull(filter);
        assertEquals("João Silva", filter.getNome());
        assertEquals("12345678909", filter.getCpf());
        assertEquals("11444777000161", filter.getCnpj());
        assertEquals("joao.silva@exemplo.com", filter.getEmail());
        assertEquals(StatusCliente.ATIVO, filter.getStatus());
    }

    @Test
    @DisplayName("Deve criar ClienteFilter usando método estático from")
    void deveCriarClienteFilterUsandoMetodoEstaticoFrom() {
        // Act
        ClienteFilter filter = ClienteFilter.from(
                "João Silva",
                "12345678909",
                "11444777000161",
                "joao.silva@exemplo.com",
                StatusCliente.ATIVO
        );

        // Assert
        assertNotNull(filter);
        assertEquals("João Silva", filter.getNome());
        assertEquals("12345678909", filter.getCpf());
        assertEquals("11444777000161", filter.getCnpj());
        assertEquals("joao.silva@exemplo.com", filter.getEmail());
        assertEquals(StatusCliente.ATIVO, filter.getStatus());
    }

    @Test
    @DisplayName("Deve criar ClienteFilter usando método from com valores nulos")
    void deveCriarClienteFilterUsandoMetodoFromComValoresNulos() {
        // Act
        ClienteFilter filter = ClienteFilter.from(null, null, null, null, null);

        // Assert
        assertNotNull(filter);
        assertNull(filter.getNome());
        assertNull(filter.getCpf());
        assertNull(filter.getCnpj());
        assertNull(filter.getEmail());
        assertNull(filter.getStatus());
    }

    @Test
    @DisplayName("Deve criar ClienteFilter usando método from com status INATIVO")
    void deveCriarClienteFilterUsandoMetodoFromComStatusInativo() {
        // Act
        ClienteFilter filter = ClienteFilter.from(
                "Maria Santos",
                null,
                null,
                "maria.santos@exemplo.com",
                StatusCliente.INATIVO
        );

        // Assert
        assertNotNull(filter);
        assertEquals("Maria Santos", filter.getNome());
        assertNull(filter.getCpf());
        assertNull(filter.getCnpj());
        assertEquals("maria.santos@exemplo.com", filter.getEmail());
        assertEquals(StatusCliente.INATIVO, filter.getStatus());
    }

    @Test
    @DisplayName("Deve permitir alteração de campos usando setters")
    void devePermitirAlteracaoDeCamposUsandoSetters() {
        // Arrange
        ClienteFilter filter = new ClienteFilter();

        // Act
        filter.setNome("Pedro Oliveira");
        filter.setCpf("98765432100");
        filter.setCnpj("98765432000150");
        filter.setEmail("pedro.oliveira@exemplo.com");
        filter.setStatus(StatusCliente.INATIVO);

        // Assert
        assertEquals("Pedro Oliveira", filter.getNome());
        assertEquals("98765432100", filter.getCpf());
        assertEquals("98765432000150", filter.getCnpj());
        assertEquals("pedro.oliveira@exemplo.com", filter.getEmail());
        assertEquals(StatusCliente.INATIVO, filter.getStatus());
    }

    @Test
    @DisplayName("Deve criar filtro apenas com nome")
    void deveCriarFiltroApenasComNome() {
        // Act
        ClienteFilter filter = ClienteFilter.builder()
                .nome("Carlos Alberto")
                .build();

        // Assert
        assertNotNull(filter);
        assertEquals("Carlos Alberto", filter.getNome());
        assertNull(filter.getCpf());
        assertNull(filter.getCnpj());
        assertNull(filter.getEmail());
        assertNull(filter.getStatus());
    }

    @Test
    @DisplayName("Deve criar filtro apenas com CPF")
    void deveCriarFiltroApenasComCpf() {
        // Act
        ClienteFilter filter = ClienteFilter.builder()
                .cpf("11122233344")
                .build();

        // Assert
        assertNotNull(filter);
        assertNull(filter.getNome());
        assertEquals("11122233344", filter.getCpf());
        assertNull(filter.getCnpj());
        assertNull(filter.getEmail());
        assertNull(filter.getStatus());
    }

    @Test
    @DisplayName("Deve criar filtro apenas com CNPJ")
    void deveCriarFiltroApenasComCnpj() {
        // Act
        ClienteFilter filter = ClienteFilter.builder()
                .cnpj("11444777000161")
                .build();

        // Assert
        assertNotNull(filter);
        assertNull(filter.getNome());
        assertNull(filter.getCpf());
        assertEquals("11444777000161", filter.getCnpj());
        assertNull(filter.getEmail());
        assertNull(filter.getStatus());
    }

    @Test
    @DisplayName("Deve criar filtro apenas com email")
    void deveCriarFiltroApenasComEmail() {
        // Act
        ClienteFilter filter = ClienteFilter.builder()
                .email("teste@exemplo.com")
                .build();

        // Assert
        assertNotNull(filter);
        assertNull(filter.getNome());
        assertNull(filter.getCpf());
        assertNull(filter.getCnpj());
        assertEquals("teste@exemplo.com", filter.getEmail());
        assertNull(filter.getStatus());
    }

    @Test
    @DisplayName("Deve criar filtro apenas com status")
    void deveCriarFiltroApenasComStatus() {
        // Act
        ClienteFilter filter = ClienteFilter.builder()
                .status(StatusCliente.ATIVO)
                .build();

        // Assert
        assertNotNull(filter);
        assertNull(filter.getNome());
        assertNull(filter.getCpf());
        assertNull(filter.getCnpj());
        assertNull(filter.getEmail());
        assertEquals(StatusCliente.ATIVO, filter.getStatus());
    }

    @Test
    @DisplayName("Deve criar filtro com combinação de campos")
    void deveCriarFiltroComCombinacaoDeCampos() {
        // Act
        ClienteFilter filter = ClienteFilter.builder()
                .nome("Ana Paula")
                .cpf("55566677788")
                .status(StatusCliente.ATIVO)
                .build();

        // Assert
        assertNotNull(filter);
        assertEquals("Ana Paula", filter.getNome());
        assertEquals("55566677788", filter.getCpf());
        assertNull(filter.getCnpj());
        assertNull(filter.getEmail());
        assertEquals(StatusCliente.ATIVO, filter.getStatus());
    }
}
