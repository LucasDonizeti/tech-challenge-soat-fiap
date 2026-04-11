package com.techchallenge.oficina.sharedkernel.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_HEADER = "Authorization";

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        final String requestTokenHeader = request.getHeader(AUTH_HEADER);
        String username = null;
        String jwtToken = null;

        if (isValidBearerToken(requestTokenHeader)) {
            jwtToken = extractToken(requestTokenHeader);
            username = extractUsername(jwtToken);
        }

        if (shouldAuthenticate(username)) {
            authenticateUser(username, jwtToken, request);
        }

        chain.doFilter(request, response);
    }

    private boolean isValidBearerToken(String tokenHeader) {
        return tokenHeader != null && tokenHeader.startsWith(BEARER_PREFIX);
    }

    private String extractToken(String tokenHeader) {
        return tokenHeader.substring(BEARER_PREFIX.length());
    }

    private String extractUsername(String token) {
        try {
            return jwtTokenUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            logger.warn("Token JWT inválido ou expirado. Erro: {}", e.getMessage());
            return null;
        }
    }

    private boolean shouldAuthenticate(String username) {
        return username != null && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void authenticateUser(String username, String token, HttpServletRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtTokenUtil.validateToken(token, userDetails)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            logger.info("Usuário autenticado com sucesso via JWT: {}", username);
        } else {
            logger.warn("Falha na validação do token JWT para o usuário: {}", username);
        }
    }
}
