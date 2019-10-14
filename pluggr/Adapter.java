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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Adapter extends ArrayAdapter<String> {

    public int resource;
    private boolean scientific;
    private String[] variables;
    private EditText[] arrEditText;
    private Double[] arrEditTextVals;
    private EditText[] arrExpText;
    private Integer[] arrExpTextVals;

    public Adapter(Context context, int _resource, String[] values){
        super(context, _resource, values);
        resource = _resource;
        variables = values;
        arrEditText = new EditText[values.length];
        arrExpText = new EditText[values.length];

        //Load Data
        Double[] list_default = new Double[values.length];
        try { //catch exception from previous version String[]
            arrEditTextVals = (Double[]) readData("list_vals_storage", list_default);
        } catch (ClassCastException ex) {
            arrEditTextVals = list_default;
        }
        if (arrEditTextVals.length != values.length) {
            arrEditTextVals = list_default;
        }
        Integer[] exp_default = new Integer[values.length];
        try { //catch exception from previous version Integer[]
            arrExpTextVals = (Integer[]) readData("exp_vals_storage", exp_default);
        } catch (ClassCastException ex) {
            arrExpTextVals = exp_default;
        }
        if (arrExpTextVals.length != values.length) {
            arrExpTextVals = exp_default;
        }
        scientific = (boolean) readData("scientific_switch", false);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(resource, parent, false);
            holder.editText = (EditText) view.findViewById(R.id.input);
            arrEditText[position] = holder.editText;
            if (resource == R.layout.vars_listview_detail_exp) {
                holder.expText = (EditText) view.findViewById(R.id.editText);
                arrExpText[position] = holder.expText;
            }
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
            view = convertView;
        }

        holder.ref = position;
        if (arrEditTextVals[position] != null) {
            holder.editText.setText(arrEditTextVals[position].toString());
        } else {
            holder.editText.setText(null);
        }
        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!Util.isDouble(s.toString())) {
                    arrEditTextVals[holder.ref] = null;
                } else {
                    arrEditTextVals[holder.ref] = Double.parseDouble(s.toString());
                }
                saveData("list_vals_storage", arrEditTextVals);
            }
        });

        if (resource == R.layout.vars_listview_detail_exp) {
            if (arrExpTextVals[position] != null) {
                holder.expText.setText(arrExpTextVals[position].toString());
            } else {
                holder.expText.setText(null);
            }
            holder.expText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!Util.isDouble(s.toString())) {
                        arrExpTextVals[holder.ref] = null;
                    } else {
                        arrExpTextVals[holder.ref] = Integer.parseInt(s.toString());
                    }
                    saveData("exp_vals_storage", arrExpTextVals);
                }
            });
        }

        String vars = getItem(position);
        TextView textView = (TextView) view.findViewById(R.id.vars);
        textView.setText(vars);
        if (resource == R.layout.vars_listview_detail) {
            Button clearBtn = (Button) view.findViewById(R.id.clear);
            clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.editText.setText("");
                }
            });
        }
        return view;
    }

    public Double getInput(int position) {
        if (arrEditTextVals[position] != null) {
            if (!scientific || arrExpTextVals[position] == null) {
                return arrEditTextVals[position];
            }
            return arrEditTextVals[position] * Math.pow(10, arrExpTextVals[position]);
        }
        return null;
    }

    public String getVariable(int position) {
        return variables[position];
    }

    /** Returns number of non-filled variable values */
    public int numEmpty() {
        int count = 0;
        for (Double num : arrEditTextVals) {
            if (num == null) {
                count++;
            }
        }
        return count;
    }

    public int size() {
        return arrExpText.length;
    }

    public class ViewHolder {
        EditText editText;
        EditText expText;
        int ref;
    }

    private void saveData(String filename, Object o) {
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
