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
        return CompletableFuture.supplyAsync(() -> {
            todoDatabase.getDao().updateTodo(todo);
            // null has to be returned since Void is a placeholder and does not match with a method
            // which actually has the return type void.
            return null;
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> deleteTodo(Todo todo) {
        return CompletableFuture.supplyAsync(() -> {
            todoDatabase.getDao().deleteTodo(todo);
            return null;
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> deleteTodoById(long id) {
        return CompletableFuture.supplyAsync(() -> {
            todoDatabase.getDao().deleteTodoById(id);
            return null;
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> deleteAllTodos() {
        return CompletableFuture.supplyAsync(() -> {
            todoDatabase.getDao().deleteAllTodos();
            return null;
        }, executorService);
    }

    @Override
    public void shutdownExecutors() {
        executorService.shutdown();
    }
}
