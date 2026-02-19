package com.example.demo.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.example.demo.domain.User;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User createUser() {
        return new User("William Lemos", "will21@email.com", "1234");
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
}
