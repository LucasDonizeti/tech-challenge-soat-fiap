package com.techchallenge.oficina.administrativo.application.usecases.responses;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - ClienteResponse")
class ClienteResponseTest {

    private Cliente clientePessoaFisica;
    private Cliente clientePessoaJuridica;

    @BeforeEach
    void setUp() {
        clientePessoaFisica = Cliente.criar(
                Nome.of("João Silva"),
                CPF.of("12345678909"),
                Email.of("joao.silva@email.com")
        );

        clientePessoaJuridica = Cliente.criarPJ(
                Nome.of("Empresa Ltda"),
                CNPJ.of("11444777000161"),
                Email.of("contato@empresa.com")
        );
    }

    @Test
    @DisplayName("Deve criar response a partir de cliente pessoa física")
    void deveCriarResponseAPartirDeClientePessoaFisica() {
        // Act
        ClienteResponse response = ClienteResponse.from(clientePessoaFisica);

        // Assert
        assertNotNull(response);
        assertEquals(clientePessoaFisica.getId(), response.getId());
        assertEquals("João Silva", response.getNome());
        assertEquals("123.456.789-09", response.getCpf());
        assertNull(response.getCnpj());
        assertEquals("joao.silva@email.com", response.getEmail());
        assertEquals(StatusCliente.ATIVO, response.getStatus());
        assertNotNull(response.getCriadoEm());
        assertNotNull(response.getAtualizadoEm());
        assertTrue(response.isPessoaFisica());
        assertFalse(response.isPessoaJuridica());
        assertEquals(0, response.getQuantidadeVeiculos());
    }

    @Test
    @DisplayName("Deve criar response a partir de cliente pessoa jurídica")
    void deveCriarResponseAPartirDeClientePessoaJuridica() {
        // Act
        ClienteResponse response = ClienteResponse.from(clientePessoaJuridica);

        // Assert
        assertNotNull(response);
        assertEquals(clientePessoaJuridica.getId(), response.getId());
        assertEquals("Empresa Ltda", response.getNome());
        assertNull(response.getCpf());
        assertEquals("11.444.777/0001-61", response.getCnpj());
        assertEquals("contato@empresa.com", response.getEmail());
        assertEquals(StatusCliente.ATIVO, response.getStatus());
        assertFalse(response.isPessoaFisica());
        assertTrue(response.isPessoaJuridica());
    }

    @Test
    @DisplayName("Deve retornar null quando cliente é null")
    void deveRetornarNullQuandoClienteENull() {
        // Act
        ClienteResponse response = ClienteResponse.from(null);

        // Assert
        assertNull(response);
    }

    @Test
    @DisplayName("Deve retornar tipo pessoa física")
    void deveRetornarTipoPessoaFisica() {
        // Arrange
        ClienteResponse response = ClienteResponse.from(clientePessoaFisica);

        // Act
        String tipoPessoa = response.getTipoPessoa();

        // Assert
        assertEquals("Pessoa Física", tipoPessoa);
    }

    @Test
    @DisplayName("Deve retornar tipo pessoa jurídica")
    void deveRetornarTipoPessoaJuridica() {
        // Arrange
        ClienteResponse response = ClienteResponse.from(clientePessoaJuridica);

        // Act
        String tipoPessoa = response.getTipoPessoa();

        // Assert
        assertEquals("Pessoa Jurídica", tipoPessoa);
    }

    @Test
    @DisplayName("Deve retornar não definido quando não for pessoa física nem jurídica")
    void deveRetornarNaoDefinidoQuandoNaoForPessoaFisicaNemJuridica() {
        // Arrange
        ClienteResponse response = ClienteResponse.builder()
                .id(UUID.randomUUID())
                .nome("Teste")
                .isPessoaFisica(false)
                .isPessoaJuridica(false)
                .build();

        // Act
        String tipoPessoa = response.getTipoPessoa();

        // Assert
        assertEquals("Não definido", tipoPessoa);
    }

    @Test
    @DisplayName("Deve retornar CPF como documento para pessoa física")
    void deveRetornarCpfComoDocumentoParaPessoaFisica() {
        // Arrange
        ClienteResponse response = ClienteResponse.from(clientePessoaFisica);

        // Act
        String documento = response.getDocumento();

        // Assert
        assertEquals("123.456.789-09", documento);
    }

    @Test
    @DisplayName("Deve retornar CNPJ como documento para pessoa jurídica")
    void deveRetornarCnpjComoDocumentoParaPessoaJuridica() {
        // Arrange
        ClienteResponse response = ClienteResponse.from(clientePessoaJuridica);

        // Act
        String documento = response.getDocumento();

        // Assert
        assertEquals("11.444.777/0001-61", documento);
    }

    @Test
    @DisplayName("Deve retornar null quando não tem CPF nem CNPJ")
    void deveRetornarNullQuandoNaoTemCpfNemCnpj() {
        // Arrange
        ClienteResponse response = ClienteResponse.builder()
                .id(UUID.randomUUID())
                .nome("Teste")
                .cpf(null)
                .cnpj(null)
                .build();

        // Act
        String documento = response.getDocumento();

        // Assert
        assertNull(documento);
    }

    @Test
    @DisplayName("Deve criar response com builder")
    void deveCriarResponseComBuilder() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime criadoEm = LocalDateTime.now();
        LocalDateTime atualizadoEm = LocalDateTime.now();

        // Act
        ClienteResponse response = ClienteResponse.builder()
                .id(id)
                .nome("João Silva")
                .cpf("123.456.789-09")
                .cnpj(null)
                .email("joao@email.com")
                .status(StatusCliente.ATIVO)
                .criadoEm(criadoEm)
                .atualizadoEm(atualizadoEm)
                .isPessoaFisica(true)
                .isPessoaJuridica(false)
                .quantidadeVeiculos(5)
                .build();

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("João Silva", response.getNome());
        assertEquals("123.456.789-09", response.getCpf());
        assertEquals("joao@email.com", response.getEmail());
        assertEquals(StatusCliente.ATIVO, response.getStatus());
        assertEquals(criadoEm, response.getCriadoEm());
        assertEquals(atualizadoEm, response.getAtualizadoEm());
        assertTrue(response.isPessoaFisica());
        assertFalse(response.isPessoaJuridica());
        assertEquals(5, response.getQuantidadeVeiculos());
    }

    @Test
    @DisplayName("Deve manter getters funcionando corretamente")
    void deveManterGettersFuncionandoCorretamente() {
        // Arrange
        UUID id = UUID.randomUUID();
        ClienteResponse response = ClienteResponse.builder()
                .id(id)
                .nome("Maria Santos")
                .cpf("987.654.321-00")
                .cnpj(null)
                .email("maria@email.com")
                .status(StatusCliente.INATIVO)
                .isPessoaFisica(true)
                .isPessoaJuridica(false)
                .quantidadeVeiculos(2)
                .build();

        // Assert
        assertEquals(id, response.getId());
        assertEquals("Maria Santos", response.getNome());
        assertEquals("987.654.321-00", response.getCpf());
        assertNull(response.getCnpj());
        assertEquals("maria@email.com", response.getEmail());
        assertEquals(StatusCliente.INATIVO, response.getStatus());
        assertTrue(response.isPessoaFisica());
        assertFalse(response.isPessoaJuridica());
        assertEquals(2, response.getQuantidadeVeiculos());
        assertEquals("Pessoa Física", response.getTipoPessoa());
        assertEquals("987.654.321-00", response.getDocumento());
    }
}
