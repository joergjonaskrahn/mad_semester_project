package org.dailydone.mobile.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.dailydone.mobile.android.adapters.TodoAdapter;
import org.dailydone.mobile.android.enums.TodoSortMethods;
import org.dailydone.mobile.android.model.Todo;
import org.dailydone.mobile.android.view_model.TodoOverviewViewModel;

import java.util.ArrayList;
import java.util.List;

public class TodoOverviewActivity extends AppCompatActivity {
    private ImageButton imageButtonSelectSortMethod;
    private ImageButton imageButtonAddTodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_overview);

        imageButtonSelectSortMethod = findViewById(R.id.imageButtonSelectSortMethod);
        imageButtonAddTodo = findViewById(R.id.imageButtonAddTodo);

        // Only one instance of the View Model is created and stored in the ViewModel Store
        TodoOverviewViewModel todoOverviewViewModel =
                new ViewModelProvider(this).get(TodoOverviewViewModel.class);

        TodoAdapter todoAdapter = new TodoAdapter((DailyDoneApplication) getApplicationContext());

        RecyclerView recyclerView = findViewById(R.id.todoOverviewRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setAdapter(todoAdapter);

        todoOverviewViewModel.getSortedTodos().observe(this, sortedTodos -> {
            if(sortedTodos != null) {
                todoAdapter.submitList(sortedTodos);
            }
        });

        imageButtonAddTodo.setOnClickListener(view -> {
            Intent detailViewIntent = new Intent(this, TodoDetailViewActivity.class);
            startActivity(detailViewIntent);
        });

        imageButtonSelectSortMethod.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, imageButtonSelectSortMethod);
            popupMenu.getMenuInflater().inflate(R.menu.todo_sort_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                // Cannot use a switch since Resource IDs will no longer be final constants.
                if(menuItem.getItemId() == R.id.option_sort_by_relevance) {
                    todoOverviewViewModel.getSelectedSortMethod().setValue(TodoSortMethods.RELEVANCE_DATE);
                    return true;
                }else if(menuItem.getItemId() == R.id.option_sort_by_date) {
                    todoOverviewViewModel.getSelectedSortMethod().setValue(TodoSortMethods.DATE_RELEVANCE);
                    return true;
                }else{
                    return false;
                }
            });
            popupMenu.show();
        });
    }
}