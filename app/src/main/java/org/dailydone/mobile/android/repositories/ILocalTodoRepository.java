package org.dailydone.mobile.android.repositories;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.dailydone.mobile.android.model.Todo;

import java.util.List;

@Dao
public interface ILocalTodoRepository {

    @Query("SELECT * FROM todo")
    LiveData<List<Todo>> readAllTodos();

    @Query("SELECT * FROM todo WHERE id=(:id)")
    LiveData<Todo> readTodo(long id);

    @Insert
    long createTodo(Todo todo);

    @Update
    void updateTodo(Todo todo);

    @Delete
    void deleteItem(Todo todo);
}