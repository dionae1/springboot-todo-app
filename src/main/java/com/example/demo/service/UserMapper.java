package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.domain.User;
import com.example.demo.dto.UserPostRequestBody;
import com.example.demo.dto.UserResponseBody;

@Service
public class UserMapper {
    public UserResponseBody toUserResponseBody(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return new UserResponseBody(user.getId(), user.getName(), user.getEmail());
    }

    public User toUser(UserPostRequestBody userRequest) {
        return new User(userRequest.name(), userRequest.email(), userRequest.password());
    }
}
