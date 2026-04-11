package integration.administrativo.web;

import com.techchallenge.oficina.OficinaApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de Integração para ClienteController
 * 
 * Padrões aplicados:
 * - @SpringBootTest: Contexto completo da aplicação
 * - MockMvc: Simulação de requisições HTTP
 * - H2 Database: Banco de dados em memória com isolamento completo
 * - @ActiveProfiles("test"): Profile de teste com H2
 * - @DirtiesContext: Limpa o contexto após cada teste
 * - Testes focados em integração entre camadas
 */
@SpringBootTest(classes = OficinaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
@DisplayName("Testes de Integração - ClienteController")
class ClienteControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Deve inicializar contexto e MockMvc")
    void deveInicializarContextoEMockMvc() throws Exception {
        // Arrange & Act & Assert - Verifica se o contexto foi inicializado
        mockMvc.perform(get("/v1/admin/clientes"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve criar cliente pessoa física com sucesso")
    void deveCriarClientePessoaFisica() throws Exception {
        // Arrange - CPF único
        String requestBody = """
            {
                "nome": "Carlos Alberto Silva",
                "cpf": "98765432100",
                "email": "carlos.silva@exemplo.com"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/v1/admin/clientes")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Carlos Alberto Silva"))
                .andExpect(jsonPath("$.cpf").value("987.654.321-00")) // CPF formatado
                .andExpect(jsonPath("$.email").value("carlos.silva@exemplo.com"))
                .andExpect(jsonPath("$.cnpj").doesNotExist())
                .andExpect(jsonPath("$.status").value("ATIVO"))
                .andExpect(jsonPath("$.criadoEm").exists());
    }

    @Test
    @DisplayName("Deve criar cliente pessoa jurídica com sucesso")
    void deveCriarClientePessoaJuridica() throws Exception {
        // Arrange - CNPJ único e válido
        String requestBody = """
            {
                "nome": "Empresa ABC Ltda",
                "cnpj": "11222333000181",
                "email": "contato@empresa.com"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/v1/admin/clientes")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Empresa ABC Ltda"))
                .andExpect(jsonPath("$.cnpj").exists())
                .andExpect(jsonPath("$.email").value("contato@empresa.com"))
                .andExpect(jsonPath("$.cpf").doesNotExist())
                .andExpect(jsonPath("$.status").value("ATIVO"))
                .andExpect(jsonPath("$.criadoEm").exists());
    }

    @Test
    @DisplayName("Deve listar clientes com paginação")
    void deveListarClientesComPaginacao() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/v1/admin/clientes")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.size").isNumber())
                .andExpect(jsonPath("$.first").isBoolean())
                .andExpect(jsonPath("$.last").isBoolean());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios")
    void deveValidarCamposObrigatorios() throws Exception {
        // Arrange - Request sem campos obrigatórios
        String requestBody = """
            {
                "nome": "",
                "email": "teste@exemplo.com"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/v1/admin/clientes")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
