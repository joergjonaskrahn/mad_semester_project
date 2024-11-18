package org.dailydone.mobile.android.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.dailydone.mobile.android.R;
import org.dailydone.mobile.android.databinding.ContactViewBinding;
import org.dailydone.mobile.android.enums.TodoSortMethods;
import org.dailydone.mobile.android.model.viewAbstractions.Contact;
import org.dailydone.mobile.android.model.viewAbstractions.ViewAbstractionTodo;

import lombok.Getter;
import lombok.Setter;

public class ContactAdapter extends ListAdapter<Contact, ContactAdapter.ContactViewHolder> {

    public ContactAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContactViewBinding binding = ContactViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ContactAdapter.ContactViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ContactViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static final DiffUtil.ItemCallback<Contact> DIFF_CALLBACK = new DiffUtil.ItemCallback<Contact>() {
        @Override
        public boolean areItemsTheSame(@NonNull Contact oldItem, @NonNull Contact newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Contact oldItem, @NonNull Contact newItem) {
            return oldItem.contentsEqual(newItem);
        }
    };

    @Getter
    @Setter
    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        private final ContactViewBinding binding;
        private ViewAbstractionTodo viewAbstractionTodo;

        public ContactViewHolder(@NonNull ContactViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Contact contact) {
            binding.setContact(contact);

            // TODO: Implement set Text on Textview with telephone number or mail address

            binding.textViewContact.setOnLongClickListener(view -> {
                // TODO: Implement Popup menu with removing options if contact does not have mail
                // TODO: or telephone number
                /*PopupMenu popupMenu = new PopupMenu(this, imageButtonSelectSortMethod);
                popupMenu.getMenuInflater().inflate(R.menu.todo_sort_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    // Cannot use a switch since Resource IDs will no longer be final constants.
                    if (menuItem.getItemId() == R.id.option_sort_by_relevance) {
                        todoOverviewViewModel.getSelectedSortMethod().setValue(TodoSortMethods.RELEVANCE_DATE);
                        return true;
                    } else if (menuItem.getItemId() == R.id.option_sort_by_date) {
                        todoOverviewViewModel.getSelectedSortMethod().setValue(TodoSortMethods.DATE_RELEVANCE);
                        return true;
                    } else {
                        return false;
                    }
                });
                popupMenu.show();
            });*/
                return false;
            });
        }
    }
}