package org.dailydone.mobile.android.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.dailydone.mobile.android.DailyDoneApplication;
import org.dailydone.mobile.android.model.Todo;

import java.util.List;

public class TodoOverviewViewModel extends AndroidViewModel {
    private final LiveData<List<Todo>> todos;

    public TodoOverviewViewModel(@NonNull Application application) {
        super(application);

        this.todos = ((DailyDoneApplication) application.getApplicationContext())
                .getTodoDataService().readAllTodos();
    }

    public LiveData<List<Todo>> getTodos() {
        return todos;
    }
}
