package com.techchallenge.oficina.administrativo.domain.model.aggregates;

import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class ClienteSimpleTest {

    private Nome nome;
    private CPF cpf;
    private CNPJ cnpj;
    private Email email;

    @BeforeEach
    void setUp() {
        nome = Nome.of("João Silva");
        cpf = CPF.of("12345678909");
        cnpj = CNPJ.of("12345678000195");
        email = Email.of("joao.silva@email.com");
    }

    @Test
    void deveCriarClientePessoaFisica() {
        // Act
        Cliente cliente = Cliente.criar(nome, cpf, email);

        // Assert
        assertNotNull(cliente);
        assertNotNull(cliente.getId());
        assertEquals(nome, cliente.getNome());
        assertEquals(cpf, cliente.getCpf());
        assertNull(cliente.getCnpj());
        assertEquals(email, cliente.getEmail());
        assertEquals(StatusCliente.ATIVO, cliente.getStatus());
        assertNotNull(cliente.getCriadoEm());
        assertNotNull(cliente.getAtualizadoEm());
        assertTrue(cliente.isPessoaFisica());
        assertFalse(cliente.isPessoaJuridica());
        assertTrue(cliente.isAtivo());
    }

    @Test
    void deveCriarClientePessoaJuridica() {
        // Act
        Cliente cliente = Cliente.criarPJ(nome, cnpj, email);

        // Assert
        assertNotNull(cliente);
        assertNotNull(cliente.getId());
        assertEquals(nome, cliente.getNome());
        assertNull(cliente.getCpf());
        assertEquals(cnpj, cliente.getCnpj());
        assertEquals(email, cliente.getEmail());
        assertEquals(StatusCliente.ATIVO, cliente.getStatus());
        assertNotNull(cliente.getCriadoEm());
        assertNotNull(cliente.getAtualizadoEm());
        assertFalse(cliente.isPessoaFisica());
        assertTrue(cliente.isPessoaJuridica());
        assertTrue(cliente.isAtivo());
    }

    @Test
    void deveAtualizarNomeDoCliente() {
        // Arrange
        Cliente cliente = Cliente.criar(nome, cpf, email);
        Nome novoNome = Nome.of("João da Silva");
        LocalDateTime antesAtualizacao = cliente.getAtualizadoEm();

        // Act
        cliente.atualizarNome(novoNome);

        // Assert
        assertEquals(novoNome, cliente.getNome());
        assertTrue(cliente.getAtualizadoEm().isAfter(antesAtualizacao) || cliente.getAtualizadoEm().equals(antesAtualizacao));
    }

    @Test
    void deveLancarExcecaoAoAtualizarNomeComValorNulo() {
        // Arrange
        Cliente cliente = Cliente.criar(nome, cpf, email);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cliente.atualizarNome(null)
        );
        
        assertEquals("Nome não pode ser nulo", exception.getMessage());
    }

    @Test
    void deveInativarCliente() {
        // Arrange
        Cliente cliente = Cliente.criar(nome, cpf, email);
        LocalDateTime antesAtualizacao = cliente.getAtualizadoEm();

        // Act
        cliente.inativar();

        // Assert
        assertEquals(StatusCliente.INATIVO, cliente.getStatus());
        assertFalse(cliente.isAtivo());
        assertTrue(cliente.getAtualizadoEm().isAfter(antesAtualizacao) || cliente.getAtualizadoEm().equals(antesAtualizacao));
    }

    @Test
    void deveLancarExcecaoAoInativarClienteJaInativo() {
        // Arrange
        Cliente cliente = Cliente.criar(nome, cpf, email);
        cliente.inativar();

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                cliente::inativar
        );
        
        assertEquals("Cliente já está inativo", exception.getMessage());
    }

    @Test
    void deveReativarCliente() {
        // Arrange
        Cliente cliente = Cliente.criar(nome, cpf, email);
        cliente.inativar();

        // Act
        cliente.reativar();

        // Assert
        assertEquals(StatusCliente.ATIVO, cliente.getStatus());
        assertTrue(cliente.isAtivo());
        assertTrue(cliente.getAtualizadoEm().isAfter(cliente.getCriadoEm()) || cliente.getAtualizadoEm().equals(cliente.getCriadoEm()));
    }

    @Test
    void deveLancarExcecaoAoReativarClienteJaAtivo() {
        // Arrange
        Cliente cliente = Cliente.criar(nome, cpf, email);

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                cliente::reativar
        );
        
        assertEquals("Cliente já está ativo", exception.getMessage());
    }

    @Test
    void deveAdicionarVeiculoAoCliente() {
        // Arrange
        Cliente cliente = Cliente.criar(nome, cpf, email);
        Veiculo veiculo = new Veiculo(
                Placa.of("ABC1234"),
                "Volkswagen",
                "Gol",
                2020,
                "Branco"
        );

        // Act
        cliente.adicionarVeiculo(veiculo);

        // Assert
        assertEquals(1, cliente.getQuantidadeVeiculos());
        assertTrue(cliente.possuiVeiculos());
        assertTrue(cliente.getVeiculos().contains(veiculo));
    }

    @Test
    void deveLancarExcecaoAoAdicionarVeiculoNulo() {
        // Arrange
        Cliente cliente = Cliente.criar(nome, cpf, email);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cliente.adicionarVeiculo(null)
        );
        
        assertEquals("Veículo não pode ser nulo", exception.getMessage());
    }

    @Test
    void deveRemoverVeiculoDoCliente() {
        // Arrange
        Cliente cliente = Cliente.criar(nome, cpf, email);
        Veiculo veiculo = new Veiculo(
                Placa.of("ABC1234"),
                "Volkswagen",
                "Gol",
                2020,
                "Branco"
        );
        cliente.adicionarVeiculo(veiculo);

        // Act
        cliente.removerVeiculo(veiculo);

        // Assert
        assertEquals(0, cliente.getQuantidadeVeiculos());
        assertFalse(cliente.possuiVeiculos());
        assertFalse(cliente.getVeiculos().contains(veiculo));
    }

    @Test
    void deveAtualizarEmailDoCliente() {
        // Arrange
        Cliente cliente = Cliente.criar(nome, cpf, email);
        Email novoEmail = Email.of("joao.novo@email.com");
        LocalDateTime antesAtualizacao = cliente.getAtualizadoEm();

        // Act
        cliente.atualizarEmail(novoEmail);

        // Assert
        assertEquals(novoEmail, cliente.getEmail());
        assertTrue(cliente.getAtualizadoEm().isAfter(antesAtualizacao) || cliente.getAtualizadoEm().equals(antesAtualizacao));
    }

    @Test
    void deveLancarExcecaoAoAtualizarEmailComValorNulo() {
        // Arrange
        Cliente cliente = Cliente.criar(nome, cpf, email);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cliente.atualizarEmail(null)
        );

        assertEquals("Email não pode ser nulo", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoAoRemoverVeiculoNulo() {
        // Arrange
        Cliente cliente = Cliente.criar(nome, cpf, email);

        // Act & Assert
        assertDoesNotThrow(() -> cliente.removerVeiculo(null));
        assertEquals(0, cliente.getQuantidadeVeiculos());
    }

    @Test
    void deveLancarExcecaoAoRemoverVeiculoNaoAdicionado() {
        // Arrange
        Cliente cliente = Cliente.criar(nome, cpf, email);
        Veiculo veiculo = new Veiculo(
                Placa.of("ABC1234"),
                "Volkswagen",
                "Gol",
                2020,
                "Branco"
        );

        // Act & Assert
        assertDoesNotThrow(() -> cliente.removerVeiculo(veiculo));
        assertEquals(0, cliente.getQuantidadeVeiculos());
    }

    @Test
    void naoDeveAdicionarVeiculoDuplicado() {
        // Arrange
        Cliente cliente = Cliente.criar(nome, cpf, email);
        Veiculo veiculo = new Veiculo(
                Placa.of("ABC1234"),
                "Volkswagen",
                "Gol",
                2020,
                "Branco"
        );
        cliente.adicionarVeiculo(veiculo);

        // Act
        cliente.adicionarVeiculo(veiculo);

        // Assert
        assertEquals(1, cliente.getQuantidadeVeiculos());
    }

    @Test
    void deveRestaurarClienteDoBanco() {
        // Arrange
        ClienteRestauracaoParams params = new ClienteRestauracaoParams(
                UUID.randomUUID(),
                nome,
                cpf,
                cnpj,
                email,
                StatusCliente.ATIVO,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        // Act
        Cliente cliente = Cliente.restaurar(params);

        // Assert
        assertNotNull(cliente);
        assertEquals(params.id(), cliente.getId());
        assertEquals(params.nome(), cliente.getNome());
        assertEquals(params.cpf(), cliente.getCpf());
        assertEquals(params.cnpj(), cliente.getCnpj());
        assertEquals(params.email(), cliente.getEmail());
        assertEquals(params.status(), cliente.getStatus());
        assertEquals(params.criadoEm(), cliente.getCriadoEm());
        assertEquals(params.atualizadoEm(), cliente.getAtualizadoEm());
    }

    @Test
    void deveRestaurarClienteInativo() {
        // Arrange
        ClienteRestauracaoParams params = new ClienteRestauracaoParams(
                UUID.randomUUID(),
                nome,
                cpf,
                null,
                email,
                StatusCliente.INATIVO,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        // Act
        Cliente cliente = Cliente.restaurar(params);

        // Assert
        assertNotNull(cliente);
        assertEquals(StatusCliente.INATIVO, cliente.getStatus());
        assertFalse(cliente.isAtivo());
    }

    @Test
    void deveRetornarListaImutavelDeVeiculos() {
        // Arrange
        Cliente cliente = Cliente.criar(nome, cpf, email);
        Veiculo veiculo = new Veiculo(
                Placa.of("ABC1234"),
                "Volkswagen",
                "Gol",
                2020,
                "Branco"
        );
        cliente.adicionarVeiculo(veiculo);

        // Act
        List<Veiculo> veiculos = cliente.getVeiculos();

        // Assert
        assertNotNull(veiculos);
        assertEquals(1, veiculos.size());
        assertNotSame(cliente.getVeiculos(), veiculos);
    }
}
