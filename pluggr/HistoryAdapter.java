package com.techomite.math.pluggr;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Steven Yu on 4/5/2018.
 */

public class HistoryAdapter extends ArrayAdapter<String>{
    private ArrayList<String> eq;
    private ArrayList<String> vars;
    private ArrayList<String> root;

    public HistoryAdapter(Context context, ArrayList<String> eq_, ArrayList<String> vars_, ArrayList<String> root_) {
        super(context, R.layout.history_detail, new String[eq_.size()]);
        eq = eq_;
        vars = vars_;
        root = root_;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.history_detail, parent, false);
        }
        else {
            view = convertView;
        }
        TextView equation = (TextView) view.findViewById(R.id.textViewEq);
        equation.setText(eq.get(position));
        TextView variables = (TextView) view.findViewById(R.id.vars);
        variables.setText(vars.get(position));
        TextView roots = (TextView) view.findViewById(R.id.roots);
        roots.setText(root.get(position));
        return view;
    }
}
