package com.techchallenge.oficina.sharedkernel.application.ports;

import com.techchallenge.oficina.sharedkernel.infrastructure.security.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        logger.info("Tentativa de login para o usuário: {}", authRequest.getUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            response.put("username", userDetails.getUsername());

            logger.info("Login bem-sucedido para o usuário: {}", authRequest.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Falha na autenticação para o usuário: {}. Erro: {}", authRequest.getUsername(), e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Credenciais inválidas");
            return ResponseEntity.status(401).body(error);
        }
    }

    public static class AuthRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
