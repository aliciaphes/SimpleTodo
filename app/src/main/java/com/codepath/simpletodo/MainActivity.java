package com.codepath.simpletodo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.codepath.data.TodoDbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.orm.SugarContext;

/**
 * Pending:
 * r - read all - devuelve un arraylist<todo>
 * <p/>
 * c - enviar todo
 * <p/>
 * u - enviar todo
 * <p/>
 * d - enviar id del todo a borrar
 * <p/>
 * <p/>
 * <p/>
 * el tema fechas?
 * <p/>
 * set all strings to R.string.whatever
 */


public class MainActivity extends AppCompatActivity {

    static final int EDIT_TODO = 1;//action identifier

    ArrayList<String> items; //----> DELETE !!!!
    ArrayList<Todo> items2;

    //ArrayAdapter<String> itemsAdapter;
    SimpleAdapter todoAdapter;

    ListView lvItems;
    int index;

    TodoDbHelper todoDBHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //disable instant run in Android Studio settings

        //SugarContext.init(getApplicationContext());


        //initialize database helper
        todoDBHelper = new TodoDbHelper(this);

        //CREATE DUMMY TODOS
        todoDBHelper.cleanDatabase();
        for (int i = 0; i < 5; i++) {
            if (todoDBHelper.createDummyTodo("todo" + i) != -1L) {
                Toast.makeText(this, "record inserted!", Toast.LENGTH_LONG).show();
            }
        }

        //retrieve all items
        items2 = todoDBHelper.readAllItems();

        if (items2.size() != 0) {
            lvItems = (ListView) findViewById(R.id.lvItems);

            //http://files.idg.co.kr/itworld/todoapp05.jpg
            //https://lh3.ggpht.com/C87iFAaYzDqLfwNf3cyC8EkBOdZN-7bQ1JceYuITVfkvapmpZ3A1g1U66enAdB_AyBA

            items = new ArrayList<String>();
            for (int i = 0; i < items2.size(); i++) {
                items.add(items2.get(i).getTitle());
            }

            showItems();
        }
    }


    private void openDeleteDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this todo?")
                .setTitle("Delete Todo");


        // Add the buttons
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Todo toDelete = items2.get(position);//retrieve todo that was clicked
                if (todoDBHelper.deleteTodo(toDelete) != -1L) {//update persistent

                    //items.remove(position);
                    items2 = todoDBHelper.readAllItems();
                    showItems();


                    Toast.makeText(MainActivity.this, "todo was deleted", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showItems() {
        //itemsAdapter.notifyDataSetChanged();//update visibility of items
        //todoAdapter.notifyDataSetChanged();//update visibility of items

        //https://4.bp.blogspot.com/_I2Ctfz7eew4/S82CgLXsgqI/AAAAAAAAAZo/o10yCm3Efzc/s1600/CustomListView2.1.PNG
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        for (Todo item : items2) {
            Map<String, String> todoInfo = new HashMap<String, String>(2);
            todoInfo.put("title", item.getTitle());
            todoInfo.put("urgent", item.isUrgent() ? "urgent" : "");
            data.add(todoInfo);
        }
        todoAdapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[]{"title", "urgent"},
                new int[]{android.R.id.text1, android.R.id.text2});
        lvItems.setAdapter(todoAdapter);


        //itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        //lvItems.setAdapter(itemsAdapter);
        setupListViewListener();


    }


    private void setupListViewListener() {
        //long clicking on an item deletes it (and updates accordingly):
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {

                openDeleteDialog(pos);//send position of the todo that was clicked

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


    public void addItem(View v) {//action for the 'add item' button
        //retrieve text
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();

        //launch dialog

        //todoAdapter.add(itemText);//add item to adapter

        etNewItem.setText("");//clean text area

        //WRITE ON DATABASE
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

                //itemsAdapter.notifyDataSetChanged(); //it is needed so results are updated visibly
                todoAdapter.notifyDataSetChanged();


                //SAVE DATA IN THE DATABASE, EITHER BY INSERTING OR UPDATING!!!!!!!!
            }
        }
    }

/*
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
    */



/*
    @Override
    public void onDestroy() {
        super.onDestroy();
        //SugarContext.terminate();
    }
    */

}