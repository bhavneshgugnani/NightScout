package com.nightscout.android.angellist;

import android.telephony.SmsManager;

/**
 * Created by Bhavnesh Gugnani on 4/2/2015.
 */
public class MessageHandler {

    private static final SmsManager smsManager = SmsManager.getDefault();

    public static void sendMessageToAngel(String phoneNum, String message) throws AngelListException {
        try {
            smsManager.sendTextMessage(phoneNum, null, message, null, null);
        } catch (Exception e) {
            // failed to send message
            throw new AngelListException("Failed to send message.");
        }
    }
}