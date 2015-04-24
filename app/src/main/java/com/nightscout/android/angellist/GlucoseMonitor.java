package com.nightscout.android.angellist;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.nightscout.android.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Bhavnesh Gugnani on 4/17/2015.
 * <p/>
 * STATIC REASON : This class is designed to be having all elements static because of the existing architecture. The Syncing
 */
public class GlucoseMonitor {
    private static final String GOOGLE_MAP_URL = " http://maps.google.com/?q=";

    private static final int RECORD_CACHE_SIZE = 3;
    private static final int MIN_GLUCOSE_THRESHOLD = 70;
    private static final int MAX_GLUCOSE_THRESHOLD = 200;

    private static Context context = null;
    private static LocationManager locationManager = null;
    private static List<Integer> cachedRecords = new LinkedList<>();

    public static void setContext(Context context) {
        GlucoseMonitor.context = context;
    }

    public static void setLocationManager(LocationManager locationManager) {
        GlucoseMonitor.locationManager = locationManager;
    }

    public static void registerGlucoseLevel(int glucoseLevel) {
        if (cachedRecords.size() < RECORD_CACHE_SIZE)
            cachedRecords.add(new Integer(glucoseLevel));
        else {
            for (int i = 0; i < cachedRecords.size() - 1; i++)
                cachedRecords.set(i, cachedRecords.get(i + 1));
            cachedRecords.set(cachedRecords.size() - 1, glucoseLevel);
        }
        if (cachedRecords.size() == RECORD_CACHE_SIZE)
            if (assessLastCachedValuesForHelp()) {
                sendMessagesToAngelList();
                //clear cached records
                while (!cachedRecords.isEmpty())
                    cachedRecords.remove(0);
            }
    }

    private static boolean assessLastCachedValuesForHelp() {
        boolean result = true;
        for (Integer glucoseLevel : cachedRecords) {
            if (glucoseLevel >= MIN_GLUCOSE_THRESHOLD && glucoseLevel <= MAX_GLUCOSE_THRESHOLD)
                result = false;
        }
        return result;
    }

    private static void sendMessagesToAngelList() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // Find current location of patient.
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    // The total length of text of message should also be kept in limit as suggested by telephone service provided
                    StringBuffer smsBody = new StringBuffer();
                    smsBody.append(context.getString(R.string.angel_list_message_text));
                    if (location != null) {
                        smsBody.append(GOOGLE_MAP_URL);
                        smsBody.append(location.getLatitude());
                        smsBody.append(",");
                        smsBody.append(location.getLongitude());
                    } else {
                        Log.d("AngelList", "Cannot find current location of Patient, issue with the location manager.Sending only help message.");
                    }

                    String angelListIdCSV = FileReaderWriter.readAngelListDataFromFileInCSV(context, context.openFileInput(FileReaderWriter.ANGEL_LIST_STORAGE_FILENAME));
                    String[] angelListIds = angelListIdCSV.split(AngelListActivity.ANGEL_LIST_CSV_DELIMITER);
                    if (angelListIds.length > 0) {
                        AngelListModel[] contacts = ContactListReader.fetchContacts(context.getContentResolver());
                        for (String id : angelListIds) {
                            for (AngelListModel model : contacts) {
                                if (id.equalsIgnoreCase(model.getId())) {
                                    for (String phoneNum : model.getPhoneNumber())
                                        MessageHandler.sendMessageToAngel(phoneNum, smsBody.toString());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}