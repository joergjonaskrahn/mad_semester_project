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
import org.dailydone.mobile.android.infrastructure.databases.TodoDatabase;
import org.dailydone.mobile.android.infrastructure.rest.ITodoRestOperations;
import org.dailydone.mobile.android.infrastructure.services.DistributedTodoDataService;
import org.dailydone.mobile.android.infrastructure.services.ITodoDataService;
import org.dailydone.mobile.android.util.AsyncUtils;
import org.dailydone.mobile.android.view_model.TodoOverviewViewModel;

import java.io.IOException;

public class TodoOverviewActivity extends AppCompatActivity {
    private ITodoDataService dataService;

    private TodoDatabase todoDatabase;

    private ITodoRestOperations todoRestOperations;

    private ImageButton imageButtonSelectSortMethod;
    private ImageButton imageButtonAddTodo;
    private ImageButton imageButtonDebug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_overview);

        DailyDoneApplication dailyDoneApplication = (DailyDoneApplication) getApplicationContext();
        dataService = dailyDoneApplication.getTodoDataService();
        todoDatabase = dailyDoneApplication.getTodoDatabase();
        todoRestOperations = dailyDoneApplication.getTodoRestOperations();

        imageButtonSelectSortMethod = findViewById(R.id.imageButtonSelectSortMethod);
        imageButtonAddTodo = findViewById(R.id.imageButtonAddTodo);
        imageButtonDebug = findViewById(R.id.imageButtonDebug);

        TodoOverviewViewModel todoOverviewViewModel =
                new ViewModelProvider(this).get(TodoOverviewViewModel.class);

        TodoAdapter todoAdapter = new TodoAdapter((DailyDoneApplication) getApplicationContext());
        RecyclerView recyclerView = findViewById(R.id.todoOverviewRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(todoAdapter);

        todoOverviewViewModel.getSortedTodos().observe(this, sortedTodos -> {
            if (sortedTodos != null) {
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
                if (menuItem.getItemId() == R.id.option_sort_by_relevance) {
                    todoOverviewViewModel.getSelectedSortMethod()
                            .setValue(TodoSortMethods.RELEVANCE_DATE);
                    return true;
                } else if (menuItem.getItemId() == R.id.option_sort_by_date) {
                    todoOverviewViewModel.getSelectedSortMethod()
                            .setValue(TodoSortMethods.DATE_RELEVANCE);
                    return true;
                } else {
                    return false;
                }
            });
            popupMenu.show();
        });

        imageButtonDebug.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, imageButtonDebug);
            popupMenu.getMenuInflater().inflate(R.menu.debug_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                // Cannot use a switch since Resource IDs will no longer be final constants.
                if (menuItem.getItemId() == R.id.option_delete_local_todos) {
                    if (todoDatabase != null) {
                        AsyncUtils.executeAsync(() -> {
                            todoDatabase.getDao().deleteAllTodos();
                        });
                    }
                    return true;
                } else if (menuItem.getItemId() == R.id.option_delete_remote_todos) {
                    if (todoRestOperations != null) {
                        AsyncUtils.executeAsync(() -> {
                            try {
                                todoRestOperations.deleteAllTodos().execute();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        return true;
                    }
                } else if (menuItem.getItemId() == R.id.option_synchronization) {
                    // This solution seems quite "dirty" but it is the correct way to go as
                    // the synchronization can only be executed if the Backend is available and
                    // because of this the Data Service is the Distributed Data Service.
                    // Under normal conditions the synchronize Data Sources method would not
                    // be triggered from outside the Data Service Factory.
                    if (dailyDoneApplication.getIsWebBackendAvailable().getValue()) {
                        AsyncUtils.executeAsync(() -> {
                            ((DistributedTodoDataService) dataService).synchronizeDataSources();
                        });
                        return true;
                    }
                } else if (menuItem.getItemId() == R.id.option_reset_remote) {
                    if (todoRestOperations != null) {
                        AsyncUtils.executeAsync(() -> {
                            try {
                                todoRestOperations.resetTodos().execute();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        return true;
                    }
                }
                return false;
            });

            popupMenu.show();
        });
    }
}