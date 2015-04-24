package com.nightscout.android.angellist;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Bhavnesh Gugnani on 4/2/2015.
 */
public class FileReaderWriter {
    public static final String ANGEL_LIST_STORAGE_FILENAME = "android_uploader_angel_list.txt";

    public static String readAngelListDataFromFileInCSV(Context context, FileInputStream inputStream) throws AngelListException {
        String angelListData = "";
        File file = new File(context.getFilesDir(), ANGEL_LIST_STORAGE_FILENAME);
        CharSequence path = context.getFilesDir().getAbsolutePath();
        //Toast.makeText(context, path, Toast.LENGTH_LONG).show();
        try {
            if (!file.exists())
                throw new AngelListException("Angel list file missing. No Angel List stored till now.");
            int data = inputStream.read();
            while (data != -1) {
                char theChar = (char) data;
                angelListData += theChar;
                data = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            throw new AngelListException("Unable to read angel list from memory file.");
        }
        return angelListData;
    }

    public static void writeAngelListDataToFileInCSV(Context context, FileOutputStream outputStream, String text) throws AngelListException {
        File file = new File(context.getFilesDir(), ANGEL_LIST_STORAGE_FILENAME);
        try {
            if (!file.exists())
                file.createNewFile();
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
            outputWriter.write(text);
            outputWriter.close();
        } catch (IOException e) {
            //exception thrown here is caught in activity
            throw new AngelListException("Error writing data to file.");
        }
    }
}