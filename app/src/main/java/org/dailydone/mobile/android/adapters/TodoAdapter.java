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
import org.dailydone.mobile.android.util.Constants;

import java.util.Calendar;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

// The Adapter was created according to
// https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter
public class TodoAdapter extends ListAdapter<Todo, TodoAdapter.TodoViewHolder> {
    // TODO Hint
    // Is needed to get the data service. However, passing the DataService itself would be better.
    private final DailyDoneApplication application;

    public TodoAdapter(DailyDoneApplication application) {
        super(DIFF_CALLBACK);
        this.application = application;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // The adapter has no own inflater
        TodoViewBinding binding = TodoViewBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TodoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        holder.bind(new ViewAbstractionTodo(getItem(position), application.getTodoDataService()));
    }

    public static final DiffUtil.ItemCallback<Todo> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Todo oldItem, @NonNull Todo newItem) {
            return oldItem.equals(newItem);
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

        public void bind(ViewAbstractionTodo viewAbstractionTodo) {
            this.viewAbstractionTodo = viewAbstractionTodo;

            // TODO Hint
            // Instead of setting the listeners here, they should be set inside the
            // constructor to be more efficient.
            binding.setTodoAbstraction(viewAbstractionTodo);

            binding.checkBoxDone.setOnClickListener(view -> {
                adaptBackgroundColor();
            });

            binding.linearLayoutTodoOverview.setOnClickListener(view -> {
                // The root is the "top parent" inside the view group.
                Context context = binding.getRoot().getContext();
                Intent detailViewIntent = new Intent(context, TodoDetailViewActivity.class);
                detailViewIntent.putExtra(
                        TodoDetailViewActivity.EXTRA_TODO_ID, viewAbstractionTodo.getId());
                context.startActivity(detailViewIntent);
            });

            // Initially set background color of view
            adaptBackgroundColor();
        }

        private void adaptBackgroundColor() {
            Calendar calendar = Calendar.getInstance(Constants.TIMEZONE);

            int backgroundColor;

            Date expiryDate = new Date(viewAbstractionTodo.getExpiry());
            Date currentDate = calendar.getTime();

            if (viewAbstractionTodo.isDone()) {
                backgroundColor = R.color.gray_6;
            } else if (expiryDate.before(currentDate)) {
                // Signal color for overdue Todos
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
