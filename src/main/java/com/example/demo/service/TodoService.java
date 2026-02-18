package com.example.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.domain.Todo;
import com.example.demo.domain.User;
import com.example.demo.dto.TodoPostRequestBody;
import com.example.demo.dto.TodoPutRequestBody;
import com.example.demo.dto.TodoResponseBody;
import com.example.demo.repository.TodoRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final TodoMapper todoMapper;

    public Page<TodoResponseBody> list(Pageable pageable) {
        return todoRepository.findAll(pageable).map(todoMapper::toTodoResponseBody);
    }

    public TodoResponseBody find(Long id) {
        return todoMapper.toTodoResponseBody(todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found")));
    }

    public TodoResponseBody save(TodoPostRequestBody todoRequest) {
        User user = userRepository.findById(todoRequest.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Todo todo = todoMapper.toTodo(todoRequest, user);
        return todoMapper.toTodoResponseBody(todoRepository.save(todo));
    }

    public TodoResponseBody update(Long id, TodoPutRequestBody todoRequest) {
        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));

        existingTodo.setTitle(todoRequest.title());
        existingTodo.setDescription(todoRequest.description());
        existingTodo.setStage(todoRequest.stage());
        return todoMapper.toTodoResponseBody(todoRepository.save(existingTodo));
    }

    public void remove(Long id) {
        todoRepository.deleteById(id);
    }
}
