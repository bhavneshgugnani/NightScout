package com.nightscout.android.angellist;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhavnesh Gugnani on 4/2/2015.
 */
public class ContactListReader {

    public static AngelListModel[] fetchContacts(ContentResolver contentResolver) {
        AngelListModel[] contacts = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        //ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {
            contacts = new AngelListModel[cursor.getCount()];
            int index = 0;

            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                List<String> phoneNumberList = null;
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    // Query and loop for every phone number of the contact
                    phoneNumberList = new ArrayList<String>();
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                    while (phoneCursor.moveToNext()) {
                        phoneNumberList.add(phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)));
                    }
                    phoneCursor.close();
                }
                //phone numbers for contact are available for messaging
                //if(phoneNumberList != null && phoneNumberList.size() > 0) {
                    contacts[index] = new AngelListModel(contact_id, name, phoneNumberList, false);
                    index++;
                //}

            }
        }
        return contacts;
    }
}
