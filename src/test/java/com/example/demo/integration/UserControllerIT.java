package com.example.demo.integration;

import static com.example.demo.util.TodoCreator.createSecondaryTodo;
import static com.example.demo.util.TodoCreator.createTodo;
import static com.example.demo.util.UserCreator.createSecondaryUser;
import static com.example.demo.util.UserCreator.createUser;
import static com.example.demo.util.UserCreator.createUserPostRequestBody;

import java.util.List;

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
import com.example.demo.dto.TodoResponseBody;
import com.example.demo.dto.UserPostRequestBody;
import com.example.demo.dto.UserResponseBody;
import com.example.demo.repository.TodoRepository;
import com.example.demo.repository.UserRepository;

@AutoConfigureTestDatabase
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIT {

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
    void shouldReturnAllUsersPage() {
        User user = userRepository.save(createUser());

        PageResponseBody<User> response = restTestClient.get()
                .uri("/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PageResponseBody<User>>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.content().isEmpty());

        Assertions.assertEquals(user.getId(), response.content().get(0).getId());
    }

    @Test
    void shouldReturnEmptyPageWhenNoUsers() {
        PageResponseBody<User> response = restTestClient.get()
                .uri("/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PageResponseBody<User>>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.content().isEmpty());
    }

    @Test
    void shouldReturnUserById() {
        User user = userRepository.save(createUser());

        UserResponseBody response = restTestClient.get()
                .uri("/users/{id}", user.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseBody.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(user.getId(), response.id());
    }

    @Test
    void shouldThrowResponseStatusExceptionWhenUserNotFound() {
        restTestClient.get()
                .uri("/users/{id}", 999L)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldCreateUser() {
        UserPostRequestBody userPostRequestBody = createUserPostRequestBody(createUser());

        UserResponseBody response = restTestClient.post()
                .uri("/users")
                .body(userPostRequestBody)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseBody.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(userPostRequestBody.name(), response.name());
    }

    @Test
    void shouldThrowResponseStatusExceptionWhenUserEmailIsDuplicated() {
        UserPostRequestBody userPostRequestBody = createUserPostRequestBody(createUser());

        restTestClient.post()
                .uri("/users")
                .body(userPostRequestBody)
                .exchange()
                .expectStatus().isCreated();

        restTestClient.post()
                .uri("/users")
                .body(userPostRequestBody)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldUpdateUser() {
        User user = userRepository.save(createUser());
        UserPostRequestBody userPostRequestBody = createUserPostRequestBody(user);

        UserResponseBody response = restTestClient.put()
                .uri("/users/{id}", user.getId())
                .body(userPostRequestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseBody.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(userPostRequestBody.name(), response.name());
    }

    @Test
    void shouldThrowResponseStatusExceptionWhenUpdatingUserWithDuplicatedEmail() {
        User user1 = userRepository.save(createUser());
        User user2 = userRepository.save(createSecondaryUser());

        UserPostRequestBody userPostRequestBody = createUserPostRequestBody(user1);

        restTestClient.put()
                .uri("/users/{id}", user2.getId())
                .body(userPostRequestBody)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldThrowResponseStatusExceptionWhenUpdatingNonExistingUser() {
        UserPostRequestBody userPostRequestBody = createUserPostRequestBody(createUser());

        restTestClient.put()
                .uri("/users/{id}", 999L)
                .body(userPostRequestBody)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldDeleteUser() {
        User user = userRepository.save(createUser());
        restTestClient.delete()
                .uri("/users/{id}", user.getId())
                .exchange()
                .expectStatus().isNoContent();

        Assertions.assertFalse(userRepository.findById(user.getId()).isPresent());
    }

    @Test
    void shouldReturnNoContentWhenDeletingNonExistingUser() {
        restTestClient.delete()
                .uri("/users/{id}", 999L)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldReturnUserTodos() {
        User user = userRepository.save(createUser());
        Todo todo1 = createTodo(user);
        Todo todo2 = createSecondaryTodo(user);

        todoRepository.save(todo1);
        todoRepository.save(todo2);

        List<TodoResponseBody> response = restTestClient.get()
                .uri("/users/{id}/todos", user.getId())
                .exchange()
                .expectStatus().isOk()
                .returnResult(new ParameterizedTypeReference<List<TodoResponseBody>>() {
                })
                .getResponseBody();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.size());
        Assertions.assertEquals(todo1.getTitle(), response.get(0).title());
        Assertions.assertEquals(todo2.getTitle(), response.get(1).title());
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoTodos() {
        User user = userRepository.save(createUser());

        List<TodoResponseBody> response = restTestClient.get()
                .uri("/users/{id}/todos", user.getId())
                .exchange()
                .expectStatus().isOk()
                .returnResult(new ParameterizedTypeReference<List<TodoResponseBody>>() {
                })
                .getResponseBody();

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isEmpty());
    }
}