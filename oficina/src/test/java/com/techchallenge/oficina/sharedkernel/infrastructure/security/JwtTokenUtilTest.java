package com.techchallenge.oficina.sharedkernel.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - JwtTokenUtil")
class JwtTokenUtilTest {

    @InjectMocks
    private JwtTokenUtil jwtTokenUtil;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() throws Exception {
        userDetails = User.withUsername("testuser")
                .password("password")
                .roles("USER")
                .build();

        // Manually set the @Value fields since Spring context is not available
        setField(jwtTokenUtil, "secret", "mySecretKeyForJWTTokenGenerationThatIsLongEnough");
        setField(jwtTokenUtil, "expiration", 86400L);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    @DisplayName("Deve gerar token com sucesso")
    void deveGerarTokenComSucesso() {
        // Act
        String token = jwtTokenUtil.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Deve extrair username do token")
    void deveExtrairUsernameDoToken() {
        // Arrange
        String token = jwtTokenUtil.generateToken(userDetails);

        // Act
        String username = jwtTokenUtil.getUsernameFromToken(token);

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Deve validar token válido")
    void deveValidarTokenValido() {
        // Arrange
        String token = jwtTokenUtil.generateToken(userDetails);

        // Act
        boolean isValid = jwtTokenUtil.validateToken(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve validar token com username incorreto")
    void deveValidarTokenComUsernameIncorreto() {
        // Arrange
        String token = jwtTokenUtil.generateToken(userDetails);
        UserDetails differentUser = User.withUsername("differentuser")
                .password("password")
                .roles("USER")
                .build();

        // Act
        boolean isValid = jwtTokenUtil.validateToken(token, differentUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve gerar tokens válidos para chamadas consecutivas")
    void deveGerarTokensValidosParaChamadasConsecutivas() {
        // Act
        String token1 = jwtTokenUtil.generateToken(userDetails);
        String token2 = jwtTokenUtil.generateToken(userDetails);

        // Assert - Both tokens should be valid and contain the same username
        assertNotNull(token1);
        assertNotNull(token2);
        assertEquals("testuser", jwtTokenUtil.getUsernameFromToken(token1));
        assertEquals("testuser", jwtTokenUtil.getUsernameFromToken(token2));
        assertTrue(jwtTokenUtil.validateToken(token1, userDetails));
        assertTrue(jwtTokenUtil.validateToken(token2, userDetails));
    }

    @Test
    @DisplayName("Deve lançar exceção ao extrair username de token inválido")
    void deveLancarExcecaoAoExtrairUsernameDeTokenInvalido() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtTokenUtil.getUsernameFromToken("invalid.token.string");
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para UserDetails nulo")
    void deveLancarExcecaoParaUserDetailsNulo() {
        // Arrange
        String token = jwtTokenUtil.generateToken(userDetails);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            jwtTokenUtil.validateToken(token, null);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para token nulo")
    void deveLancarExcecaoParaTokenNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            jwtTokenUtil.validateToken(null, userDetails);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para token vazio")
    void deveLancarExcecaoParaTokenVazio() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            jwtTokenUtil.validateToken("", userDetails);
        });
    }

    @Test
    @DisplayName("Deve gerar token com username de UserDetails")
    void deveGerarTokenComUsernameDeUserDetails() {
        // Arrange
        UserDetails user = User.withUsername("joao.silva")
                .password("pass123")
                .roles("ADMIN")
                .build();

        // Act
        String token = jwtTokenUtil.generateToken(user);
        String username = jwtTokenUtil.getUsernameFromToken(token);

        // Assert
        assertEquals("joao.silva", username);
    }

    @Test
    @DisplayName("Deve gerar token não nulo para UserDetails válido")
    void deveGerarTokenNaoNuloParaUserDetailsValido() {
        // Act
        String token = jwtTokenUtil.generateToken(userDetails);

        // Assert
        assertNotNull(token);
    }
}
