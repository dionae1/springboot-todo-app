package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.TodoResponseBody;
import com.example.demo.dto.UserPostRequestBody;
import com.example.demo.dto.UserPutRequestBody;
import com.example.demo.dto.UserResponseBody;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userServices;

    @GetMapping()
    public ResponseEntity<List<UserResponseBody>> getAllUsers() {
        return ResponseEntity.ok(userServices.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseBody> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userServices.find(id));
    }

    @PostMapping()
    public ResponseEntity<UserResponseBody> createUser(@Valid @RequestBody UserPostRequestBody user) {
        return ResponseEntity.created(null).body(userServices.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseBody> updateUser(@PathVariable Long id,
            @Valid @RequestBody UserPutRequestBody user) {
        return ResponseEntity.ok(userServices.update(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseBody> deleteUser(@PathVariable Long id) {
        userServices.remove(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/todos")
    public ResponseEntity<List<TodoResponseBody>> getUserTodos(@PathVariable Long id) {
        return ResponseEntity.ok(userServices.listUserTodos(id));
    }
}
