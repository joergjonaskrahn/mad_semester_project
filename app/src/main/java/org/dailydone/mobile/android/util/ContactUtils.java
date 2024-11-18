package org.dailydone.mobile.android.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

public class ContactUtils {

    public static void getContacts(ContentResolver contentResolver) {
        // URI zum Abrufen von Kontakten
        Cursor cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // Kontakt-ID abrufen
                String contactId = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts._ID)
                );

                // Kontaktname abrufen
                String contactName = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                );

                Log.d("ContactUtils", "Contact ID: " + contactId + ", Name: " + contactName);
            }
            cursor.close();
        } else {
            Log.d("ContactUtils", "No contacts found.");
        }
    }
}