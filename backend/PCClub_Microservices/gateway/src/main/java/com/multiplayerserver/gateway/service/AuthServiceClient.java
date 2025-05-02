package com.multiplayerserver.gateway.service;

import com.multiplayerserver.gateway.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import reactor.core.publisher.Mono;


@FeignClient(name = "AUTH-SERVICE", path = "/auth")
public interface AuthServiceClient {

    @GetMapping("/me")
    Mono<UserDTO> getUserFromToken(@RequestHeader("Authorization") String token);
}



