package com.example.demo.service;

import static com.example.demo.util.UserCreator.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;



    @Test
    void shouldReturnAllUsers() {
        User user1 = createUser();
        User user2 = createSecondaryUser();

        UserResponseBody response1 = createUserResponseBody(user1);
        UserResponseBody response2 = createUserResponseBody(user2);

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
        User user = createUser();
        UserResponseBody response = createUserResponseBody(user);
        UserPostRequestBody userRequest = createUserPostRequestBody(user);

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
        User user = createUser();
        UserPostRequestBody userRequest = createUserPostRequestBody(user);

        Mockito.when(userRepository.existsByEmail(userRequest.email())).thenReturn(true);

        Assertions.assertThrows(ResponseStatusException.class, () -> userService.save(userRequest));
    }

    @Test
    void shouldReturnUserById() {
        User user = createUser();
        UserResponseBody expectedResponse = createUserResponseBody(user);

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
        User existingUser = createUser();
        UserPutRequestBody userRequest = createUserPutRequestBody();
        UserResponseBody expectedResponse = createUserResponseBody(existingUser);

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
        UserPutRequestBody userRequest = createUserPutRequestBody();

        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> userService.update(1L, userRequest));
    }
}