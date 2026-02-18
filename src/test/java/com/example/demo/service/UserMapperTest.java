package com.example.demo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.demo.domain.User;
import com.example.demo.dto.UserPostRequestBody;
import com.example.demo.dto.UserResponseBody;

public class UserMapperTest {
    
    private UserMapper userMapper = new UserMapper();

    @Test
    void shouldMapUserToUserResponseBody() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        var result = userMapper.toUserResponseBody(user);

        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(UserResponseBody.class, result);

        Assertions.assertEquals(user.getId(), result.id());
        Assertions.assertEquals(user.getName(), result.name());
        Assertions.assertEquals(user.getEmail(), result.email());
    }

    @Test
    void shouldMapUserRequestBodyToUser() {
        UserPostRequestBody requestBody = new UserPostRequestBody("Test User", "test@example.com", "password");

        var result = userMapper.toUser(requestBody);

        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(User.class, result);

        Assertions.assertEquals(requestBody.name(), result.getName());
        Assertions.assertEquals(requestBody.email(), result.getEmail());
        Assertions.assertEquals(requestBody.password(), result.getPassword());
    }

    @Test
    void shouldThrowExceptionWhenMappingNullUserToUserResponseBody() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userMapper.toUserResponseBody(null);
        });
    }
}