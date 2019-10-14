package com.techomite.math.pluggr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by Steven Yu on 4/4/2018.
 */

public class HistoryActivity extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.history, container, false);
        ListView history = (ListView) rootView.findViewById(R.id.listory);
        ArrayList<ArrayList<String>> history_default =  new ArrayList<>();
        history_default.add(new ArrayList<String>());
        history_default.add(new ArrayList<String>());
        history_default.add(new ArrayList<String>());
        ArrayList<ArrayList<String>> historyData = (ArrayList<ArrayList<String>>) readData("history_storage", history_default);
        history.setAdapter(new HistoryAdapter(getContext(), historyData.get(0),  historyData.get(1),  historyData.get(2)));
        TextView emptyText = (TextView) rootView.findViewById(R.id.emptyText);
        history.setEmptyView(emptyText);
        return rootView;
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
