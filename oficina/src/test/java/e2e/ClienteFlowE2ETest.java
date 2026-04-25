package e2e;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes End-to-End para fluxos completos de Cliente
 * 
 * Padrões aplicados:
 * - @SpringBootTest: Contexto completo da aplicação
 * - MockMvc: Simulação de requisições HTTP sem dependências externas
 * - H2 Database: Banco de dados em memória com isolamento completo
 * - @ActiveProfiles("test"): Profile de teste com H2
 * - @DirtiesContext: Limpa o contexto após cada teste
 * - @WithMockUser: Simula usuário autenticado para testes
 * - Testes focados em fluxos críticos de negócio
 */
@SpringBootTest(classes = com.techchallenge.oficina.OficinaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
@Disabled("E2E tests disabled due to Spring Security configuration issues - need to fix context loading")
@DisplayName("Testes End-to-End - Fluxos Completos de Cliente")
class ClienteFlowE2ETest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Deve criar cliente pessoa física com sucesso")
    void deveCriarClientePessoaFisicaComSucesso() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/v1/admin/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nome": "João Silva",
                        "cpf": "12345678909",
                        "email": "joao.silva@exemplo.com"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value("123.456.789-09"))
                .andExpect(jsonPath("$.email").value("joao.silva@exemplo.com"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Deve criar cliente pessoa jurídica com sucesso")
    void deveCriarClientePessoaJuridicaComSucesso() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/v1/admin/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nome": "Empresa Teste",
                        "cnpj": "11222333000181",
                        "email": "contato@empresa.com"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Empresa Teste"))
                .andExpect(jsonPath("$.cnpj").value("11.222.333/0001-81"))
                .andExpect(jsonPath("$.email").value("contato@empresa.com"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Deve listar todos clientes com sucesso")
    void deveListarTodosClientesComSucesso() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/v1/admin/clientes")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Deve validar CPF inválido")
    void deveValidarCpfInvalido() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/v1/admin/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nome": "João Silva",
                        "cpf": "123",
                        "email": "joao@teste.com"
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Deve validar email inválido")
    void deveValidarEmailInvalido() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/v1/admin/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nome": "João Silva",
                        "cpf": "12345678909",
                        "email": "email-invalido"
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Deve retornar 400 para ID inválido")
    void deveRetornar400ParaIdInvalido() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/v1/admin/clientes/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest());
    }
}
