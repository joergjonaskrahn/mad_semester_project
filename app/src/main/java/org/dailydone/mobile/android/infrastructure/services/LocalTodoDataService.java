package org.dailydone.mobile.android.infrastructure.services;

import androidx.lifecycle.LiveData;

import org.dailydone.mobile.android.infrastructure.databases.TodoDatabase;
import org.dailydone.mobile.android.model.Todo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalTodoDataService implements ITodoDataService {
    private final TodoDatabase todoDatabase;
    // A single SingleThreadExecutorService is created to ensure the order of the transactions
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public LocalTodoDataService(TodoDatabase todoDatabase) {
        this.todoDatabase = todoDatabase;
    }

    @Override
    public LiveData<List<Todo>> readAllTodos() {
        return todoDatabase.getDao().readAllTodos();
    }

    public CompletableFuture<List<Todo>> readAllTodosFuture() {
        return CompletableFuture.supplyAsync(() -> todoDatabase.getDao().readAllTodosSync(),
                executorService);
    }

    @Override
    public LiveData<Todo> readTodo(long id) {
        return todoDatabase.getDao().readTodo(id);
    }

    @Override
    public CompletableFuture<Todo> readTodoFuture(long id) {
        return CompletableFuture.supplyAsync(() -> todoDatabase.getDao().readTodoSync(id),
                executorService);
    }

    @Override
    public CompletableFuture<Long> createTodo(Todo todo) {
        return CompletableFuture.supplyAsync(
                () -> todoDatabase.getDao().createTodo(todo), executorService);
    }

    @Override
    public CompletableFuture<Void> updateTodo(Todo todo) {
        return CompletableFuture.runAsync(() -> {
            todoDatabase.getDao().updateTodo(todo);
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> deleteTodo(Todo todo) {
        return CompletableFuture.runAsync(() -> {
            todoDatabase.getDao().deleteTodo(todo);
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> deleteTodoById(long id) {
        return CompletableFuture.runAsync(() -> {
            todoDatabase.getDao().deleteTodoById(id);
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> deleteAllTodos() {
        return CompletableFuture.runAsync(() -> {
            todoDatabase.getDao().deleteAllTodos();
        }, executorService);
    }

    @Override
    public void shutdownExecutors() {
        executorService.shutdown();
    }
}
