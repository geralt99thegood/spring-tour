package com.example.springtour.controller;

import com.example.springtour.model.UserListResponse;
import com.example.springtour.model.UserResponse;
import com.example.springtour.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable int id) {
        UserResponse userResponse = userService.getUserById(id);
        if (userResponse != null) {
            return ResponseEntity.ok(userResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<UserListResponse> getUsers(@RequestParam(defaultValue = "1") int page) {
        UserListResponse userListResponse = userService.getUsers(page);
        if (userListResponse != null) {
            return ResponseEntity.ok(userListResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
