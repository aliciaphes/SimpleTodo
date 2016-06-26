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
        setContentView(R.layout.activity_current_item);

        etItemToEdit = (EditText) findViewById(R.id.itemToEdit);

        //get the intent that started this activity
        editIntent = getIntent();

        //retrieve text from parent activity and put it in the text area
        String retrievedValue = editIntent.getStringExtra("value");
        etItemToEdit.setText(retrievedValue);
        etItemToEdit.setSelection(retrievedValue.length());//put cursor at end of word
    }

    public void saveNewValue(View v) {//action to perform when 'save' button is clicked
        String newValue = etItemToEdit.getText().toString();

        //ArrayList<String> items = returnIntent.getStringArrayListExtra("items");
        //if(pos != -1) {
        //items.set(pos, newValue);
        //}

        //send new value back to parent activity
        editIntent.putExtra("newValue", newValue);
        Toast.makeText(getBaseContext(), "Updating...", Toast.LENGTH_LONG).show();
        setResult(Activity.RESULT_OK, editIntent);//send result as Intent object
        finish();//end intent
    }
}
