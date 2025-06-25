package com.example.springtour.service;

import com.example.springtour.model.UserListResponse;
import com.example.springtour.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {
    private final RestTemplate restTemplate;
    private static final String BASE_URL = "https://reqres.in/api";

    @Autowired
    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserResponse getUserById(int id) {
        String url = BASE_URL + "/users/" + id;
        return restTemplate.getForObject(url, UserResponse.class);
    }

    public UserListResponse getUsers(int page) {
        String url = BASE_URL + "/users?page=" + page;
        return restTemplate.getForObject(url, UserListResponse.class);
    }
}
