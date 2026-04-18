package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - ClienteFilterRequest")
class ClienteFilterRequestTest {

    @Test
    @DisplayName("Deve criar ClienteFilterRequest usando builder")
    void deveCriarClienteFilterRequestUsandoBuilder() {
        // Act
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .nome("João Silva")
                .cpf("12345678909")
                .cnpj("11444777000161")
                .email("joao.silva@exemplo.com")
                .status(StatusCliente.ATIVO)
                .build();

        // Assert
        assertNotNull(request);
        assertEquals("João Silva", request.getNome());
        assertEquals("12345678909", request.getCpf());
        assertEquals("11444777000161", request.getCnpj());
        assertEquals("joao.silva@exemplo.com", request.getEmail());
        assertEquals(StatusCliente.ATIVO, request.getStatus());
    }

    @Test
    @DisplayName("Deve criar ClienteFilterRequest vazio usando builder")
    void deveCriarClienteFilterRequestVazioUsandoBuilder() {
        // Act
        ClienteFilterRequest request = ClienteFilterRequest.builder().build();

        // Assert
        assertNotNull(request);
        assertNull(request.getNome());
        assertNull(request.getCpf());
        assertNull(request.getCnpj());
        assertNull(request.getEmail());
        assertNull(request.getStatus());
    }

    @Test
    @DisplayName("Deve retornar true quando hasNome com nome válido")
    void deveRetornarTrueQuandoHasNomeComNomeValido() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .nome("João Silva")
                .build();

