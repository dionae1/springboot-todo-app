package com.example.demo.integration;

import static com.example.demo.util.TodoCreator.createTodo;
import static com.example.demo.util.TodoCreator.createTodoPostRequestBody;
import static com.example.demo.util.TodoCreator.createTodoPutRequestBody;
import static com.example.demo.util.UserCreator.createUser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.example.demo.domain.Todo;
import com.example.demo.domain.User;
import com.example.demo.dto.PageResponseBody;
import com.example.demo.dto.TodoPostRequestBody;
import com.example.demo.dto.TodoPutRequestBody;
import com.example.demo.dto.TodoResponseBody;
import com.example.demo.repository.TodoRepository;
import com.example.demo.repository.UserRepository;

@AutoConfigureTestDatabase
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoControllerIT {
    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnAllTodosPage() {
        User user = userRepository.save(createUser());
        Todo todo = todoRepository.save(createTodo(user));

        PageResponseBody<Todo> response = restTestClient.get()
                .uri("/todos")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PageResponseBody<Todo>>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.content().isEmpty());

        Assertions.assertEquals(todo.getId(), response.content().get(0).getId());
    }

    @Test
    void shouldReturnEmptyPageWhenNoTodos() {
        PageResponseBody<Todo> response = restTestClient.get()
                .uri("/todos")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PageResponseBody<Todo>>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.content().isEmpty());
    }

    @Test
    void shouldReturnTodoById() {
        User user = userRepository.save(createUser());
        Todo todo = todoRepository.save(createTodo(user));

        TodoResponseBody response = restTestClient.get()
                .uri("/todos/{id}", todo.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(TodoResponseBody.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(todo.getId(), response.id());
    }

    @Test
    void shouldThrowResponseStatusExceptionWhenTodoNotFound() {
        restTestClient.get()
                .uri("/todos/{id}", 999L)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldCreateTodo() {
        User user = userRepository.save(createUser());

        TodoPostRequestBody requestBody = createTodoPostRequestBody(user);

        TodoResponseBody response = restTestClient.post()
                .uri("/todos")
                .body(requestBody)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TodoResponseBody.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(requestBody.title(), response.title());
        Assertions.assertEquals(requestBody.description(), response.description());
    }

    @Test
    void shouldThrowResponseStatusExceptionWhenTodoUserIsInvalid() {
        TodoPostRequestBody requestBody = createTodoPostRequestBody(createUser());

        restTestClient.post()
                .uri("/todos")
                .body(requestBody)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldUpdateTodo() {
        User user = userRepository.save(createUser());
        Todo todo = todoRepository.save(createTodo(user));

        TodoPutRequestBody requestBody = createTodoPutRequestBody();

        TodoResponseBody response = restTestClient.put()
                .uri("/todos/{id}", todo.getId())
                .body(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TodoResponseBody.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(todo.getId(), response.id());
        Assertions.assertEquals(requestBody.title(), response.title());
        Assertions.assertEquals(requestBody.description(), response.description());
    }

    @Test
    void shouldThrowResponseStatusExceptionWhenUpdatingNonExistingTodo() {
        TodoPutRequestBody requestBody = createTodoPutRequestBody();

        restTestClient.put()
                .uri("/todos/{id}", 999L)
                .body(requestBody)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldDeleteTodo() {
        User user = userRepository.save(createUser());
        Todo todo = todoRepository.save(createTodo(user));

        restTestClient.delete()
                .uri("/todos/{id}", todo.getId())
                .exchange()
                .expectStatus().isNoContent();

        Assertions.assertFalse(todoRepository.findById(todo.getId()).isPresent());
    }

    @Test
    void shouldReturnNoContentWhenDeletingNonExistingTodo() {
        restTestClient.delete()
                .uri("/todos/{id}", 999L)
                .exchange()
                .expectStatus().isNoContent();
    }
}
