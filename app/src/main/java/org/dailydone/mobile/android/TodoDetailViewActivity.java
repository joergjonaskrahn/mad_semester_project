package org.dailydone.mobile.android;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.dailydone.mobile.android.adapters.ContactAdapter;
import org.dailydone.mobile.android.databinding.ActivityTodoDetailViewBinding;
import org.dailydone.mobile.android.infrastructure.services.ITodoDataService;
import org.dailydone.mobile.android.util.Constants;
import org.dailydone.mobile.android.util.ContactUtils;
import org.dailydone.mobile.android.util.DateUtil;
import org.dailydone.mobile.android.util.Toasts;
import org.dailydone.mobile.android.view_model.TodoDetailViewViewModel;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class TodoDetailViewActivity extends AppCompatActivity {
    public static final String EXTRA_TODO_ID = "TODO_ID";

    private TodoDetailViewViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail_view);

        ActivityTodoDetailViewBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_todo_detail_view);
        viewModel = new ViewModelProvider(this).get(TodoDetailViewViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        long todoId = getIntent().getLongExtra(EXTRA_TODO_ID, -1);

        if (todoId != -1) {
            ITodoDataService todoDataService =
                    ((DailyDoneApplication) getApplicationContext()).getTodoDataService();
            todoDataService.readTodoFuture(todoId)
                    .thenAccept(todo -> {
                        viewModel.setFromTodo(todo);
                        // Check whether application has needed permission
                        if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS)
                                == PackageManager.PERMISSION_GRANTED) {
                            viewModel.loadContacts(getContentResolver());
                        } else {
                            requestPermissions(new String[]{
                                    android.Manifest.permission.READ_CONTACTS}, 1);
                        }
                    });
            // Allow deletion if the view references an existing TodoEntry
            binding.imageButtonDeleteTodo.setEnabled(true);
            binding.imageButtonDeleteTodo.setAlpha(Constants.BUTTON_ENABLED_ALPHA);
        }

        binding.imageButtonSaveTodo.setOnClickListener(view -> {
            // Trim removes leading and "attached" whitespaces.
            String nameInput = binding.editTextName.getText().toString().trim();

            if (nameInput.isEmpty()) {
                binding.editTextName.setError(getString(R.string.required_input_error));
                return;
            }

            try {
                viewModel.saveTodo();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            finish();
        });

        // Clear error on name input field on input
        binding.editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (binding.editTextName.getError() != null) {
                    binding.editTextName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.editTextDate.setOnClickListener(view -> {
            showDatePicker();
        });

        binding.editTextTime.setOnClickListener(view -> {
            showTimePickerDialog();
        });

        // Add click listener for deletion if a TodoEntry was passed.
        // This is necessary since adding a listener overwrites the enabled and clickable
        // properties of an image button.
        if (todoId != -1) {
            binding.imageButtonDeleteTodo.setOnClickListener(view -> {
                showDeleteDialog();
            });
        }

        // Contacts
        ContactAdapter contactAdapter = new ContactAdapter(
                viewModel::removeContact, viewModel.getName(), viewModel.getDescription());
        RecyclerView recyclerView = binding.recyclerViewContacts;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactAdapter);

        binding.imageButtonAddContact.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            contactPickerLauncher.launch(intent);
        });

        viewModel.getContacts().observe(this, contactAdapter::submitList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewModel.loadContacts(getContentResolver());
        } else {
            Toast toast = Toasts.getWarningToast(getLayoutInflater(),
                    getString(R.string.warning_contact_permission),
                    getApplicationContext());
            toast.show();
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance(Constants.TIMEZONE);

        try {
            calendar.setTime(
                    new Date(
                            DateUtil.parseStringToUnixTimestamp(
                                    viewModel.getDate().getValue(), viewModel.getTime().getValue()
                            )
                    )
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(this, R.style.DarkDatePickerDialog,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            // Format the date as dd.MM.yyyy
                            String formattedDate = String.format(
                                    Constants.LOCALE, "%02d.%02d.%04d", selectedDay,
                                    selectedMonth + 1, selectedYear);
                            viewModel.getDate().setValue(formattedDate);
                        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance(Constants.TIMEZONE);

        try {
            calendar.setTime(
                    new Date(
                            DateUtil.parseStringToUnixTimestamp(
                                    viewModel.getDate().getValue(), viewModel.getTime().getValue()
                            )
                    )
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                R.style.DarkSpinnerTimePicker,
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format(
                            Constants.LOCALE, "%02d:%02d", selectedHour, selectedMinute);
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

        builder.setTitle(getString(R.string.delete_dialog_title));
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

                    if (contactUri != null) {
                        try {
                            viewModel.addContact(ContactUtils.getContactForUri(
                                    contactUri, getContentResolver()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
}