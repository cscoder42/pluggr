package com.techomite.math.pluggr;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity{

    ListView list;
    ListView listView;
    String[] items;
    private static String[] btnChars;
    private boolean radians;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        items = new String[] {"Custom Equation Input", "Custom Button Input", "Radians", "Clear History", "Help"};
        radians = (boolean) readData("radians_setting", false);
        if (!radians) {
            items[2] = "Degrees";
        }
        String[] buttons_default = new String[] {"[", "]", "=", "^", "π"};
        btnChars = (String []) readData("button_chars_storage", buttons_default);

        list = (ListView) findViewById(R.id.listView);
        final ArrayAdapter arrayAdapter = new SwitchAdapter(this);
        list.setDivider(null);
        list.setDividerHeight(0);
        list.setAdapter(arrayAdapter);

        listView = (ListView) findViewById(R.id.settingsList);
        final ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.settings_detail, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { //Custom Equation Input
                    final AlertDialog.Builder eqDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                    LayoutInflater inflater = SettingsActivity.this.getLayoutInflater();
                    View eqDialog = inflater.inflate(R.layout.eq_dialog, null);
                    eqDialogBuilder.setView(eqDialog);
                    eqDialogBuilder.setTitle("Custom Equation Input");
                    eqDialogBuilder.setPositiveButton("Add", null);
                    eqDialogBuilder.setNegativeButton("Cancel", null);
                    final AlertDialog container = eqDialogBuilder.create();
                    container.show();

                    final EditText name = (EditText) eqDialog.findViewById(R.id.eqName);
                    final EditText text = (EditText) eqDialog.findViewById(R.id.eqText);

                    container.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (name.getText().length() == 0 || text.getText().length() == 0) {
                                Toast.makeText(SettingsActivity.this, "Make sure all fields are not empty.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                SelectActivity.addCustom(name.getText().toString(), text.getText().toString(), SelectActivity.getCustom().size());
                                saveData("custom_storage", SelectActivity.getCustom());
                                saveData("custom_equation_storage", SelectActivity.getCustomEq());
                                container.dismiss();
                                Toast.makeText(SettingsActivity.this, "New custom equation created.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else if (position == 1) { //Custom Button Input
                    final AlertDialog.Builder btnDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                    LayoutInflater inflater = SettingsActivity.this.getLayoutInflater();
                    View btnDialog = inflater.inflate(R.layout.btn_dialog, null);
                    btnDialogBuilder.setView(btnDialog);
                    btnDialogBuilder.setTitle("Custom Button Input");
                    btnDialogBuilder.setPositiveButton("Save", null);
                    btnDialogBuilder.setNegativeButton("Cancel", null);
                    final AlertDialog container = btnDialogBuilder.create();
                    container.show();

                    final EditText btnList[] = new EditText[5];
                    btnList[0] = (EditText) btnDialog.findViewById(R.id.edit1);
                    btnList[1] = (EditText) btnDialog.findViewById(R.id.edit2);
                    btnList[2] = (EditText) btnDialog.findViewById(R.id.edit3);
                    btnList[3] = (EditText) btnDialog.findViewById(R.id.edit4);
                    btnList[4] = (EditText) btnDialog.findViewById(R.id.edit5);

                    for (int i = 0; i < btnList.length; i++) {
                        btnList[i].setText(btnChars[i]);
                    }
                    btnList[0].setSelection(1);

                    container.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean clear = true;
                            for (int i = 0; i < 5; i++) {
                                if (btnList[i].getText().toString().length() != 1) {
                                    clear = false;
                                    Toast.makeText(SettingsActivity.this, "Button" + (i + 1) + " has no input", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            if (clear) {
                                for (int i = 0; i < 5; i++) {
                                    btnChars[i] = btnList[i].getText().toString();
                                }
                                saveData("button_chars_storage", btnChars);
                                container.dismiss();
                                Toast.makeText(SettingsActivity.this, "Your changes were saved", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else if (position == 2) {
                    if (items[2].equals("Degrees")) { //Degrees-Radians
                        items[2] = "Radians";
                        radians = true;
                        CalculatorActivity.radians = true; //forced edge case
                        saveData("radians_setting", true);
                        adapter.notifyDataSetChanged();
                    }
                    else {//if (items[2].equals("Radians")) //Radians-Degrees
                        items[2] = "Degrees";
                        radians = false;
                        CalculatorActivity.radians = false; //forced edge case
                        saveData("radians_setting", false);
                        adapter.notifyDataSetChanged();
                    }
                }
                else if (position == 3) { //Clear History
                    ArrayList<ArrayList<String>> reset = new ArrayList<ArrayList<String>>(3);
                    for (int x = 0; x < 3; x++) {
                        reset.add(new ArrayList<String>());
                    }
                    saveData("history_storage", reset);
                    Toast.makeText(SettingsActivity.this, "History deleted successfully", Toast.LENGTH_LONG).show();
                }
                else if (position == 4) { //Help
                    final AlertDialog.Builder helpDialog = new AlertDialog.Builder(SettingsActivity.this);
                    LayoutInflater inflater = SettingsActivity.this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.help_dialog, null);
                    helpDialog.setPositiveButton("OK", null);
                    helpDialog.setTitle("Help");

                    TextView text = (TextView) dialogView.findViewById(R.id.textView);
                    text.setText(Html.fromHtml("<b><br>① Denote any unknown characters in brackets.</b><br>" +
                            "<i>Only variables A-Z and a-z are supported in this version. (except e represents the number e) However, by enclosing any " +
                            "characters in between brackets, (i.e. [σ]) you can use other characters as variables, up to two characters per variable.</i><br><br>" +
                            "<b>② Select a pre-set equation</b><br>" +
                            "<i>Select the</i>  ▼  <i>when the equation input is empty, and you can select equations to use from 6 different categories. This " +
                            "version offers over 50 pre-set equations for you to select, as well as custom equations you can input yourself.</i><br><br>" +
                            "<b>③ Add your own equations</b><br>" +
                            "<i>Tap</i>  ⋮  <i>in the toolbar to access the Custom Equation Input menu in <b>Settings</b>. Your saved equations can be found in the " +
                            "Equation Select menu under the <b>Custom</b> category.</i><br><br>" +
                            "<b>④ Customize the buttons</b><br>" +
                            "<i>Tap</i>  ⋮  <i>in the toolbar to access the Custom Button Input menu in <b>Settings</b>. You can edit the 5 characters that are accessed by the buttons.</i><br>"));
                    text.setMovementMethod(new ScrollingMovementMethod());
                    helpDialog.setView(dialogView);
                    helpDialog.show();
                }
            }
        });
    }

    private void saveData(String filename, Object o) {
        try {
            FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE);
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
            FileInputStream fis = openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object returnObject = ois.readObject();
            ois.close();
            fis.close();
            return returnObject;
        } catch (ClassNotFoundException | IOException e) {
            return _default;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
