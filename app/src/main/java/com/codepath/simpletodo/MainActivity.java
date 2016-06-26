package com.codepath.simpletodo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.codepath.data.TodoDbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import android.app.AlertDialog;

//import com.orm.SugarContext;

/*
 Pending:

el tema fechas?

-create asynctask to run queries off the main thread
-review names of all the variables I use to make sense
(and rename every 'item' to 'todo')
-set all strings to R.string.whatever
-use try/catch blocks


 public static final ALLCAPS for our constants


 sqliteopenbrowser

disable instant run in Android Studio settings if we use Sugar
 */


public class MainActivity extends AppCompatActivity {

    public static final char ACTION_DELETE = 'd';
    public static final char ACTION_UPDATE = 'u';
    public static final char ACTION_READ = 'r';
    public static final char ACTION_CREATE = 'c';
    static final int EDIT_TODO = 1;//action identifier
    static TodoDbHelper todoDBHelper;
    ArrayList<String> items; //----> DELETE !!!!
    ArrayList<Todo> items2;
    //ArrayAdapter<String> itemsAdapter;
    SimpleAdapter todoAdapter;
    ListView lvItems;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize database helper
        todoDBHelper = new TodoDbHelper(this);

        createDummyTodos();


        //retrieve all items
        items2 = todoDBHelper.readAllItems();

        if (items2.size() != 0) {

            //http://files.idg.co.kr/itworld/todoapp05.jpg
            //https://lh3.ggpht.com/C87iFAaYzDqLfwNf3cyC8EkBOdZN-7bQ1JceYuITVfkvapmpZ3A1g1U66enAdB_AyBA

/*
            items = new ArrayList<String>();
            for (int i = 0; i < items2.size(); i++) {
                items.add(items2.get(i).getTitle());
            }
*/

            initializeList();

            //set listener on 'Add' button:
            final Button button = (Button) findViewById(R.id.btnAddUpdateItem);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    openAddOrUpdateDialog(v, null, ACTION_CREATE);
                }
            });
        }
    }

    private void createDummyTodos() {

        todoDBHelper.cleanDatabase();

        for (int i = 0; i < 2; i++) {
            Todo t = new Todo(-1L, "todo" + (i + 1), (i % 2 == 0));
            if (todoDBHelper.createDummyTodo(t) != -1L) {
                //Toast.makeText(this, "record inserted!", Toast.LENGTH_LONG).show();
            }
        }


    }


    private void openDeleteDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this todo?").setTitle("Delete Todo");


        // Add the buttons
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Todo toDelete = items2.get(position);//retrieve todo that was clicked


                long resultOfDeletion = todoDBHelper.deleteTodo(toDelete);//update persistent
                if (resultOfDeletion != -1L) {

                    //items2 = todoDBHelper.readAllItems();
                    //initializeList();
                    refreshListWith(toDelete, ACTION_DELETE);

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


    private void initializeList() {
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

        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(todoAdapter);


        //itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        //lvItems.setAdapter(itemsAdapter);
        setupListViewListener();//set listener for actions to perform on the todos
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
/*
                //create intent that will be launched
                Intent editItemIntent = new Intent(MainActivity.this, EditItemActivity.class);

                //initialize the position of the item that was clicked on
                index = pos;

                //pass current item value to the intent
                editItemIntent.putExtra("value", items.get(index).toString());

                //launch intent ()start the 'edit' activity
                startActivityForResult(editItemIntent, EDIT_TODO);
                */
                Todo todo = items2.get(pos);
                openAddOrUpdateDialog(lvItems, todo, ACTION_UPDATE);
            }
        });
    }


    private void openAddOrUpdateDialog(View v, final Todo t, final char action) {

        LayoutInflater inflater = LayoutInflater.from(this);
        final View textEntryView = inflater.inflate(R.layout.activity_add_dialog, null);

        //create 'hooks' to retrieve the information they contain to use in the listeners below
        final EditText etNewItem = (EditText) textEntryView.findViewById(R.id.todo_title);
        final CheckBox urgentCheckbox = (CheckBox) textEntryView.findViewById(R.id.checkBoxAdd);

        AlertDialog.Builder db = new AlertDialog.Builder(this)
                .setView(textEntryView)
                .setTitle(action == ACTION_CREATE ? "Add Todo" : "Edit Todo");

        String title = "";
        if (action == ACTION_CREATE) {
            title = "add";
            //retrieve text value
            EditText a = (EditText) findViewById(R.id.etNewItem);
            String itemText = a.getText().toString();

            etNewItem.setText(itemText);
            etNewItem.setSelection(itemText.length());

        } else if (action == ACTION_UPDATE) {
            title = "edit";
            etNewItem.setText(t.getTitle());
            etNewItem.setSelection(t.getTitle().length());

            urgentCheckbox.setChecked(t.isUrgent());
        }

        // Add the button and their actions
        db.setPositiveButton(title, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {// User clicked OK button

                long newId;
                boolean isChecked;

                if (action == ACTION_CREATE) {

                    String newText = etNewItem.getText().toString();

                    //retrieve value of checkbox
                    isChecked = urgentCheckbox.isChecked();
                    Todo newTodo = new Todo(-1L, newText, isChecked);

                    //add it to the database
                    newId = todoDBHelper.insertTodo(newTodo);
                    if (newId != -1L) {//insertion successful
                        newTodo.setId(newId);
                        //refresh the visible list
                        refreshListWith(newTodo, ACTION_CREATE);
                    }
                } else if (action == ACTION_UPDATE) {

                    t.setTitle(etNewItem.getText().toString());

                    isChecked = urgentCheckbox.isChecked();
                    t.setUrgency(isChecked);
                    //update todo in the database
                    newId = todoDBHelper.updateTodo(t);
                    if (newId != -1L) {//update successful
                        //refresh the visible list
                        refreshListWith(t, ACTION_UPDATE);
                    }
                }
                cleanTextArea();
            }
        })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                        cleanTextArea();
                    }
                });

        Dialog d = db.create();

        d.show();
    }

    private void cleanTextArea() {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        etNewItem.setText("");
    }


    private void refreshListWith(Todo newTodo, char action) {

        int index;

        switch (action) {
            case ACTION_CREATE:
                items2.add(newTodo);
                break;

            case ACTION_UPDATE:
                //modify the corresponding todo in items2
                index = items2.indexOf(newTodo);
                if (index != -1) {
                    items2.set(index, newTodo);
                }
                break;

            case ACTION_DELETE:
                //remove newTodo from items2
                index = items2.indexOf(newTodo);
                if (index != -1) {
                    items2.remove(index);
                }
                break;

            default:
                return;
        }


        //todoAdapter.clear();
        //todoAdapter.addAll(items2);

        todoAdapter.notifyDataSetChanged();
        lvItems.refreshDrawableState();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == EDIT_TODO) {//identify action

            if (resultCode == RESULT_OK) {//otherwise data will be null

                if (index != -1) {
                    //Log.v("updating", data.getStringExtra("newValue"));
                    items.set(index, data.getStringExtra("newValue"));//make sure it returns something
                }

                //itemsAdapter.notifyDataSetChanged(); //it is needed so results are updated visibly
                todoAdapter.notifyDataSetChanged();


                //SAVE DATA IN THE DATABASE, EITHER BY INSERTING OR UPDATING!!!!!!!!
            }
        }
    }

/*
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
        //THIS IS WHERE WE CLOSE THE DBHELPER

    }
    */

}