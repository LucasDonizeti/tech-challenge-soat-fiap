package integration.administrativo.persistence;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.mappers.VeiculoJpaMapper;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.mappers.ClienteJpaMapper;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.repositories.ClienteJpaRepository;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.repositories.ClienteRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testes de Integração para ClienteRepository
 * 
 * Foco: Verificar inicialização do contexto Spring e funcionamento básico
 * Padrões aplicados:
 * - @SpringBootTest: Contexto completo para testes de integração
 * - @Transactional: Rollback automático após cada teste
 * - @ActiveProfiles("test"): Configurações específicas de teste
 */
@SpringBootTest(classes = com.techchallenge.oficina.OficinaApplication.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - ClienteRepository")
class ClienteRepositoryIntegrationTest {

    @Autowired
    private ClienteJpaRepository jpaRepository;

    @Test
    @DisplayName("Deve inicializar contexto Spring e injetar dependências")
    void deveInicializarContextoSpringEInjetarDependencias() {
        // Assert - Verifica se o contexto foi inicializado e as dependências injetadas
        assertNotNull(jpaRepository, "ClienteJpaRepository deveria ser injetado");
        assertNotNull(jpaRepository, "Repository não deveria ser nulo");
        
        // Verifica se o banco de dados está acessível
        long count = jpaRepository.count();
        assertTrue(count >= 0, "Banco de dados deveria estar acessível");
    }

    @Test
    @DisplayName("Deve criar e salvar cliente pessoa física com dados válidos")
    void deveCriarESalvarClientePessoaFisica() {
        // Arrange - Usando dados válidos
        Nome nome = Nome.of("Carlos Alberto Silva");
        CPF cpf = CPF.of("12345678909"); // CPF válido
        Email email = Email.of("carlos.silva@exemplo.com");
        
        Cliente cliente = Cliente.criar(nome, cpf, email);
        
        // Criar repository com mock do VeiculoJpaMapper
        VeiculoJpaMapper veiculoJpaMapper = org.mockito.Mockito.mock(VeiculoJpaMapper.class);
        ClienteRepository repository = new ClienteRepositoryImpl(
                jpaRepository, 
                new ClienteJpaMapper(veiculoJpaMapper)
        );

        // Act
        Cliente salvo = repository.save(cliente);

        // Assert
        assertNotNull(salvo, "Cliente salvo não deveria ser nulo");
        assertNotNull(salvo.getId(), "ID do cliente não deveria ser nulo");
        assertEquals(nome.getValor(), salvo.getNome().getValor());
        assertEquals(cpf.getValor(), salvo.getCpf().getValor());
        assertEquals(email.getEndereco(), salvo.getEmail().getEndereco());
        assertEquals(StatusCliente.ATIVO, salvo.getStatus());
        assertNull(salvo.getCnpj(), "Cliente PF não deveria ter CNPJ");
    }

    @Test
    @DisplayName("Deve verificar se CPF existe no banco")
    void deveVerificarExistenciaPorCpf() {
        // Arrange
        CPF cpf = CPF.of("98765432100");
        
        // Criar repository
        VeiculoJpaMapper veiculoJpaMapper = org.mockito.Mockito.mock(VeiculoJpaMapper.class);
        ClienteRepository repository = new ClienteRepositoryImpl(
                jpaRepository, 
                new ClienteJpaMapper(veiculoJpaMapper)
        );

        // Act & Assert - Verifica CPF que não existe
        assertFalse(repository.existsByCPF(cpf), "CPF não deveria existir inicialmente");
    }

    @Test
    @DisplayName("Deve verificar se email existe no banco")
    void deveVerificarExistenciaPorEmail() {
        // Arrange
        Email email = Email.of("teste@exemplo.com");
        
        // Criar repository
        VeiculoJpaMapper veiculoJpaMapper = org.mockito.Mockito.mock(VeiculoJpaMapper.class);
        ClienteRepository repository = new ClienteRepositoryImpl(
                jpaRepository, 
                new ClienteJpaMapper(veiculoJpaMapper)
        );

        // Act & Assert - Verifica email que não existe
        assertFalse(repository.existsByEmail(email), "Email não deveria existir inicialmente");
    }
}
