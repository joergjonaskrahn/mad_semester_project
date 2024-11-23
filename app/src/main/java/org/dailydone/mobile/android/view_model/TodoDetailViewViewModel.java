package org.dailydone.mobile.android.view_model;

import android.app.Application;
import android.content.ContentResolver;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.dailydone.mobile.android.DailyDoneApplication;
import org.dailydone.mobile.android.exceptions.FetchContactException;
import org.dailydone.mobile.android.infrastructure.services.ITodoDataService;
import org.dailydone.mobile.android.model.Todo;
import org.dailydone.mobile.android.model.viewAbstractions.Contact;
import org.dailydone.mobile.android.model.viewAbstractions.ViewAbstractionTodo;
import org.dailydone.mobile.android.util.ContactUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    private final MutableLiveData<String> name = new MutableLiveData<>("");
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

        setInitialDate();
    }

    // This method allows initializing the view model from a TodoEntry. (without the contacts)
    public void setFromTodo(Todo todo) {
        // As display orientation changes the activity is destroyed and onCreate is called
        // again. However, the View Model is only destroyed when the activity calls finish.
        // Because of multiple calls of onCreate setFromTodo may be called more than once.
        // To prevent resetting all the inputs in case of display orientation change it is
        // checked whether the view model was already bound to a TodoEntry and if yes whether
        // they are the same.
        if(this.viewAbstractionTodo == null || this.viewAbstractionTodo.getId() != todo.getId()) {
            this.viewAbstractionTodo = new ViewAbstractionTodo(todo);

            name.postValue(viewAbstractionTodo.getName());
            description.postValue(viewAbstractionTodo.getDescription());
            date.postValue(viewAbstractionTodo.getExpiryDateString());
            time.postValue(viewAbstractionTodo.getExpiryTimeString());
            done.postValue(viewAbstractionTodo.isDone());
            favourite.postValue(viewAbstractionTodo.isFavourite());
        }
    }

    // Methods for contact management

    // This method loads the Contacts for the referenced TodoEntry
    public void loadContacts(ContentResolver contentResolver) {
        try {
            this.contacts.postValue(ContactUtils.getContactsForIds(
                    viewAbstractionTodo.getContacts(), contentResolver));
        } catch (FetchContactException e) {
            e.printStackTrace();
        }
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

    // Methods to save and delete
    public void saveTodo() throws ParseException {
        Todo newTodo = new Todo(
                name.getValue(),
                description.getValue(),
                parseDateToUnixTimestamp(date.getValue(), time.getValue()),
                done.getValue(),
                favourite.getValue()
        );

        List<Contact> currentContacts = contacts.getValue();
        if (currentContacts != null && !currentContacts.isEmpty()) {
            // Just the IDs are saved for contacts.
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

    // Helper methods
    // Explicit encoding of the question if the view model describes a new TodoEntry or an
    // entry to be updated.
    public boolean isNewTodo() {
        return viewAbstractionTodo == null;
    }

    private void setInitialDate() {
        Calendar calendar = Calendar.getInstance();
        date.postValue(String.format(
                Locale.UK,
                "%02d.%02d.%04d",
                calendar.get(Calendar.DAY_OF_MONTH),  // Day
                calendar.get(Calendar.MONTH) + 1,    // Month (Calendar.MONTH is zero-based)
                calendar.get(Calendar.YEAR)          // Year
        ));

        time.postValue(String.format(
                Locale.UK, "%02d:%02d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
        ));
    }

    private long parseDateToUnixTimestamp(String date, String time) throws ParseException {
        // Define the formatter for the input string
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.UK);
        Date parsedDate = formatter.parse(date + " " + time);
        return parsedDate.getTime();
    }
}
