package org.dailydone.mobile.android;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import org.dailydone.mobile.android.adapters.ContactAdapter;
import org.dailydone.mobile.android.adapters.TodoAdapter;
import org.dailydone.mobile.android.databinding.ActivityTodoDetailViewBinding;
import org.dailydone.mobile.android.infrastructure.services.ITodoDataService;
import org.dailydone.mobile.android.model.viewAbstractions.Contact;
import org.dailydone.mobile.android.model.viewAbstractions.ViewAbstractionTodo;
import org.dailydone.mobile.android.util.Constants;
import org.dailydone.mobile.android.util.ContactUtils;
import org.dailydone.mobile.android.util.Toasts;
import org.dailydone.mobile.android.view_model.TodoDetailViewViewModel;
import org.dailydone.mobile.android.view_model.TodoOverviewViewModel;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class TodoDetailViewActivity extends AppCompatActivity {
    public static final String EXTRA_TODO_ID = "TODO_ID";

    private TodoDetailViewViewModel viewModel;

    private static final int PICK_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail_view);

        ActivityTodoDetailViewBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_todo_detail_view);

        viewModel = new ViewModelProvider(this).get(TodoDetailViewViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // Reset the view model at the beginning of the activity instead of resetting it when
        // saving in order to prevent flickering on save.
        //viewModel.reset();

        long todoId = getIntent().getLongExtra(EXTRA_TODO_ID, -1);

        if (todoId != -1) {
            ITodoDataService todoDataService =
                    ((DailyDoneApplication) getApplicationContext()).getTodoDataService();
            todoDataService.readTodoFuture(todoId)
                    .thenAccept(todo -> {
                        viewModel.setFromTodo(todo);
                        // Überprüfen, ob die Berechtigung vorhanden ist
                        if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                            viewModel.loadContacts(getContentResolver());
                        } else {
                            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, 1);
                        }
                    });
            binding.imageButtonDeleteTodo.setEnabled(true);
            binding.imageButtonDeleteTodo.setAlpha(Constants.BUTTON_ENABLED_ALPHA);
        }

        binding.imageButtonSaveTodo.setOnClickListener(view -> {
            try {
                viewModel.save();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            finish();
        });

        binding.imageButtonDeleteTodo.setOnClickListener(view -> {
            showDeleteDialog();
        });

        binding.editTextDate.setOnClickListener(view -> {
            showDatePicker();
        });

        binding.editTextTime.setOnClickListener(view -> {
            showTimePickerDialog();
        });

        // Contacts
        ContactAdapter contactAdapter = new ContactAdapter(viewModel::removeContact);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(contactAdapter);

        binding.imageButtonAddContact.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            contactPickerLauncher.launch(intent);
        });

        viewModel.getContacts().observe(this, contacts -> {
            contactAdapter.submitList(contacts);
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(viewModel.getViewAbstractionTodo().getExpiryAsDate());

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(this, R.style.DarkDatePickerDialog,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            // Format the date as dd.MM.yyyy
                            String formattedDate = String.format(Locale.UK, "%02d.%02d.%04d", selectedDay, selectedMonth + 1, selectedYear);
                            viewModel.getDate().setValue(formattedDate); // Update the ViewModel
                        }, year, month, day);

        datePickerDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewModel.loadContacts(getContentResolver());
        } else {
            Toast toast = Toasts.getWarningToast(getLayoutInflater(),
                    getString(R.string.warning_contact_permission),
                    getApplicationContext());
            toast.show();
        }
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(viewModel.getViewAbstractionTodo().getExpiryAsDate());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                R.style.DarkSpinnerTimePicker,
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format(Locale.UK, "%02d:%02d", selectedHour, selectedMinute);
                    viewModel.getTime().setValue(time);
                },
                hour,
                minute,
                true
        );

        timePickerDialog.show();
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DarkAlertDialog);

        builder.setTitle("Confirm deletion");
        builder.setMessage("Do you want to delete the TODO " + viewModel.getName().getValue() + "?");

        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            viewModel.deleteTodo()
                    .thenRun(this::finish);
        });

        builder.setNegativeButton("No", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Declare the ActivityResultLauncher as a private final variable
    private final ActivityResultLauncher<Intent> contactPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Get the contact URI
                    Uri contactUri = result.getData().getData();

                    System.out.println(contactUri);

                    if (contactUri != null) {
                        viewModel.addContact(
                                ContactUtils.getContactForUri(contactUri, getContentResolver()));
                    }
                }
            }
    );


}