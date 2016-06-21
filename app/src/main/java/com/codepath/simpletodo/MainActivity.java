package com.codepath.simpletodo;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.codepath.data.TodoDbHelper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//import com.orm.SugarContext;

public class MainActivity extends AppCompatActivity {

    static final int EDIT_TODO = 1;//action identifier
    ArrayList<String> items;
    ArrayList<Todo> items2;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    int index;
    TodoDbHelper todoDBHelper;
    ContentValues records;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //disable instant run in Android Studio settings

        //SugarContext.init(getApplicationContext());


        //initialize database helper
        todoDBHelper = new TodoDbHelper(this);

        //retrieve all items
        //readItems();//initialize 'items'
        items2 = todoDBHelper.readAllItems();

        if (items2 != null) {
            lvItems = (ListView) findViewById(R.id.lvItems);

            //Log.v("test", items.get(0));

            //http://files.idg.co.kr/itworld/todoapp05.jpg
            //https://lh3.ggpht.com/C87iFAaYzDqLfwNf3cyC8EkBOdZN-7bQ1JceYuITVfkvapmpZ3A1g1U66enAdB_AyBA

            itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
            lvItems.setAdapter(itemsAdapter);
            setupListViewListener();
        }
    }


    private void setupListViewListener() {
        //long clicking on an item deletes it (and updates accordingly):
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                items.remove(pos);
                itemsAdapter.notifyDataSetChanged();//update visibility of items
                writeItems2();//update persistent
                return true;
            }
        });

        //simple click launches the EditItem activity:
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {

                //create intent that will be launched
                Intent editItemIntent = new Intent(MainActivity.this, EditItemActivity.class);

                //initialize the position of the item that was clicked on
                index = pos;

                //pass current item value to the intent
                editItemIntent.putExtra("value", items.get(index).toString());

                //launch intent ()start the 'edit' activity
                startActivityForResult(editItemIntent, EDIT_TODO);
            }
        });
    }

    public void onAddItem(View v) {//action for the 'add item' button
        //retrieve text
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();

        itemsAdapter.add(itemText);//add item to adapter
        etNewItem.setText("");//clean text area
        writeItems2();//update persistent
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == EDIT_TODO) {//identify action

            if (resultCode == RESULT_OK) {//otherwise data will be null

                if (index != -1) {
                    //Log.v("updating", data.getStringExtra("newValue"));
                    items.set(index, data.getStringExtra("newValue"));//make sure it returns something
                }
                /*
                for (String itemStr : items) {
                    Log.v("ains", itemStr);
                }*/
                itemsAdapter.notifyDataSetChanged(); //it is needed so results are updated visibly
                writeItems2();//make current data persistent
            }
        }
    }


    private void readItems() {
        File filesDir = getFilesDir();

        //CharSequence text = filesDir.toString();
        //Log.v(getBaseContext(), text, Toast.LENGTH_LONG).show();

        File todoFile = new File(filesDir, "todo.txt");
        try {
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            items = new ArrayList<String>();
        }
    }



    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void writeItems2() {

    }


/*    @Override
    public void onDestroy() {
        super.onDestroy();
        //SugarContext.terminate();
    }*/

}