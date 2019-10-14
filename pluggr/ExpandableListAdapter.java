package com.techomite.math.pluggr;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Steven Yu on 10/1/2017.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<String> listHeader;
    private HashMap<String, ArrayList<String>> listHashMap;

    public ExpandableListAdapter(Context context, ArrayList<String> listHeader, HashMap<String, ArrayList<String>> listHashMap) {
        this.context = context;
        this.listHeader = listHeader;
        this.listHashMap = listHashMap;
    }


    @Override
    public int getGroupCount() {
        return listHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listHashMap.get(listHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listHashMap.get(listHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String title = (String) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.select_group, null);
        }
        TextView listHeader = (TextView) convertView.findViewById(R.id.listHeader);
        listHeader.setTypeface(null, Typeface.BOLD);
        listHeader.setText(title);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String listText = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (groupPosition < 5) {
            convertView = inflater.inflate(R.layout.select_item, null);
        } else {
            convertView = inflater.inflate(R.layout.select_custom_item, null);
            ImageButton imageButton = (ImageButton)  convertView.findViewById(R.id.deleteIB);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(context, v);
                    menu.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick (MenuItem item)
                        {
                            if (item.getItemId() == R.id.item_edit) {
                                final AlertDialog.Builder eqDialogBuilder = new AlertDialog.Builder(context);
                                LayoutInflater inflater = LayoutInflater.from(context);
                                View eqDialog = inflater.inflate(R.layout.eq_dialog, null);
                                eqDialogBuilder.setView(eqDialog);
                                eqDialogBuilder.setTitle("Custom Equation Input");
                                eqDialogBuilder.setPositiveButton("Add", null);
                                eqDialogBuilder.setNegativeButton("Cancel", null);
                                final AlertDialog container = eqDialogBuilder.create();
                                container.show();

                                final EditText eqName = (EditText) eqDialog.findViewById(R.id.eqName);
                                eqName.setText(SelectActivity.getCustom().get(childPosition));
                                eqName.setSelection(SelectActivity.getCustom().get(childPosition).length());
                                final EditText eqText = (EditText) eqDialog.findViewById(R.id.eqText);
                                eqText.setText(SelectActivity.getCustomEq()[childPosition]);

                                container.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (eqName.getText().length() == 0 || eqText.getText().length() == 0) {
                                            Toast.makeText(context, "Make sure all fields are not empty.", Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            SelectActivity.deleteCustom(childPosition);
                                            SelectActivity.addCustom(eqName.getText().toString(), eqText.getText().toString(), childPosition);
                                            saveData("custom_storage", SelectActivity.getCustom());
                                            saveData("custom_equation_storage", SelectActivity.getCustomEq());
                                            SelectActivity.updateEqStorage();
                                            ExpandableListAdapter.this.notifyDataSetChanged();
                                            container.dismiss();
                                            Toast.makeText(context, "Your changes were saved.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            else if (item.getItemId() == R.id.item_delete) {
                                SelectActivity.deleteCustom(childPosition);
                                saveData("custom_storage", SelectActivity.getCustom());
                                saveData("custom_equation_storage", SelectActivity.getCustomEq());
                                SelectActivity.updateEqStorage();
                                ExpandableListAdapter.this.notifyDataSetChanged();
                                Toast.makeText(context, "Item was deleted successfully.", Toast.LENGTH_LONG).show();
                            }
                            return true;
                        }
                    });
                    menu.inflate(R.menu.custom_menu);
                    menu.show();
                }
            });
        }
        TextView listChild = (TextView) convertView.findViewById(R.id.listItem);
        listChild.setText(Html.fromHtml("<b>" + listText + "</b><br><i>" + SelectActivity.getEqStorage()[groupPosition][childPosition] + "</i>"));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void saveData(String filename, Object o) {
        try {
            FileOutputStream fos = this.context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
            oos.flush();
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
