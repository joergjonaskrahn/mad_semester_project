package org.dailydone.mobile.android.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import org.dailydone.mobile.android.exceptions.FetchContactException;
import org.dailydone.mobile.android.model.viewAbstractions.Contact;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class ContactUtils {
    private static final String MSG_TOO_MANY_CONTACTS = "There were to many contacts.";
    private static final String MSG_CURSOR_NULL = "The cursor was null.";

    private static final String[] contactProjection = new String[]{
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.DATA1
    };

    // This method fetches the ID for a Contact given it`s URI and then fetches the Contact
    // itself.
    @SuppressLint("Range")
    public static Contact getContactForUri(
            Uri contactUri, ContentResolver contentResolver) throws Exception {
        String[] projection = {ContactsContract.Contacts._ID};
        // Fetch the ID based on the specific URI of the contact.
        Cursor contactCursor = contentResolver.query(
                contactUri, projection, null, null, null);

        if (contactCursor != null && contactCursor.moveToFirst()) {
            String contactId = contactCursor.getString(
                    contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
            contactCursor.close();
            return getContactForId(contactId, contentResolver);
        }

        throw new FetchContactException(MSG_CURSOR_NULL);
    }

    // This method fetches a contact based on it`s ID
    public static Contact getContactForId(
            String contactId, ContentResolver contentResolver) throws FetchContactException {
        Cursor contactCursor = contentResolver.query(
                // URI for querying the Data table
                ContactsContract.Data.CONTENT_URI,
                contactProjection,
                // Select is needed as no Contact specific URI is provided
                ContactsContract.Data.CONTACT_ID + " = ?",
                // Selection arguments which are injected into the search query
                new String[]{contactId},
                null
        );

        return fetchContactForCursor(contactCursor);
    }

    public static List<Contact> getContactsForIds(
            List<String> contactIds, ContentResolver contentResolver) throws FetchContactException {
        // Create String for selection query (comma seperated list for all contact IDs)
        String selectionPlaceholders = TextUtils.join(
                ",", Collections.nCopies(contactIds.size(), "?"));

        Cursor contactCursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                contactProjection,
                ContactsContract.Data.CONTACT_ID + " IN (" + selectionPlaceholders + ")",
                contactIds.toArray(new String[contactIds.size()]),
                null
        );

        return fetchContactsForCursor(contactCursor);
    }

    private static Contact fetchContactForCursor(Cursor cursor) throws FetchContactException {
        List<Contact> contacts = fetchContactsForCursor(cursor);
        if (contacts.size() == 1) {
            return contacts.get(0);
        }
        throw new FetchContactException(MSG_TOO_MANY_CONTACTS);
    }

    @SuppressLint("Range")
    private static List<Contact> fetchContactsForCursor(Cursor cursor)
            throws FetchContactException {
        if (cursor == null) {
            throw new FetchContactException(MSG_CURSOR_NULL);
        }

        Stack<Contact> contacts = new Stack<>();

        String lastContactId = "";

        // Important: The loop may have multiple runs for one entity, as one entity can have
        // multiple rows, for example because of multiple telephone numbers. That`s why
        // lastContactId is needed since a new line does not always mean a new entity.
        while (cursor.moveToNext()) {
            String currentContactId = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));

            String mimeType = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));

            String data = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Data.DATA1));

            if (!currentContactId.equals(lastContactId)) {
                String displayName = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                Contact newContact = new Contact(currentContactId, displayName);
                contacts.push(newContact);
            }

            Contact currentContact = contacts.peek();

            if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(mimeType)) {
                currentContact.getTelephoneNumbers().add(data);
            } else if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(mimeType)) {
                currentContact.getMailAddresses().add(data);
            }

            lastContactId = currentContactId;
        }

        cursor.close();

        return contacts;
    }
}