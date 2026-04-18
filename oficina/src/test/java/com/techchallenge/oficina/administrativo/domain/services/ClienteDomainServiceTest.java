package com.techchallenge.oficina.administrativo.domain.services;

import com.techchallenge.oficina.administrativo.domain.exceptions.CnpjJaCadastradoException;
import com.techchallenge.oficina.administrativo.domain.exceptions.CpfJaCadastradoException;
import com.techchallenge.oficina.administrativo.domain.exceptions.EmailJaCadastradoException;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoValorException;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de ClienteDomainService - Domain Layer")
class ClienteDomainServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteDomainService domainService;

    private CPF cpf;
    private CNPJ cnpj;
    private Email email;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cpf = CPF.of("12345678909");
        cnpj = CNPJ.of("12345678000195");
        email = Email.of("joao.silva@email.com");
        cliente = Cliente.criar(Nome.of("João Silva"), cpf, email);
    }

    @Test
    @DisplayName("Deve validar CPF único com sucesso")
    void deveValidarCpfUnicoComSucesso() {
        // Arrange
        when(repository.existsByCPF(cpf)).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> domainService.validarCPFUnico(cpf));
        verify(repository, times(1)).existsByCPF(cpf);
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já cadastrado")
    void deveLancarExcecaoQuandoCpfJaCadastrado() {
        // Arrange
        when(repository.existsByCPF(cpf)).thenReturn(true);

        // Act & Assert
        CpfJaCadastradoException exception = assertThrows(
                CpfJaCadastradoException.class,
                () -> domainService.validarCPFUnico(cpf)
        );
        assertEquals(cpf, exception.getCpf());
        verify(repository, times(1)).existsByCPF(cpf);
    }

    @Test
    @DisplayName("Deve validar CNPJ único com sucesso")
    void deveValidarCnpjUnicoComSucesso() {
        // Arrange
        when(repository.existsByCNPJ(cnpj)).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> domainService.validarCNPJUnico(cnpj));
        verify(repository, times(1)).existsByCNPJ(cnpj);
    }

    @Test
    @DisplayName("Deve lançar exceção quando CNPJ já cadastrado")
    void deveLancarExcecaoQuandoCnpjJaCadastrado() {
        // Arrange
        when(repository.existsByCNPJ(cnpj)).thenReturn(true);

        // Act & Assert
        CnpjJaCadastradoException exception = assertThrows(
                CnpjJaCadastradoException.class,
                () -> domainService.validarCNPJUnico(cnpj)
        );
        assertEquals(cnpj, exception.getCnpj());
        verify(repository, times(1)).existsByCNPJ(cnpj);
    }

    @Test
    @DisplayName("Deve validar email único com sucesso")
    void deveValidarEmailUnicoComSucesso() {
        // Arrange
        when(repository.existsByEmail(email)).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> domainService.validarEmailUnico(email));
        verify(repository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já cadastrado")
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        // Arrange
        when(repository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        EmailJaCadastradoException exception = assertThrows(
                EmailJaCadastradoException.class,
                () -> domainService.validarEmailUnico(email)
        );
        assertEquals(email, exception.getEmail());
        verify(repository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("Deve validar identificadores únicos com CPF e email")
    void deveValidarIdentificadoresUnicosComCpfEEmail() {
        // Arrange
        when(repository.existsByCPF(cpf)).thenReturn(false);
        when(repository.existsByEmail(email)).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> domainService.validarIdentificadoresUnicos(cpf, null, email));
        verify(repository, times(1)).existsByCPF(cpf);
        verify(repository, never()).existsByCNPJ(any());
        verify(repository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("Deve validar identificadores únicos com CNPJ e email")
    void deveValidarIdentificadoresUnicosComCnpjEEmail() {
        // Arrange
        when(repository.existsByCNPJ(cnpj)).thenReturn(false);
        when(repository.existsByEmail(email)).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> domainService.validarIdentificadoresUnicos(null, cnpj, email));
        verify(repository, never()).existsByCPF(any());
        verify(repository, times(1)).existsByCNPJ(cnpj);
        verify(repository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("Deve validar identificadores únicos com apenas email")
    void deveValidarIdentificadoresUnicosComApenasEmail() {
        // Arrange
        when(repository.existsByEmail(email)).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> domainService.validarIdentificadoresUnicos(null, null, email));
        verify(repository, never()).existsByCPF(any());
        verify(repository, never()).existsByCNPJ(any());
        verify(repository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF duplicado na validação combinada")
    void deveLancarExcecaoQuandoCpfDuplicadoNaValidacaoCombinada() {
        // Arrange
        when(repository.existsByCPF(cpf)).thenReturn(true);

        // Act & Assert
        assertThrows(CpfJaCadastradoException.class, 
                () -> domainService.validarIdentificadoresUnicos(cpf, null, email));
        verify(repository, times(1)).existsByCPF(cpf);
    }

    @Test
    @DisplayName("Deve lançar exceção quando CNPJ duplicado na validação combinada")
    void deveLancarExcecaoQuandoCnpjDuplicadoNaValidacaoCombinada() {
        // Arrange
        when(repository.existsByCNPJ(cnpj)).thenReturn(true);

        // Act & Assert
        assertThrows(CnpjJaCadastradoException.class, 
                () -> domainService.validarIdentificadoresUnicos(null, cnpj, email));
        verify(repository, times(1)).existsByCNPJ(cnpj);
    }

    @Test
    @DisplayName("Deve lançar exceção quando email duplicado na validação combinada")
    void deveLancarExcecaoQuandoEmailDuplicadoNaValidacaoCombinada() {
        // Arrange
        when(repository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        assertThrows(EmailJaCadastradoException.class, 
                () -> domainService.validarIdentificadoresUnicos(cpf, cnpj, email));
    }

    @Test
    @DisplayName("Deve retornar true quando cliente está ativo")
    void deveRetornarTrueQuandoClienteAtivo() {
        // Act & Assert
        assertTrue(domainService.isClienteAtivo(cliente));
    }

    @Test
    @DisplayName("Deve retornar false quando cliente está inativo")
    void deveRetornarFalseQuandoClienteInativo() {
        // Arrange
        cliente.inativar();

        // Act & Assert
        assertFalse(domainService.isClienteAtivo(cliente));
    }

    @Test
    @DisplayName("Deve retornar false quando cliente é nulo")
    void deveRetornarFalseQuandoClienteNulo() {
        // Act & Assert
        assertFalse(domainService.isClienteAtivo(null));
    }

    @Test
    @DisplayName("Deve retornar true quando cliente pode ser excluído sem veículos")
    void deveRetornarTrueQuandoClientePodeSerExcluidoSemVeiculos() {
        // Act & Assert
        assertTrue(domainService.podeExcluirCliente(cliente));
    }

    @Test
    @DisplayName("Deve retornar false quando cliente possui veículos")
    void deveRetornarFalseQuandoClientePossuiVeiculos() {
        // Arrange
        // Como a lista de veículos agora é apenas para consulta, adicionamos manualmente para teste
        com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo veiculo =
                new com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo(
                com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa.of("ABC1234"),
                "Volkswagen",
                "Gol",
                2020,
                "Branco"
        );
        cliente.adicionarVeiculo(veiculo);

        // Act & Assert
        assertFalse(domainService.podeExcluirCliente(cliente));
    }

    @Test
    @DisplayName("Deve retornar false quando cliente é nulo para exclusão")
    void deveRetornarFalseQuandoClienteNuloParaExclusao() {
        // Act & Assert
        assertFalse(domainService.podeExcluirCliente(null));
    }

    @Test
    @DisplayName("Deve validar exclusão de cliente com sucesso")
    void deveValidarExclusaoClienteComSucesso() {
        // Act & Assert
        assertDoesNotThrow(() -> domainService.validarExclusaoCliente(cliente));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não pode ser excluído")
    void deveLancarExcecaoQuandoClienteNaoPodeSerExcluido() {
        // Arrange
        // Como a lista de veículos agora é apenas para consulta, adicionamos manualmente para teste
        com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo veiculo =
                new com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo(
                com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa.of("ABC1234"),
                "Volkswagen",
                "Gol",
                2020,
                "Branco"
        );
        cliente.adicionarVeiculo(veiculo);

        // Act & Assert
        ValidacaoValorException exception = assertThrows(
                ValidacaoValorException.class,
                () -> domainService.validarExclusaoCliente(cliente)
        );
        assertEquals("Cliente não pode ser excluído. Verifique se há ordens de serviço em andamento ou veículos cadastrados.",
                exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente nulo na validação de exclusão")
    void deveLancarExcecaoQuandoClienteNuloNaValidacaoExclusao() {
        // Act & Assert
        ValidacaoValorException exception = assertThrows(
                ValidacaoValorException.class,
                () -> domainService.validarExclusaoCliente(null)
        );
        assertEquals("Cliente não pode ser excluído. Verifique se há ordens de serviço em andamento ou veículos cadastrados.", 
                exception.getMessage());
    }
}
