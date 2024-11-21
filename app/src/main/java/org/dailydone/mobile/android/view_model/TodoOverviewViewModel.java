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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;

@Getter
public class TodoOverviewViewModel extends AndroidViewModel {
    // Sorted todos are saved inside the View Model in order to persist them over the lifecycle
    // of the Overview Activity. Would they be saved inside the Overview Activity, they would
    // have to be recreated when the display orientation changes.
    // MediatorLiveData is used to add multiple sources which trigger an update.
    private final MediatorLiveData<List<Todo>> sortedTodos = new MediatorLiveData<>();

    private final LiveData<List<Todo>> todos;
    private final MutableLiveData<TodoSortMethods> selectedSortMethod =
            new MutableLiveData<>(TodoSortMethods.RELEVANCE_DATE);

    public TodoOverviewViewModel(@NonNull Application application) {
        super(application);

        todos = ((DailyDoneApplication) application.getApplicationContext())
                .getTodoDataService().readAllTodos();

        // Update sorted todos when the todos itself change
        sortedTodos.addSource(todos, todosAsList -> {
            if(todosAsList != null) {
                sortedTodos.postValue(sortTodos(todosAsList, selectedSortMethod.getValue()));
            }
        });

        // Update sorted todos when the sort method changes
        sortedTodos.addSource(selectedSortMethod, sortMethod -> {
            List<Todo> todosAsList = todos.getValue();
            if(todosAsList != null) {
                sortedTodos.postValue(sortTodos(todosAsList, sortMethod));
            }
        });
    }

    private List<Todo> sortTodos(List<Todo> todos, TodoSortMethods sortMethod) {
        // Create a shallow clone of the list in order to prevent side effects on the
        // original list.
        List<Todo> sortedTodos = new ArrayList<>(todos);
        Comparator<Todo> comparator;
        if (sortMethod == TodoSortMethods.RELEVANCE_DATE) {
            comparator = Comparator
                    .comparing(Todo::isDone)
                    // The next compare callback is used if the objects have the same value
                    // regarding the preceding comparison.
                    .thenComparing((todo) -> !todo.isFavourite())
                    .thenComparing(Todo::getExpiry);
        } else {
            comparator = Comparator
                    .comparing(Todo::isDone)
                    .thenComparing(Todo::getExpiry)
                    .thenComparing((todo) -> !todo.isFavourite());
        }

        sortedTodos.sort(comparator);
        return sortedTodos;
    }
}
