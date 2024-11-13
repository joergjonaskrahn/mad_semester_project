package org.dailydone.mobile.android.services;

import androidx.lifecycle.LiveData;

import org.dailydone.mobile.android.model.Todo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ITodoDataService {
    LiveData<List<Todo>> readAllTodos();

    LiveData<Todo> readTodo(long id);

    // Use CompletableFuture to allow chaining
    CompletableFuture<Long> createTodo(Todo todo);

    CompletableFuture<Void> updateTodo(Todo todo);

    CompletableFuture<Void> deleteTodo(Todo todo);

    void shutdownExecutors();
}