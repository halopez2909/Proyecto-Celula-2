package com.reto.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Filtro global que valida JWT en rutas protegidas.
 *
 * - /auth/**           → libre (login, register)
 * - /actuator/**       → libre (health checks)
 * - /orders/**         → requiere JWT válido
 * - /catalog/**        → requiere JWT válido
 */
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Value("${security.jwt.secret}")
    private String secret;

    // Rutas que NO requieren autenticación
    private static final List<String> OPEN_PATHS = List.of(
            "/auth/",
            "/actuator/"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Dejar pasar rutas abiertas
        if (isOpenPath(path)) {
            return chain.filter(exchange);
        }

        // Verificar que venga el header Authorization
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[Gateway] 401 - Token ausente en: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Extraer y validar el token
        String token = authHeader.substring(7);

        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = claims.getSubject();
            String role = claims.get("role", String.class);

            log.info("[Gateway] Token válido - usuario: {}, rol: {}, path: {}", email, role, path);

            // Propagar info del usuario al microservicio downstream via headers
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("[Gateway] 401 - Token expirado en: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();

        } catch (Exception e) {
            log.warn("[Gateway] 401 - Token inválido en: {} - {}", path, e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isOpenPath(String path) {
        return OPEN_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        // Se ejecuta DESPUÉS del CorrelationIdFilter
        return 0;
    }
}
