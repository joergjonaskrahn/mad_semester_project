package org.dailydone.mobile.android.infrastructure.services;

import androidx.lifecycle.LiveData;

import org.dailydone.mobile.android.model.Todo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ITodoDataService {
    LiveData<List<Todo>> readAllTodos();

    // This method is needed for callers which just want to read out of the database
    // and don`t want to observe. (Observing once would be possible but would result in
    // boilerplate code.)
    CompletableFuture<List<Todo>> readAllTodosFuture();

    LiveData<Todo> readTodo(long id);

    CompletableFuture<Todo> readTodoFuture(long id);

    // Use CompletableFuture to allow chaining
    CompletableFuture<Long> createTodo(Todo todo);

    CompletableFuture<Void> updateTodo(Todo todo);

    CompletableFuture<Void> deleteTodo(Todo todo);

    CompletableFuture<Void> deleteTodoById(long id);

    CompletableFuture<Void> deleteAllTodos();

    void shutdownExecutors();
}