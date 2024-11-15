package org.dailydone.mobile.android.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.dailydone.mobile.android.DailyDoneApplication;
import org.dailydone.mobile.android.infrastructure.services.ITodoDataService;
import org.dailydone.mobile.android.model.Todo;
import org.dailydone.mobile.android.model.viewAbstractions.ViewAbstractionTodo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import lombok.Getter;

/*
This View Model encapsulates the saving of a TodoEntry including the question whether the
entry has to be created or updated. This approach decreases complexity in the corresponding
activity.
 */
@Getter
public class TodoDetailViewViewModel extends AndroidViewModel {
    private ViewAbstractionTodo viewAbstractionTodo;

    private final MutableLiveData<String> name = new MutableLiveData<>("");
    private final MutableLiveData<String> description = new MutableLiveData<>("");
    private final MutableLiveData<String> date = new MutableLiveData<>("");
    private final MutableLiveData<String> time = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> done = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> favourite = new MutableLiveData<>(false);

    public TodoDetailViewViewModel(@NonNull Application application) {
        super(application);
    }

    public void setFromTodo(ViewAbstractionTodo viewAbstractionTodo) {
        this.viewAbstractionTodo = viewAbstractionTodo;

        name.setValue(viewAbstractionTodo.getName());
        description.setValue(viewAbstractionTodo.getDescription());
        date.setValue(viewAbstractionTodo.getExpiryDate());
        time.setValue(viewAbstractionTodo.getExpiryTime());
        done.setValue(viewAbstractionTodo.isDone());
        favourite.setValue(viewAbstractionTodo.isFavourite());
    }

    public void save() throws ParseException {
        ITodoDataService dataService = ((DailyDoneApplication) getApplication()
                .getApplicationContext()).getTodoDataService();

        Todo newTodo = new Todo(
                name.getValue(),
                description.getValue(),
                parseDateToUnixTimestamp(date.getValue(), time.getValue()),
                done.getValue(),
                favourite.getValue()
        );

        if(isNewTodo()) {
            Todo oldTodo = viewAbstractionTodo.getTodo();
            newTodo.setId(oldTodo.getId());
            dataService.updateTodo(newTodo);
        }else{
            dataService.createTodo(newTodo);
        }

        // Resetting the view model here decreases complexity for the corresponding activity.
        reset();
    }

    private long parseDateToUnixTimestamp(String date, String time) throws ParseException {
        // Define the formatter for the input string
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.UK);
        Date parsedDate = formatter.parse(date + " " + time);
        return parsedDate.getTime();
    }

    private void reset() {
        this.viewAbstractionTodo = null;
        name.setValue("");
        description.setValue("");
        date.setValue("");
        time.setValue("");
        done.setValue(false);
        favourite.setValue(false);
    }

    // Explicit encoding of the question if the view model describes a new TodoEntry or an
    // entry to be updated.
    private boolean isNewTodo() {
        return viewAbstractionTodo == null;
    }
}
