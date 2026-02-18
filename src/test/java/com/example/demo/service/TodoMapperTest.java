package com.example.demo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.demo.domain.Todo;
import com.example.demo.domain.TodoStage;
import com.example.demo.domain.User;
import com.example.demo.dto.TodoPostRequestBody;
import com.example.demo.dto.TodoResponseBody;

public class TodoMapperTest {

    private TodoMapper todoMapper = new TodoMapper();

    @Test
    void shouldMapTodoToTodoResponseBody() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password");

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Test Todo");
        todo.setDescription("This is a test todo.");
        todo.setUser(user);
        todo.setStage(TodoStage.NOT_STARTED);

        var result = todoMapper.toTodoResponseBody(todo);

        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(TodoResponseBody.class, result);

        Assertions.assertEquals(todo.getId(), result.id());
        Assertions.assertEquals(todo.getTitle(), result.title());
        Assertions.assertEquals(todo.getDescription(), result.description());
        Assertions.assertEquals(todo.getStage().toString(), result.stage());
        Assertions.assertEquals(todo.getUser().getId(), result.userId());
    }

    @Test
    void shouldMapTodoPostRequestBodyToTodo() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password");

        TodoPostRequestBody requestBody = new TodoPostRequestBody(1L, "Test Todo", "This is a test todo.");

        var result = todoMapper.toTodo(requestBody, user);

        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(Todo.class, result);

        Assertions.assertEquals(requestBody.title(), result.getTitle());
        Assertions.assertEquals(requestBody.description(), result.getDescription());
        Assertions.assertEquals(requestBody.userId(), result.getUser().getId());
        Assertions.assertEquals(TodoStage.NOT_STARTED, result.getStage());
    }

    @Test
    void shouldThrowExceptionWhenMappingNullTodoToTodoResponseBody() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            todoMapper.toTodoResponseBody(null);
        });
    }
}