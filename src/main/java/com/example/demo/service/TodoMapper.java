package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.domain.Todo;
import com.example.demo.domain.User;
import com.example.demo.dto.TodoPostRequestBody;
import com.example.demo.dto.TodoResponseBody;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoMapper {
    public TodoResponseBody toTodoResponseBody(Todo todo) {
        if (todo == null) {
            throw new IllegalArgumentException("Todo cannot be null");
        }

        return new TodoResponseBody(todo.getId(), todo.getTitle(), todo.getDescription(), todo.getStage().toString(),
                todo.getUser().getId());
    }

    public Todo toTodo(TodoPostRequestBody todoPostRequestBody, User user) {
        return new Todo(todoPostRequestBody.title(), todoPostRequestBody.description(), user);
    }
}
