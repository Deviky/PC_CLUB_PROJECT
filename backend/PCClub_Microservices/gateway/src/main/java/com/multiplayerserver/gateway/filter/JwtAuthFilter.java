package com.multiplayerserver.gateway.filter;


import com.multiplayerserver.gateway.dto.UserDTO;
import com.multiplayerserver.gateway.enums.Role;
import com.multiplayerserver.gateway.service.AuthServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {
    @Autowired
    private RouteValidator validator;

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private AuthServiceClient authServiceClient;


    public JwtAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            log.debug("Incoming request path: {}", path);

            // Пропускаем незашищённые эндпоинты
            if (!validator.isSecured.test(request)) {
                log.debug("Open endpoint, skipping auth: {}", path);
                return chain.filter(exchange);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Missing or invalid Authorization header");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);
            log.debug("Extracted token: {}", token);

            try {
                UserDTO user = authServiceClient.getUserFromToken(token);
                log.debug("Authenticated user: {}", user);

                if (user == null || user.getRole() == null) {
                    log.warn("User or role is null");
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                Role role = user.getRole();
                boolean isAdmin = role == Role.ROLE_ADMIN;
                boolean isTechnical = role == Role.ROLE_TECHNICAL;

                log.debug("User role: {}", role);

                boolean isAllowed = isTechnical
                        || (isAdmin && validator.adminApiEndpoints.stream().anyMatch(path::matches)); // Используем регулярное выражение

                if (!isAllowed) {
                    log.warn("Access denied. Role: {}, Path: {}", role, path);
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }

                log.debug("Access granted for role: {}, path: {}", role, path);
                return chain.filter(exchange);

            } catch (Exception e) {
                log.error("Exception during auth", e);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }



    public static class Config {

    }

}
