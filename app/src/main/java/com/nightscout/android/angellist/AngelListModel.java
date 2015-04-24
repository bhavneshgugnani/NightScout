package com.nightscout.android.angellist;

import android.widget.Checkable;

import java.util.List;

/**
 * Created by Bhavnesh Gugnani on 3/30/2015.
 */
public class AngelListModel implements Checkable {
    private String id;
    private String name;
    private List<String> phoneNumber;
    private boolean checked;//1=checked ; 0=unchecked

    public AngelListModel(String id, String name, List<String> phoneNumber, boolean checked) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public List<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(List<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void setChecked(boolean checked) {

        this.checked = checked;
    }

    @Override
    public boolean isChecked() {

        return checked;
    }

    @Override
    public void toggle() {
        if (checked == true)
            checked = false;
        else
            checked = true;
    }
}
