package org.dailydone.mobile.android.infrastructure.databases;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import org.dailydone.mobile.android.model.Todo;
import org.dailydone.mobile.android.infrastructure.repositories.ILocalTodoRepository;

@Database(entities = {Todo.class}, version = 1)
@TypeConverters(TypeConverters.class)
public abstract class TodoDatabase extends RoomDatabase {
    public abstract ILocalTodoRepository getDao();
}
