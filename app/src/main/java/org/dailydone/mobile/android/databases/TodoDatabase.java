package org.dailydone.mobile.android.databases;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import org.dailydone.mobile.android.model.Todo;
import org.dailydone.mobile.android.repositories.ILocalTodoRepository;

@Database(entities = {Todo.class}, version = 1)
public abstract class TodoDatabase extends RoomDatabase {
    public abstract ILocalTodoRepository getDao();
}
