package com.example.demo.service;

import java.util.List;
import java.util.Optional;

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

import com.example.demo.domain.Todo;
import com.example.demo.domain.TodoStage;
import com.example.demo.domain.User;
import com.example.demo.dto.TodoPostRequestBody;
import com.example.demo.dto.TodoPutRequestBody;
import com.example.demo.dto.TodoResponseBody;
import com.example.demo.repository.TodoRepository;
import com.example.demo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoService todoService;

    private User createUser() {
        return new User("William Lemos", "will21@email.com", "1234");
    }

    private Todo createTodo(User user) {
        return new Todo("Change Password", "Change runescape password due the yesterday problem!", user);
    }

    private Todo createSecondaryTodo(User user) {
        return new Todo("Buy Groceries", "Buy milk, eggs, and bread for the week.", user);
    }

    private TodoPostRequestBody createTodoPostRequestBody(User user) {
        return new TodoPostRequestBody(user.getId(), "Change Password",
                "Change runescape password due the yesterday problem!");
    }

    private TodoPutRequestBody createTodoPutRequestBody() {
        return new TodoPutRequestBody("Change Password Updated",
                "Change runescape password due the yesterday problem updated!", TodoStage.IN_PROGRESS);
    }

    private TodoResponseBody createTodoResponseBody(Todo todo) {
        return new TodoResponseBody(todo.getId(), todo.getTitle(), todo.getDescription(),
                todo.getStage().toString(), todo.getUser().getId());
    }

    @Test
    void shouldReturnAllTodos() {
        User user = createUser();
        Todo todo1 = createTodo(user);
        Todo todo2 = createSecondaryTodo(user);

        TodoResponseBody response1 = createTodoResponseBody(todo1);
        TodoResponseBody response2 = createTodoResponseBody(todo2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Todo> page = new PageImpl<>(List.of(todo1, todo2), pageable, 2);

        Mockito.when(todoRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(todoMapper.toTodoResponseBody(todo1)).thenReturn(response1);
        Mockito.when(todoMapper.toTodoResponseBody(todo2)).thenReturn(response2);

        var result = todoService.list(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(Page.class, result);
        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertTrue(result.getContent().contains(response1));
        Assertions.assertTrue(result.getContent().contains(response2));
    }

    @Test
    void shouldReturnEmptyListWhenNoTodos() {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(todoRepository.findAll(pageable)).thenReturn(Page.empty());

        var result = todoService.list(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(Page.class, result);
        Assertions.assertEquals(0, result.getTotalElements());
        Assertions.assertTrue(result.getContent().isEmpty());
    }

    @Test
    void shouldReturnSavedTodo() {
        User user = createUser();
        Todo todo = createTodo(user);
        TodoPostRequestBody todoRequest = createTodoPostRequestBody(user);
        TodoResponseBody expectedResponse = createTodoResponseBody(todo);

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(todoMapper.toTodo(todoRequest, user)).thenReturn(todo);
        Mockito.when(todoRepository.save(Mockito.any(Todo.class))).thenReturn(todo);
        Mockito.when(todoMapper.toTodoResponseBody(Mockito.any(Todo.class))).thenReturn(expectedResponse);

        var result = todoService.save(todoRequest);

        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(TodoResponseBody.class, result);
        Assertions.assertEquals(expectedResponse, result);

        Mockito.verify(todoRepository).save(Mockito.any(Todo.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnSave() {
        TodoPostRequestBody todoRequest = createTodoPostRequestBody(createUser());

        Mockito.when(userRepository.findById(todoRequest.userId())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> todoService.save(todoRequest));
    }

    @Test
    void shouldReturnTodoById() {
        User user = createUser();
        Todo todo = createTodo(user);
        TodoResponseBody expectedResponse = createTodoResponseBody(todo);

        Mockito.when(todoRepository.findById(todo.getId())).thenReturn(Optional.of(todo));
        Mockito.when(todoMapper.toTodoResponseBody(todo)).thenReturn(expectedResponse);

        var result = todoService.find(todo.getId());

        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(TodoResponseBody.class, result);
        Assertions.assertEquals(expectedResponse, result);
    }

    @Test
    void shouldThrowExceptionWhenTodoNotFound() {
        Mockito.when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> todoService.find(1L));
    }

    @Test
    void shouldRemoveTodo() {
        todoService.remove(1L);
        Mockito.verify(todoRepository).deleteById(1L);
    }

    @Test
    void shouldUpdateTodo() {
        User user = createUser();
        Todo todo = createTodo(user);
        TodoPutRequestBody todoRequest = createTodoPutRequestBody();
        TodoResponseBody expectedResponse = createTodoResponseBody(todo);

        Mockito.when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        Mockito.when(todoRepository.save(Mockito.any(Todo.class))).thenReturn(todo);
        Mockito.when(todoMapper.toTodoResponseBody(Mockito.any(Todo.class))).thenReturn(expectedResponse);

        var result = todoService.update(1L, todoRequest);

        Mockito.verify(todoRepository).save(Mockito.any(Todo.class));

        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(TodoResponseBody.class, result);

        Assertions.assertEquals(expectedResponse, result);
    }

    @Test
    void shouldNotUpdateWhenTodoNotFound() {
        TodoPutRequestBody todoRequest = createTodoPutRequestBody();
        Mockito.when(todoRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResponseStatusException.class, () -> todoService.update(1L, todoRequest));
    }

    @Test
    void shouldUpdateTodoStage() {
        User user = createUser();
        Todo todo = createTodo(user);
        TodoPutRequestBody todoRequest = createTodoPutRequestBody();
        TodoResponseBody expectedResponse = createTodoResponseBody(todo);

        Mockito.when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        Mockito.when(todoRepository.save(Mockito.any(Todo.class))).thenReturn(todo);
        Mockito.when(todoMapper.toTodoResponseBody(Mockito.any(Todo.class))).thenReturn(expectedResponse);

        var result = todoService.update(1L, todoRequest);

        Mockito.verify(todoRepository).save(Mockito.any(Todo.class));

        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(TodoResponseBody.class, result);

        Assertions.assertEquals(expectedResponse, result);
    }

    @Test
    void shouldUpdateDescriptionToEmpty() {
        User user = createUser();
        Todo todo = createTodo(user);
        Todo updatedTodo = new Todo(todo.getTitle(), "", user);
        updatedTodo.setId(todo.getId());

        TodoPutRequestBody todoRequest = createTodoPutRequestBody();
        TodoResponseBody expectedResponse = createTodoResponseBody(updatedTodo);

        Mockito.when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        Mockito.when(todoRepository.save(Mockito.any(Todo.class))).thenReturn(updatedTodo);
        Mockito.when(todoMapper.toTodoResponseBody(Mockito.any(Todo.class))).thenReturn(expectedResponse);

        var result = todoService.update(1L, todoRequest);

        Mockito.verify(todoRepository).save(Mockito.any(Todo.class));

        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(TodoResponseBody.class, result);

        Assertions.assertEquals(expectedResponse, result);
    }
}
