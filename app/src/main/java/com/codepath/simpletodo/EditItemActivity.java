package com.codepath.simpletodo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    EditText etItemToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        etItemToEdit = (EditText) findViewById(R.id.itemToEdit);
        String retrievedValue = getIntent().getStringExtra("value");
        etItemToEdit.setText(retrievedValue);
        etItemToEdit.setSelection(retrievedValue.length());//put cursor at end of word
    }

    public void saveNewValue(View v) {
        //this.putExtra("newValue", etItemToEdit.getText());
        finish();
    }
}
