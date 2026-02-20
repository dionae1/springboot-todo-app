package com.example.demo.util;

import com.example.demo.domain.User;
import com.example.demo.dto.UserPostRequestBody;
import com.example.demo.dto.UserPutRequestBody;
import com.example.demo.dto.UserResponseBody;

public class UserCreator {
    public static User createUser() {
        return new User("William Lemos", "will@mail.com", "1234");
    }

    public static User createSecondaryUser() {
        return new User("John Doe", "john@mail.com", "5678");
    }

    public static UserResponseBody createUserResponseBody(User user) {
        return new UserResponseBody(user.getId(), user.getName(), user.getEmail());
    }

    public static UserPostRequestBody createUserPostRequestBody(User user) {
        return new UserPostRequestBody(user.getName(), user.getEmail(), user.getPassword());
    }

    public static UserPutRequestBody createUserPutRequestBody() {
        return new UserPutRequestBody("Updated User Name", "updated@email.com");
    }
}
