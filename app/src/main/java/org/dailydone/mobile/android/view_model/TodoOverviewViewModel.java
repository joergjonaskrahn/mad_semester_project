package org.dailydone.mobile.android.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import org.dailydone.mobile.android.DailyDoneApplication;
import org.dailydone.mobile.android.enums.TodoSortMethods;
import org.dailydone.mobile.android.model.Todo;

import java.util.List;

import lombok.Getter;

@Getter
public class TodoOverviewViewModel extends AndroidViewModel {
    private final MediatorLiveData<List<Todo>> sortedTodos = new MediatorLiveData<>();

    private final LiveData<List<Todo>> todos;
    private final MutableLiveData<TodoSortMethods> selectedSortMethod =
            new MutableLiveData<>(TodoSortMethods.RELEVANCE_DATE);

    public TodoOverviewViewModel(@NonNull Application application) {
        super(application);

        todos = ((DailyDoneApplication) application.getApplicationContext())
                .getTodoDataService().readAllTodos();

        sortedTodos.addSource(todos, todosAsList -> {
            if(todosAsList != null) {
                sortedTodos.setValue(sortTodos(todosAsList, selectedSortMethod.getValue()));
            }
        });

        sortedTodos.addSource(selectedSortMethod, sortMethod -> {
            List<Todo> todosAsList = todos.getValue();
            if(todosAsList != null) {
                sortedTodos.setValue(sortTodos(todosAsList, sortMethod));
            }
        });
    }

    private List<Todo> sortTodos(List<Todo> todos, TodoSortMethods sortMethod) {
        todos.sort((t1, t2) -> Boolean.compare(t1.isDone(), t2.isDone()));
        return todos;
    }
}
