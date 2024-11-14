package org.dailydone.mobile.android.model.observableModel;

import org.dailydone.mobile.android.infrastructure.services.ITodoDataService;
import org.dailydone.mobile.android.model.Todo;

import lombok.experimental.Delegate;

public class ObservableTodo extends Todo {
    @Delegate
    private final Todo todo;

    private final ITodoDataService dataService;

    public ObservableTodo(Todo todo, ITodoDataService dataService) {
        this.todo = todo;
        this.dataService = dataService;
    }

    @Override
    public void setDone(boolean done) {
        System.out.println("SET DONE !!!");
        todo.setDone(done);
        dataService.updateTodo(todo);
    }

    @Override
    public void setFavourite(boolean favourite) {
        todo.setFavourite(favourite);
        dataService.updateTodo(todo);
    }
}
