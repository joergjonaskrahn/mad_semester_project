package org.dailydone.mobile.android.infrastructure.repositories;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.dailydone.mobile.android.model.Todo;

import java.util.List;

// TODO Hint
// A Data Access Object is an abstraction interface over a database table. In this case the specific
// interface implementation is generated by Room.
// A repository is a higher abstractions of data access. This is for example necessary if there
// are specific objects for the database which have to be mapped to domain specific objects which
// is described by Martin Fowler. Repositories can also encapsulate other complex logic if more
// actions are needed, for example for saving data. Because of this the accesss chain could be
// the following: Domain --> Repository --> DAO(s)
// Because of this this interface should be renamed to ILocalTodoDao. Furthermore, the so called
// DataServices could be called repositories.
@Dao
public interface ILocalTodoRepository {

    @Query("SELECT * FROM todo")
    LiveData<List<Todo>> readAllTodos();

    // Sync methods are needed for some use cases in which you want to react "instantly"
    // to the return data and not to observe the data for multiple changes.
    @Query("SELECT * FROM todo")
    List<Todo> readAllTodosSync();

    @Query("SELECT * FROM todo WHERE id=(:id)")
    LiveData<Todo> readTodo(long id);

    @Query("SELECT * FROM todo WHERE id=(:id)")
    Todo readTodoSync(long id);

    @Insert
    long createTodo(Todo todo);

    @Update
    void updateTodo(Todo todo);

    @Delete
    void deleteTodo(Todo todo);

    @Query("DELETE FROM todo WHERE id=(:id)")
    void deleteTodoById(long id);

    @Query("DELETE FROM todo")
    void deleteAllTodos();
}