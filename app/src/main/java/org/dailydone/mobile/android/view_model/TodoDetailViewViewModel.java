package org.dailydone.mobile.android.view_model;

import android.app.Application;
import android.content.ContentResolver;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.dailydone.mobile.android.DailyDoneApplication;
import org.dailydone.mobile.android.infrastructure.services.ITodoDataService;
import org.dailydone.mobile.android.model.Todo;
import org.dailydone.mobile.android.model.viewAbstractions.Contact;
import org.dailydone.mobile.android.model.viewAbstractions.ViewAbstractionTodo;
import org.dailydone.mobile.android.util.ContactUtils;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import lombok.Getter;

/*
This View Model encapsulates the saving of a TodoEntry including the question whether the
entry has to be created or updated. This approach decreases complexity in the corresponding
activity.
 */
public class TodoDetailViewViewModel extends AndroidViewModel {
    private final ITodoDataService dataService;

    @Getter
    private ViewAbstractionTodo viewAbstractionTodo;

    @Getter
    private final MutableLiveData<String> name = new MutableLiveData<>("abc");
    @Getter
    private final MutableLiveData<String> description = new MutableLiveData<>("");
    @Getter
    private final MutableLiveData<String> date = new MutableLiveData<>("");
    @Getter
    private final MutableLiveData<String> time = new MutableLiveData<>("");
    @Getter
    private final MutableLiveData<Boolean> done = new MutableLiveData<>(false);
    @Getter
    private final MutableLiveData<Boolean> favourite = new MutableLiveData<>(false);
    @Getter
    private final MutableLiveData<List<Contact>> contacts = new MutableLiveData<>(new ArrayList<>());

    public TodoDetailViewViewModel(@NonNull Application application) {
        super(application);

        this.dataService = ((DailyDoneApplication) getApplication()
                .getApplicationContext()).getTodoDataService();
    }

    public void setFromTodo(Todo todo) {
        this.viewAbstractionTodo = new ViewAbstractionTodo(todo);

        name.postValue(viewAbstractionTodo.getName());
        description.postValue(viewAbstractionTodo.getDescription());
        date.postValue(viewAbstractionTodo.getExpiryDate());
        time.postValue(viewAbstractionTodo.getExpiryTime());
        done.postValue(viewAbstractionTodo.isDone());
        favourite.postValue(viewAbstractionTodo.isFavourite());
    }

    public void save() throws ParseException {
        Todo newTodo = new Todo(
                name.getValue(),
                description.getValue(),
                parseDateToUnixTimestamp(date.getValue(), time.getValue()),
                done.getValue(),
                favourite.getValue()
        );

        List<Contact> currentContacts = contacts.getValue();

        if (currentContacts != null && !currentContacts.isEmpty()) {
            newTodo.setContacts(currentContacts.stream().map(
                    Contact::getId).collect(Collectors.toList()));
        }

        if (isNewTodo()) {
            dataService.createTodo(newTodo);
        } else {
            Todo oldTodo = viewAbstractionTodo.getTodo();
            newTodo.setId(oldTodo.getId());
            dataService.updateTodo(newTodo);
        }
    }

    public CompletableFuture<Void> deleteTodo() {
        return dataService.deleteTodo(viewAbstractionTodo.getTodo());
    }

    public void addContact(Contact contact) {
        List<Contact> oldContacts = contacts.getValue();
        if (oldContacts != null && !oldContacts.contains(contact)) {
            List<Contact> newContacts = new ArrayList<>(oldContacts);
            newContacts.add(contact);
            contacts.postValue(newContacts);
        }
    }

    public void removeContact(String contactId) {
        List<Contact> currentContacts = contacts.getValue();
        if (currentContacts != null) {
            List<Contact> newContacts = new ArrayList<>(currentContacts);
            newContacts.removeIf(contact -> contact.getId().equals(contactId));
            contacts.postValue(newContacts);
        }
    }

    public void loadContacts(ContentResolver contentResolver) {
        this.contacts.postValue(ContactUtils.getContactsForContactIds(
                viewAbstractionTodo.getContacts(), contentResolver));
    }

    public boolean getDeletable() {
        return !isNewTodo();
    }

    private long parseDateToUnixTimestamp(String date, String time) throws ParseException {
        // Define the formatter for the input string
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.UK);
        Date parsedDate = formatter.parse(date + " " + time);
        return parsedDate.getTime();
    }

    public void reset() {
        this.viewAbstractionTodo = null;
        name.postValue("");
        description.postValue("");
        date.postValue("");
        time.postValue("");
        done.postValue(false);
        favourite.postValue(false);
        contacts.postValue(new ArrayList<>());
    }

    // Explicit encoding of the question if the view model describes a new TodoEntry or an
    // entry to be updated.
    private boolean isNewTodo() {
        return viewAbstractionTodo == null;
    }
}
