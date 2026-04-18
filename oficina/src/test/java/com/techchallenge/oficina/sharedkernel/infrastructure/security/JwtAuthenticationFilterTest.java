package com.techchallenge.oficina.sharedkernel.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - JwtAuthenticationFilter")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userDetails = User.withUsername("testuser")
                .password("password")
                .roles("USER")
                .build();
    }

    @Test
    @DisplayName("Deve autenticar usuário com token válido")
    void deveAutenticarUsuarioComTokenValido() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtTokenUtil.validateToken(token, userDetails)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve ignorar requisição sem header Authorization")
    void deveIgnorarRequisicaoSemHeaderAuthorization() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtTokenUtil, never()).getUsernameFromToken(anyString());
    }

    @Test
    @DisplayName("Deve ignorar requisição com Authorization sem prefixo Bearer")
    void deveIgnorarRequisicaoComAuthorizationSemPrefixoBearer() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtTokenUtil, never()).getUsernameFromToken(anyString());
    }

    @Test
    @DisplayName("Deve ignorar requisição com token inválido")
    void deveIgnorarRequisicaoComTokenInvalido() throws ServletException, IOException {
        // Arrange
        String token = "invalid.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenUtil.getUsernameFromToken(token)).thenThrow(new RuntimeException("Invalid token"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve ignorar requisição quando token não é válido para o usuário")
    void deveIgnorarRequisicaoQuandoTokenNaoEValidoParaUsuario() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtTokenUtil.validateToken(token, userDetails)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve ignorar requisição quando usuário já está autenticado")
    void deveIgnorarRequisicaoQuandoUsuarioJaEstaAutenticado() throws ServletException, IOException {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                )
        );
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn("testuser");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("Deve extrair token corretamente do header Authorization")
    void deveExtrairTokenCorretamenteDoHeaderAuthorization() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtTokenUtil, times(1)).getUsernameFromToken(token);
    }

    @Test
    @DisplayName("Deve autenticar usuário com sucesso")
    void deveAutenticarUsuarioComSucesso() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtTokenUtil.validateToken(token, userDetails)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(userDetailsService, times(1)).loadUserByUsername("testuser");
        verify(jwtTokenUtil, times(1)).validateToken(token, userDetails);
    }

    @Test
    @DisplayName("Deve continuar chain mesmo com erro na extração do username")
    void deveContinuarChainMesmoComErroNaExtracaoDoUsername() throws ServletException, IOException {
        // Arrange
        String token = "invalid.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenUtil.getUsernameFromToken(token)).thenThrow(new RuntimeException("Parse error"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve inicializar filtro com dependências")
    void deveInicializarFiltroComDependencias() {
        // Assert
        assertNotNull(jwtAuthenticationFilter);
        assertNotNull(jwtTokenUtil);
        assertNotNull(userDetailsService);
    }

    @Test
    @DisplayName("Deve chamar doFilter mesmo sem autenticação")
    void deveChamarDoFilterMesmoSemAutenticacao() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
