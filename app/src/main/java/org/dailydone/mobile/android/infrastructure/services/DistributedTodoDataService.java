package org.dailydone.mobile.android.infrastructure.services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import org.dailydone.mobile.android.DailyDoneApplication;
import org.dailydone.mobile.android.infrastructure.databases.TodoDatabase;
import org.dailydone.mobile.android.model.Todo;
import org.dailydone.mobile.android.infrastructure.rest.ITodoRestOperations;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    // Instead of using thenApplyAsync synchronous thenApply with the async methods
    // od the RestTodoDataService could be used. However, because the method calls to
    // the rest service are already executed inside a "CompleteableFuture environment" it
    // was considered redundant to introduce a new CompleteableFuture by using the async
    // methods of RestTodoDataService.
    @Override
    public CompletableFuture<Long> createTodo(Todo todo) {
        return localTodoDataService.createTodo(todo)
                .thenApplyAsync((id) -> {
                    try {
                        restTodoDataService.createTodo(todo).execute();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return id;
                });
    }

    @Override
    public CompletableFuture<Void> updateTodo(Todo todo) {
        return localTodoDataService.updateTodo(todo)
                .thenRunAsync(() -> {
                    try {
                        restTodoDataService.updateTodo(todo.getId(), todo).execute();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public CompletableFuture<Void> deleteTodo(Todo todo) {
        return localTodoDataService.deleteTodo(todo)
                .thenRunAsync(() -> {
                    try {
                        restTodoDataService.deleteTodo(todo.getId()).execute();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public CompletableFuture<Void> deleteTodoById(long id) {
        return localTodoDataService.deleteTodoById(id)
                .thenRunAsync(() -> {
                    try {
                        restTodoDataService.deleteTodo(id).execute();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public CompletableFuture<Void> deleteAllTodos() {
        return localTodoDataService.deleteAllTodos()
                .thenRunAsync(() -> {
                    try {
                        restTodoDataService.deleteAllTodos().execute();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void shutdownExecutors() {
        localTodoDataService.shutdownExecutors();
    }

    public void synchronizeDataSources() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        CompletableFuture.supplyAsync(localTodoDataService::readAllTodosSynchronously,
                        executorService)
                .thenAccept(localTodos -> {
                    if (localTodos.isEmpty()) {
                        // Read all entries from the remote Data Source
                        restTodoDataService.readAllTodos().enqueue(new Callback<>() {
                            @Override
                            public void onResponse(@NonNull Call<List<Todo>> call,
                                                   @NonNull Response<List<Todo>> response) {
                                // Clone entries to local database
                                response.body().forEach(localTodoDataService::createTodo);
                            }

                            @Override
                            public void onFailure(@NonNull Call<List<Todo>> call,
                                                  @NonNull Throwable t) {
                                t.printStackTrace();
                            }
                        });
                    } else {
                        // Delete all entries in the remote Data Source
                        restTodoDataService.deleteAllTodos().enqueue(new Callback<Boolean>() {
                            @Override
                            public void onResponse(@NonNull Call<Boolean> call,
                                                   @NonNull Response<Boolean> response) {
                                // Clone all local entries to the remote Data Source
                                localTodos.forEach(todo -> {
                                    try {
                                        restTodoDataService.createTodo(todo).execute();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(@NonNull Call<Boolean> call,
                                                  @NonNull Throwable t) {
                                t.printStackTrace();
                            }
                        });
                    }
                })
                .thenRun(executorService::shutdown);
    }
}