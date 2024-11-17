package org.dailydone.mobile.android.infrastructure.rest;

import org.dailydone.mobile.android.model.Todo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ITodoRestOperations {

    @GET("todos")
    Call<List<Todo>> readTodos();

    @GET("todos/{id}")
    Call<Todo> readTodo(@Path("id") long id);

    @POST("todos")
    Call<Todo> createTodo(@Body Todo todo);

    @PUT("todos/{id}")
    Call<Todo> updateTodo(@Path("id") long id, @Body Todo todo);

    @PUT("todos/reset")
    Call<Boolean> resetTodos();

    @DELETE("todos/{id}")
    Call<Boolean> deleteTodo(@Path("id") long id);

    @DELETE("todos")
    Call<Boolean> deleteAllTodos();
}