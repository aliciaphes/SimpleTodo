package com.codepath.simpletodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditItemActivity extends AppCompatActivity {

    EditText etItemToEdit;
    Intent editIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        etItemToEdit = (EditText) findViewById(R.id.itemToEdit);

        editIntent = getIntent();
        String retrievedValue = editIntent.getStringExtra("value");
        etItemToEdit.setText(retrievedValue);
        etItemToEdit.setSelection(retrievedValue.length());//put cursor at end of word
    }

    public void saveNewValue(View v) {
        String newValue = etItemToEdit.getText().toString();

        //int pos = returnIntent.getIntExtra("index", -1);
        //ArrayList<String> items = returnIntent.getStringArrayListExtra("items");
        //if(pos != -1) {
        //items.set(pos, newValue);
        //Toast.makeText(getBaseContext(), "updating", Toast.LENGTH_LONG).show();
        //}
        //returnIntent.putExtra("items", items);

        
        editIntent.putExtra("newValue", newValue);
        Toast.makeText(getBaseContext(), "new value = " + newValue, Toast.LENGTH_LONG).show();
        int pos = editIntent.getIntExtra("index", -1);
        editIntent.putExtra("index", pos);
        setResult(Activity.RESULT_OK, editIntent);
        finish();
    }
}
