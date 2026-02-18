package com.example.demo.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.domain.User;
import com.example.demo.dto.UserPostRequestBody;
import com.example.demo.dto.UserPutRequestBody;
import com.example.demo.dto.UserResponseBody;
import com.example.demo.repository.UserRepository;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllUsers() {
        User user1 = new User("William Lemos", "will@mail.com", "1234");
        User user2 = new User("John Doe", "john@mail.com", "5678");

        UserResponseBody response1 = new UserResponseBody(user1.getId(), user1.getName(), user1.getEmail());
        UserResponseBody response2 = new UserResponseBody(user2.getId(), user2.getName(), user2.getEmail());

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user1, user2), pageable, 2);

        Mockito.when(userRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(userMapper.toUserResponseBody(user1)).thenReturn(response1);
        Mockito.when(userMapper.toUserResponseBody(user2)).thenReturn(response2);

        var result = userService.list(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(Page.class, result);
        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertTrue(result.getContent().contains(response1));
        Assertions.assertTrue(result.getContent().contains(response2));
    }

    @Test
    void shouldReturnEmptyListWhenNoUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(userRepository.findAll(pageable)).thenReturn(Page.empty());

        var result = userService.list(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(Page.class, result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnSavedUser() {
        User user = new User("William Lemos", "will@mail.com", "1234");
        UserResponseBody response = new UserResponseBody(user.getId(), user.getName(), user.getEmail());
        UserPostRequestBody userRequest = new UserPostRequestBody(user.getName(), user.getEmail(), user.getPassword());

        Mockito.when(userRepository.existsByEmail(userRequest.email())).thenReturn(false);
        Mockito.when(userMapper.toUser(userRequest)).thenReturn(user);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(userMapper.toUserResponseBody(user)).thenReturn(response);

        var result = userService.save(userRequest);

        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(UserResponseBody.class, result);
        Assertions.assertEquals(response, result);
        Mockito.verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        UserPostRequestBody userRequest = new UserPostRequestBody("William Lemos", "will21@email.com", "1234");

        Mockito.when(userRepository.existsByEmail(userRequest.email())).thenReturn(true);

        Assertions.assertThrows(ResponseStatusException.class, () -> userService.save(userRequest));
    }

    @Test
    void shouldReturnUserById() {
        User user = new User("William Lemos", "will@mail.com", "1234");
        UserResponseBody expectedResponse = new UserResponseBody(user.getId(), user.getName(), user.getEmail());

        Mockito.when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
        Mockito.when(userMapper.toUserResponseBody(user)).thenReturn(expectedResponse);

        var result = userService.find(user.getId());

        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(UserResponseBody.class, result);
        Assertions.assertEquals(expectedResponse, result);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> userService.find(1L));
    }

    @Test
    void shouldRemoveUser() {
        userService.remove(1L);
        Mockito.verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldUpdateUser() {
        User existingUser = new User("William Lemos", "will@mail.com", "1234");
        UserPutRequestBody userRequest = new UserPutRequestBody("Updated User Name", "newmail@mail.com");
        UserResponseBody expectedResponse = new UserResponseBody(existingUser.getId(), userRequest.name(),
                userRequest.email());

        existingUser.setId(1L);

        Mockito.when(userRepository.findById(existingUser.getId())).thenReturn(java.util.Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(existingUser);
        Mockito.when(userMapper.toUserResponseBody(existingUser)).thenReturn(expectedResponse);

        var result = userService.update(existingUser.getId(), userRequest);

        Mockito.verify(userRepository).save(Mockito.any(User.class));

        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(UserResponseBody.class, result);

        Assertions.assertEquals(expectedResponse, result);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnUpdate() {
        UserPutRequestBody userRequest = new UserPutRequestBody("Updated User Name", "updated@email.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> userService.update(1L, userRequest));
    }
}