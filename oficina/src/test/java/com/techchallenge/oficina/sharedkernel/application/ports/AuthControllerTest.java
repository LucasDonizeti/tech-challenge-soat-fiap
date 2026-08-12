package com.techchallenge.oficina.sharedkernel.application.ports;

import com.techchallenge.oficina.sharedkernel.application.usecases.ports.input.AutenticarUsuarioInput;
import com.techchallenge.oficina.sharedkernel.application.usecases.ports.input.CadastrarSenhaClienteInput;
import com.techchallenge.oficina.sharedkernel.application.usecases.responses.AuthResponse;
import com.techchallenge.oficina.sharedkernel.web.dto.AuthRequestDto;
import com.techchallenge.oficina.sharedkernel.web.dto.AuthResponseDto;
import com.techchallenge.oficina.sharedkernel.web.dto.CadastrarSenhaRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitarios - AuthController")
class AuthControllerTest {

    @Mock
    private AutenticarUsuarioInput autenticarUsuarioInput;

    @Mock
    private CadastrarSenhaClienteInput cadastrarSenhaClienteInput;

    @InjectMocks
    private AuthController authController;

    @InjectMocks
    private AuthClienteController authClienteController;

    private AuthRequestDto authRequest;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequestDto();
        authRequest.setUsername("testuser");
        authRequest.setPassword("testpassword");
    }

    @Test
    @DisplayName("Deve autenticar usuario com sucesso e retornar token")
    void deveAutenticarUsuarioComSucessoERetornarToken() {
        when(autenticarUsuarioInput.execute(any())).thenReturn(new AuthResponse("jwt-token-123", "Bearer", "testuser", "ADMIN"));

        ResponseEntity<?> response = authController.createAuthenticationToken(authRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof AuthResponseDto);

        AuthResponseDto body = (AuthResponseDto) response.getBody();
        assertNotNull(body);
        assertEquals("jwt-token-123", body.getToken());
        assertEquals("Bearer", body.getType());
        assertEquals("testuser", body.getUsername());
        assertEquals("ADMIN", body.getRole());

        verify(autenticarUsuarioInput, times(1)).execute(any());
    }

    @Test
    @DisplayName("Deve retornar erro 401 quando credenciais sao invalidas")
    void deveRetornarErro401QuandoCredenciaisSaoInvalidas() {
        when(autenticarUsuarioInput.execute(any())).thenThrow(new RuntimeException("Bad credentials"));

        ResponseEntity<?> response = authController.createAuthenticationToken(authRequest);

        assertNotNull(response);
        assertEquals(401, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof java.util.Map);
        assertEquals("Credenciais inválidas", ((java.util.Map<?, ?>) response.getBody()).get("error"));

        verify(autenticarUsuarioInput, times(1)).execute(any());
    }

    @Test
    @DisplayName("Deve retornar username no corpo da resposta")
    void deveRetornarUsernameNoCorpoDaResposta() {
        when(autenticarUsuarioInput.execute(any())).thenReturn(new AuthResponse("jwt-token-456", "Bearer", "john.doe", "ADMIN"));
        authRequest.setUsername("john.doe");

        ResponseEntity<?> response = authController.createAuthenticationToken(authRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof AuthResponseDto);
        AuthResponseDto body = (AuthResponseDto) response.getBody();
        assertEquals("john.doe", body.getUsername());
    }

    @Test
    @DisplayName("Deve chamar AutenticarUsuarioInput com credenciais corretas")
    void deveChamarAutenticarUsuarioInputComCredenciaisCorretas() {
        authRequest.setUsername("testuser");
        authRequest.setPassword("testpassword");
        when(autenticarUsuarioInput.execute(any())).thenReturn(new AuthResponse("jwt-token", "Bearer", "testuser", "ADMIN"));

        authController.createAuthenticationToken(authRequest);

        verify(autenticarUsuarioInput, times(1)).execute(argThat(command ->
                command instanceof com.techchallenge.oficina.sharedkernel.application.usecases.commands.AutenticarUsuarioCommand
                        && ((com.techchallenge.oficina.sharedkernel.application.usecases.commands.AutenticarUsuarioCommand) command).getUsername().equals("testuser")
                        && ((com.techchallenge.oficina.sharedkernel.application.usecases.commands.AutenticarUsuarioCommand) command).getPassword().equals("testpassword")
        ));
    }

    @Test
    @DisplayName("Deve cadastrar senha do cliente usando CNPJ")
    void deveCadastrarSenhaDoClienteUsandoCnpj() {
        CadastrarSenhaRequestDto request = new CadastrarSenhaRequestDto();
        request.setCpf("12.345.678/0001-95");
        request.setSenha("novaSenha123");

        ResponseEntity<Void> response = authClienteController.cadastrarSenha(request);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(cadastrarSenhaClienteInput, times(1)).execute(any());
    }

    @Test
    @DisplayName("Deve retornar erro 401 quando cliente nao consegue autenticar")
    void deveRetornarErro401QuandoClienteNaoConsegueAutenticar() {
        when(autenticarUsuarioInput.execute(any())).thenThrow(new RuntimeException("Bad credentials"));

        ResponseEntity<?> response = authController.createAuthenticationToken(authRequest);

        assertNotNull(response);
        assertEquals(401, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof java.util.Map);
        assertEquals("Credenciais inválidas", ((java.util.Map<?, ?>) response.getBody()).get("error"));
    }
}
