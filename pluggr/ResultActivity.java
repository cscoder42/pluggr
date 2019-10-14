package com.techomite.math.pluggr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Steven Yu on 4/6/2018.
 */

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Result");
        Intent intent = getIntent();
        String[] texts = intent.getStringArrayExtra("result_storage");
        TextView equationText = (TextView) findViewById(R.id.equationText);
        equationText.setText(texts[0]);
        TextView variablesText = (TextView) findViewById(R.id.variablesText);
        variablesText.setText(texts[1]);
        TextView valuesText = (TextView) findViewById(R.id.valuesText);
        valuesText.setText(texts[2]);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
