package com.nightscout.android.angellist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nightscout.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhavnesh Gugnani on 3/30/2015.
 */
public class AngelListAdapter extends ArrayAdapter {
    public static final int CHECKED_COLOR = Color.LTGRAY;
    public static final int UNCHECKED_COLOR = Color.BLACK;

    private AngelListModel[] modelItems = null;
    private Context context = null;

    public AngelListAdapter(Context context, AngelListModel[] resource) {
        super(context, R.layout.angel_list_row, resource);
        this.context = context;
        this.modelItems = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.angel_list_row, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.textView1);
        name.setText(modelItems[position].getName());
        if (modelItems[position].isChecked())
            convertView.setBackgroundColor(CHECKED_COLOR);
        else
            convertView.setBackgroundColor(UNCHECKED_COLOR);
        return convertView;
    }

    public void updateCheckedStatus(int position, boolean isChecked) {
        modelItems[position].setChecked(isChecked);
    }

    public List<AngelListModel> getSelectedModels() {
        List<AngelListModel> selectedAngels = new ArrayList<>();

        for (AngelListModel model : modelItems) {
            if (model.isChecked())
                selectedAngels.add(model);
        }

        return selectedAngels;
    }
}
