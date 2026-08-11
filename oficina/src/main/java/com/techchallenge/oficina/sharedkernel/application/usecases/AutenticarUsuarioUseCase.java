package com.techchallenge.oficina.sharedkernel.application.usecases;

import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ClienteEntity;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.repositories.ClienteJpaRepository;
import com.techchallenge.oficina.sharedkernel.application.usecases.commands.AutenticarUsuarioCommand;
import com.techchallenge.oficina.sharedkernel.application.usecases.ports.input.AutenticarUsuarioInput;
import com.techchallenge.oficina.sharedkernel.application.usecases.responses.AuthResponse;
import com.techchallenge.oficina.sharedkernel.infrastructure.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AutenticarUsuarioUseCase implements AutenticarUsuarioInput {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final ClienteJpaRepository clienteJpaRepository;

    @Override
    public AuthResponse execute(AutenticarUsuarioCommand command) {
        String normalizedUsername = normalizeUsername(command.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedUsername, command.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = resolveRole(userDetails);
        String token = generateTokenForUser(role, normalizedUsername, userDetails);

        return new AuthResponse(token, "Bearer", normalizedUsername, role);
    }

    private String normalizeUsername(String username) {
        if (username == null) {
            return null;
        }

        String digitsOnly = username.replaceAll("\\D", "");
        if (digitsOnly.length() == 11 || digitsOnly.length() == 14) {
            return digitsOnly;
        }

        return username;
    }

    private String resolveRole(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .map(auth -> auth.substring(5))
                .findFirst()
                .orElse("USER");
    }

    private String generateTokenForUser(String role, String normalizedUsername, UserDetails userDetails) {
        if ("CLIENTE".equals(role)) {
            Optional<ClienteEntity> clienteOpt = clienteJpaRepository.findByCpf(normalizedUsername)
                    .or(() -> clienteJpaRepository.findByCnpj(normalizedUsername));
            String nome = clienteOpt.map(ClienteEntity::getNome).orElse("");
            String clienteId = clienteOpt.map(cliente -> cliente.getId() != null ? cliente.getId().toString() : "").orElse("");
            return jwtTokenUtil.generateClienteToken(normalizedUsername, nome, clienteId);
        }

        return jwtTokenUtil.generateToken(userDetails);
    }
}
