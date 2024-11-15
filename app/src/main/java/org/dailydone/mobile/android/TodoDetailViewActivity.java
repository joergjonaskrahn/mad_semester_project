package org.dailydone.mobile.android;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import org.dailydone.mobile.android.databinding.ActivityTodoDetailViewBinding;
import org.dailydone.mobile.android.infrastructure.services.ITodoDataService;
import org.dailydone.mobile.android.model.viewAbstractions.ViewAbstractionTodo;
import org.dailydone.mobile.android.view_model.TodoDetailViewViewModel;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

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
            todoDataService.readTodo(todoId).observe(this, todo -> {
                viewModel.setFromTodo(new ViewAbstractionTodo(todo));
            });
        }

        binding.imageButtonSaveTodo.setOnClickListener(view -> {
            try {
                viewModel.save();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        binding.editTextDate.setOnClickListener(view -> {
            showDatePicker();
        });

        binding.editTextTime.setOnClickListener(view -> {
            showTimePickerDialog();
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
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

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                R.style.DarkTimePickerDialog,
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format(Locale.UK, "%02d:%02d", selectedHour, selectedMinute);
                    viewModel.getTime().setValue(time);
                },
                hour,
                minute,
                DateFormat.is24HourFormat(this)
        );

        timePickerDialog.show();
    }
}