package org.dailydone.mobile.android.services;

import android.content.Context;

import androidx.lifecycle.LiveData;

import org.dailydone.mobile.android.databases.TodoDatabase;
import org.dailydone.mobile.android.model.Todo;
import org.dailydone.mobile.android.rest.ITodoRestOperations;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DistributedTodoDataService implements ITodoDataService {
    private final LocalTodoDataService localTodoDataService;
    private final RestTodoDataService restTodoDataService;

    public DistributedTodoDataService(TodoDatabase todoDatabase,
                                      ITodoRestOperations todoRestOperations) {
        localTodoDataService = new LocalTodoDataService(todoDatabase);
        restTodoDataService = new RestTodoDataService(todoRestOperations);
    }

    @Override
    public LiveData<List<Todo>> readAllTodos() {
        return localTodoDataService.readAllTodos();
    }

    @Override
    public LiveData<Todo> readTodo(long id) {
        return localTodoDataService.readTodo(id);
    }

    @Override
    public CompletableFuture<Long> createTodo(Todo todo) {
        return localTodoDataService.createTodo(todo)
                .thenApply((id) -> {
                    restTodoDataService.createTodo(todo);
                    return id;
                });
    }

    @Override
    public CompletableFuture<Void> updateTodo(Todo todo) {
        return localTodoDataService.updateTodo(todo)
                .thenRun(() -> restTodoDataService.updateTodo(todo.getId(), todo));
    }

    @Override
    public CompletableFuture<Void> deleteTodo(Todo todo) {
        return localTodoDataService.deleteTodo(todo)
                .thenRun(() -> restTodoDataService.deleteTodo(todo.getId()));
    }

    @Override
    public void shutdownExecutors() {
        localTodoDataService.shutdownExecutors();
    }
}