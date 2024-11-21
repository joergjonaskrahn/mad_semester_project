package org.dailydone.mobile.android.infrastructure.services;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import org.dailydone.mobile.android.infrastructure.databases.TodoDatabase;
import org.dailydone.mobile.android.model.Todo;
import org.dailydone.mobile.android.infrastructure.rest.ITodoRestOperations;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<List<Todo>> readAllTodosFuture() {
        return localTodoDataService.readAllTodosFuture();
    }

    @Override
    public LiveData<Todo> readTodo(long id) {
        return localTodoDataService.readTodo(id);
    }

    @Override
    public CompletableFuture<Todo> readTodoFuture(long id) {
        return localTodoDataService.readTodoFuture(id);
    }

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
        localTodoDataService.readAllTodosFuture()
                .thenAcceptAsync(localTodos -> {
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
                });
    }
}