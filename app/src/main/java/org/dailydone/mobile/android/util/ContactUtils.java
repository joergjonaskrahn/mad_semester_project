package org.dailydone.mobile.android.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import org.dailydone.mobile.android.model.viewAbstractions.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactUtils {

    @SuppressLint("Range")
    public static Contact getContactForUri(Uri contactUri, ContentResolver contentResolver) {
        // Query the contact ID
        String[] projection = {ContactsContract.Contacts._ID};
        Cursor contactCursor = contentResolver.query(contactUri, projection, null, null, null);

        if (contactCursor != null && contactCursor.moveToFirst()) {
            // Retrieve the contact ID
            String contactId = contactCursor.getString(
                    contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
            contactCursor.close();

            return getContactForContactId(contactId, contentResolver);
        }
        return null;
    }

    public static List<Contact> getContactsForContactIds(List<String> contactIds,
                                                         ContentResolver contentResolver) {
        List<Contact> contacts = new ArrayList<>();
        contactIds.forEach(contactId -> {
            Contact contact = ContactUtils.getContactForContactId(contactId, contentResolver);
            if(contact != null) {
                contacts.add(contact);
            }
        });
        return contacts;
    }

    @SuppressLint("Range")
    public static Contact getContactForContactId(String contactId, ContentResolver contentResolver) {
        // Query the Data table (Entity table functionality) for contact details
        Cursor entityCursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,  // Use Data.CONTENT_URI here
                new String[]{
                        ContactsContract.Data.DATA1,
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.Contacts.DISPLAY_NAME
                },
                ContactsContract.Data.CONTACT_ID + " = ?",
                new String[]{contactId},
                null
        );

        if (entityCursor != null) {
            String contactName = "";
            List<String> phoneNumbers = new ArrayList<>();
            List<String> emailAdresses = new ArrayList<>();

            while (entityCursor.moveToNext()) {
                String mimeType = entityCursor.getString(
                        entityCursor.getColumnIndex(ContactsContract.Data.MIMETYPE));

                String data = entityCursor.getString(
                        entityCursor.getColumnIndex(ContactsContract.Data.DATA1));

                // Extract contact name
                if (contactName.isEmpty()) {
                    contactName = entityCursor.getString(
                            entityCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                }

                // Extract phone numbers
                if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(mimeType)) {
                    phoneNumbers.add(data);
                }

                // Extract emailAdresses
                if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(mimeType)) {
                    emailAdresses.add(data);
                }
            }

            entityCursor.close();

            return new Contact(
                    contactId,
                    contactName,
                    phoneNumbers,
                    emailAdresses
            );
        }
        return null;
    }
}