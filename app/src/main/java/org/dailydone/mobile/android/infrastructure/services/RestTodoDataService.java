package org.dailydone.mobile.android.infrastructure.services;

import org.dailydone.mobile.android.model.Todo;
import org.dailydone.mobile.android.infrastructure.rest.ITodoRestOperations;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestTodoDataService {
    private final ITodoRestOperations todoRestOperations;

    public RestTodoDataService(ITodoRestOperations todoRestOperations) {
        this.todoRestOperations = todoRestOperations;
    }

    public Call<List<Todo>> readAllTodos() {
        return todoRestOperations.readTodos();
    }

    // In most use cases callers just want to retrieve the response body and not deal
    // with the response itself. Furthermore, dealing with Calls using enqueue or execute with
    // ExecutorService results in much boilerplate code in callers. Because of this, this service
    // defines methods to facilitate asynchronous method calls using CompleteableFuture.
    public CompletableFuture<List<Todo>> readAllTodosAsync() {
        return callToCompleteableFuture(todoRestOperations.readTodos());
    }

    public Call<Todo> readTodo(long id) {
        return todoRestOperations.readTodo(id);
    }

    public CompletableFuture<Todo> readTodoAsync(long id) {
        return callToCompleteableFuture(todoRestOperations.readTodo(id));
    }

    public Call<Todo> createTodo(Todo todo) {
        return todoRestOperations.createTodo(todo);
    }

    public CompletableFuture<Todo> createTodoAsync(Todo todo) {
        return callToCompleteableFuture(todoRestOperations.createTodo(todo));
    }

    public Call<Todo> updateTodo(long id, Todo todo) {
        return todoRestOperations.updateTodo(id, todo);
    }

    public CompletableFuture<Todo> updateTodoAsync(long id, Todo todo) {
        return callToCompleteableFuture(todoRestOperations.updateTodo(id, todo));
    }

    public Call<Boolean> deleteTodo(long id) {
        return todoRestOperations.deleteTodo(id);
    }

    public CompletableFuture<Boolean> deleteTodoAsync(long id) {
        return callToCompleteableFuture(todoRestOperations.deleteTodo(id));
    }

    public Call<Boolean> deleteAllTodos() {
        return todoRestOperations.deleteAllTodos();
    }

    public CompletableFuture<Boolean> deleteAllTodosAsync() {
        return callToCompleteableFuture(todoRestOperations.deleteAllTodos());
    }

    private <T> CompletableFuture<T> callToCompleteableFuture(Call<T> call) {
        CompletableFuture<T> future = new CompletableFuture<>();
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful() && response.body() != null) {
                    future.complete(response.body());
                } else {
                    future.completeExceptionally(new RuntimeException(response.message()));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }
}