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
import org.dailydone.mobile.android.model.viewAbstractions.Contact;

import lombok.Getter;
import lombok.Setter;

// The Adapter was created according to
// https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter
public class ContactAdapter extends ListAdapter<Contact, ContactAdapter.ContactViewHolder> {
    private final RemoveContactCallback removeContactCallback;

    public ContactAdapter(RemoveContactCallback removeContactCallback) {
        super(DIFF_CALLBACK);
        this.removeContactCallback = removeContactCallback;
    }

    @NonNull
    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // The adapter has no own inflater
        ContactViewBinding binding = ContactViewBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
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

        public ContactViewHolder(ContactViewBinding binding,
                                 RemoveContactCallback removeContactCallback) {
            super(binding.getRoot());
            this.binding = binding;
            this.removeContactCallback = removeContactCallback;
        }

        public void bind(Contact contact) {
            binding.setContact(contact);

            binding.linearLayoutContactView.setOnLongClickListener(view -> {
                // The context of a View is set when it is created. In this case the context
                // comes from the LayoutInflater in onCreateViewHolder
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.contact_menu, popupMenu.getMenu());

                // Rmove call or mail option if there is no telephone or mail data
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
                        // First number is called
                        String phoneNumber = contact.getTelephoneNumbers().get(0);
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        // URIs with specific schemas are used to identify the actions
                        // (/ resources)
                        callIntent.setData(Uri.parse("tel:" + phoneNumber));
                        context.startActivity(callIntent);
                    } else if (menuItem.getItemId() == R.id.option_mail) {
                        // First mail address is used
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