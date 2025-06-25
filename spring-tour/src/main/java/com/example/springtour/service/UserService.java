package com.example.springtour.service;

import com.example.springtour.model.UserListResponse;
import com.example.springtour.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    private final WebClient webClient;

    @Autowired
    public UserService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://reqres.in/api").build();
    }

    public Mono<UserResponse> getUserById(int id) {
        return webClient.get()
                .uri("/users/{id}", id)
                .retrieve()
                .bodyToMono(UserResponse.class);
    }

    public Mono<UserListResponse> getUsers(int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/users").queryParam("page", page).build())
                .retrieve()
                .bodyToMono(UserListResponse.class);
    }
}
