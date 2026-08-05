package com.techchallenge.oficina.os.infrastructure.acl.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.bouncycastle.cms.PasswordRecipientId;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - MROIntegrationDto")
class MROIntegrationDtoTest {

    private UUID id;
    private String nome;
    private String descricao;
    private String tipo;
    private Integer quantidadeEstoque;
    private BigDecimal precoUnitario;
    private Boolean ativo;
    private String codigo;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        nome = "Óleo Motor 5W30";
        descricao = "Óleo sintético para motor automotivo";
        tipo = "INSUMO";
        quantidadeEstoque = 100;
        precoUnitario = new BigDecimal("45.90");
        ativo = true;
        codigo = "COD001";
    }

    @Test
    @DisplayName("Deve criar MROIntegrationDto com builder com sucesso")
    void deveCriarMROIntegrationDtoComBuilder() {
        // Act
        MROIntegrationDto dto = MROIntegrationDto.builder()
                .id(id)
                .nome(nome)
                .codigo(codigo)
                .descricao(descricao)
                .tipo(tipo)
                .quantidadeEstoque(quantidadeEstoque)
                .precoUnitario(precoUnitario)
                .ativo(ativo)
                .build();

        // Assert
        assertNotNull(dto);
        assertEquals(codigo, dto.getCodigo());
        assertEquals(id, dto.getId());
        assertEquals(nome, dto.getNome());
        assertEquals(descricao, dto.getDescricao());
        assertEquals(tipo, dto.getTipo());
        assertEquals(quantidadeEstoque, dto.getQuantidadeEstoque());
        assertEquals(precoUnitario, dto.getPrecoUnitario());
        assertEquals(ativo, dto.getAtivo());
    }

    @Test
    @DisplayName("Deve criar MROIntegrationDto com allArgsConstructor com sucesso")
    void deveCriarMROIntegrationDtoComAllArgsConstructor() {
        // Act
        MROIntegrationDto dto = new MROIntegrationDto(
                id, nome, codigo, descricao, tipo, quantidadeEstoque, precoUnitario, ativo
        );

        // Assert
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals(codigo, dto.getCodigo());
        assertEquals(nome, dto.getNome());
        assertEquals(descricao, dto.getDescricao());
        assertEquals(tipo, dto.getTipo());
        assertEquals(quantidadeEstoque, dto.getQuantidadeEstoque());
        assertEquals(precoUnitario, dto.getPrecoUnitario());
        assertEquals(ativo, dto.getAtivo());
    }

    @Test
    @DisplayName("Deve criar MROIntegrationDto com noArgsConstructor com sucesso")
    void deveCriarMROIntegrationDtoComNoArgsConstructor() {
        // Act
        MROIntegrationDto dto = new MROIntegrationDto();

        // Assert
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getNome());
        assertNull(dto.getDescricao());
        assertNull(dto.getTipo());
        assertNull(dto.getQuantidadeEstoque());
        assertNull(dto.getPrecoUnitario());
        assertNull(dto.getAtivo());
    }

    @Test
    @DisplayName("Deve identificar corretamente MRO do tipo PECA")
    void deveIdentificarCorretamenteMRODoTipoPeca() {
        // Arrange
        MROIntegrationDto dto = MROIntegrationDto.builder()
                .id(id)
                .nome(nome)
                .descricao(descricao)
                .tipo("PECA")
                .quantidadeEstoque(quantidadeEstoque)
                .precoUnitario(precoUnitario)
                .ativo(ativo)
                .build();

        // Act & Assert
        assertTrue(dto.isPeca());
        assertFalse(dto.isInsumo());
    }

    @Test
    @DisplayName("Deve identificar corretamente MRO do tipo INSUMO")
    void deveIdentificarCorretamenteMRODoTipoInsumo() {
        // Arrange
        MROIntegrationDto dto = MROIntegrationDto.builder()
                .id(id)
                .nome(nome)
                .descricao(descricao)
                .tipo("INSUMO")
                .quantidadeEstoque(quantidadeEstoque)
                .precoUnitario(precoUnitario)
                .ativo(ativo)
                .build();

        // Act & Assert
        assertFalse(dto.isPeca());
        assertTrue(dto.isInsumo());
    }

    @Test
    @DisplayName("Deve tratar tipo desconhecido corretamente")
    void deveTratarTipoDesconhecidoCorretamente() {
        // Arrange
        MROIntegrationDto dto = MROIntegrationDto.builder()
                .id(id)
                .nome(nome)
                .descricao(descricao)
                .tipo("OUTRO")
                .quantidadeEstoque(quantidadeEstoque)
                .precoUnitario(precoUnitario)
                .ativo(ativo)
                .build();

        // Act & Assert
        assertFalse(dto.isPeca());
        assertFalse(dto.isInsumo());
    }

    @Test
    @DisplayName("Deve identificar estoque suficiente corretamente")
    void deveIdentificarEstoqueSuficienteCorretamente() {
        // Arrange
        MROIntegrationDto dto = MROIntegrationDto.builder()
                .id(id)
                .nome(nome)
                .descricao(descricao)
                .tipo(tipo)
                .quantidadeEstoque(100)
                .precoUnitario(precoUnitario)
                .ativo(ativo)
                .build();

        // Act & Assert
        assertTrue(dto.temEstoqueSuficiente(50));
        assertTrue(dto.temEstoqueSuficiente(100));
        assertFalse(dto.temEstoqueSuficiente(101));
        assertFalse(dto.temEstoqueSuficiente(150));
    }

    @Test
    @DisplayName("Deve identificar estoque insuficiente quando quantidadeEstoque é null")
    void deveIdentificarEstoqueInsuficienteQuandoQuantidadeEstoqueENull() {
        // Arrange
        MROIntegrationDto dto = MROIntegrationDto.builder()
                .id(id)
                .nome(nome)
                .descricao(descricao)
                .tipo(tipo)
                .quantidadeEstoque(null)
                .precoUnitario(precoUnitario)
                .ativo(ativo)
                .build();

        // Act & Assert
        assertFalse(dto.temEstoqueSuficiente(10));
        assertFalse(dto.temEstoqueSuficiente(0));
        assertFalse(dto.temEstoqueSuficiente(null));
    }

    @Test
    @DisplayName("Deve identificar estoque suficiente quando quantidade é zero")
    void deveIdentificarEstoqueSuficienteQuandoQuantidadeEZero() {
        // Arrange
        MROIntegrationDto dto = MROIntegrationDto.builder()
                .id(id)
                .nome(nome)
                .descricao(descricao)
                .tipo(tipo)
                .quantidadeEstoque(10)
                .precoUnitario(precoUnitario)
                .ativo(ativo)
                .build();

        // Act & Assert
        assertTrue(dto.temEstoqueSuficiente(0));
    }

    @Test
    @DisplayName("Deve tratar estoque suficiente com quantidade negativa")
    void deveTratarEstoqueSuficienteComQuantidadeNegativa() {
        // Arrange
        MROIntegrationDto dto = MROIntegrationDto.builder()
                .id(id)
                .nome(nome)
                .descricao(descricao)
                .tipo(tipo)
                .quantidadeEstoque(10)
                .precoUnitario(precoUnitario)
                .ativo(ativo)
                .build();

        // Act & Assert
        assertTrue(dto.temEstoqueSuficiente(-5));
    }

    @Test
    @DisplayName("Deve tratar tipo null corretamente")
    void deveTratarTipoNullCorretamente() {
        // Arrange
        MROIntegrationDto dto = MROIntegrationDto.builder()
                .id(id)
                .nome(nome)
                .descricao(descricao)
                .tipo(null)
                .quantidadeEstoque(quantidadeEstoque)
                .precoUnitario(precoUnitario)
                .ativo(ativo)
                .build();

        // Act & Assert
        assertFalse(dto.isPeca());
        assertFalse(dto.isInsumo());
    }

    @Test
    @DisplayName("Deve tratar tipo case sensitive corretamente")
    void deveTratarTipoCaseSensitiveCorretamente() {
        // Arrange
        MROIntegrationDto dtoMinusculo = MROIntegrationDto.builder()
                .id(id)
                .nome(nome)
                .descricao(descricao)
                .tipo("peca")
                .quantidadeEstoque(quantidadeEstoque)
                .precoUnitario(precoUnitario)
                .ativo(ativo)
                .build();

        MROIntegrationDto dtoMaiusculo = MROIntegrationDto.builder()
                .id(id)
                .nome(nome)
                .descricao(descricao)
                .tipo("PECA")
                .quantidadeEstoque(quantidadeEstoque)
                .precoUnitario(precoUnitario)
                .ativo(ativo)
                .build();

        // Act & Assert
        assertFalse(dtoMinusculo.isPeca());
        assertTrue(dtoMaiusculo.isPeca());
    }

    @Test
    @DisplayName("Deve testar equals e hashCode")
    void deveTestarEqualsEHashCode() {
        // Arrange
        MROIntegrationDto dto1 = MROIntegrationDto.builder()
                .id(id)
                .nome(nome)
                .descricao(descricao)
                .tipo(tipo)
                .quantidadeEstoque(quantidadeEstoque)
                .precoUnitario(precoUnitario)
                .ativo(ativo)
                .build();

        MROIntegrationDto dto2 = MROIntegrationDto.builder()
                .id(id)
                .nome(nome)
                .descricao(descricao)
                .tipo(tipo)
                .quantidadeEstoque(quantidadeEstoque)
                .precoUnitario(precoUnitario)
                .ativo(ativo)
                .build();

        MROIntegrationDto dto3 = MROIntegrationDto.builder()
                .id(UUID.randomUUID())
                .nome(nome)
                .descricao(descricao)
                .tipo(tipo)
                .quantidadeEstoque(quantidadeEstoque)
                .precoUnitario(precoUnitario)
                .ativo(ativo)
                .build();

        // Act & Assert
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, "string");
    }

    @Test
    @DisplayName("Deve testar toString")
    void deveTestarToString() {
        // Arrange
        MROIntegrationDto dto = MROIntegrationDto.builder()
                .id(id)
                .nome(nome)
                .descricao(descricao)
                .tipo(tipo)
                .quantidadeEstoque(quantidadeEstoque)
                .precoUnitario(precoUnitario)
                .ativo(ativo)
                .build();

        // Act
        String toString = dto.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("MROIntegrationDto"));
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains(nome));
        assertTrue(toString.contains(descricao));
        assertTrue(toString.contains(tipo));
        assertTrue(toString.contains(quantidadeEstoque.toString()));
        assertTrue(toString.contains(precoUnitario.toString()));
        assertTrue(toString.contains(ativo.toString()));
    }

    @Test
    @DisplayName("Deve testar setters e getters")
    void deveTestarSettersEGetters() {
        // Arrange
        MROIntegrationDto dto = new MROIntegrationDto();

        // Act
        dto.setId(id);
        dto.setNome(nome);
        dto.setDescricao(descricao);
        dto.setTipo(tipo);
        dto.setQuantidadeEstoque(quantidadeEstoque);
        dto.setPrecoUnitario(precoUnitario);
        dto.setAtivo(ativo);

        // Assert
        assertEquals(id, dto.getId());
        assertEquals(nome, dto.getNome());
        assertEquals(descricao, dto.getDescricao());
        assertEquals(tipo, dto.getTipo());
        assertEquals(quantidadeEstoque, dto.getQuantidadeEstoque());
        assertEquals(precoUnitario, dto.getPrecoUnitario());
        assertEquals(ativo, dto.getAtivo());
    }

    @Test
    @DisplayName("Deve criar MROIntegrationDto com valores decimais precisos")
    void deveCriarMROIntegrationDtoComValoresDecimaisPrecisos() {
        // Arrange
        BigDecimal precoPreciso = new BigDecimal("123.456789");

        // Act
        MROIntegrationDto dto = MROIntegrationDto.builder()
                .id(id)
                .nome("Peça Precisa")
                .descricao("Peça com preço decimal preciso")
                .tipo("PECA")
                .quantidadeEstoque(5)
                .precoUnitario(precoPreciso)
                .ativo(true)
                .build();

        // Assert
        assertNotNull(dto);
        assertEquals(precoPreciso, dto.getPrecoUnitario());
        assertTrue(dto.isPeca());
        assertFalse(dto.isInsumo());
        assertTrue(dto.temEstoqueSuficiente(3));
    }

    @Test
    @DisplayName("Deve criar MROIntegrationDto com estoque zero")
    void deveCriarMROIntegrationDtoComEstoqueZero() {
        // Act
        MROIntegrationDto dto = MROIntegrationDto.builder()
                .id(id)
                .nome("Item Sem Estoque")
                .descricao("Item com estoque zerado")
                .tipo("INSUMO")
                .quantidadeEstoque(0)
                .precoUnitario(precoUnitario)
                .ativo(true)
                .build();

        // Assert
        assertNotNull(dto);
        assertEquals(0, dto.getQuantidadeEstoque());
        assertFalse(dto.isPeca());
        assertTrue(dto.isInsumo());
        assertTrue(dto.temEstoqueSuficiente(0));
        assertFalse(dto.temEstoqueSuficiente(1));
    }

    @Test
    @DisplayName("Deve criar MROIntegrationDto inativo")
    void deveCriarMROIntegrationDtoInativo() {
        // Act
        MROIntegrationDto dto = MROIntegrationDto.builder()
                .id(id)
                .nome("Item Inativo")
                .descricao("Item não disponível")
                .tipo("PECA")
                .quantidadeEstoque(50)
                .precoUnitario(precoUnitario)
                .ativo(false)
                .build();

        // Assert
        assertNotNull(dto);
        assertFalse(dto.getAtivo());
        assertTrue(dto.isPeca());
        assertFalse(dto.isInsumo());
        assertTrue(dto.temEstoqueSuficiente(10));
    }
}
