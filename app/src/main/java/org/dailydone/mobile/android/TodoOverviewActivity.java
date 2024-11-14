package org.dailydone.mobile.android;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.dailydone.mobile.android.adapters.TodoAdapter;
import org.dailydone.mobile.android.model.Todo;
import org.dailydone.mobile.android.view_model.TodoOverviewViewModel;

import java.util.List;

public class TodoOverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_overview);

        // Only one instance of the View Model is created and stored in the ViewModel Store
        TodoOverviewViewModel todoOverviewViewModel =
                new ViewModelProvider(this).get(TodoOverviewViewModel.class);

        TodoAdapter todoAdapter = new TodoAdapter((DailyDoneApplication) getApplicationContext());

        RecyclerView recyclerView = findViewById(R.id.todoOverviewRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(todoAdapter);

        todoOverviewViewModel.getTodos().observe(this, todos -> {
            if(todos != null) {
                todoAdapter.setTodos(todos);
            }
        });
    }
}