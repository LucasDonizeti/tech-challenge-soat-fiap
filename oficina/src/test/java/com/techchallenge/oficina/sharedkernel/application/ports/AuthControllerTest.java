package com.techchallenge.oficina.sharedkernel.application.ports;

import com.techchallenge.oficina.sharedkernel.infrastructure.security.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - AuthController")
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private AuthController authController;

    private AuthController.AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        authRequest = new AuthController.AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("testpassword");
    }

    @Test
    @DisplayName("Deve autenticar usuário com sucesso e retornar token")
    void deveAutenticarUsuarioComSucessoERetornarToken() {
        // Arrange
        UserDetails userDetails = User.withUsername("testuser")
                .password("testpassword")
                .roles("USER")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("jwt-token-123");

        // Act
        ResponseEntity<?> response = authController.createAuthenticationToken(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("jwt-token-123", body.get("token"));
        assertEquals("Bearer", body.get("type"));
        assertEquals("testuser", body.get("username"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenUtil, times(1)).generateToken(userDetails);
    }

    @Test
    @DisplayName("Deve retornar erro 401 quando credenciais são inválidas")
    void deveRetornarErro401QuandoCredenciaisSaoInvalidas() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act
        ResponseEntity<?> response = authController.createAuthenticationToken(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals(401, response.getStatusCode().value());
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Credenciais inválidas", body.get("error"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("Deve retornar erro 401 quando ocorre exceção genérica na autenticação")
    void deveRetornarErro401QuandoOcorreExcecaoGenericaNaAutenticacao() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<?> response = authController.createAuthenticationToken(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals(401, response.getStatusCode().value());
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Credenciais inválidas", body.get("error"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("Deve permitir definir username no AuthRequest")
    void devePermitirDefinirUsernameNoAuthRequest() {
        // Act
        authRequest.setUsername("newuser");

        // Assert
        assertEquals("newuser", authRequest.getUsername());
    }

    @Test
    @DisplayName("Deve permitir definir password no AuthRequest")
    void devePermitirDefinirPasswordNoAuthRequest() {
        // Act
        authRequest.setPassword("newpassword");

        // Assert
        assertEquals("newpassword", authRequest.getPassword());
    }

    @Test
    @DisplayName("Deve criar AuthRequest com construtor padrão")
    void deveCriarAuthRequestComConstrutorPadrao() {
        // Act
        AuthController.AuthRequest request = new AuthController.AuthRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }

    @Test
    @DisplayName("Deve criar AuthRequest com username e password definidos")
    void deveCriarAuthRequestComUsernameEPasswordDefinidos() {
        // Act
        AuthController.AuthRequest request = new AuthController.AuthRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        // Assert
        assertEquals("admin", request.getUsername());
        assertEquals("admin123", request.getPassword());
    }

    @Test
    @DisplayName("Deve gerar token com tipo Bearer")
    void deveGerarTokenComTipoBearer() {
        // Arrange
        UserDetails userDetails = User.withUsername("testuser")
                .password("testpassword")
                .roles("USER")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("generated-jwt-token");

        // Act
        ResponseEntity<?> response = authController.createAuthenticationToken(authRequest);

        // Assert
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Bearer", body.get("type"));
        assertEquals("generated-jwt-token", body.get("token"));
    }

    @Test
    @DisplayName("Deve retornar username no corpo da resposta")
    void deveRetornarUsernameNoCorpoDaResposta() {
        // Arrange
        UserDetails userDetails = User.withUsername("john.doe")
                .password("password123")
                .roles("ADMIN")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("jwt-token-456");

        // Act
        ResponseEntity<?> response = authController.createAuthenticationToken(authRequest);
        authRequest.setUsername("john.doe");

        // Assert
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("john.doe", body.get("username"));
    }

    @Test
    @DisplayName("Deve inicializar AuthController com dependências")
    void deveInicializarAuthControllerComDependencias() {
        // Assert
        assertNotNull(authController);
        assertNotNull(authenticationManager);
        assertNotNull(jwtTokenUtil);
    }

    @Test
    @DisplayName("Deve chamar authenticationManager com credenciais corretas")
    void deveChamarAuthenticationManagerComCredenciaisCorretas() {
        // Arrange
        UserDetails userDetails = User.withUsername("testuser")
                .password("testpassword")
                .roles("USER")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("jwt-token");

        // Act
        authController.createAuthenticationToken(authRequest);

        // Assert
        verify(authenticationManager, times(1)).authenticate(
                argThat(auth -> {
                    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) auth;
                    return "testuser".equals(token.getPrincipal()) &&
                           "testpassword".equals(token.getCredentials());
                })
        );
    }

    @Test
    @DisplayName("Deve gerar token usando UserDetails do authentication")
    void deveGerarTokenUsandoUserDetailsDoAuthentication() {
        // Arrange
        UserDetails userDetails = User.withUsername("testuser")
                .password("testpassword")
                .roles("USER")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("jwt-token-789");

        // Act
        authController.createAuthenticationToken(authRequest);

        // Assert
        verify(jwtTokenUtil, times(1)).generateToken(userDetails);
    }
}