        // Act & Assert
        assertTrue(request.hasNome());
    }

    @ParameterizedTest
    @DisplayName("Deve retornar false quando hasNome com nome inválido")
    @MethodSource("provideInvalidNomes")
    void deveRetornarFalseQuandoHasNomeComNomeInvalido(String nome) {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .nome(nome)
                .build();

        // Act & Assert
        assertFalse(request.hasNome());
    }

    private static Stream<Arguments> provideInvalidNomes() {
        return Stream.of(
                Arguments.of((String) null),
                Arguments.of(""),
                Arguments.of("   "),
                Arguments.of("\t\n")
        );
    }

    @Test
    @DisplayName("Deve retornar true quando hasCpf com CPF válido")
    void deveRetornarTrueQuandoHasCpfComCpfValido() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .cpf("12345678909")
                .build();

        // Act & Assert
        assertTrue(request.hasCpf());
    }

    @ParameterizedTest
    @DisplayName("Deve retornar false quando hasCpf com CPF inválido")
    @MethodSource("provideInvalidCpfs")
    void deveRetornarFalseQuandoHasCpfComCpfInvalido(String cpf) {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .cpf(cpf)
                .build();

        // Act & Assert
        assertFalse(request.hasCpf());
    }

    private static Stream<Arguments> provideInvalidCpfs() {
        return Stream.of(
                Arguments.of((String) null),
                Arguments.of(""),
                Arguments.of("  ")
        );
    }

    @Test
    @DisplayName("Deve retornar true quando hasCnpj com CNPJ válido")
    void deveRetornarTrueQuandoHasCnpjComCnpjValido() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .cnpj("12345678000190")
                .build();

        // Act & Assert
        assertTrue(request.hasCnpj());
    }

    @Test
    @DisplayName("Deve retornar false quando hasCnpj com CNPJ nulo")
    void deveRetornarFalseQuandoHasCnpjComCnpjNulo() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .cnpj(null)
                .build();

        // Act & Assert
        assertFalse(request.hasCnpj());
    }

    @Test
    @DisplayName("Deve retornar false quando hasCnpj com CNPJ vazio")
    void deveRetornarFalseQuandoHasCnpjComCnpjVazio() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .cnpj("")
                .build();

        // Act & Assert
        assertFalse(request.hasCnpj());
    }

    @Test
    @DisplayName("Deve retornar true quando hasEmail com email válido")
    void deveRetornarTrueQuandoHasEmailComEmailValido() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .email("joao.silva@exemplo.com")
                .build();

        // Act & Assert
        assertTrue(request.hasEmail());
    }

    @Test
    @DisplayName("Deve retornar false quando hasEmail com email nulo")
    void deveRetornarFalseQuandoHasEmailComEmailNulo() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .email(null)
                .build();

        // Act & Assert
        assertFalse(request.hasEmail());
    }

    @Test
    @DisplayName("Deve retornar false quando hasEmail com email vazio")
    void deveRetornarFalseQuandoHasEmailComEmailVazio() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .email("")
                .build();

        // Act & Assert
        assertFalse(request.hasEmail());
    }

    @Test
    @DisplayName("Deve retornar true quando hasStatus com status válido")
    void deveRetornarTrueQuandoHasStatusComStatusValido() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .status(StatusCliente.ATIVO)
                .build();

        // Act & Assert
        assertTrue(request.hasStatus());
    }

    @Test
    @DisplayName("Deve retornar false quando hasStatus com status nulo")
    void deveRetornarFalseQuandoHasStatusComStatusNulo() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .status(null)
                .build();

        // Act & Assert
        assertFalse(request.hasStatus());
    }

    @Test
    @DisplayName("Deve retornar true quando hasAnyFilter com pelo menos um filtro")
    void deveRetornarTrueQuandoHasAnyFilterComPeloMenosUmFiltro() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .nome("João Silva")
                .build();

        // Act & Assert
        assertTrue(request.hasAnyFilter());
    }

    @Test
    @DisplayName("Deve retornar false quando hasAnyFilter sem filtros")
    void deveRetornarFalseQuandoHasAnyFilterSemFiltros() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder().build();

        // Act & Assert
        assertFalse(request.hasAnyFilter());
    }

    @Test
    @DisplayName("Deve retornar true quando hasAnyFilter com múltiplos filtros")
    void deveRetornarTrueQuandoHasAnyFilterComMultiplosFiltros() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .nome("João Silva")
                .cpf("12345678909")
                .status(StatusCliente.ATIVO)
                .build();

        // Act & Assert
        assertTrue(request.hasAnyFilter());
    }

    @Test
    @DisplayName("Deve permitir alteração de campos usando setters")
    void devePermitirAlteracaoDeCamposUsandoSetters() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder().build();

        // Act
        request.setNome("Pedro Oliveira");
        request.setCpf("98765432100");
        request.setCnpj("11444777000161");
        request.setEmail("pedro.oliveira@exemplo.com");
        request.setStatus(StatusCliente.INATIVO);

        // Assert
        assertEquals("Pedro Oliveira", request.getNome());
        assertEquals("98765432100", request.getCpf());
        assertEquals("11444777000161", request.getCnpj());
        assertEquals("pedro.oliveira@exemplo.com", request.getEmail());
        assertEquals(StatusCliente.INATIVO, request.getStatus());
    }

    @Test
    @DisplayName("Deve retornar true para hasAnyFilter apenas com CPF")
    void deveRetornarTrueParaHasAnyFilterApenasComCpf() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .cpf("11122233344")
                .build();

        // Act & Assert
        assertTrue(request.hasAnyFilter());
        assertTrue(request.hasCpf());
        assertFalse(request.hasNome());
        assertFalse(request.hasCnpj());
        assertFalse(request.hasEmail());
        assertFalse(request.hasStatus());
    }

    @Test
    @DisplayName("Deve retornar true para hasAnyFilter apenas com CNPJ")
    void deveRetornarTrueParaHasAnyFilterApenasComCnpj() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .cnpj("11444777000161")
                .build();

        // Act & Assert
        assertTrue(request.hasAnyFilter());
        assertFalse(request.hasCpf());
        assertFalse(request.hasNome());
        assertTrue(request.hasCnpj());
        assertFalse(request.hasEmail());
        assertFalse(request.hasStatus());
    }

    @Test
    @DisplayName("Deve retornar true para hasAnyFilter apenas com status")
    void deveRetornarTrueParaHasAnyFilterApenasComStatus() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .status(StatusCliente.INATIVO)
                .build();

        // Act & Assert
        assertTrue(request.hasAnyFilter());
        assertFalse(request.hasCpf());
        assertFalse(request.hasNome());
        assertFalse(request.hasCnpj());
        assertFalse(request.hasEmail());
        assertTrue(request.hasStatus());
    }

    @Test
    @DisplayName("Deve retornar true para hasAnyFilter apenas com email")
    void deveRetornarTrueParaHasAnyFilterApenasComEmail() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .email("teste@exemplo.com")
                .build();

        // Act & Assert
        assertTrue(request.hasAnyFilter());
        assertFalse(request.hasCpf());
        assertFalse(request.hasNome());
        assertFalse(request.hasCnpj());
        assertTrue(request.hasEmail());
        assertFalse(request.hasStatus());
    }

    @Test
    @DisplayName("Deve retornar true para hasAnyFilter apenas com nome")
    void deveRetornarTrueParaHasAnyFilterApenasComNome() {
        // Arrange
        ClienteFilterRequest request = ClienteFilterRequest.builder()
                .nome("Carlos Alberto")
                .build();

        // Act & Assert
        assertTrue(request.hasAnyFilter());
        assertFalse(request.hasCpf());
        assertTrue(request.hasNome());
        assertFalse(request.hasCnpj());
        assertFalse(request.hasEmail());
        assertFalse(request.hasStatus());
    }
}
