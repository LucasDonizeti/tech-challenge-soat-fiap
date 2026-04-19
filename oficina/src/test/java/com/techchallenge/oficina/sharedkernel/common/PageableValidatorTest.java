package com.techchallenge.oficina.sharedkernel.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - PageableValidator")
class PageableValidatorTest {

    private PageableValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PageableValidator();
    }

    @Test
    @DisplayName("Deve retornar pageable sem alterações quando sort está vazio")
    void deveRetornarPageableSemAlteracoesQuandoSortEstaVazio() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Set<String> allowedFields = Set.of("nome", "preco");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertEquals(pageable, result);
    }

    @Test
    @DisplayName("Deve manter ordenação quando campo é permitido")
    void deveManterOrdenacaoQuandoCampoEPermitido() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        Set<String> allowedFields = Set.of("nome", "preco");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertNotNull(result.getSort());
        assertEquals("nome", result.getSort().iterator().next().getProperty());
    }

    @Test
    @DisplayName("Deve remover ordenação quando campo não é permitido")
    void deveRemoverOrdenacaoQuandoCampoNaoEPermitido() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("campoInvalido"));
        Set<String> allowedFields = Set.of("nome", "preco");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertTrue(result.getSort().isEmpty());
    }

    @Test
    @DisplayName("Deve remover ordenação quando todos os campos são inválidos")
    void deveRemoverOrdenacaoQuandoTodosOsCamposSaoInvalidos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("campo1", "campo2"));
        Set<String> allowedFields = Set.of("nome", "preco");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertTrue(result.getSort().isEmpty());
        assertEquals(0, result.getPageNumber());
        assertEquals(10, result.getPageSize());
    }

    @Test
    @DisplayName("Deve manter apenas campos permitidos quando há múltiplos campos")
    void deveManterApenasCamposPermitidosQuandoHaMultiplosCampos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome", "campoInvalido", "preco"));
        Set<String> allowedFields = Set.of("nome", "preco");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertFalse(result.getSort().isEmpty());
        assertEquals(2, result.getSort().stream().count());
    }

    @Test
    @DisplayName("Deve manter ordenação ascendente quando campo é permitido")
    void deveManterOrdenacaoAscendenteQuandoCampoEPermitido() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "nome"));
        Set<String> allowedFields = Set.of("nome", "preco");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertNotNull(result.getSort());
        assertEquals("nome", result.getSort().iterator().next().getProperty());
        assertEquals(Sort.Direction.ASC, result.getSort().iterator().next().getDirection());
    }

    @Test
    @DisplayName("Deve manter ordenação descendente quando campo é permitido")
    void deveManterOrdenacaoDescendenteQuandoCampoEPermitido() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "nome"));
        Set<String> allowedFields = Set.of("nome", "preco");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertNotNull(result.getSort());
        assertEquals("nome", result.getSort().iterator().next().getProperty());
        assertEquals(Sort.Direction.DESC, result.getSort().iterator().next().getDirection());
    }

    @Test
    @DisplayName("Deve manter número da página após validação")
    void deveManterNumeroDaPaginaAposValidacao() {
        // Arrange
        Pageable pageable = PageRequest.of(2, 10, Sort.by("campoInvalido"));
        Set<String> allowedFields = Set.of("nome", "preco");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertEquals(2, result.getPageNumber());
    }

    @Test
    @DisplayName("Deve manter tamanho da página após validação")
    void deveManterTamanhoDaPaginaAposValidacao() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20, Sort.by("campoInvalido"));
        Set<String> allowedFields = Set.of("nome", "preco");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertEquals(20, result.getPageSize());
    }

    @Test
    @DisplayName("Deve manter ambos quando todos os campos são permitidos")
    void deveManterAmbosQuandoTodosOsCamposSaoPermitidos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome", "preco"));
        Set<String> allowedFields = Set.of("nome", "preco");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertNotNull(result.getSort());
        assertEquals(2, result.getSort().stream().count());
    }

    @Test
    @DisplayName("Deve funcionar com conjunto vazio de campos permitidos")
    void deveFuncionarComConjuntoVazioDeCamposPermitidos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        Set<String> allowedFields = Set.of();

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertTrue(result.getSort().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando allowedFields é null")
    void deveLancarNullPointerExceptionQuandoAllowedFieldsENull() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        Set<String> allowedFields = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> validator.validate(pageable, allowedFields));
    }

    @Test
    @DisplayName("Deve manter ordenação case sensitive")
    void deveManterOrdenacaoCaseSensitive() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("Nome"));
        Set<String> allowedFields = Set.of("nome", "preco");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertTrue(result.getSort().isEmpty());
    }
}
