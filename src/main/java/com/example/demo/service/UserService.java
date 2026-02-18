package com.example.demo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.domain.User;
import com.example.demo.dto.TodoResponseBody;
import com.example.demo.dto.UserPostRequestBody;
import com.example.demo.dto.UserPutRequestBody;
import com.example.demo.dto.UserResponseBody;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TodoMapper todoMapper;

    public List<UserResponseBody> list() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponseBody)
                .toList();
    }

    public UserResponseBody find(Long id) {
        UserResponseBody userResponse = userRepository.findById(id)
                .map(userMapper::toUserResponseBody)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return userResponse;
    }

    public UserResponseBody save(UserPostRequestBody userRequest) {
        if (userRepository.existsByEmail(userRequest.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User user = userMapper.toUser(userRequest);
        return userMapper.toUserResponseBody(userRepository.save(user));
    }

    public UserResponseBody update(Long id, UserPutRequestBody userRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!existingUser.getEmail().equals(userRequest.email()) && userRepository.existsByEmail(userRequest.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        existingUser.setName(userRequest.name());
        existingUser.setEmail(userRequest.email());
        userRepository.save(existingUser);
        return userMapper.toUserResponseBody(existingUser);
    }

    public void remove(Long id) {
        userRepository.deleteById(id);
    }

    public List<TodoResponseBody> listUserTodos(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return user.getTodos().stream()
                .map(todoMapper::toTodoResponseBody)
                .toList();
    }
}
