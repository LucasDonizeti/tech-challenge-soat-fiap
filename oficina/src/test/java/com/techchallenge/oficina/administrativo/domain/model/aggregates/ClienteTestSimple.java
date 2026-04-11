package com.techchallenge.oficina.administrativo.domain.model.aggregates;

import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTestSimple {

    private Nome nome;
    private CPF cpf;
    private CNPJ cnpj;
    private Email email;

    @BeforeEach
    void setUp() {
        nome = Nome.of("João Silva");
        cpf = CPF.of("12345678909");
        cnpj = CNPJ.of("12345678000123");
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
        
        // Act
        cliente.atualizarNome(novoNome);

        // Assert
        assertEquals(novoNome, cliente.getNome());
        assertTrue(cliente.getAtualizadoEm().isAfter(cliente.getCriadoEm()));
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

        // Act
        cliente.inativar();

        // Assert
        assertEquals(StatusCliente.INATIVO, cliente.getStatus());
        assertFalse(cliente.isAtivo());
        assertTrue(cliente.getAtualizadoEm().isAfter(cliente.getCriadoEm()));
    }

    @Test
    void deveLancarExcecaoAoInativarClienteJaInativo() {
        // Arrange
        Cliente cliente = Cliente.criar(nome, cpf, email);
        cliente.inativar();

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> cliente.inativar()
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
        assertTrue(cliente.getAtualizadoEm().isAfter(cliente.getCriadoEm()));
    }

    @Test
    void deveLancarExcecaoAoReativarClienteJaAtivo() {
        // Arrange
        Cliente cliente = Cliente.criar(nome, cpf, email);

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> cliente.reativar()
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
}
