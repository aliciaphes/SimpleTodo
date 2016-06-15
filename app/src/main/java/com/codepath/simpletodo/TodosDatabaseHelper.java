package com.codepath.simpletodo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alicia P on 15-Jun-16.
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

        // In any activity just pass the context and use the singleton method
        //PostsDatabaseHelper helper = PostsDatabaseHelper.getInstance(this);
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


}
