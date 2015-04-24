package com.nightscout.android.angellist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nightscout.android.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AngelListActivity extends Activity {

    public static final String ANGEL_LIST_CSV_DELIMITER = ";";
    private static final String EMERGENCY_911_PHONE_NUMBER = "911";

    private static final String TAG = AngelListActivity.class.getSimpleName();

    private ListView angelListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_angel_list);

        angelListView = (ListView) findViewById(R.id.angels);

        //read contacts
        AngelListModel[] modelItemsList = ContactListReader.fetchContacts(getContentResolver());
        String angelListInCSV = null;
        //read old angel list from memory
        try {
            //read old angel list id as csv from file
            angelListInCSV = FileReaderWriter.readAngelListDataFromFileInCSV(getApplicationContext(), openFileInput(FileReaderWriter.ANGEL_LIST_STORAGE_FILENAME));
        } catch (AngelListException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }

        //compile contacts with old angel list
        modelItemsList = compileAngelListBeforeDisplay(modelItemsList, angelListInCSV);

        AngelListAdapter adapter = new AngelListAdapter(this, modelItemsList);
        angelListView.setAdapter(adapter);
        angelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AngelListModel model = (AngelListModel) parent.getItemAtPosition(position);
                AngelListAdapter adapter = (AngelListAdapter) parent.getAdapter();

                //Emergency 911 enabled support advice dialog
                if (position == 0 && !model.isChecked()) {
                    open911AvailabilityAdviceDialog();
                }

                if (!model.isChecked()) {
                    //enable selection of current item
                    view.setBackgroundColor(AngelListAdapter.CHECKED_COLOR);
                    model.setChecked(true);
                    adapter.updateCheckedStatus(position, true);
                } else {
                    //disable selection of current item
                    view.setBackgroundColor(AngelListAdapter.UNCHECKED_COLOR);
                    model.setChecked(false);
                    adapter.updateCheckedStatus(position, false);
                }
            }
        });

    }

    private AngelListModel[] compileAngelListBeforeDisplay(AngelListModel[] modelItems, String angelListCSV) {
        AngelListModel[] compiledItemList = new AngelListModel[modelItems.length + 1];
        //add emergency 911
        List<String> emergency911PhoneNumberList = new ArrayList<String>();
        emergency911PhoneNumberList.add(EMERGENCY_911_PHONE_NUMBER);
        compiledItemList[0] = new AngelListModel("-1", getString(R.string.emergency_911), emergency911PhoneNumberList, false);
        int index = 1;
        for (AngelListModel item : modelItems) {
            compiledItemList[index] = item;
            index++;
        }
        //compile contacts with older angel list before display
        if (!(angelListCSV == "" || angelListCSV == null)) {
            String[] olderAngelListContactIds = angelListCSV.split(ANGEL_LIST_CSV_DELIMITER);
            for (String olderAngelListContactId : olderAngelListContactIds) {
                for (AngelListModel model : compiledItemList) {
                    if (model.getId().equalsIgnoreCase(olderAngelListContactId))
                        model.setChecked(true);
                }
            }
        }

        return compiledItemList;
    }

    private void open911AvailabilityAdviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AngelListActivity.this);
        builder.setTitle(R.string.title_911_selection_advice_dialog).setMessage(R.string.message_911_selection_advice_dialog)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do nothing
                    }
                });

        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    private void openNoContactsPresentWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AngelListActivity.this);
        builder.setTitle(R.string.title_no_contact_available_warning).setMessage(R.string.no_contact_present_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });

        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_angel_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save) {
            saveChangesToAngelListInMemory();
        } else if (id == R.id.cancel) {
            setResult(RESULT_OK);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveChangesToAngelListInMemory() {
        try {
            ListView lv = (ListView) findViewById(R.id.angels);
            AngelListAdapter adapter = (AngelListAdapter) lv.getAdapter();
            List<AngelListModel> selectedAngels = adapter.getSelectedModels();
            //write new angel list id to file as csv
            String newAngelListIdCSV = "";
            for (AngelListModel model : selectedAngels) {
                newAngelListIdCSV += model.getId() + ANGEL_LIST_CSV_DELIMITER;
            }
            newAngelListIdCSV = newAngelListIdCSV.substring(0, newAngelListIdCSV.length() - 1);//loose the extra delimiter at end
            FileReaderWriter.writeAngelListDataToFileInCSV(getApplicationContext(), openFileOutput(FileReaderWriter.ANGEL_LIST_STORAGE_FILENAME, Context.MODE_PRIVATE), newAngelListIdCSV);

            CharSequence messageText = getString(R.string.message_angel_list_save_succcesful);
            Toast.makeText(getApplicationContext(), messageText, Toast.LENGTH_LONG).show();
        } catch (AngelListException e) {
            Log.d(TAG, e.getMessage());
            CharSequence toastMessage = getString(R.string.message_angel_list_save_failure);
            Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } finally {
            //return to parent activity
            setResult(RESULT_OK);
            finish();
        }
    }
}