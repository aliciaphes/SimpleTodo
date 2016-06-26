package com.codepath.simpletodo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Writing databases:
 * - get reference to writable database
 * - create ContentValues of what you want to insert
 * - insert ContentValues and get a row ID back
 *
 * Reading databases:
 * - query the database and receive a cursor back
 *
 * Validating databases:
 * - validate data in resulting cursor with the original ContentValues
 *
 * CLOSE CURSOR AND DATABASE WHEN FINISHED
 */
public class TodosDatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "todosDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_TODOS = "todos";

    // Table Columns
    private static final String KEY_TODO_ID = "id";
    private static final String KEY_TODO_TITLE = "title";
    private static final String KEY_TODO_DATE = "date";
    private static final String KEY_TODO_PRIORITY = "urgent";

    private static TodosDatabaseHelper sInstance;


    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private TodosDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized TodosDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TodosDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODOS_TABLE = "CREATE TABLE " + TABLE_TODOS +
                "(" +
                KEY_TODO_ID + " INTEGER PRIMARY KEY," + //primary key
                KEY_TODO_TITLE + " TEXT " +
                KEY_TODO_DATE + " DATE" + //check if this data type exists
                KEY_TODO_PRIORITY + " INTEGER" + //sort-of-boolean: 0 means false
                ")";

        db.execSQL(CREATE_TODOS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOS);
            onCreate(db);
        }
    }

    /*
    public ArrayList<String> getAllTodos() {

        ArrayList<String> todos = new ArrayList<String>();//initialize

        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s", TABLE_TODOS);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object
        // (except under low disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Todo newTodo = new Todo();
                    newTodo.title = cursor.getString(cursor.getColumnIndex(KEY_TODO_TITLE));
                    todos.add(newTodo.title);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("Sqlite", "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return todos;
    }


    public ArrayList<String> getAllTodos2() {
        List<Todo> allValues = Todo.listAll(Todo.class);

        //convert this to items and return them
        ArrayList<String> todos = new ArrayList<String>();//initialize
        for (Todo t : allValues) {
            todos.add(t.title);
        }
        return todos;
    }
    */

}
