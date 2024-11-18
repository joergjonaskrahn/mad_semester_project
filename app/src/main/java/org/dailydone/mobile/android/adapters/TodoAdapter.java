package org.dailydone.mobile.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.dailydone.mobile.android.DailyDoneApplication;
import org.dailydone.mobile.android.R;
import org.dailydone.mobile.android.TodoDetailViewActivity;
import org.dailydone.mobile.android.databinding.TodoViewBinding;
import org.dailydone.mobile.android.model.Todo;
import org.dailydone.mobile.android.model.viewAbstractions.ViewAbstractionTodo;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

// The Adapter was created according to
// https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter
public class TodoAdapter extends ListAdapter<Todo, TodoAdapter.TodoViewHolder> {
    private final DailyDoneApplication application;

    public TodoAdapter(DailyDoneApplication application) {
        super(DIFF_CALLBACK);
        this.application = application;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TodoViewBinding binding = TodoViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TodoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        holder.bind(new ViewAbstractionTodo(getItem(position), application.getTodoDataService()));
    }

    public static final DiffUtil.ItemCallback<Todo> DIFF_CALLBACK = new DiffUtil.ItemCallback<Todo>() {
        @Override
        public boolean areItemsTheSame(@NonNull Todo oldItem, @NonNull Todo newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Todo oldItem, @NonNull Todo newItem) {
            return oldItem.contentsEqual(newItem);
        }
    };

    @Getter
    @Setter
    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        private final TodoViewBinding binding;
        private ViewAbstractionTodo viewAbstractionTodo;

        public TodoViewHolder(@NonNull TodoViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ViewAbstractionTodo observableTodo) {
            binding.setTodoAbstraction(observableTodo);
            this.viewAbstractionTodo = observableTodo;
            adaptBackgroundColor();

            binding.checkBoxDone.setOnClickListener(view -> {
                adaptBackgroundColor();
            });

            binding.linearLayoutTodoOverview.setOnClickListener(view -> {
                Context context = binding.getRoot().getContext();
                // ???
                Intent detailViewIntent = new Intent(context, TodoDetailViewActivity.class);
                detailViewIntent.putExtra(
                        TodoDetailViewActivity.EXTRA_TODO_ID, observableTodo.getId());
                context.startActivity(detailViewIntent);
            });
        }

        private void adaptBackgroundColor() {
            int backgroundColor;

            Date expiryDate = new Date(viewAbstractionTodo.getExpiry());
            Date currentDate = new Date();
            // Date controlDate = new Date(2147483647L * 1000);

            if (viewAbstractionTodo.isDone()) {
                backgroundColor = R.color.light_gray;
            } else if (expiryDate.before(currentDate)) {
                backgroundColor = R.color.decent_red;
            } else {
                backgroundColor = R.color.decent_blue;
            }

            Context context = binding.getRoot().getContext();
            binding.linearLayoutTodoOverview.setBackgroundColor(
                    ContextCompat.getColor(context, backgroundColor));
        }
    }
}
