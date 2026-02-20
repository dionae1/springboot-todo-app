package com.example.demo.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.example.demo.domain.Todo;
import com.example.demo.domain.User;

@DataJpaTest
public class TodoRepositoryTest {
    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    private User createUser() {
        return new User("William Lemos", "will21@email.com", "1234");
    }

    private Todo createTodo(User user) {
        return new Todo("Change Password", "Change runescape password due the yesterday problem!", user);
    }

    @Test
    void shouldMapUserRelationship() {
        User user = createUser();
        Todo todo = createTodo(user);

        userRepository.save(user);
        todoRepository.save(todo);

        Todo savedTodo = todoRepository.findById(todo.getId()).orElse(null);

        Assertions.assertNotNull(savedTodo);
        Assertions.assertNotNull(savedTodo.getUser());
        Assertions.assertEquals(user.getId(), savedTodo.getUser().getId());
        Assertions.assertEquals(user.getName(), savedTodo.getUser().getName());
    }

    @Test
    void shouldTrowExceptionWhenUserIsInvalid() {
        Todo todo = createTodo(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            todoRepository.save(todo);
        });
    }
}
