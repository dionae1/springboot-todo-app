package com.example.demo.repository;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.example.demo.domain.Todo;
import com.example.demo.domain.User;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    private User createUser() {
        return new User("William Lemos", "will21@email.com", "1234");
    }

    private Todo createTodo(User user) {
        return new Todo("Change Password", "Change runescape password due the yesterday problem!", user);
    }

    @Test
    void shouldFindUserByEmail() {
        User user = createUser();
        userRepository.save(user);

        User foundUser = userRepository.findByEmail(user.getEmail()).orElseThrow();

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(user.getName(), foundUser.getName());
        Assertions.assertEquals(user.getEmail(), foundUser.getEmail());
        Assertions.assertEquals(user.getPassword(), foundUser.getPassword());
    }

    @Test
    void shouldReturnEmptyOptionalWhenEmailNotFound() {
        User user = createUser();
        userRepository.save(user);

        Assertions.assertTrue(userRepository.findByEmail("nonexistent@email.com").isEmpty());
    }

    @Test
    void shouldCheckIfEmailExists() {
        User user = createUser();
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail(user.getEmail());

        Assertions.assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        User user = createUser();
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("nonexistent@email.com");

        Assertions.assertFalse(exists);
    }

    @Test
    void shouldStartWithEmptyTodoRelationship() {
        User user = createUser();
        userRepository.save(user);

        User foundUser = userRepository.findByEmail(user.getEmail()).orElseThrow();

        Assertions.assertNotNull(foundUser);
        Assertions.assertNull(foundUser.getTodos());
    }

    @Test
    void shouldAddTodoToUser() {
        User user = createUser();
        Todo todo = createTodo(user);

        userRepository.save(user);
        todoRepository.save(todo);

        User foundUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
        foundUser.setTodos(new ArrayList<>(List.of(todo)));
        userRepository.save(foundUser);

        User updatedUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
        List<Todo> updatedTodos = updatedUser.getTodos();

        Assertions.assertNotNull(updatedUser);
        Assertions.assertNotNull(updatedTodos);

        Assertions.assertEquals(1, updatedTodos.size());
        Assertions.assertEquals(todo.getTitle(), updatedTodos.get(0).getTitle());
        Assertions.assertEquals(todo.getDescription(), updatedTodos.get(0).getDescription());
        Assertions.assertEquals(todo.getStage(), updatedTodos.get(0).getStage());
    }
}
