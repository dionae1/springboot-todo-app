package com.example.demo.util;

import com.example.demo.domain.Todo;
import com.example.demo.domain.TodoStage;
import com.example.demo.domain.User;
import com.example.demo.dto.TodoPostRequestBody;
import com.example.demo.dto.TodoPutRequestBody;
import com.example.demo.dto.TodoResponseBody;

public class TodoCreator {
    public static Todo createTodo(User user) {
        return new Todo("Change Password", "Change runescape password due the yesterday problem!", user);
    }

    public static Todo createSecondaryTodo(User user) {
        return new Todo("Buy Groceries", "Buy milk, eggs, and bread for the week.", user);
    }

    public static TodoPostRequestBody createTodoPostRequestBody(User user) {
        return new TodoPostRequestBody(user.getId(), "Change Password",
                "Change runescape password due the yesterday problem!");
    }

    public static TodoPutRequestBody createTodoPutRequestBody() {
        return new TodoPutRequestBody("Change Password Updated",
                "Change runescape password due the yesterday problem updated!", TodoStage.IN_PROGRESS);
    }

    public static TodoResponseBody createTodoResponseBody(Todo todo) {
        return new TodoResponseBody(todo.getId(), todo.getTitle(), todo.getDescription(),
                todo.getStage().toString(), todo.getUser().getId());
    } 
}
