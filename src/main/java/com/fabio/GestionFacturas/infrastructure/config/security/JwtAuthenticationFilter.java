package com.fabio.GestionFacturas.infrastructure.config.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Se ejecuta una vez por petición (antes de llegar a cualquier controller): lee el token Bearer y, si es válido,
 * rellena el {@code SecurityContext} para que {@code @AuthenticationPrincipal Long usuarioId} funcione en los
 * controllers. Se registra en {@link com.fabio.GestionFacturas.infrastructure.config.SecurityConfig} antes del
 * filtro por defecto de Spring. Sin token, o con uno inválido, la petición sigue sin autenticar;
 * después {@code anyRequest().authenticated()} de SecurityConfig la rechaza.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;


    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtTokenProvider.esValido(token)){
            filterChain.doFilter(request, response);
            return;
        }

        Long usuarioId = jwtTokenProvider.extraerUsuarioId(token);

        // El principal es solo el id del usuario (Long); la app no usa roles ni authorities, de ahí List.of()
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                usuarioId, null, List.of());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);


    }
}
