package com.techomite.math.pluggr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Steven Yu on 4/10/2018.
 */

public class SwitchAdapter extends ArrayAdapter<String> {
    public SwitchAdapter(Context context) {
        super(context, R.layout.history_detail, new String[1]);
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.preferences_switch, parent, false);
        }
        else {
            view = convertView;
        }
        TextView text = (TextView) view.findViewById(R.id.textTitle);
        text.setText("Exponent Input");
        TextView text1 = (TextView) view.findViewById(R.id.textSub);
        text1.setText("Enable scientific notation inputs for variables");
        final Switch switch1 = (Switch) view.findViewById(R.id.switch1);
        switch1.setChecked((boolean) readData("scientific_switch", false));
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveData("scientific_switch", isChecked);
            }
        });
        return view;
    }

    public void saveData(String filename, Object o) {
        try {
            FileOutputStream fos = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
            oos.flush();
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object readData(String filename, Object _default) {
        try {
            FileInputStream fis = getContext().openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object returnObject = ois.readObject();
            ois.close();
            fis.close();
            return returnObject;
        } catch (ClassNotFoundException | IOException e) {
            return _default;
        }
    }
}
