package com.codepath.simpletodo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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



/*
 Pending:

el tema fechas?

-create asynctask to run queries off the main thread
-review names of all the variables I use to make sense
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
    //static final int EDIT_TODO = 1;//action identifier
    static TodoDbHelper todoDBHelper;

    ArrayList<Todo> todoList;
    List<Map<String, String>> todoListVisible;
    SimpleAdapter todoAdapter;
    ListView lvTodos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize database helper
        todoDBHelper = new TodoDbHelper(this);

        createDummyTodos();


        //retrieve all todos
        todoList = todoDBHelper.readAllItems();


        //http://files.idg.co.kr/itworld/todoapp05.jpg
        //https://lh3.ggpht.com/C87iFAaYzDqLfwNf3cyC8EkBOdZN-7bQ1JceYuITVfkvapmpZ3A1g1U66enAdB_AyBA

        initializeList();


        //set listener on 'Add' button:
        final Button button = (Button) findViewById(R.id.btnAddUpdateTodo);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openAddOrUpdateDialog(v, null, ACTION_CREATE);
            }
        });

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
                Todo toDelete = todoList.get(position);//retrieve todo that was clicked


                long resultOfDeletion = todoDBHelper.deleteTodo(toDelete);//update persistent
                if (resultOfDeletion != -1L) {

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

        //https://4.bp.blogspot.com/_I2Ctfz7eew4/S82CgLXsgqI/AAAAAAAAAZo/o10yCm3Efzc/s1600/CustomListView2.1.PNG
        //List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        todoListVisible = new ArrayList<Map<String, String>>();
        for (Todo todo : todoList) {
            Map<String, String> todoInfo = new HashMap<String, String>(2);
            todoInfo.put("title", todo.getTitle());
            todoInfo.put("urgent", todo.isUrgent() ? "urgent" : "");
            todoListVisible.add(todoInfo);
        }
        todoAdapter = new SimpleAdapter(this, todoListVisible,
                android.R.layout.simple_list_item_2,
                new String[]{"title", "urgent"},
                new int[]{android.R.id.text1, android.R.id.text2});

        lvTodos = (ListView) findViewById(R.id.lvTodos);
        lvTodos.setAdapter(todoAdapter);

        setupListViewListener();//set listener for actions to perform on the todos
    }


    private void setupListViewListener() {
        //long clicking on an todo deletes it (and updates accordingly):
        lvTodos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {

                openDeleteDialog(pos);//send position of the todo that was clicked

                return true;
            }
        });

        //simple click launches the EditItem activity:
        lvTodos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                Todo todo = todoList.get(pos);
                openAddOrUpdateDialog(lvTodos, todo, ACTION_UPDATE);
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
            EditText a = (EditText) findViewById(R.id.etNewTodo);
            String todoText = a.getText().toString();

            etNewItem.setText(todoText);
            etNewItem.setSelection(todoText.length());

        } else if (action == ACTION_UPDATE) {
            title = "update";
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
                        Toast.makeText(MainActivity.this, "todo was created", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(MainActivity.this, "todo was updated", Toast.LENGTH_LONG).show();
                    }
                }
                cleanTextArea();
            }
        })
                .setNeutralButton("email", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //create intent that will be launched
                        Intent emailTodoIntent = new Intent(Intent.ACTION_SENDTO);

                        emailTodoIntent.setData(Uri.parse("mailto:")); // only allow email apps

                        String urgent = (t.isUrgent() ? "[URGENT] " : "");
                        emailTodoIntent.putExtra(Intent.EXTRA_SUBJECT, "Don't forget to...");
                        emailTodoIntent.putExtra(Intent.EXTRA_TEXT, urgent + t.getTitle());

                        if (emailTodoIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(emailTodoIntent);
                        }
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
        EditText etNewItem = (EditText) findViewById(R.id.etNewTodo);
        etNewItem.setText("");
    }


    private void refreshListWith(Todo newTodo, char action) {

        int index;
        Map<String, String> todoInfo;

        switch (action) {
            case ACTION_CREATE:
                todoList.add(newTodo);

                todoInfo = new HashMap<String, String>(2);
                todoInfo.put("title", newTodo.getTitle());
                todoInfo.put("urgent", newTodo.isUrgent() ? "urgent" : "");
                todoListVisible.add(todoInfo);
                break;

            case ACTION_UPDATE:
                //modify the corresponding todo in todoList
                index = todoList.indexOf(newTodo);
                if (index != -1) {
                    todoList.set(index, newTodo);

                    todoInfo = new HashMap<String, String>(2);
                    todoInfo.put("title", newTodo.getTitle());
                    todoInfo.put("urgent", newTodo.isUrgent() ? "urgent" : "");
                    todoListVisible.set(index, todoInfo);
                }
                break;

            case ACTION_DELETE:
                //remove newTodo from todos
                index = todoList.indexOf(newTodo);
                if (index != -1) {
                    todoList.remove(index);
                    todoListVisible.remove(index);
                }
                break;

            default:
                return;
        }

        todoAdapter.notifyDataSetChanged();//update visibility of todos
    }




/*
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