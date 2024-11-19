package org.dailydone.mobile.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import org.dailydone.mobile.android.view_model.TodoDetailViewViewModel;

import lombok.Getter;
import lombok.Setter;

public class ContactAdapter extends ListAdapter<Contact, ContactAdapter.ContactViewHolder> {
    private final RemoveContactCallback removeContactCallback;

    public ContactAdapter(RemoveContactCallback removeContactCallback) {
        super(DIFF_CALLBACK);
        this.removeContactCallback = removeContactCallback;
    }

    @NonNull
    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContactViewBinding binding = ContactViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ContactAdapter.ContactViewHolder(binding, removeContactCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ContactViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static final DiffUtil.ItemCallback<Contact> DIFF_CALLBACK = new DiffUtil.ItemCallback<Contact>() {
        @Override
        public boolean areItemsTheSame(@NonNull Contact oldItem, @NonNull Contact newItem) {
            return oldItem.equals(newItem);
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
        private final RemoveContactCallback removeContactCallback;

        private ViewAbstractionTodo viewAbstractionTodo;

        public ContactViewHolder(ContactViewBinding binding,
                                 RemoveContactCallback removeContactCallback) {
            super(binding.getRoot());
            this.binding = binding;
            this.removeContactCallback = removeContactCallback;
        }

        public void bind(Contact contact) {
            binding.setContact(contact);

            binding.linearLayoutContactView.setOnLongClickListener(view -> {
                // TODO: ?? Do Views have a Context ??
                PopupMenu popupMenu = new PopupMenu(view.getRootView().getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.contact_menu, popupMenu.getMenu());

                if (contact.getTelephoneNumbers().isEmpty()) {
                    popupMenu.getMenu().removeItem(R.id.option_call);
                }

                if (contact.getMailAddresses().isEmpty()) {
                    popupMenu.getMenu().removeItem(R.id.option_mail);
                }

                Context context = view.getContext();

                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    // Cannot use a switch since Resource IDs will no longer be final constants.
                    if (menuItem.getItemId() == R.id.option_remove_contact) {
                        removeContactCallback.removeContact(contact.getId());
                    } else if (menuItem.getItemId() == R.id.option_call) {
                        String phoneNumber = contact.getTelephoneNumbers().get(0);
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + phoneNumber));
                        context.startActivity(callIntent);
                    } else if (menuItem.getItemId() == R.id.option_mail) {
                        String emailAddress = contact.getMailAddresses().get(0);
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto:" + emailAddress));
                        context.startActivity(emailIntent);
                    }
                    return false;
                });
                popupMenu.show();
                return false;
            });
        }
    }

    public interface RemoveContactCallback {
        void removeContact(String id);
    }
}