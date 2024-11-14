package org.dailydone.mobile.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.dailydone.mobile.android.DailyDoneApplication;
import org.dailydone.mobile.android.R;
import org.dailydone.mobile.android.databinding.TodoViewBinding;
import org.dailydone.mobile.android.model.Todo;
import org.dailydone.mobile.android.model.observableModel.ObservableTodo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private final DailyDoneApplication application;

    private List<Todo> todos = new ArrayList<>();

    public TodoAdapter(DailyDoneApplication application) {
        this.application = application;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_view, parent, false);
        TodoViewBinding binding = TodoViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TodoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        System.out.println(todos.get(position).getName());
        System.out.println(position);
        holder.bind(new ObservableTodo(todos.get(position), application.getTodoDataService()));
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
        notifyItemChanged(0);
    }

    @Getter
    @Setter
    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        private ObservableTodo observableTodo;
        private final TodoViewBinding binding;

        public TodoViewHolder(@NonNull TodoViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ObservableTodo observableTodo) {
            binding.setViewHolder(observableTodo);
            this.observableTodo = observableTodo;
            binding.executePendingBindings();
        }
    }
}
