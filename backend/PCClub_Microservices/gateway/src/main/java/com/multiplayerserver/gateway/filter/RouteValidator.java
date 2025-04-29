package com.multiplayerserver.gateway.filter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/auth/login"
    );

    public static final List<String> adminApiEndpoints = List.of(
            "/client/add",
            "/client/get-all",
            "/client/get-by-email/.*",  // Регулярное выражение для get-by-email
            "/client/get/\\d+",         // Регулярное выражение для get/{id}, где {id} - это цифры
            "/order/create",
            "/order/get-by-client/\\d+",
            "/order/cancel/\\d+",
            "/payment/top-up",
            "/payment/wallet/.*",       // Для поддержания динамических частей в пути
            "/payment/operations/.*",   // Для поддержания динамических частей в пути

            // PC
            "/pc-service/pc/create",
            "/pc-service/pc/update",
            "/pc-service/pc/get/\\d+", // Регулярное выражение для get/{id}
            "/pc-service/pc/get-all",
            "/pc-service/pc/change-status",
            "/pc-service/pc/delete/\\d+", // Регулярное выражение для delete/{id}
            "/pc-service/pc/reserve",

            // PC Group
            "/pc-service/pc-group/create",
            "/pc-service/pc-group/get/\\d+", // Регулярное выражение для get/{id}
            "/pc-service/pc-group/get-all",
            "/pc-service/pc-group/delete/\\d+", // Регулярное выражение для delete/{id}

            // Service
            "/pc-service/service/create",
            "/pc-service/service/get/\\d+", // Регулярное выражение для get/{id}
            "/pc-service/service/get-all"
    );


    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
