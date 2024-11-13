package org.dailydone.mobile.android.services;

import org.dailydone.mobile.android.model.Todo;
import org.dailydone.mobile.android.rest.ITodoRestOperations;

import java.util.List;

import retrofit2.Call;

public class RestTodoDataService {
    private final ITodoRestOperations todoRestOperations;

    public RestTodoDataService(ITodoRestOperations todoRestOperations) {
        this.todoRestOperations = todoRestOperations;
    }

    public Call<List<Todo>> readAllTodos() {
        return todoRestOperations.readTodos();
    }

    public Call<Todo> readTodo(long id) {
        return todoRestOperations.readTodo(id);
    }

    public Call<Todo> createTodo(Todo todo) {
        return todoRestOperations.createTodo(todo);
    }

    public Call<Todo> updateTodo(long id, Todo todo) {
        return todoRestOperations.updateTodo(id, todo);
    }

    public Call<Boolean> deleteTodo(long id) {
        return todoRestOperations.deleteTodo(id);
    }

    public Call<Boolean> deleteTodos() {
        return todoRestOperations.deleteTodos();
    }
}