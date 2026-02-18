package com.example.demo.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.TodoPostRequestBody;
import com.example.demo.dto.TodoPutRequestBody;
import com.example.demo.dto.TodoResponseBody;
import com.example.demo.service.TodoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoServices;

    @GetMapping
    public ResponseEntity<Page<TodoResponseBody>> getAll(Pageable pageable) {
        return ResponseEntity.ok(todoServices.list(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponseBody> getTodoById(@PathVariable Long id) {
        return ResponseEntity.ok(todoServices.find(id));
    }

    @PostMapping()
    public ResponseEntity<TodoResponseBody> createTodo(@Valid @RequestBody TodoPostRequestBody todo) {
        return ResponseEntity.ok(todoServices.save(todo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponseBody> updateTodo(@PathVariable Long id,
            @Valid @RequestBody TodoPutRequestBody updatedTodo) {
        return ResponseEntity.ok(todoServices.update(id, updatedTodo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoServices.remove(id);
        return ResponseEntity.noContent().build();
    }
}
