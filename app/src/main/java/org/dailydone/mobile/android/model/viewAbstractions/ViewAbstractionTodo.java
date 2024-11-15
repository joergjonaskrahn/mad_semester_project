package org.dailydone.mobile.android.model.viewAbstractions;

import org.dailydone.mobile.android.infrastructure.services.ITodoDataService;
import org.dailydone.mobile.android.model.Todo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.experimental.Delegate;

/*
When I developed the functionality of the overview at first I wanted to integrate a ViewModel
for every view. However, this was not suitable as ViewModels should be managed by Android and
not by the developer. If I would use a ViewModel for every View I would have to do this inside
the Adapter using unique keys for the ViewModels. However, ViewModels have to be bound to the
lifecycle of, for example, an activity. What would be the candidate for this lifecycle? Although
I do not know if it`s possible to tie ViewModels to the lifecycle of a ViewHolder I think this
option is not suitable even if it were possible as ViewHolders are reused by RecyclerView. If it
were possible the ViewModel would have to be reset to the new data every time new data is bound
(onBindViewHolder).
In following thoughts I found out that using a ViewModel would also be redundant as only done
and favourite can be changed inside the overview and the changes should be saved instantly.
--> MutableLiveData is not necessary
Because of this it is more efficient to bind the fields directly to the data and save the
modified data directly. However, to trigger these saves I had to integrate an update call
into the setter methods of done and favourite. Furthermore I have to provide the expiry
date and time using a String and not using a long like it`s used in the data model. Because
of this I introduced this abstraction layer which uses a corresponding TODOEntry as a Delegate.
 */
public class ViewAbstractionTodo extends Todo {
    @Delegate
    private final Todo todo;

    private final ITodoDataService dataService;

    public ViewAbstractionTodo(Todo todo, ITodoDataService dataService) {
        this.todo = todo;
        this.dataService = dataService;
    }

    public String getExpiryDate() {
        Date date = new Date(getExpiry());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.UK);
        return dateFormat.format(date);
    }

    public String getExpiryTime() {
        Date date = new Date(getExpiry());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
        return timeFormat.format(date);
    }

    @Override
    public void setDone(boolean done) {
        todo.setDone(done);
        dataService.updateTodo(todo);
    }

    @Override
    public void setFavourite(boolean favourite) {
        todo.setFavourite(favourite);
        dataService.updateTodo(todo);
    }
}